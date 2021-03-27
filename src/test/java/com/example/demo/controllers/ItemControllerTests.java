package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTests {
    private ItemController itemController;
    private ItemRepository  itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItems_Success_Test() {
        // Stub
        Item firstItem = createStubItem();
        firstItem.setId(0L);
        Item secondItem = createStubItem();
        secondItem.setId(1L);

        List<Item> items = Arrays.asList(firstItem, secondItem);

        when(itemRepository.findAll()).thenReturn(items);

        // Test
        ResponseEntity<List<Item>> response = itemController.getItems();

        // Verification
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(items, response.getBody());
    }

    @Test
    public void getItemsByName_Success_Test() {
        // Stub
        Item firstItem = createStubItem();
        firstItem.setId(0L);
        Item secondItem = createStubItem();
        secondItem.setId(1L);

        List<Item> items = Arrays.asList(firstItem, secondItem);

        when(itemRepository.findByName(Mockito.anyString())).thenReturn(items);

        // Test
        ResponseEntity<List<Item>> response = itemController.getItemsByName("boxing gloves");

        // Verification
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(items, response.getBody());
    }

    @Test
    public void getItemById_Success_Test() {
        // Stub
        Item item = createStubItem();
        item.setId(0L);
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(java.util.Optional.of(item));

        // Test
        ResponseEntity<Item> response = itemController.getItemById(0L);

        // Verification
        Assert.assertNotNull(response);
    }

    private Item createStubItem() {
        Item item = new Item();
        BigDecimal total = new BigDecimal("199.99");
        item.setDescription("boxing gloves");
        item.setName("boxing gloves");
        item.setPrice(total);
        return item;
    }
}
