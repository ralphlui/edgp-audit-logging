package sg.edu.nus.iss.edgp.audit.logging.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import sg.edu.nus.iss.edgp.audit.logging.dto.AuditLogDTO;


public interface AuditLogService {


	void executeAuditLog(AuditLogDTO auditLogDTO);


	Map<Long, List<AuditLogDTO>> retrieveAllAuditLogs(Pageable pageable);

	Map<Long, List<AuditLogDTO>> searchAuditLogs(String activityType, String userId, Pageable pageable);

}
