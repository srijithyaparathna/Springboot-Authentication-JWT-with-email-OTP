package com.RNR_CarSaleApp.carsale.service;

import com.RNR_CarSaleApp.carsale.dto.ReqRes;
import com.RNR_CarSaleApp.carsale.model.Users;

public interface UsersManagementService {
    ReqRes register(ReqRes registrationRequest);
    ReqRes login(ReqRes loginRequest);
    ReqRes refreshToken(ReqRes refreshTokenRequest);
    ReqRes getAllUsers();
    ReqRes getUsersById(Integer id);
    ReqRes deleteUser(Integer userId);
    ReqRes updateUser(Integer userId, Users updatedUser);
    ReqRes getMyInfo(String email);
    ReqRes getAdmins();
    ReqRes getUsers();
    ReqRes filterUsers(String name, String email, String role);

    // Added verifyAndRegister method
    ReqRes verifyAndRegister(String email, String otpEntered);
}
