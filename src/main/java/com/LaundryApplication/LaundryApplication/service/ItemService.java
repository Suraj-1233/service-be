package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.exception.BadRequestException;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.model.Item;
import com.LaundryApplication.LaundryApplication.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // ✅ Get all items
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // ✅ Add new item
    public Item addItem(Item item) {
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new BadRequestException("Item name is required");
        }
        return itemRepository.save(item);
    }

    // ✅ Update existing item
    public Item updateItem(String id, Item updatedItem) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));

        item.setName(updatedItem.getName());
        item.setCategory(updatedItem.getCategory());
        item.setImageUrl(updatedItem.getImageUrl());
        item.setDescription(updatedItem.getDescription());
        item.setActive(updatedItem.isActive());
        return itemRepository.save(item);
    }

    // ✅ Delete item
    public void deleteItem(String id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with ID: " + id);
        }
        itemRepository.deleteById(id);
    }

    // ✅ Get single item
    public Item getItemById(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + id));
    }
}
