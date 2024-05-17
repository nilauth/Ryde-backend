package com.cosmos.usersmanagementsystem.service;

import com.cosmos.usersmanagementsystem.dto.OffresDTO;
import com.cosmos.usersmanagementsystem.dto.ReqRes;
import com.cosmos.usersmanagementsystem.dto.ReservationDTO;
import com.cosmos.usersmanagementsystem.entity.Offres;
import com.cosmos.usersmanagementsystem.entity.OurUsers;
import com.cosmos.usersmanagementsystem.entity.Reservation;
import com.cosmos.usersmanagementsystem.repository.OffresRepository;
import com.cosmos.usersmanagementsystem.repository.ReservationRepository;
import com.cosmos.usersmanagementsystem.repository.UsersRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final OffresRepository offresRepository;
    private final UsersRepo usersRepo;

    private Reservation mapToEntityReservation(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setId(reservationDTO.getId());
        System.out.println(reservationDTO.getId());
        System.out.println(reservationDTO.getOffreid());
        System.out.println(reservationDTO.getUserid());
        if (reservationDTO.getOffreid() != null) {
            Offres offre = offresRepository.findOffresById(reservationDTO.getOffreid());
            reservation.setOffre(offre);
        }

        if (reservationDTO.getUserid() != null) {
           OurUsers user = usersRepo.findOurUsersById(reservationDTO.getUserid());
            reservation.setUser(user);
        }

        return reservation;
    }


    private ReservationDTO mapToDTO(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(reservation.getId());
        if (reservation.getOffre() != null) {
            reservationDTO.setOffreid(String.valueOf(reservation.getOffre().getId()));
        }
        if (reservation.getUser() != null) {
            reservationDTO.setUserid(reservation.getUser().getId());
        }
        return reservationDTO;
    }

    public ReservationDTO addReservation(ReservationDTO reservationDTO) {
            Reservation reservation = mapToEntityReservation(reservationDTO);
            try {
                OurUsers ourUsers = reservation.getUser();
                double solde = ourUsers.getSolde();

                Offres offres = reservation.getOffre();
                double prixOffre = offres.getPrix();

                if(solde > prixOffre && offres.getPlaceDispo() < offres.getPlaceInitiale() && !offres.getStatusOffres() && !offres.getStatusVoyages()){
                    reservationDTO.setStatusCode(200);
                    reservationDTO.setMessage("Successfully Added Reservation");
                    ourUsers.setSolde(solde-prixOffre);
                    offres.setPlaceDispo(offres.getPlaceDispo()+1);
                    usersRepo.save(ourUsers);
                    reservationRepository.save(reservation);
                }
                else {
                    reservationDTO.setStatusCode(404);
                    reservationDTO.setMessage("Reservation not Added");
                }
            }catch (Exception e){
                reservationDTO.setStatusCode(500);
                reservationDTO.setMessage(e.getMessage());
            }
            return reservationDTO;
    }

    public ReservationDTO updateReservation(Integer reservationId, ReservationDTO updatedReservationDTO) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        ReservationDTO reservationDTO = new ReservationDTO();
        try{
            if (optionalReservation.isPresent()) {
                Reservation existingReservation = optionalReservation.get();
                existingReservation.setOffre(offresRepository
                        .findOffresById(updatedReservationDTO.getOffreid()));
                //existingReservation.setUser(usersRepo.findOurUsersById(
               //         updatedReservationDTO.getUserid()
                //));
                Reservation updatedReservation = reservationRepository.save(existingReservation);
                reservationDTO = mapToDTO(updatedReservation);
                reservationDTO.setMessage("Successfully Updated Reservation");
                reservationDTO.setStatusCode(200);
            }
            else {
                reservationDTO.setStatusCode(404);
                reservationDTO.setMessage("Reservation not found");
            }
        }
        catch (Exception e){
            reservationDTO.setStatusCode(500);
            reservationDTO.setMessage(e.getMessage());
        }
        return reservationDTO;
    }

    public boolean deleteReservation(Integer reservationId) {
        ReservationDTO reservationDTO = new ReservationDTO();
        try{
            if (reservationRepository.existsById(reservationId)) {
                reservationRepository.deleteById(reservationId);
                reservationDTO.setMessage("Successfully Deleted Reservation");
                reservationDTO.setStatusCode(200);
                return true;
            }
            else {
                reservationDTO.setStatusCode(404);
                reservationDTO.setMessage("Reservation not Deleted");
            }
        }
        catch (Exception e){
            reservationDTO.setStatusCode(500);
            reservationDTO.setMessage(e.getMessage());
        }
        return false;
    }

    public List<ReservationDTO> getAllReservations(OurUsers user) {
        List<Reservation> reservationList = reservationRepository.findAllByUser(user);
        return reservationList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public List<ReservationDTO> getAllReservationsAdmin() {
        List<Reservation> reservationList = reservationRepository.findAll();
        return reservationList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

//    public ReservationDTO CloseStatus(ReservationDTO reservationDTO){
//        Reservation reservation = reservationRepository.getReferenceById(reservationDTO.getId());
//        try {
//
//        }catch (Exception e){
//
//        }
//
//    }
}

