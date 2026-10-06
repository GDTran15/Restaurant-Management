package com.duong.RestaurantManagement.member.model;

import java.math.BigDecimal;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "memberships")
@Builder
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long membershipId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private MembershipRank membershipRank;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal minSpent;

    @Column(nullable = false, precision = 7, scale = 6)
    private BigDecimal discountRate;
}
