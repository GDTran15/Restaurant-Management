package com.duong.RestaurantManagement.serviceImp;

import com.duong.RestaurantManagement.dto.table.request.AddTableDTO;
import com.duong.RestaurantManagement.dto.table.response.GetTableDTO;
import com.duong.RestaurantManagement.exception.DuplicateResourceException;
import com.duong.RestaurantManagement.model.RestaurantTable;
import com.duong.RestaurantManagement.repo.RestaurantTableRepo;
import com.duong.RestaurantManagement.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepo restaurantTableRepo;

    @Override
    public void createANewTable(AddTableDTO addTableDTO) {

        if (restaurantTableRepo.existsByRestaurantTableNumber(addTableDTO.tableNumber())) {
            throw new DuplicateResourceException("Table number already exists");
        }

        RestaurantTable restaurantTable = RestaurantTable.builder()
                .restaurantTableNumber(addTableDTO.tableNumber())
                .capacity(addTableDTO.capacity())
                .restaurantTableStatus(true)
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
}