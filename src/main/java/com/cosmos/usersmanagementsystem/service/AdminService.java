package com.cosmos.usersmanagementsystem.service;

import com.cosmos.usersmanagementsystem.dto.DemandeDriverDto;
import com.cosmos.usersmanagementsystem.dto.TrajetAdminDto;
import com.cosmos.usersmanagementsystem.entity.DemandeDriver;
import com.cosmos.usersmanagementsystem.entity.Offres;
import com.cosmos.usersmanagementsystem.entity.OurUsers;
import com.cosmos.usersmanagementsystem.entity.Reservation;
import com.cosmos.usersmanagementsystem.repository.DemandeRepository;
import com.cosmos.usersmanagementsystem.repository.ReservationRepository;
import com.cosmos.usersmanagementsystem.repository.UsersRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {
    private final ReservationRepository reservationRepository;
    private final DemandeRepository demandeRepository;
    private final UsersRepo usersRepo;
    public List<TrajetAdminDto> getAllTrajets(){
        List<Reservation> reservations = reservationRepository.findAll();
        System.out.println("res:" + reservations.size());
        List<TrajetAdminDto> trajets = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Offres offre = reservation.getOffre();
            System.out.println("offre" + offre.getId());
            TrajetAdminDto trajetAdminDto = mapToDto(reservation, offre);
            trajets.add(trajetAdminDto);
        }

        return trajets;
    }
    public TrajetAdminDto mapToDto(Reservation reservation,Offres offres){
        TrajetAdminDto trajetAdminDto=new TrajetAdminDto();
        trajetAdminDto.setId(offres.getId());
        trajetAdminDto.setDriverName(offres.getDriver().getName());
        trajetAdminDto.setClientName(reservation.getUser().getName());
        trajetAdminDto.setVilleDepart(offres.getVilleDepart());
        trajetAdminDto.setVilleArriv(offres.getVilleArriv());
        trajetAdminDto.setPrix(offres.getPrix());
        return trajetAdminDto;
    }

    public List<DemandeDriverDto> getAllDemande() {
        List<DemandeDriver> demandeDrivers= demandeRepository.findAll();
        return demandeDrivers.stream()
                .map(this::DemandetoDto)
                .collect(Collectors.toList());

    }
    public DemandeDriverDto DemandetoDto(DemandeDriver demandeDriver) {
        if (demandeDriver == null) {
            return null;
        }
        return DemandeDriverDto.builder()
                .id(demandeDriver.getId())
                .driverId(demandeDriver.getOurUsers() != null ? demandeDriver.getOurUsers().getId() : null)
                .status(demandeDriver.getStatus())
                .build();
        }
    public DemandeDriver DemandetoEntity(DemandeDriverDto demandeDriverDto, OurUsers ourUsers) {
        if (demandeDriverDto == null) {
            return null;
        }
        return DemandeDriver.builder()
                .id(demandeDriverDto.getId())
                .ourUsers(ourUsers)
                .status(demandeDriverDto.getStatus())
                .build();
    }

    public void becomeDriver(Integer demandeId) {
        DemandeDriver demandeDriver=demandeRepository.findById(demandeId).get();
        demandeDriver.setStatus("Accepter");
        demandeRepository.save(demandeDriver);
        OurUsers driver = demandeDriver.getOurUsers();
        driver.setRole("DRIVER");
        usersRepo.save(driver);
    }

    public void stayUser(Integer demandeId) {
        DemandeDriver demandeDriver=demandeRepository.findById(demandeId).get();
        demandeDriver.setStatus("Refuser");
        demandeRepository.save(demandeDriver);
    }
}

