package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "userRepository", userRepository);
    }

    @Test
    public void addTocart_Success_Test() {
        // Stub
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(0);
        request.setItemId(0L);
        request.setUsername("test-user");
        User stubUser = createStubUser();
        Item stubItem = createStubItem();
        Cart cart = createStubCart(stubUser, stubItem);
        stubUser.setCart(cart);
        Optional<Item> foundItem = Optional.of(stubItem);

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(stubUser);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(foundItem);

        // Test
        ResponseEntity<Cart> responseEntity = cartController.addTocart(request);

        // Verify
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertEquals(cart, responseEntity.getBody());
    }

    @Test
    public void removeFromCart_Success_Test() {
        // Stub
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(0);
        request.setItemId(0L);
        request.setUsername("test-user");
        User stubUser = createStubUser();
        Item stubItem = createStubItem();
        Cart cart = createStubCart(stubUser, stubItem);
        stubUser.setCart(cart);

        Optional<Item> foundItem = Optional.of(stubItem);

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(stubUser);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(foundItem);

        // Test
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);

        // Verify
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());
        Assert.assertEquals(cart, responseEntity.getBody());
    }

    @Test
    public void removeFromCart_noItem_Fail_Test() {
        // Stub
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(0);
        request.setItemId(0L);
        request.setUsername("test-user");
        User stubUser = createStubUser();
        Optional<Item> foundItem = Optional.empty();
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(stubUser);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(foundItem);

        // Test
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);

        // Verify
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCart_userNotFound_Fail_Test() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(0);
        request.setItemId(0L);
        request.setUsername("test-user");
        User user = createStubUser();
        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(user);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);
        Assert.assertNotNull(responseEntity);
        Assert.assertEquals(404, responseEntity.getStatusCodeValue());
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

}
