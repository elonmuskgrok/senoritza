package com.siva.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siva.dto.LoginRequestDTO;
import com.siva.dto.RegisterRequestDTO;
import com.siva.dto.AuthResponse;
import com.siva.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.siva.security.CustomUserDetailsService;
import com.siva.security.JwtTokenProvider;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister_Http200() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("test@test.com");
        request.setPassword("Password@123");
        request.setName("Test");
        request.setMobileNumber("9876543210");
        request.setAddressLine1("Line1");
        request.setAddressLine2("Line2");
        request.setArea("Area");
        request.setCity("City");
        request.setState("State");
        request.setPincode("123456");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testLogin_Http200() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@test.com");
        request.setPassword("password");

        AuthResponse response = AuthResponse.builder()
                .token("mockJwt")
                .email("test@test.com")
                .build();

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockJwt"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }
}
