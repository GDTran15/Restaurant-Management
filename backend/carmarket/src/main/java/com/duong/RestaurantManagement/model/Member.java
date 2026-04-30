package com.duong.RestaurantManagement.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_id;

    private String firstName;

    private String lastName;

    private int memberPhone;

    private String memberEmail;

    @Enumerated(EnumType.STRING)
    private MembershipRank memberRank;

    private double totalSpent;

    @OneToMany(mappedBy = "member")
    private List<Invoice> invoices;


}
