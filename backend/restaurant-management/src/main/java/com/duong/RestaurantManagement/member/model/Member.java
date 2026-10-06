package com.duong.RestaurantManagement.member.model;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.invoice.model.Invoice;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
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

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSpent;

    @OneToMany(mappedBy = "member")
    private List<Invoice> invoices;


}
