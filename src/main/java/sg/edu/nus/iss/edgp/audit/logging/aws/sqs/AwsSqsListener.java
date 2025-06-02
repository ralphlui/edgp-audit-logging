package sg.edu.nus.iss.edgp.audit.logging.aws.sqs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import sg.edu.nus.iss.edgp.audit.logging.dto.AuditLogDTO;
import sg.edu.nus.iss.edgp.audit.logging.service.AuditLogService;

@RestController
public class AwsSqsListener {
	
	private static final Logger logger = LoggerFactory.getLogger(AwsSqsListener.class);
	
	@Autowired
	SqsTemplate sqsTemplate;
	
	@Autowired
    private AuditLogService auditLogService;  

    private final ObjectMapper objectMapper = new ObjectMapper();
	
	@SqsListener(value = "${cloud.aws.sqs.queue.url}")
	public void receiveMessage(String message) {
        try {
            logger.info("Received Message from SQS: {}", message);
            
            AuditLogDTO auditLogDTO;
            try {
                auditLogDTO = objectMapper.readValue(message, AuditLogDTO.class);
                logger.info("Message parsed successfully in receiveMessage SqsListener.");
            } catch (Exception e) {
                logger.error("Error parsing message from SQS: Invalid format or structure", e);
                return; 
            }
            auditLogService.executeAuditLog(auditLogDTO);  
            
            logger.info("Audit log processed successfully for User: {}", auditLogDTO.getUsername());
            
        } catch (Exception e) {
            logger.error("Error processing message from SQS", e);
        }
    }

}
