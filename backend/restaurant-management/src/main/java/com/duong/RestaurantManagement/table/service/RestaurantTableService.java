package com.duong.RestaurantManagement.table.service;

import com.duong.RestaurantManagement.table.dto.request.AddTableDTO;
import com.duong.RestaurantManagement.table.dto.response.GetTableDTO;
import com.duong.RestaurantManagement.table.model.RestaurantTable;

import java.util.List;

public interface RestaurantTableService {

    void createANewTable(AddTableDTO addTableDTO);

    List<GetTableDTO> getAllTables();

    void removeTable(long tableId);

    byte[] getTableQrCode(long restaurantTableId);

    void changeTableStatus(RestaurantTable restaurantTable);
}