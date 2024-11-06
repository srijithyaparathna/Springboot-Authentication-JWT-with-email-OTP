package com.RNR_CarSaleApp.carsale.service.impl;

import com.RNR_CarSaleApp.carsale.dao.UsersRepo;
import com.RNR_CarSaleApp.carsale.dto.ReqRes;
import com.RNR_CarSaleApp.carsale.model.OTPVerification;
import com.RNR_CarSaleApp.carsale.model.Users;
import com.RNR_CarSaleApp.carsale.service.UsersManagementService;
import com.RNR_CarSaleApp.carsale.service.JWTUtils;
import com.RNR_CarSaleApp.carsale.dao.OTPVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.RNR_CarSaleApp.carsale.service.impl.SendEmailServiceImpl;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UsersManagementServiceImpl implements UsersManagementService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SendEmailServiceImpl sendEmailService;

@Autowired
    private OTPVerificationRepository otpVerificationRepository;

    // Generate a 6-digit OTP
    public String generateOTP() {
        Random rand = new Random();
        int otp = rand.nextInt(999999 - 100000 + 1) + 100000; // Generates a 6-digit OTP
        return String.valueOf(otp);
    }

    @Override
    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            // Generate OTP
            String otp = generateOTP();

            // Create OTP verification details
            OTPVerification otpVerification = new OTPVerification();
            otpVerification.setOtp(otp);
            otpVerification.setExpiredAt(LocalDateTime.now().plusMinutes(10)); // OTP expires in 10 minutes
            otpVerification.setVerified(false); // Initially unverified

            // Prepare user data
            Users user = new Users();
            user.setEmail(registrationRequest.getEmail());
            user.setRole(registrationRequest.getRole());
            user.setName(registrationRequest.getName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setAccountVerified(String.valueOf(false)); // Set as unverified initially

            // Save user to the database
            Users savedUser = usersRepo.save(user);
            if (savedUser.getId() > 0) {
                otpVerification.setUser(savedUser); // Associate OTP with the user
                otpVerificationRepository.save(otpVerification); // Save OTP verification data

                // Send OTP to user's email
                sendEmailService.sendOTPEmail(registrationRequest.getEmail(), otp); // Assuming sendEmailService is injected

                // Response message
                resp.setUsers(savedUser);
                resp.setMessage("User created and OTP sent. Please verify your email.");
                resp.setStatusCode(200);
            } else {
                resp.setMessage("User creation failed.");
                resp.setStatusCode(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Override
    public ReqRes verifyAndRegister(String email, String otpEntered) {
        ReqRes resp = new ReqRes();
        try {
            // Retrieve OTP record from the database
            OTPVerification otpVerification = otpVerificationRepository.findByUser_EmailAndOtp(email, otpEntered)
                    .orElse(null);

            // Validate OTP existence and expiry
            if (otpVerification != null) {
                if (otpVerification.getExpiredAt().isBefore(LocalDateTime.now())) {
                    resp.setMessage("OTP has expired.");
                    resp.setStatusCode(400);
                    return resp;
                }

                // Verify OTP and update user's verification status
                if (otpVerification.getOtp().equals(otpEntered)) {
                    otpVerification.setVerified(true); // Mark OTP as verified
                    otpVerificationRepository.save(otpVerification);

                    Users user = usersRepo.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    user.setAccountVerified(String.valueOf(true)); // Activate user account
                    usersRepo.save(user);

                    resp.setMessage("OTP verified successfully. Account is now activated.");
                    resp.setStatusCode(200);
                } else {
                    resp.setMessage("Incorrect OTP.");
                    resp.setStatusCode(400);
                }
            } else {
                resp.setMessage("OTP not found or expired.");
                resp.setStatusCode(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }


    @Override
    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes response = new ReqRes();
        try {
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            Users users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ReqRes updateUser(Integer userId, Users updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                Users existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                Users savedUser = usersRepo.save(existingUser);
                reqRes.setUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();
        try {
            if (!isAdminUser()) {
                reqRes.setStatusCode(403);
                reqRes.setMessage("User is not admin");
                return reqRes;
            }
            List<Users> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    private boolean isAdminUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        }
        return false;
    }

    @Override
    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            Users usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }




    @Override
    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Users> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;
    }

    @Override
    public ReqRes getAdmins(){
        ReqRes reqRes = new ReqRes();

        try{
            List<Users> admins = usersRepo.findByRole("ADMIN");
            if (!admins.isEmpty()){
                reqRes.setUsersList(admins);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successfully retrived all admins");
            }else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No admins found");
            }
        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while retrieving admins" + e.getMessage() );
        }
        return reqRes;
    }


    @Override
    public ReqRes getUsers(){
        ReqRes reqRes = new ReqRes();

        try {
            List<Users> users = usersRepo.findByRole("USER"); // Adjusted method name to match UsersRepo method
            if (!users.isEmpty()) {
                reqRes.setUsersList(users);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successfully retrieved all Users");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while retrieving users: " + e.getMessage());
        }
        return reqRes;
    }


    @Override
    public ReqRes filterUsers(String name, String email, String role) {
        ReqRes reqRes = new ReqRes();
        try {
            List<Users> filteredUsers;

            // Fetch users based on the provided criteria
            if (name != null && !name.isEmpty() && email != null && !email.isEmpty() && role != null && !role.isEmpty()) {
                // Filter by name, email, and role
                filteredUsers = usersRepo.findByNameContainingIgnoreCaseAndEmailAndRole(name, email, role);
            } else if (name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
                // Filter by name and email
                filteredUsers = usersRepo.findByNameContainingIgnoreCaseAndEmail(name, email);
            } else if (name != null && !name.isEmpty() && role != null && !role.isEmpty()) {
                // Filter by name and role
                filteredUsers = usersRepo.findByNameContainingIgnoreCaseAndRole(name, role);
            } else if (email != null && !email.isEmpty() && role != null && !role.isEmpty()) {
                // Filter by email and role
                filteredUsers = usersRepo.findByEmailAndRole(email, role);
            } else if (name != null && !name.isEmpty()) {
                // Filter by name only
                filteredUsers = usersRepo.findByNameContainingIgnoreCase(name);
            } else if (email != null && !email.isEmpty()) {
                // Filter by email only
                filteredUsers = usersRepo.findByEmail(email).map(List::of).orElse(List.of());
            } else if (role != null && !role.isEmpty()) {
                // Filter by role only
                filteredUsers = usersRepo.findByRole(role);
            } else {
                // If no criteria are provided, retrieve all users
                filteredUsers = usersRepo.findAll();
            }

            if (!filteredUsers.isEmpty()) {
                reqRes.setUsersList(filteredUsers);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successfully retrieved filtered users");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found matching the criteria");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while filtering users: " + e.getMessage());
        }
        return reqRes;
    }


}
