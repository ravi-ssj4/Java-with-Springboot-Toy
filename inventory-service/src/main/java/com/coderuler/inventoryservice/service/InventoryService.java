package com.coderuler.inventoryservice.service;

import com.coderuler.inventoryservice.dto.InventoryResponse;
import com.coderuler.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        // queries the inventoryRepository
        // this is going to be an extension method from Spring Data JPA
        // findBySkuCodeIn is going to return to me a list of Inventory objects
        // So, I need to map all those objects to their boolean values(present/not present)
        // for that I'll create a Dto object -> InventoryResponse
        return inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(inventory -> InventoryResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build()
                ).toList();
    }
}
