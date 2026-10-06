package com.duong.RestaurantManagement.table.service;

import com.duong.RestaurantManagement.table.dto.request.AddTableDTO;
import com.duong.RestaurantManagement.table.dto.response.GetTableDTO;
import com.duong.RestaurantManagement.common.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.table.exception.InvalidQrCodeException;
import com.duong.RestaurantManagement.common.exception.ResourceNotFoundException;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.table.model.RestaurantTable;
import com.duong.RestaurantManagement.table.repository.RestaurantTableRepo;
import com.duong.RestaurantManagement.table.service.QRCodeService;
import com.duong.RestaurantManagement.table.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepo restaurantTableRepo;
    private final QRCodeService qrCodeService;
    @org.springframework.beans.factory.annotation.Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void createANewTable(AddTableDTO addTableDTO) {

        if (restaurantTableRepo.existsByRestaurantTableNumber(addTableDTO.tableNumber())) {
            throw new DuplicateResourceException(
                    Map.of("tableNumber","Table number already exists")
            );
        }



        RestaurantTable restaurantTable = RestaurantTable.builder()
                .restaurantTableNumber(addTableDTO.tableNumber())
                .capacity(addTableDTO.capacity())
                .restaurantTableStatus(true)
                .tableQrCodeValue(UUID.randomUUID().toString())
                .build();

        restaurantTableRepo.save(restaurantTable);
    }

    @Override
    public List<GetTableDTO> getAllTables() {

        return restaurantTableRepo.findAll(Sort.by("restaurantTableNumber"))
                .stream()
                .map(table -> new GetTableDTO(
                        (long) table.getRestaurantTableId(),
                        table.getRestaurantTableNumber(),
                        table.isRestaurantTableStatus(),
                        table.getCapacity()
                ))
                .toList();
    }

    @Override
    public void removeTable(long tableId) {
        restaurantTableRepo.deleteById(tableId);
    }

    @Override
    public byte[] getTableQrCode(long restaurantTableId) {
        RestaurantTable restaurantTable = restaurantTableRepo.findById(restaurantTableId).orElseThrow(
                () -> new ResourceNotFoundException("Restaurant table not found")
        );
        return  qrCodeService.getQrCode(frontendUrl + "/customer/menu/" + restaurantTable.getTableQrCodeValue()) ;
    }



    @Override
    public void changeTableStatus(RestaurantTable restaurantTable) {
        restaurantTable.setRestaurantTableStatus(!restaurantTable.isRestaurantTableStatus());
        restaurantTableRepo.save(restaurantTable);
    }

}