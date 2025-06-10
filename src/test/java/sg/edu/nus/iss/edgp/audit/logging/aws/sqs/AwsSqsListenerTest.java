package sg.edu.nus.iss.edgp.audit.logging.aws.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.nus.iss.edgp.audit.logging.aws.sqs.AwsSqsListener;
import sg.edu.nus.iss.edgp.audit.logging.dto.AuditLogDTO;
import sg.edu.nus.iss.edgp.audit.logging.service.AuditLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwsSqsListenerTest {

    @InjectMocks
    private AwsSqsListener awsSqsListener;

    @Mock
    private AuditLogService auditLogService;

    @Captor
    private ArgumentCaptor<AuditLogDTO> auditLogCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void receiveMessage_validJson_shouldProcessAuditLog() throws Exception {
        // Arrange
        AuditLogDTO dto = new AuditLogDTO();
        dto.setUsername("john_doe");

        String jsonMessage = objectMapper.writeValueAsString(dto);

        // Act
        awsSqsListener.receiveMessage(jsonMessage);

        // Assert
        verify(auditLogService, times(1)).executeAuditLog(auditLogCaptor.capture());
        AuditLogDTO capturedDto = auditLogCaptor.getValue();
        assert capturedDto.getUsername().equals("john_doe");
    }

    @Test
    void receiveMessage_invalidJson_shouldLogErrorAndNotCallService() {
        // Arrange
        String invalidJson = "{ invalid json }";

        // Act
        awsSqsListener.receiveMessage(invalidJson);

        // Assert
        verify(auditLogService, never()).executeAuditLog(any());
    }

    @Test
    void receiveMessage_validJson_butServiceThrowsException_shouldLogError() throws Exception {
        // Arrange
        AuditLogDTO dto = new AuditLogDTO();
        dto.setUsername("jane_doe");

        String jsonMessage = objectMapper.writeValueAsString(dto);

        doThrow(new RuntimeException("Service exception")).when(auditLogService).executeAuditLog(any());

        // Act
        awsSqsListener.receiveMessage(jsonMessage);

        // Assert
        verify(auditLogService, times(1)).executeAuditLog(any());
    }
}
