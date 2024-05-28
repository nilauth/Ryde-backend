package com.cosmos.usersmanagementsystem.service;

import com.cosmos.usersmanagementsystem.dto.TrajetAdminDto;
import com.cosmos.usersmanagementsystem.entity.Offres;
import com.cosmos.usersmanagementsystem.entity.Reservation;
import com.cosmos.usersmanagementsystem.repository.OffresRepository;
import com.cosmos.usersmanagementsystem.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {
    private final ReservationRepository reservationRepository;
    public List<TrajetAdminDto> getAllTrajets(){
        List<Reservation> reservations = reservationRepository.findAll();
        List<TrajetAdminDto> trajets = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Offres offre = reservation.getOffre();
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
}
