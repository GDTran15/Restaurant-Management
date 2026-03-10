package com.duong.RestaurantManagement.controller;

import com.duong.RestaurantManagement.dto.table.request.AddTableDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/tables")
public class TableController {



    @PostMapping
    public ResponseEntity<String> addTable(@RequestBody AddTableDTO addTableDTO) {

    }
}
