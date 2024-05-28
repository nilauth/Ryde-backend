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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DriverServices {
    private final OffresRepository offresRepository;
    private final UsersRepo usersRepo;
    private final ReservationRepository reservationRepository;


    private Offres mapToEntity(OffresDTO offreDTO) {
        Offres offre = new Offres();
        offre.setId(offreDTO.getId());
        offre.setDriver(usersRepo.findOurUsersById(offreDTO.getDriverId()));
        offre.setVilleDepart(offreDTO.getVilleDepart());
        offre.setVilleArriv(offreDTO.getVilleArriv());
        offre.setHeureDepart(offreDTO.getHeureDepart());
        offre.setDate(offreDTO.getDate());
        offre.setHeureDarriv(offreDTO.getHeureArriv());
        offre.setPrix(offreDTO.getPrix());
        offre.setPlaceDispo(offreDTO.getPlaceDispo());
        offre.setPlaceInitiale(offreDTO.getPlaceInitiale());
        offre.setStatusOffres(offreDTO.getStatusOffres());
        offre.setStatusVoyages(offreDTO.getStatusVoyages());
        return offre;
    }

    private OffresDTO mapToDTO(Offres offres) {
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


    public Offres addOffre(OffresDTO offreDTO) {
            Offres offre = mapToEntity(offreDTO);
            offre.setStatusOffres(true);
            offre.setStatusVoyages(true);
            return offresRepository.save(offre);
    }

    public OffresDTO updateOffres(Integer offreId, Offres offres) {
        OffresDTO offresDTO = new OffresDTO();
        try {
            Optional<Offres> optionalOffres = offresRepository.findById(offreId);
            if (optionalOffres.isPresent()) {
                Offres existantOffre = optionalOffres.get();
                existantOffre.setVilleDepart(offres.getVilleDepart());
                existantOffre.setVilleArriv(offres.getVilleArriv());
                existantOffre.setHeureDepart(offres.getHeureDepart());
                existantOffre.setHeureDarriv(offres.getHeureDarriv());
                existantOffre.setPrix(offres.getPrix());
                existantOffre.setPlaceDispo(offres.getPlaceDispo());
                existantOffre.setPlaceInitiale(offres.getPlaceInitiale());
                existantOffre.setStatusOffres(offres.getStatusOffres());
                existantOffre.setStatusVoyages(offres.getStatusVoyages());
                Offres savedOffre = offresRepository.save(existantOffre);
                offresDTO = mapToDTO(savedOffre);
                offresDTO.setStatusCode(200);
                offresDTO.setMessage("Successfully Updated Offre");
            }
            else {
                offresDTO.setStatusCode(404);
                offresDTO.setMessage("Offer not found");
            }
        }catch (Exception e){
            offresDTO.setStatusCode(500);
            offresDTO.setMessage(e.getMessage());
        }
        return offresDTO;
    }


    public boolean deleteOffre(Integer offreId) {
        OffresDTO offresDTO = new OffresDTO();
        try {
            if (offresRepository.existsById(offreId)) {
                offresRepository.deleteById(offreId);
                offresDTO.setStatusCode(200);
                offresDTO.setMessage("Successfully Deleted Offer");
                return true;
            }
        }catch (Exception e){
            offresDTO.setStatusCode(500);
            offresDTO.setMessage("Error occurred: " + e.getMessage());
        }
        return false;
    }
    public List<OffresDTO> getAllOffres() {
        List<Offres> offresList = offresRepository.findAll();
        return offresList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

    }

    public OffresDTO CloseOffre(String offreId){
        Offres offres = offresRepository.findOffresById(offreId);
        OffresDTO offresDTO = null;
        try {
            offres.setStatusOffres(false);
            offresRepository.save(offres);
            offresDTO=mapToDTO(offres);
            offresDTO.setMessage("Successfully Closed Offer status");
            offresDTO.setStatusCode(200);
        }catch (Exception e) {
            offresDTO.setMessage("Error to Closed Offer status" + e.getMessage());
            offresDTO.setStatusCode(500);
        }
        return offresDTO;
    }

    public OffresDTO CloseVoyage(String offresId){
        Offres offres = offresRepository.findOffresById(offresId);
        Reservation reservation = reservationRepository.findByOffre(offres);
        OffresDTO offresDTO = null;

        try {
            if (!(offres==null)){
                offres.setStatusVoyages(false);
                if (!(reservation==null)){
                    reservation.setStatus(offres.getStatusVoyages());
                    reservationRepository.save(reservation);
                }
                offresDTO=mapToDTO(offres);
                OurUsers user= offres.getDriver();
                System.out.println(user);
                double prix=offres.getPrix();
                int nbr=offres.getPlaceInitiale()-offres.getPlaceDispo();
                double rslt=prix*nbr;
                user.setSolde(user.getSolde()+rslt);
                usersRepo.save(user);
                offresRepository.save(offres);
                offresDTO.setMessage("Successfully Closed Offer voyage");
                offresDTO.setStatusCode(200);
            }
        }catch (Exception e) {
            offresDTO.setMessage("Error to Closed Offer voyage" + e.getMessage());
            offresDTO.setStatusCode(500);
        }
        return offresDTO;
    }

    public List<OffresDTO> getAllOffresDriver(Integer driverId) {
        OurUsers user=usersRepo.findOurUsersById(driverId);
        List<Offres>offres =offresRepository.findOffresByDriver(user);
        return offres.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

    }
}

