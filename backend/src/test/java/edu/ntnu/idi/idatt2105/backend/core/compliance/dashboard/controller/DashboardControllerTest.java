package edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.dto.DashboardOverviewDTO;
import edu.ntnu.idi.idatt2105.backend.core.compliance.dashboard.service.DashboardService;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void testReturnOkApiResponseWithOverviewData() {
        DashboardOverviewDTO overview = new DashboardOverviewDTO();
        when(dashboardService.getOverview()).thenReturn(overview);

        ResponseEntity<ApiResponse<DashboardOverviewDTO>> response = dashboardController.getOverview();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertSame(overview, response.getBody().getData());
        assertNull(response.getBody().getMessage());

        verify(dashboardService).getOverview();
        verifyNoMoreInteractions(dashboardService);
    }
}
