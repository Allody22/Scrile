package ru.scrile.org.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.scrile.org.config.exception.GlobalExceptionHandler;
import ru.scrile.org.config.security.MyAuthenticationEntryPoint;
import ru.scrile.org.config.security.WebSecurityConfig;
import ru.scrile.org.controller.AuthController;
import ru.scrile.org.payload.request.AuthRequest;
import ru.scrile.org.payload.request.RegistrationRequest;
import ru.scrile.org.service.MyUserDetailsService;
import ru.scrile.org.service.UserService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({WebSecurityConfig.class, GlobalExceptionHandler.class})
public class AuthControllerTest {

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
    void authWithRegister_shouldReturn200() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("misha", "password", 25);
        doNothing().when(userService).register(registrationRequest.getName(), registrationRequest.getPassword(), registrationRequest.getAge());

        AuthRequest request = new AuthRequest("user", "password");
        String token = "Auth successful for user: user";
        when(userService.authenticate(request.getUsername(), request.getPassword())).thenReturn(token);


        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));
    }


}