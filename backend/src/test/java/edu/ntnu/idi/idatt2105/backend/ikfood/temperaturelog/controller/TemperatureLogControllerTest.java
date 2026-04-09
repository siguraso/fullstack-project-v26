package edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto.TemperatureLogCreateRequest;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.dto.TemperatureLogDTO;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.mapper.TemperatureLogMapper;
import edu.ntnu.idi.idatt2105.backend.ikfood.temperaturelog.service.TemperatureLogService;

@ExtendWith(MockitoExtension.class)
class TemperatureLogControllerTest {

    @Mock
    private TemperatureLogService service;

    @Mock
    private TemperatureLogMapper mapper;

    @InjectMocks
    private TemperatureLogController controller;

    @Test
    void createReturns201Created() {
        TemperatureLogCreateRequest request = new TemperatureLogCreateRequest();
        request.setTemperatureZoneId(1L);
        request.setTemperatureCelsius(4.5);

        TemperatureLogDTO dto = new TemperatureLogDTO();
        dto.setId(55L);

        when(service.createLog(request)).thenReturn(dto);

        ResponseEntity<ApiResponse<TemperatureLogDTO>> response = controller.createLog(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(55L, response.getBody().getData().getId());
        verify(service).createLog(request);
    }

    @Test
    void deleteReturns204NoContent() {
        ResponseEntity<Void> response = controller.delete(11L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).delete(11L);
    }
}

