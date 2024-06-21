package com.cosmos.usersmanagementsystem.service;

import com.cosmos.usersmanagementsystem.dto.DemandeDriverDto;
import com.cosmos.usersmanagementsystem.dto.OffresDTO;
import com.cosmos.usersmanagementsystem.dto.SoldeDto;
import com.cosmos.usersmanagementsystem.entity.DemandeDriver;
import com.cosmos.usersmanagementsystem.entity.Offres;
import com.cosmos.usersmanagementsystem.entity.OurUsers;
import com.cosmos.usersmanagementsystem.entity.Solde;
import com.cosmos.usersmanagementsystem.repository.DemandeRepository;
import com.cosmos.usersmanagementsystem.repository.OffresRepository;
import com.cosmos.usersmanagementsystem.repository.SoldeRepository;
import com.cosmos.usersmanagementsystem.repository.UsersRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServices {
    private final OffresRepository offresRepository;
    private final UsersRepo usersRepo;
    private final SoldeRepository soldeRepository;
    private final DemandeRepository demandeRepository;
    private final PasswordEncoder passwordEncoder;


    public List<OffresDTO> getOffreFiltered(String  villeDep,String villeArrvi, Date date){
        List<Offres> offres= offresRepository
                .findOffresByVilleDepartAndVilleArrivAndDate(villeDep,villeArrvi,date);
        //for (int i = 0; i < offres.size(); i++) {
        //    Offres offre = offres.get(i);
        //    System.out.println("OffresDTO at index " + i + ": " + offre);
        //}
        return offres.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public SoldeDto chargerSolde(SoldeDto soldeDto){
        if (!(soldeDto ==null)) {
            OurUsers client = usersRepo.findById(soldeDto.getClientId()).get();
            Solde solde = soldetoEntity(soldeDto, client);
            soldeRepository.save(solde);
            client.setSolde(client.getSolde() + soldeDto.getSolde());
            usersRepo.save(client);
            soldeDto.setMessage("Solde MAJ");
        }else {
            soldeDto.setMessage("Solde MAJ failed");
        }
        return soldeDto;

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
    public static SoldeDto soldetoDto(Solde solde) {
        if (solde == null) {
            return null;
        }

        return SoldeDto.builder()
                .id(solde.getId())
                .nomClient(solde.getNomClient())
                .cardNumber(solde.getCardNumber())
                .dateExpiration(solde.getDateExpiration())
                .cvv(solde.getCvv())
                .clientId(solde.getOurUsers() != null ? solde.getOurUsers().getId() : null)
                .build();
    }

    public Solde soldetoEntity(SoldeDto soldeDto, OurUsers ourUsers) {
        if (soldeDto == null) {
            return null;
        }

        return Solde.builder()
                .id(soldeDto.getId())
                .solde(soldeDto.getSolde())
                .nomClient(soldeDto.getNomClient())
                .cardNumber(passwordEncoder.encode(soldeDto.getCardNumber()))
                .dateExpiration(soldeDto.getDateExpiration())
                .cvv(soldeDto.getCvv())
                .ourUsers(ourUsers)
                .build();
    }

    public void becomeDriver(Integer driverId) {
        OurUsers driver = usersRepo.findById(driverId).get();
        DemandeDriver demandeDriver= DemandeDriver.builder()
                                .ourUsers(driver)
                                .status("pending")
                                .build();
        demandeRepository.save(demandeDriver);
    }
}
