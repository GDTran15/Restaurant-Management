package com.duong.RestaurantManagement.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "memberships")
@Builder
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int membershipId;

    @Enumerated(EnumType.STRING)
    private MembershipRank membershipRank;

    private double minSpent;

    private double discountRate;
}
