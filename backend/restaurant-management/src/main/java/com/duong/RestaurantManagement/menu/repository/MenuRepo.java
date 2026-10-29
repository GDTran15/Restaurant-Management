package com.duong.RestaurantManagement.menu.repository;

import com.duong.RestaurantManagement.food.dto.response.GetFoodOfMenuDTO;
import com.duong.RestaurantManagement.menu.dto.response.GetListOfMenuDTO;
import com.duong.RestaurantManagement.menu.model.Menu;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Long> {

    boolean existsByMenuName(String menuName);

    @Query("""
        select new com.duong.RestaurantManagement.menu.dto.response.GetListOfMenuDTO(
            m.menuId,
               m.menuName,
                m.menuDesc,
                 m.isActivated,
                     COUNT(mi.menuItemId)
            ) from Menu m left join m.menuItems mi
                group by  m.menuId, m.menuName, m.menuDesc, m.isActivated
   """)
    List<GetListOfMenuDTO> getListOfMenu();


    @Query("""
    select new com.duong.RestaurantManagement.food.dto.response.GetFoodOfMenuDTO(
        mi.food.foodId,
            mi.food.foodName,
                mi.food.price,
                    mi.food.isAvailable
        ) from Menu m join m.menuItems mi
             where (lower(mi.food.foodName) like lower(concat('%', :search, '%')))
             and m.menuId = :menuId
    """)
    Page<GetFoodOfMenuDTO> getFoodOfMenu(Pageable pageable, @Param("menuId") Long menuId, @Param("search") String search);

    @Query("""
    select m from
        Menu m left join fetch
        m.menuItems
        where m.menuId = :menuId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Menu> findMenuWithMenuItemByIdForUpdating(@Param("menuId") Long menuId);

    @Query("""
    SELECT m from Menu m WHERE m.menuId = :menuId
""")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Menu> findByIdForUpdate(@Param("menuId") Long menuId);

    boolean existsByMenuIdAndIsActivatedTrue(Long menuId);

    boolean existsByIsActivatedTrue();

    Optional<Menu> findByIsActivatedTrue();
}

