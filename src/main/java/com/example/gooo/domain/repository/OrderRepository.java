package com.example.gooo.domain.repository;

import com.example.gooo.domain.entity.Order;
import com.example.gooo.domain.projections.OrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            SELECT 
                o.created_at as orderDate, 
                up.first_name as customerName, 
                oi.totalAmount
            FROM orders o
            JOIN user_profiles up ON o.user_id = up.user_id
            JOIN (
                    select order_id, SUM(quantity * price_at_purchase) as totalAmount 
                    from order_items
                    GROUP BY order_id
            ) oi ON o.id = oi.order_id
            WHERE o.id = :id
            """, nativeQuery = true)

    Optional<OrderView> findOrderDetailsNative(@Param("id") Long id);




}
