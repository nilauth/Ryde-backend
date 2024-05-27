package com.cosmos.usersmanagementsystem.service;

import com.cosmos.usersmanagementsystem.dto.OffresDTO;
import com.cosmos.usersmanagementsystem.dto.ReqRes;
import com.cosmos.usersmanagementsystem.dto.ResOffresDto;
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
        if (reservationDTO.getOffreid() != null) {
            Offres offre = offresRepository.findOffresById(reservationDTO.getOffreid());
            reservation.setOffre(offre);
        }

        if (reservationDTO.getUserid() != null) {
           OurUsers user = usersRepo.findOurUsersById(reservationDTO.getUserid());
            reservation.setUser(user);
        }
        reservation.setStatus(reservationDTO.getStatus());
        reservation.setPlaceReserv(reservationDTO.getPlaceReserv());

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
            if (solde > prixOffre) {
                if (offres.getPlaceDispo()>0) {
                    if (offres.getStatusOffres()) {
                        if (offres.getStatusVoyages()) {
                            reservationDTO.setStatusCode(200);
                            reservationDTO.setMessage("Successfully Added Reservation");
                            ourUsers.setSolde(solde - reservationDTO.getPrix());
                            offres.setPlaceDispo(offres.getPlaceDispo() - reservationDTO.getPlaceReserv());
                            if(offres.getPlaceDispo()==0){
                                offres.setStatusOffres(false);
                            }
                            reservation.setStatus(offres.getStatusVoyages());
                            usersRepo.save(ourUsers);
                            reservationRepository.save(reservation);
                        } else {
                            reservationDTO.setStatusCode(404);
                            reservationDTO.setMessage("Reservation not Added: Voyage status is not active");
                        }
                    } else {
                        reservationDTO.setStatusCode(404);
                        reservationDTO.setMessage("Reservation not Added: Offer status is not active");
                    }
                } else {
                    offres.setStatusOffres(false);
                    offresRepository.save(offres);
                    reservationDTO.setStatusCode(404);
                    reservationDTO.setMessage("Reservation not Added: No available places");
                }
            } else {
                reservationDTO.setStatusCode(404);
                reservationDTO.setMessage("Reservation not Added: Insufficient balance");
            }
        } catch (Exception e) {
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

    public ReservationDTO deleteReservation(Integer reservationId) {
        ReservationDTO reservationDTO = new ReservationDTO();
        try{
            if (reservationRepository.existsById(reservationId)) {
                reservationRepository.deleteById(reservationId);
                reservationDTO.setMessage("Successfully Deleted Reservation");
                reservationDTO.setStatusCode(200);
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
        return reservationDTO;
    }
    public List<ResOffresDto> getAllReservations(Integer userId) {
        OurUsers user = usersRepo.findOurUsersById(userId);
        List<Reservation> reservationList = reservationRepository.findAllByUser(user);
        List<Offres> offres = new ArrayList<>();

        for (Reservation reservation : reservationList) {
            System.out.println(reservation.getOffre());
            String idOffre = reservation.getOffre().getId();
            Offres offre = offresRepository.findOffresById(idOffre);
            offres.add(offre);
        }

        return reservationList.stream()
                .map(reservation -> {
                    Offres offre = offresRepository.findOffresById(reservation.getOffre().getId());
                    return mapToDToResOffres(offre, reservation);
                })
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


    private OffresDTO mapToDToOffres(Offres offres) {
        OffresDTO offresDTO = new OffresDTO();
        offresDTO.setId(offres.getId());
        offresDTO.setDriverId(offres.getDriver().getId());
        offresDTO.setVilleDepart(offres.getVilleDepart());
        offresDTO.setVilleArriv(offres.getVilleArriv());
        offresDTO.setHeureDepart(offres.getHeureDepart());
        offresDTO.setHeureArriv(offres.getHeureDarriv());
        offresDTO.setDate(offres.getDate());
        offresDTO.setPrix(offres.getPrix());
        offresDTO.setPlaceDispo(offres.getPlaceDispo());
        offresDTO.setPlaceInitiale(offres.getPlaceInitiale());
        offresDTO.setStatusOffres(offres.getStatusOffres());
        offresDTO.setStatusVoyages(offres.getStatusVoyages());
        offresDTO.setStatusCode(200);
        return offresDTO;
    }
    public ResOffresDto mapToDToResOffres(Offres offre, Reservation reservation) {
        if (offre == null || reservation == null) {
            return null;
        }

        return ResOffresDto.builder()
                .id(offre.getId())
                .driverId(offre.getDriver() != null ? offre.getDriver().getId() : null) // Assuming OurUsers has a getId() method
                .villeDepart(offre.getVilleDepart())
                .villeArriv(offre.getVilleArriv())
                .heureDepart(offre.getHeureDepart())
                .heureArriv(offre.getHeureDarriv())
                .date(offre.getDate())
                .prix(offre.getPrix())
                .placeDispo(offre.getPlaceDispo())
                .placeInitiale(offre.getPlaceInitiale())
                .statusOffres(offre.getStatusOffres())
                .statusVoyages(offre.getStatusVoyages())
                .idReservation(reservation.getId())
                .build();
    }

}
