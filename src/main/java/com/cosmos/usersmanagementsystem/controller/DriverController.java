package com.cosmos.usersmanagementsystem.controller;


import com.cosmos.usersmanagementsystem.dto.OffresDTO;
import com.cosmos.usersmanagementsystem.entity.Offres;
import com.cosmos.usersmanagementsystem.service.DriverServices;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/driver")
@AllArgsConstructor
public class DriverController {
    private DriverServices driverServices;
    @PostMapping("/offers/add")
    public ResponseEntity<String> addOffre(@RequestBody OffresDTO offreDTO) {
        try {
            System.out.println("Received offer DTO: {}"+ offreDTO);
            Offres addedOffre = driverServices.addOffre(offreDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Offer added successfully with ID: " + addedOffre.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add offer: " + e.getMessage());
        }
    }
    @PutMapping("/offers/{offreId}")
    public ResponseEntity<OffresDTO> updateOffres(@PathVariable Integer offreId, @RequestBody Offres offres) {
        OffresDTO updatedOffreDTO = driverServices.updateOffres(offreId, offres);
        if (updatedOffreDTO != null) {
            return ResponseEntity.ok(updatedOffreDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/offers/{offreId}")
    public ResponseEntity<String> deleteOffre(@PathVariable Integer offreId) {
        boolean deleted = driverServices.deleteOffre(offreId);
        if (deleted) {
            return ResponseEntity.ok("Offer with ID " + offreId + " deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/get-offers/{driverId}")
    public ResponseEntity<List<OffresDTO>> getAllOffres(@PathVariable Integer driverId) {
        List<OffresDTO> offresList = driverServices.getAllOffresDriver(driverId);
        if(!(offresList==null)){
            return ResponseEntity.ok(offresList);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/closeOffer/{offreId}")
    public ResponseEntity<String> closeoffer(@PathVariable String offreId){
        OffresDTO closeed = driverServices.CloseOffre(offreId);
        if (closeed != null) {
            return ResponseEntity.ok(closeed.getMessage());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/closeVoyage")
    public ResponseEntity<String> closevoyage(@RequestBody OffresDTO offresDTO){
        OffresDTO closeed = driverServices.CloseVoyage(offresDTO);
        if (closeed != null) {
            return ResponseEntity.ok(offresDTO.getMessage());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

