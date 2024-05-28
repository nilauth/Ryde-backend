package com.cosmos.usersmanagementsystem.controller;

import com.cosmos.usersmanagementsystem.dto.ReqRes;
import com.cosmos.usersmanagementsystem.dto.ReservationDTO;
import com.cosmos.usersmanagementsystem.dto.TrajetAdminDto;
import com.cosmos.usersmanagementsystem.entity.OurUsers;
import com.cosmos.usersmanagementsystem.service.AdminService;
import com.cosmos.usersmanagementsystem.service.ReservationService;
import com.cosmos.usersmanagementsystem.service.UsersManagementService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class Admin {
    private UsersManagementService usersManagementService;
    private  ReservationService reservationService;
    private AdminService adminService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> regeister(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.register(req));
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

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUSerByID(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody OurUsers reqres){
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqres));
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
    @DeleteMapping("/delete-reservation-admin/{reservationId}")
    public ResponseEntity<String> deleteResevation(@PathVariable Integer reservationId){
        ReservationDTO deletedResevation = reservationService.deleteReservation(reservationId);
        if(deletedResevation != null){
            return ResponseEntity.ok(deletedResevation.getMessage());
        }else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/admin/get-all-trajets")
    public ResponseEntity<String> getAlltrajets(){
        if (!(adminService.getAllTrajets()==null)){
            return ResponseEntity.ok("Listes des Trajets");
        }else {
            return ResponseEntity.notFound().build();
        }

    }


}
