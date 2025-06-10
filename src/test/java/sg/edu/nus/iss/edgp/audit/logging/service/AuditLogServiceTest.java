package sg.edu.nus.iss.edgp.audit.logging.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import sg.edu.nus.iss.edgp.audit.logging.controller.AuditLogController;
import sg.edu.nus.iss.edgp.audit.logging.dto.AuditLogDTO;
import sg.edu.nus.iss.edgp.audit.logging.entity.AuditLog;
import sg.edu.nus.iss.edgp.audit.logging.repository.AuditLogRepository;
import sg.edu.nus.iss.edgp.audit.logging.service.impl.AuditLogServiceImpl;
import sg.edu.nus.iss.edgp.audit.logging.utility.DTOMapper;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuditLogServiceTest {

	@Mock
    private AuditLogRepository auditLogRepository;

	@InjectMocks
	private AuditLogServiceImpl auditLogService;
	
	private final Pageable pageable = PageRequest.of(0, 10);
	
	private AuditLog sampleAuditLog;
	private AuditLogDTO sampleAuditLogDTO;

	@BeforeEach
	void setup() {
		sampleAuditLog = new AuditLog();
		sampleAuditLogDTO = new AuditLogDTO();
	}
    
    @Test
    void testExecuteAuditLog() {
        // Arrange
        AuditLogDTO dto = new AuditLogDTO();
        dto.setStatusCode("200");
        dto.setUserId("user123");
        dto.setUsername("john.doe");
        dto.setActivityType("LOGIN");
        dto.setActivityDescription("User logged in");
        dto.setRequestActionEndpoint("/api/login");
        dto.setResponseStatus("SUCCESS");
        dto.setRequestType("POST");
        dto.setRemarks("No issues");
        
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        auditLogService.executeAuditLog(dto);
        verify(auditLogRepository, times(1)).save(captor.capture());
  
        AuditLog savedLog = captor.getValue();
        assertEquals(dto.getStatusCode(), savedLog.getStatusCode());
        assertEquals(dto.getUserId(), savedLog.getUserId());
    }
    
    
	@Test
	void testRetrieveAllAuditLogs() {
		// Arrange
		AuditLog auditLog = new AuditLog();
		AuditLogDTO auditLogDTO = new AuditLogDTO();
		Pageable pageable = PageRequest.of(0, 10);

		List<AuditLog> auditLogList = List.of(auditLog);
		Page<AuditLog> auditLogPage = new PageImpl<>(auditLogList, pageable, 1);

		when(auditLogRepository.retrieveAuditLogWith(pageable)).thenReturn(auditLogPage);

		try (MockedStatic<DTOMapper> mockedStatic = Mockito.mockStatic(DTOMapper.class)) {
			mockedStatic.when(() -> DTOMapper.toauditLogDTO(auditLog)).thenReturn(auditLogDTO);

			// Act
			Map<Long, List<AuditLogDTO>> result = auditLogService.retrieveAllAuditLogs(pageable);

			// Assert
			assertEquals(1, result.size());
			List<AuditLogDTO> dtoList = result.get(1L);
			assertNotNull(dtoList);
			assertEquals(1, dtoList.size());
			assertSame(auditLogDTO, dtoList.get(0));
		}
	}
	

	@Test
	void testSearchByActivityTypeAndUserId() {

		// Arrange
		Page<AuditLog> auditLogPage = new PageImpl<>(List.of(sampleAuditLog), pageable, 1);

		when(auditLogRepository.findByActivityTypeAndUserId("LOGIN", "user123", pageable)).thenReturn(auditLogPage);

		try (MockedStatic<DTOMapper> staticMock = Mockito.mockStatic(DTOMapper.class)) {
			staticMock.when(() -> DTOMapper.toauditLogDTO(sampleAuditLog)).thenReturn(sampleAuditLogDTO);

			// Act
			Map<Long, List<AuditLogDTO>> result = auditLogService.searchAuditLogs("LOGIN", "user123", pageable);

			// Assert
			assertEquals(1, result.size());
			assertEquals(List.of(sampleAuditLogDTO), result.get(1L));
		}
	}
    
	@Test
	void testSearchByActivityTypeOnly() {
		Page<AuditLog> auditLogPage = new PageImpl<>(List.of(sampleAuditLog), pageable, 1);
		when(auditLogRepository.findByActivityType("LOGIN", pageable)).thenReturn(auditLogPage);

		try (MockedStatic<DTOMapper> staticMock = Mockito.mockStatic(DTOMapper.class)) {
			staticMock.when(() -> DTOMapper.toauditLogDTO(sampleAuditLog)).thenReturn(sampleAuditLogDTO);

			Map<Long, List<AuditLogDTO>> result = auditLogService.searchAuditLogs("LOGIN", null, pageable);

			assertEquals(1, result.size());
			assertEquals(List.of(sampleAuditLogDTO), result.get(1L));
		}
	}
}
