package com.duong.RestaurantManagement.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Invalid phone number")
    private String memberPhone;

    private String memberEmail;

    @Enumerated(EnumType.STRING)
    private MembershipRank memberRank;

    private double totalSpent;

    @OneToMany(mappedBy = "member")
    private List<Invoice> invoices;


}
