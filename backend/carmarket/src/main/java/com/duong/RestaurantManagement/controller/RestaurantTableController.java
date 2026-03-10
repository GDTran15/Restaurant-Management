package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.table.request.AddTableDTO;
import com.duong.RestaurantManagement.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/tables")
@RequiredArgsConstructor
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    @PostMapping
    public ResponseEntity<String> addTable(@RequestBody AddTableDTO addTableDTO) {
        restaurantTableService.createANewTable(addTableDTO);
        return ResponseEntity.ok("Table created");
    }
}
