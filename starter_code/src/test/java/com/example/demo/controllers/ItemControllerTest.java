package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;

import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    List<Item> items = new ArrayList<>(); //Test item

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        //Create test item1
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setPrice(new BigDecimal("10.00"));
        item1.setDescription("Test Item1");

        //Add item
        items.add(item1);
    }

    @Test
    public void get_all_item_happy_path() {
        //Mock the behavior of itemRepository
        Mockito.when(itemRepository.findAll()).thenReturn(items);

        //Test get all item
        final ResponseEntity<List<Item>> response = itemController.getItems();
        //Assert response status
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        //Assert body
        List<Item> itemList = response.getBody();
        assertNotNull(itemList);
        assertEquals(items,itemList);
    }

    @Test
    public void get_item_by_id_happy_path() {
        // First item ID in list is 1
        Long itemId = 1L;
        Item expectedItem = items.get(0);

        // Mock behavior of itemRepository
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        // Test getItemById
        final ResponseEntity<Item> response = itemController.getItemById(itemId);
        // Assert response status
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Assert body
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(expectedItem, item);
    }

    @Test
    public void get_items_by_name_happy_path() {
        // First item in list name "Item1"
        String itemName = "Item1";
        List<Item> expectedItems = items.stream()
                .filter(item -> item.getName().equals(itemName))
                .collect(Collectors.toList());

        // Mock behavior of itemRepository
        Mockito.when(itemRepository.findByName(itemName)).thenReturn(expectedItems);

        // Test getItemsByName
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
        // Assert response status
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Assert body
        List<Item> itemList = response.getBody();
        assertNotNull(itemList);
        assertEquals(expectedItems, itemList);
    }
}
