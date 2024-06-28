package com.coderuler.orderservice.service;

import com.coderuler.orderservice.dto.InventoryResponse;
import com.coderuler.orderservice.dto.OrderLineItemDto;
import com.coderuler.orderservice.dto.OrderRequest;
import com.coderuler.orderservice.model.Order;
import com.coderuler.orderservice.model.OrderLineItem;
import com.coderuler.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    // as this is a constructor injection of OrderRepository, have to create it, or use - RequiredArgsConstructor annotation
    private final OrderRepository orderRepository;
    // webClient bean injected directly here
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        // Debugging: Print the OrderRequest object
        System.out.println("OrderRequest: " + orderRequest);


        // Create an Order from the OrderRequest
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        // need to map each and every OrderLineItemDto object to OrderLineItem object
        // This is because the OrderRequest has OrderLineItemDto and we need OrderLineItem objects for the Order object
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemList(orderLineItems);

        // For the Request to the inventory-service, we need Request Param in the form of a list of skuCodes
        // As, each order contains a list of orderLineItems ( there are multiple items in an order )
        // We need to figure out if all those items are in stock or not to complete the order!
        List<String> skuCodes = order.getOrderLineItemList()
                .stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        System.out.println("SKU Codes: " + skuCodes);

        // Todo: Before saving, check the inventory(inventory-service)
        //  if the product is in stock or not. (use of WebClient)
        // We have to check for each item if its in stock or not.
        // We get a InventoryResponse array representing info about each item
        // in the inventory -> hence, here also to receive this object correctly,
        // we have created InventoryResponse object in the dto package of the order-service
        // (exactly same as the dto package of the inventory-service)

//        InventoryResponse[] inventoryResponseArray = webClient
//                .get()
//                .uri("http://localhost:8082/api/inventory",
//                                uriBuilder -> uriBuilder
//                                        .queryParam("skuCode", skuCodes)
//                                        .build())
//                .retrieve()
//                .bodyToMono(InventoryResponse[].class)
//                .block();
//        InventoryResponse[] inventoryResponseArray = webClient
//                .get()
//                .uri("http://inventory-service/api/inventory",
//                        uriBuilder -> uriBuilder
//                                .queryParam("skuCode", skuCodes)
//                                .build())
//                .retrieve()
//                .bodyToMono(InventoryResponse[].class)
//                .block();
                InventoryResponse[] inventoryResponseArray = webClientBuilder.build()
                .get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder
                                .queryParam("skuCode", skuCodes)
                                .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();


        // checking for any nulls
        assert inventoryResponseArray != null;

        System.out.println("Number of responses: " + inventoryResponseArray.length);
        Arrays.stream(inventoryResponseArray).forEach(System.out::println);


        // checking if all the items are in stock using the allMatch method on the Array stream
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

        // Debugging: Print whether all products are in stock
        System.out.println("All products in stock: " + allProductsInStock);



        // Save this order in the Database(MySQL)
        if (allProductsInStock) {
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Product is not in stock, please try again some other time.");
        }


    }

    private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return orderLineItem;
    }
}
