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
import ru.scrile.org.model.exception.UserAlreadyExistException;
import ru.scrile.org.payload.request.RegistrationRequest;
import ru.scrile.org.service.UserService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class RegistrationExceptionTest {

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
    void registerUser_WithDuplicateName_shouldReturn409() throws Exception {
        RegistrationRequest request1 = new RegistrationRequest("misha", "password1", 25);
        RegistrationRequest request2 = new RegistrationRequest("misha", "password2", 30);

        doNothing().when(userService).register(request1.getName(), request1.getPassword(), request1.getAge());
        doThrow(new UserAlreadyExistException("misha")).when(userService)
                .register(request2.getName(), request2.getPassword(), request2.getAge());

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with misha username already exists."));
    }

}