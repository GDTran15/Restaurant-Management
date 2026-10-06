package com.duong.RestaurantManagement.order.repository;

import java.math.BigDecimal;

import com.duong.RestaurantManagement.order.dto.response.GetCustomerOrderDTO;
import com.duong.RestaurantManagement.order.dto.response.GetOrderNumberForEachStatus;
import com.duong.RestaurantManagement.dining.model.DiningStatus;
import com.duong.RestaurantManagement.order.model.Order;
import com.duong.RestaurantManagement.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Order e where e.orderId = :id")
    java.util.Optional<Order> findLockedById(@org.springframework.data.repository.query.Param("id") Long id);



//    @Query("""
//    select new com.duong.RestaurantManagement.order.dto.response.GetCustomerOrderDTO(
//    o.orderNumber,
//    o.createdAt,
//    o.orderStatus,
//
//    ) from Order o join fetch o.orderItems oi
//""")
//    List<GetCustomerOrderDTO> getCustomerOrderByDiningSessionId(Long diningSessionId);


    @Query("""
    select distinct o from Order o
    join fetch o.orderItems oi join
    fetch oi.food f
    where o.diningSession.diningSessionId = :diningSessionId
""")
    List<Order> findByDiningSession_DiningSessionId(@Param("diningSessionId") Long diningSessionId);

    @Query("""
    select distinct o
    from Order o
    join fetch o.orderItems oi
    join fetch oi.food f
    join fetch o.diningSession ds
    join fetch ds.restaurantTable rt
    where ds.diningStatus = :diningSessionStatus
""")
    List<Order> findByActiveDiningSession(@Param("diningSessionStatus") DiningStatus diningStatus);

    @Query("""
    select distinct o
    from Order o
    join fetch o.orderItems oi
    join fetch oi.food f
    join fetch o.diningSession ds
    join fetch ds.restaurantTable rt
    where ds.diningStatus = :diningSessionStatus and o.orderStatus = :orderStatus
""")
    List<Order> findByActiveDiningSessionAndOrderStatus(@Param("diningSessionStatus") DiningStatus diningStatus,@Param("orderStatus") OrderStatus orderStatus);

    @Query("""
    select new com.duong.RestaurantManagement.order.dto.response.GetOrderNumberForEachStatus(
    o.orderStatus , count(o) as Count
    )
    from Order o
    join o.diningSession
    where o.diningSession.diningStatus = com.duong.RestaurantManagement.dining.model.DiningStatus.ACTIVE
    group by o.orderStatus
    
""")
    List<GetOrderNumberForEachStatus> getOrderCountByOrderStatus();

    boolean existsByDiningSession_DiningSessionIdAndOrderStatusIn(Long diningSessionId, List<OrderStatus> orderStatus);

    @Query("""
    SELECT COALESCE(SUM(o.orderPrice), 0)
    FROM Order o
    WHERE o.diningSession.diningSessionId = :diningSessionId
    AND o.orderStatus = :orderStatus
    """)
    BigDecimal getTotalOrderPriceByDiningSessionIdAndStatus(@Param("diningSessionId") Long diningSessionId,@Param("orderStatus") OrderStatus orderStatus);
}
