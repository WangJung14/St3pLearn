package com.tommy.identity.presentation.controller;

import com.tommy.identity.application.dto.response.UserProfileResponse;
import com.tommy.identity.application.service.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Sử dụng @MockBean để giả lập IUserService, tránh việc gọi logic nghiệp vụ thực tế hoặc xuống DB
    @MockBean
    private IUserService userService;

    @Test
    @DisplayName("Should return user profile successfully when user is authenticated")
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000") // Giả lập Security Context với userId
    void shouldGetMyProfileSuccessfully() throws Exception {
        // 1. Arrange (Chuẩn bị dữ liệu)
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        
        UserProfileResponse mockProfile = UserProfileResponse.builder()
                .userId(userId)
                .username("testuser")
                .email("testuser@gmail.com")
                .fullName("Test User")
                .build();

        // Giả lập behavior của userService: khi gọi getMyProfile với userId trên thì trả về mockProfile
        Mockito.when(userService.getMyProfile(userId)).thenReturn(mockProfile);

        // 2. Act & Assert (Thực thi và Kiểm tra)
        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Kiểm tra HTTP Status 200
                .andExpect(jsonPath("$.code").value(200)) // Kiểm tra trường code trong ApiResponse
                .andExpect(jsonPath("$.data.username").value("testuser")) // Kiểm tra dữ liệu trả về đúng như mock
                .andExpect(jsonPath("$.data.email").value("testuser@gmail.com"));
    }
}
