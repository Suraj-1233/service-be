package com.LaundryApplication.LaundryApplication.service;


import com.LaundryApplication.LaundryApplication.model.Item;
import com.LaundryApplication.LaundryApplication.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // Get all items
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Add new item
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    // Update existing item
    public Item updateItem(String id, Item updatedItem) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setName(updatedItem.getName());
        item.setPrice(updatedItem.getPrice());
        return itemRepository.save(item);
    }

    // Delete item
    public void deleteItem(String id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found");
        }
        itemRepository.deleteById(id);
    }

    // Get single item
    public Item getItemById(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }
}
