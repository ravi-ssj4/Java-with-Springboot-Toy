package com.coderuler.orderservice.repository;

import com.coderuler.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;


// this will extend the JpaRepository with key of type Long
// hence, we do not have to write any code for this, all the parent class code is sufficient
public interface OrderRepository extends JpaRepository<Order, Long> {
}
