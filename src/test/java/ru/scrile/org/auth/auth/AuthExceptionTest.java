package ru.scrile.org.auth.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import ru.scrile.org.config.exception.GlobalExceptionHandler;
import ru.scrile.org.config.security.MyAuthenticationEntryPoint;
import ru.scrile.org.config.security.WebSecurityConfig;
import ru.scrile.org.controller.AuthController;
import ru.scrile.org.payload.request.AuthRequest;
import ru.scrile.org.service.MyUserDetailsService;
import ru.scrile.org.service.UserService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({WebSecurityConfig.class, GlobalExceptionHandler.class})
public class AuthExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @MockBean
    private MyUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void registerUser_WithDuplicateName_shouldReturn409() throws Exception {
        AuthRequest authRequest = new AuthRequest("misha", "password");

        when(userService.authenticate(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Authentication failed") {});

        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Authentication failed"));
    }

}