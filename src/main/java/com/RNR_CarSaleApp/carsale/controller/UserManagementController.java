package com.RNR_CarSaleApp.carsale.controller;


import com.RNR_CarSaleApp.carsale.dto.ReqRes;
import com.RNR_CarSaleApp.carsale.model.Users;
import com.RNR_CarSaleApp.carsale.service.UsersManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
public class UserManagementController {


    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> regeister(@RequestBody ReqRes reg){
        return ResponseEntity.ok(usersManagementService.register(reg));
    }


    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.login(req));
    }



    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-all-admins")
    public ResponseEntity<ReqRes> getAdmins(){
        return ResponseEntity.ok(usersManagementService.getAdmins());

    }


    // New endpoint for verifyAndRegister
    @PostMapping("/auth/verify")
    public ResponseEntity<ReqRes> verifyAndRegister(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otpEntered = request.get("otpEntered");

        ReqRes response = usersManagementService.verifyAndRegister(email, otpEntered);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



    @GetMapping("/admin/get-only-users")
    public ResponseEntity<ReqRes> getUsers(){
        return ResponseEntity.ok(usersManagementService.getUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUSerByID(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody Users reqres){
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqres));
    }

    @GetMapping("/admin/filter")
    public ResponseEntity<ReqRes> filterUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {

        ReqRes response = usersManagementService.filterUsers(name, email, role);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersManagementService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUSer(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }

}
