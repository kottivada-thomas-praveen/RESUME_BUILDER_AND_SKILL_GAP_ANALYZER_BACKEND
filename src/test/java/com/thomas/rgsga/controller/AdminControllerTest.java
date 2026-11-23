package com.thomas.rgsga.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thomas.rgsga.dto.AdminDashboardSummaryDTO;
import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.UserResponse;
import com.thomas.rgsga.entity.Role;
import com.thomas.rgsga.service.AnalyticsService;
import com.thomas.rgsga.service.DashboardService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AdminController adminController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setMessageConverters(
                        new ByteArrayHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(objectMapper)
                )
                .build();
    }

    @Test
    @DisplayName("GET /api/admin/dashboard-summary returns summary metrics")
    void testGetDashboardSummary() throws Exception {
        AdminDashboardSummaryDTO summaryDTO = AdminDashboardSummaryDTO.builder()
                .totalVisitors(150L)
                .uniqueVisitors(80L)
                .totalUsers(25L)
                .activeUsers(23L)
                .systemHealth("HEALTHY")
                .build();

        when(analyticsService.getDashboardSummary()).thenReturn(summaryDTO);

        mockMvc.perform(get("/api/admin/dashboard-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalVisitors").value(150))
                .andExpect(jsonPath("$.data.uniqueVisitors").value(80))
                .andExpect(jsonPath("$.data.systemHealth").value("HEALTHY"));

        verify(analyticsService, times(1)).getDashboardSummary();
    }

    @Test
    @DisplayName("getUsers returns paginated user response correctly")
    void testGetUsersMethod() {
        UserResponse user = UserResponse.builder()
                .id(1L)
                .name("Jane Admin")
                .fullName("Jane Admin")
                .email("jane@admin.com")
                .role(Role.ADMIN.name())
                .accountEnabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(analyticsService.getUsers(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        ResponseEntity<ApiResponse<Page<UserResponse>>> response = adminController.getUsers(null, null, null, 0, 15);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getContent()).hasSize(1);
        assertThat(response.getBody().getData().getContent().get(0).getEmail()).isEqualTo("jane@admin.com");

        verify(analyticsService, times(1)).getUsers(any(), any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /api/admin/users/1/status updates account status")
    void testUpdateUserStatus() throws Exception {
        doNothing().when(analyticsService).updateUserStatus(eq(1L), eq(false));

        mockMvc.perform(put("/api/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("accountEnabled", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User status updated successfully"));

        verify(analyticsService, times(1)).updateUserStatus(1L, false);
    }

    @Test
    @DisplayName("GET /api/admin/export/users returns CSV download headers")
    void testExportUsersCsv() throws Exception {
        byte[] csvData = "ID,Name,Email\n1,Jane,jane@admin.com".getBytes();
        when(analyticsService.exportUsersCsv()).thenReturn(csvData);

        mockMvc.perform(get("/api/admin/export/users"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", Matchers.containsString("text/csv")))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().bytes(csvData));

        verify(analyticsService, times(1)).exportUsersCsv();
    }
}
