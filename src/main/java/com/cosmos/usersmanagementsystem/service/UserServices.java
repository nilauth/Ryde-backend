package com.cosmos.usersmanagementsystem.service;

import com.cosmos.usersmanagementsystem.dto.OffresDTO;
import com.cosmos.usersmanagementsystem.entity.Offres;
import com.cosmos.usersmanagementsystem.entity.Villes;
import com.cosmos.usersmanagementsystem.repository.OffresRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServices {
    private final OffresRepository offresRepository;
    public List<OffresDTO> getOffreFiltered(String  villeDep,String villeArrvi, Date date){
        List<Offres> offres= offresRepository
                .findOffresByVilleDepartAndVilleArrivAndDate(villeDep,villeArrvi,date);
        for (int i = 0; i < offres.size(); i++) {
            Offres offre = offres.get(i);
            System.out.println("OffresDTO at index " + i + ": " + offre);
        }
        return offres.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    private OffresDTO mapToDTO(Offres offres) {
        OffresDTO offresDTO = new OffresDTO();
        offresDTO.setId(offres.getId());
        return offresDTO;
    }
}
