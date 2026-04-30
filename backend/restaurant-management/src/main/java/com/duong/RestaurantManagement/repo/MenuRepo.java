package com.duong.RestaurantManagement.repo;

import com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.dto.menu.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    select new com.duong.RestaurantManagement.dto.food.response.GetFoodOfMenuDTO(
        mi.food.foodId,
            mi.food.foodName,
                mi.food.price,
                    mi.food.isAvailable
        ) from Menu m join m.menuItems mi
             where (lower(mi.food.foodName) like lower(concat('%', :search, '%')))
             and m.menuId = :menuId
    """)
    Page<GetFoodOfMenuDTO> getFoodOfMenu(Pageable pageable, String search, @Param("menuId") Long menuId, @Param("search") String search1);

    boolean existsByMenuIdAndIsActivatedTrue(Long menuId);

    boolean existsByIsActivatedTrue();

    Optional<Menu> findByIsActivatedTrue();
}

