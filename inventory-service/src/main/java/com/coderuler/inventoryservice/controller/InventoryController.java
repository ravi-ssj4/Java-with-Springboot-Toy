package com.coderuler.inventoryservice.controller;

import com.coderuler.inventoryservice.dto.InventoryResponse;
import com.coderuler.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    // What the request looks like:
    // @PathVariable way: http://localhost:8082/api/inventory/iphone-12,iphone-13-red..
    // @RequestParam way: http://localhost:8082/api/inventory?skuCode=iphone-13&skuCode=iphone-13-red - preferred
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        // verifies whether the Item with given skuCode is in stock or not
        // Debug: Using a list iterator
        for (String skuCod : skuCode) {
            System.out.println(skuCod);
        }
        return inventoryService.isInStock(skuCode);

    }
}
