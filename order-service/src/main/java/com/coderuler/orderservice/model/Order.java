package com.coderuler.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "t_orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    // defines a one-to-many relationship between the Order entity and the OrderLineItems entity
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderLineItem> orderLineItemList;
}

/*
* The cascade attribute specifies the cascade operations that should be propagated from the
* parent entity to the child entity.
*
* CascadeType.ALL means that all cascade operations (including PERSIST, MERGE, REMOVE, REFRESH, and DETACH)
* should be propagated from the Order entity to its associated OrderLineItems entities.
*
* This means that when an Order is persisted, merged, removed, refreshed,
* or detached, the same operation will be applied to all its OrderLineItems.
* */