package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.exception.UnauthorizedException;
import com.LaundryApplication.LaundryApplication.model.Item;
import com.LaundryApplication.LaundryApplication.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // ✅ 1️⃣ Get all items (Public)
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    // ✅ 2️⃣ Get item by ID (Public)
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable String id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    // ✅ 3️⃣ Add new item (Admin only)
    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody Item item, Authentication auth) {
        ensureAdmin(auth);
        Item saved = itemService.addItem(item);
        return ResponseEntity.ok(Map.of("message", "Item added successfully", "data", saved));
    }

    // ✅ 4️⃣ Update item (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable String id,
            @RequestBody Item updatedItem,
            Authentication auth
    ) {
        ensureAdmin(auth);
        Item updated = itemService.updateItem(id, updatedItem);
        return ResponseEntity.ok(Map.of("message", "Item updated successfully", "data", updated));
    }

    // ✅ 5️⃣ Delete item (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable String id, Authentication auth) {
        ensureAdmin(auth);
        itemService.deleteItem(id);
        return ResponseEntity.ok(Map.of("message", "Item deleted successfully"));
    }

    // 🔒 Helper method to ensure ADMIN role
    private void ensureAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new UnauthorizedException("Access denied: Admins only");
        }
    }
}
