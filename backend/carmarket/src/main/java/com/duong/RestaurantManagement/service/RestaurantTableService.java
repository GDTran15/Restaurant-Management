package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.table.request.AddTableDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.model.RestaurantTable;
import com.duong.RestaurantManagement.repo.RestaurantTableRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantTableService {

    private final RestaurantTableRepo restaurantTableRepo;

    public void createANewTable(AddTableDTO addTableDTO) {
        if (restaurantTableRepo.existsByRestaurantTableNumber(addTableDTO.tableNumber())){
            throw new DuplicateResourceException("Table number already exists");
        }
        RestaurantTable restaurantTable = RestaurantTable.builder()
                .restaurantTableNumber(addTableDTO.tableNumber())
                .capacity(addTableDTO.capacity())
                .restaurantTableStatus(true)
                .build();
        restaurantTableRepo.save(restaurantTable);
    }
}
