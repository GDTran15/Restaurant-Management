package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.dto.food.response.GetFoodListDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.response.MenuDetailResponseDTO;
import com.duong.RestaurantManagement.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Long> {

    boolean existsByMenuName(String menuName);

    @Query("""
        select new com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO(
            m.menuId,
               m.menuName,
                m.menuDesc,
                 m.isActivated,
                     COUNT(mi.menuItemId)
            ) from Menu m left join m.menuItems mi
                group by  m.menuId, m.menuName, m.menuDesc, m.isActivated
   """)
    List<GetListOfMenuDTO> getListOfMenu();

    boolean existsByMenuId(Long menuId);

    @Query("""
       select distinct m from Menu m
           join fetch m.menuItems mi
               join fetch mi.food
                   where m.menuId = :menuId
    """)
    Optional<Menu> findMenuDetails(@Param("menuId") Long menuId);
}
