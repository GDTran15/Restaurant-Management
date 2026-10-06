package com.duong.RestaurantManagement.table.controller;

import com.duong.RestaurantManagement.table.dto.request.AddTableDTO;
import com.duong.RestaurantManagement.table.dto.response.GetTableDTO;
import com.duong.RestaurantManagement.table.service.RestaurantTableService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/tables")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    @PostMapping()
    public ResponseEntity<String> addTable(@RequestBody @Valid AddTableDTO addTableDTO) {
        restaurantTableService.createANewTable(addTableDTO);
        return ResponseEntity.ok("Table created");
    }

    @GetMapping()
    public ResponseEntity<List<GetTableDTO>> getTables() {
        return ResponseEntity.ok(restaurantTableService.getAllTables());
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<String> deleteTable(@PathVariable long tableId) {
        restaurantTableService.removeTable(tableId);
        return ResponseEntity.ok("Table removed");
    }

    @GetMapping("/{tableId}/qrCode")
    public ResponseEntity<byte[]> getTablesQrCodeByRestaurantId(@PathVariable long tableId) {

        byte[] qrCode = restaurantTableService.getTableQrCode(tableId);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(qrCode);
    }
    }




