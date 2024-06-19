package com.cosmos.usersmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Solde {
    @Id
    @GeneratedValue
    private Integer id;
    private String nomClient;
    private String cardNumber;
    private String dateExpiration;
    private String cvv;
    @ManyToOne(fetch = FetchType.LAZY)
    private OurUsers ourUsers;
}
