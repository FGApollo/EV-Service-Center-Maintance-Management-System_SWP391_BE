package com.example.Ev.System.controller;

import com.example.Ev.System.entity.User;
import com.example.Ev.System.service.InventoryServiceI;
import com.example.Ev.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/management/inventory")
public class InventoryController {
    @Autowired
    private InventoryServiceI inventoryService;
    @Autowired
    private UserService userService;

    @PutMapping("/{partId}")
    public ResponseEntity<String> updateQuantity(
            @PathVariable Integer partId,
            @RequestParam Integer quantity,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        inventoryService.addQuantityToInventory(centerId, partId, quantity);
        return ResponseEntity.ok("Quantity updated successfully");
    }

    @GetMapping("/parts")
    public ResponseEntity<?> getPartQuantities(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userService.getUserByEmail(email);
        Integer centerId = currentUser.getServiceCenter().getId();

        return ResponseEntity.ok(inventoryService.getPartQuantitiesByCenter(centerId));
    }
}
