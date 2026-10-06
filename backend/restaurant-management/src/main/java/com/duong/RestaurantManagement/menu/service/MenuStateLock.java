package com.duong.RestaurantManagement.menu.service;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class MenuStateLock {
    private final EntityManager entityManager;
    // ponytail: one restaurant-wide lock for scans/menu changes; use a restaurant
    // ID as the lock key if this application later serves multiple restaurants.
    public void acquire() { entityManager.createNativeQuery("select pg_advisory_xact_lock(73190401)").getSingleResult(); }
}
