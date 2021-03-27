package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTests {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmit_Success_Test() {
        User testUser = createStubUser();
        Item item = createStubItem();
        Cart cart = createStubCart(testUser, item);

        testUser.setCart(cart);

        // Stub
        when(userRepository.findByUsername(Mockito.anyString())).thenReturn(testUser);

        // Test
        ResponseEntity<UserOrder> response = orderController.submit("test-user");

        // Verify
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(testUser, response.getBody().getUser());
        Assert.assertEquals(cart, response.getBody().getUser().getCart());
    }

    private User createStubUser() {
        User testUser = new User();
        testUser.setUsername("test-user");
        testUser.setPassword("password");
        testUser.setId(0);
        return testUser;
    }

    private Item createStubItem() {
        Item item = new Item();
        BigDecimal total = new BigDecimal("199.99");
        item.setDescription("boxing gloves");
        item.setName("boxing gloves");
        item.setPrice(total);
        return item;
    }

    private Cart createStubCart(User user, Item item) {
        Cart cart = new Cart();
        BigDecimal total = new BigDecimal("199.99");
        cart.setTotal(total);
        cart.setUser(user);
        cart.setItems(Arrays.asList(item));
        return cart;
    }

    @Test
    public void testSubmit_userNotFound_Fail_Test() {
        // Stub
        when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        // Test
        ResponseEntity<UserOrder> response = orderController.submit("test-user");

        // Verify
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertEquals(null, response.getBody());
    }

    @Test
    public void getOrdersForUser_Success_Test() {
        // Stub
        User testUser = createStubUser();
        Item item = createStubItem();
        Cart cart = createStubCart(testUser, item);

        testUser.setCart(cart);

        UserOrder userOrder = new UserOrder();
        userOrder.setUser(testUser);
        userOrder.setItems(testUser.getCart().getItems());
        userOrder.setTotal(testUser.getCart().getTotal());

        when(userRepository.findByUsername(Mockito.anyString())).thenReturn(testUser);
        when(orderRepository.findByUser(Mockito.any(User.class))).thenReturn(Arrays.asList(userOrder));

        // Test
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(testUser.getUsername());

        // Verify
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(Arrays.asList(userOrder), response.getBody());
    }

    @Test
    public void getOrdersForUser_userNotFound_Fail_Test() {
        // Stub
        when(userRepository.findByUsername(Mockito.anyString())).thenReturn(null);

        // Test
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test-user");

        // Verify
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
        Assert.assertEquals(null, response.getBody());
    }

}
