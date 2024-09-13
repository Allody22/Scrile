package ru.scrile.org.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.scrile.org.config.exception.GlobalExceptionHandler;
import ru.scrile.org.config.security.MyAuthenticationEntryPoint;
import ru.scrile.org.config.security.WebSecurityConfig;
import ru.scrile.org.controller.OrderController;
import ru.scrile.org.model.Order;
import ru.scrile.org.payload.request.NewOrderRequest;
import ru.scrile.org.service.MyUserDetailsService;
import ru.scrile.org.service.OrderService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@Import({WebSecurityConfig.class, GlobalExceptionHandler.class})
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @MockBean
    private MyUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user")
    void createOrder_WithValidData_ShouldReturn200() throws Exception {
        NewOrderRequest request = new NewOrderRequest("Product", 100.0, null);

        doNothing().when(orderService).makeNewOrder("user", request);

        mockMvc.perform(post("/api/v1/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order made"));
    }

    @Test
    @WithMockUser(username = "user")
    void getUserOrders_WithAllTrue_ShouldReturnAllOrders() throws Exception {
        List<Order> orders = Arrays.asList(
                Order.builder()
                        .id(1L)
                        .productName("Product1")
                        .price(100.0)
                        .paymentTime(LocalDateTime.now())
                        .build(),
                Order.builder()
                        .id(2L)
                        .productName("Product2")
                        .price(200.0)
                        .paymentTime(LocalDateTime.now())
                        .build()
        );

        when(orderService.findAllOrdersByUser("user")).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders/get")
                        .param("all", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orders)));
    }

    @Test
    @WithMockUser(username = "user")
    void getUserOrders_WithAllFalse_ShouldReturnPageableOrders() throws Exception {
        List<Order> orders = Arrays.asList(
                Order.builder()
                        .id(1L)
                        .productName("Product1")
                        .price(100.0)
                        .paymentTime(LocalDateTime.now())
                        .build(),
                Order.builder()
                        .id(2L)
                        .productName("Product2")
                        .price(200.0)
                        .paymentTime(LocalDateTime.now())
                        .build()
        );
        Page<Order> ordersPage = new PageImpl<>(orders);

        when(orderService.findOrdersByUser(eq("user"), any(Pageable.class))).thenReturn(ordersPage);

        mockMvc.perform(get("/api/v1/orders/get")
                        .param("all", "false")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ordersPage)));
    }
}
