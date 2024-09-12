package ru.scrile.org.auth.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.scrile.org.config.exception.GlobalExceptionHandler;
import ru.scrile.org.controller.AuthController;
import ru.scrile.org.payload.request.RegistrationRequest;
import ru.scrile.org.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class RegistrationValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerUser_WithBlankUsername_shouldReturn400() throws Exception {
        RegistrationRequest request = new RegistrationRequest("", "password", 25);

        String expectedJsonResponse = "{"
                + "\"violations\":["
                + "{\"fieldName\":\"name\",\"message\":\"Name must not be blank\"}"
                + "]"
                + "}";

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResponse));
    }


    @Test
    void registerUser_WithBlankPassword_shouldReturn400() throws Exception {
        RegistrationRequest request = new RegistrationRequest("username", "", 25);

        String expectedJsonResponse = "{"
                + "\"violations\":["
                + "{\"fieldName\":\"password\",\"message\":\"Password must not be blank\"}"
                + "]"
                + "}";

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResponse));
    }

    @Test
    void registerUser_WithoutUsername_shouldReturn400() throws Exception {
        RegistrationRequest request = new RegistrationRequest(null, "", 25);

        String expectedJsonResponse = "{"
                + "\"violations\":["
                + "{\"fieldName\":\"name\",\"message\":\"Name must not be blank\"}," +
                "{\"fieldName\":\"password\",\"message\":\"Password must not be blank\"}"
                + "]"
                + "}";

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJsonResponse));
    }
}