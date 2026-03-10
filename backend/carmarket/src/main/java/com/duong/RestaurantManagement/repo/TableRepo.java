package com.duong.RestaurantManagement.repo;

import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepo extends JpaRepository<Table, Long> {
}
