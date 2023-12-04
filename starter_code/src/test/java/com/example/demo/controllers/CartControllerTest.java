package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    @InjectMocks
    private CartController cartController;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    final private User user = new User(); // test user
    final private List<Item> items = new ArrayList<>(); // test item
    final private Cart cart = new Cart(); // test cart
    final private ModifyCartRequest request = new ModifyCartRequest();
    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        user.setUsername("testUser");   //Set username
        cart.setUser(user);             //set user to cart

        //Create test item
        Item item1 = new Item();
        item1.setName("Item1");
        item1.setPrice(new BigDecimal("10.00"));
        item1.setDescription("Test Item1");
        items.add(item1);

        cart.setItems(items);       // Set items to cart
        user.setCart(cart);         // Set cart to user

        // Set request
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);
    }

    @Test
    public void add_to_cart_happy_path(){

        Cart expectedCart = cart.getUser().getCart();

        // Mock
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(items.get(0)));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        final ResponseEntity<Cart> response = cartController.addTocart(request);
        // Assert response status
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Assert body
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(expectedCart, cart);
    }

    @Test
    public void add_to_cart_user_not_found(){
        // Create a ModifyCartRequest
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonExistentUser");
        request.setItemId(1L);
        request.setQuantity(1);

        // Mock
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert response status
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void add_to_cart_item_not_found(){

        request.setItemId(999L); // Set non-existent item id

        // Mock
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert response status
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void remove_cart_happy_path(){

        // Create expected cart to compare later
        Cart expectedCart = new Cart();
        expectedCart.setId(cart.getId());
        expectedCart.setUser(cart.getUser());
        expectedCart.setItems(new ArrayList<>(cart.getItems()));
        // Remove the item from the expected cart
        expectedCart.removeItem(items.get(0));

        //Mock
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(items.get(0)));

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        // Assert response status
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        // Assert body
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertThat(cart).usingRecursiveComparison().isEqualTo(expectedCart);
    }
}
