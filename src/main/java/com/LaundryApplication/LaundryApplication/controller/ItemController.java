package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.model.Item;
import com.LaundryApplication.LaundryApplication.service.ItemService;
import com.LaundryApplication.LaundryApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private JwtUtil jwtUtil;

    // Anyone can get items
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    // Only ADMIN can add items
    @PostMapping
    public ResponseEntity<?> addItem(@RequestHeader("Authorization") String token, @RequestBody Item item) {
        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        itemService.addItem(item);
        return ResponseEntity.ok("Item added successfully");
    }

    // ✅ Only ADMIN can delete items
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(
            @RequestHeader("Authorization") String token,
            @PathVariable("id") String id) {  // 👈 Added explicit name

        if (!isAdmin(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        itemService.deleteItem(id);
        return ResponseEntity.ok("Item deleted successfully");
    }

    private boolean isAdmin(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            return role.equals("ADMIN");
        }
        return false;
    }

    // ✅ Only ADMIN can update items
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") String id,  // 👈 Added explicit name
            @RequestBody Item updatedItem) {

        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        Item item = itemService.updateItem(id, updatedItem);
        return ResponseEntity.ok(item);
    }


}
