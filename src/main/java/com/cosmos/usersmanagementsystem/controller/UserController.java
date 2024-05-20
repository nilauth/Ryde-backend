package com.cosmos.usersmanagementsystem.controller;
import com.cosmos.usersmanagementsystem.dto.*;
import com.cosmos.usersmanagementsystem.entity.OurUsers;
import com.cosmos.usersmanagementsystem.entity.Reservation;
import com.cosmos.usersmanagementsystem.entity.Villes;
import com.cosmos.usersmanagementsystem.service.DriverServices;
import com.cosmos.usersmanagementsystem.service.ReservationService;
import com.cosmos.usersmanagementsystem.service.UserServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final ReservationService reservationService;
    private final DriverServices driverServices;
    private final UserServices userServices;
    @GetMapping("/current-user")
    public UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (UserDetails) authentication.getPrincipal();
    }
    @PostMapping("/add-reservation")
    public ResponseEntity<String> addReservation(@RequestBody ReservationDTO reservationDTO) {
        ReservationDTO addedReservation = null;
        try {
            addedReservation = reservationService.addReservation(reservationDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(addedReservation.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(addedReservation.getMessage()+" "+e.getMessage());
        }
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Integer reservationId,
                                                            @RequestBody ReservationDTO updatedReservationDTO) {
        ReservationDTO updatedReservation = reservationService.updateReservation(reservationId, updatedReservationDTO);
        if (updatedReservation != null) {
            return ResponseEntity.ok(updatedReservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

        @GetMapping("/getAll-reservation-client/{userId}")
    public ResponseEntity<List<ReservationDTO>> getAllReservationsClient(@PathVariable Integer userId) {
        List<ReservationDTO> reservations = reservationService.getAllReservations(userId);
            System.out.println(reservations);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/getAll-reservation")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservationsAdmin();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/offers")
    public ResponseEntity<List<OffresDTO>> getAllOffres() {
        List<OffresDTO> offresList = driverServices.getAllOffres();
        return ResponseEntity.ok(offresList);
    }

    @PostMapping("/offersFiltre")
    public ResponseEntity<List<OffresDTO>> getOffresFiltered(@RequestBody OffresDTO offresDTO
    )
    {
        System.out.println();
        List<OffresDTO> offresList = userServices.getOffreFiltered(offresDTO.getVilleDepart(), offresDTO.getVilleArriv(), offresDTO.getDate());
        return ResponseEntity.ok(offresList);
    }
}


