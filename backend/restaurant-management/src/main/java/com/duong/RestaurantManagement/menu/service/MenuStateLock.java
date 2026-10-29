package com.duong.RestaurantManagement.menu.service;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class MenuStateLock {

    private final EntityManager entityManager;

    public void acquire() { entityManager.createNativeQuery("select pg_advisory_xact_lock(19102005)").getSingleResult(); }
}
