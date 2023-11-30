package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    final private UserRepository userRepo = mock(UserRepository.class);
    final private OrderRepository orderRepo = mock(OrderRepository.class);

    final private User user = new User(); // Test user
    final private Cart cart = new Cart(); //Test cart
    List<Item> items = new ArrayList<>(); //Test item

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);

        //Set username
        user.setUsername("test");
        //set user to cart
        cart.setUser(user);
        //Create test item
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setPrice(new BigDecimal("10.00"));
        item1.setDescription("Test Item1");
        //Add item
        items.add(item1);

        cart.setItems(items);   // Set items to cart
        user.setCart(cart);     // Set cart to user
    }

    @Test
    public void submit_order_happy_path(){
        //Mock behaviour of userRepo
        when(userRepo.findByUsername("test")).thenReturn(user);
        //Call method under test
        final ResponseEntity<UserOrder> response = orderController.submit("test");
        //Assert response status
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        //Assert body
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(user,order.getUser());
    }

    @Test
    public void get_orders_for_user_happy_path(){
        //Mock behaviour of userRepo and orderRepo
        when(userRepo.findByUsername("test")).thenReturn(user);
        when(orderRepo.findByUser(user)).thenReturn(Collections.emptyList());
        //Call method under test
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        //Assert response status
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        //Assert body
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        assertEquals(0, orders.size());
    }
}
