package com.duong.RestaurantManagement.service;

import com.duong.RestaurantManagement.dto.table.request.AddTableDTO;
import com.duong.RestaurantManagement.dto.table.response.GetTableDTO;
import com.duong.RestaurantManagement.model.RestaurantTable;

import java.util.List;

public interface RestaurantTableService {

    void createANewTable(AddTableDTO addTableDTO);

    List<GetTableDTO> getAllTables();

    void removeTable(long tableId);

    byte[] getTableQrCode(long restaurantTableId);



    void setTableInUsed(RestaurantTable restaurantTable);
}