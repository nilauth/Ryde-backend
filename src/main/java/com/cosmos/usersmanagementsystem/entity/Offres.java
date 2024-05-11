package com.cosmos.usersmanagementsystem.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Offres {
    @Id
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    private OurUsers driver;
    private String villeDepart;
    private String villeArriv;

    private Date heureDepart;
    private Date heureDarriv;
    @Temporal(TemporalType.DATE)
    private Date date;
    private Double prix;
    private int placeDispo;
    private int placeInitiale;
    private Boolean status;
}