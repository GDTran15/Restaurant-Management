package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Long> {

    boolean existsByMenuName(String menuName);

    @Query("""
        select new com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO(
            m.menuId,
               m.menuName,
                m.menuDesc,
                 m.isActivated,
                     COUNT(f)
            ) from Menu m left join m.foods f
                group by  m.menuId, m.menuName, m.menuDesc, m.isActivated
   """)
    List<GetListOfMenuDTO> getListOfMenu();
}
