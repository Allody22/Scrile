package ru.scrile.org.order.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.scrile.org.config.exception.GlobalExceptionHandler;
import ru.scrile.org.config.security.MyAuthenticationEntryPoint;
import ru.scrile.org.config.security.WebSecurityConfig;
import ru.scrile.org.controller.OrderController;
import ru.scrile.org.model.exception.UserNotFoundException;
import ru.scrile.org.payload.request.NewOrderRequest;
import ru.scrile.org.service.MyUserDetailsService;
import ru.scrile.org.service.OrderService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@Import({WebSecurityConfig.class, GlobalExceptionHandler.class})
public class CreateOrderExceptionTest {

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
    void createOrder_WithMissingProductName_ShouldReturnBadRequest() throws Exception {
        NewOrderRequest request = new NewOrderRequest("", 100.0, null);

        String expectedJsonResponse = "{"
                + "\"violations\":["
                + "{\"fieldName\":\"productName\",\"message\":\"Product name must not be blank\"}"
                + "]"
                + "}";

        mockMvc.perform(post("/api/v1/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResponse));
    }

    @Test
    @WithMockUser(username = "user")
    void createOrder_WithMissingPrice_ShouldReturnOk() throws Exception {
        NewOrderRequest request = new NewOrderRequest("Product", 0.0, null);

        mockMvc.perform(post("/api/v1/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "nonexistentUser")
    void createOrder_WithNonexistentUser_ShouldReturnNotFound() throws Exception {
        NewOrderRequest request = new NewOrderRequest("Product", 100.0, null);

        doThrow(new UserNotFoundException("nonexistentUser"))
                .when(orderService).makeNewOrder(eq("nonexistentUser"), any(NewOrderRequest.class));

        mockMvc.perform(post("/api/v1/orders/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

}
