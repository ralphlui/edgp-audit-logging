package sg.edu.nus.iss.edgp.audit.logging.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.edgp.audit.logging.dto.AuditLogDTO;
import sg.edu.nus.iss.edgp.audit.logging.entity.AuditLog;
import sg.edu.nus.iss.edgp.audit.logging.repository.AuditLogRepository;
import sg.edu.nus.iss.edgp.audit.logging.service.AuditLogService;
import sg.edu.nus.iss.edgp.audit.logging.utility.DTOMapper;


@Service
public class AuditLogServiceImpl implements AuditLogService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);
	
	@Autowired
	private AuditLogRepository auditLogRepository;

	
	@Override
    public void executeAuditLog(AuditLogDTO auditLogDTO) {
        AuditLog auditLog = new AuditLog();
        auditLog.setStatusCode(auditLogDTO.getStatusCode());
        auditLog.setUserId(auditLogDTO.getUserId());
        auditLog.setUsername(auditLogDTO.getUsername());
        auditLog.setActivityType(auditLogDTO.getActivityType());
        auditLog.setActivityDescription(auditLogDTO.getActivityDescription());
        auditLog.setRequestActionEndpoint(auditLogDTO.getRequestActionEndpoint());
        auditLog.setResponseStatus(auditLogDTO.getResponseStatus());
        auditLog.setRequestType(auditLogDTO.getRequestType());

        auditLog.setInsertDate(new Date());
        auditLog.setLastupdatedDate(new Date());
        
        auditLog.setRemarks(auditLogDTO.getRemarks());

        auditLogRepository.save(auditLog);

    }
	
	public Map<Long, List<AuditLogDTO>> retrieveAllAuditLogs(Pageable pageable) {
		logger.info("Retrieving all audit trail data...");
		Map<Long, List<AuditLogDTO>> result = new HashMap<>();
		List<AuditLogDTO> auditLogDTOList = new ArrayList<>();

		Page<AuditLog> auditPages = auditLogRepository.retrieveAuditLogWith(pageable);
		long totalRecord = auditPages.getTotalElements();

		if (totalRecord > 0) {
			for (AuditLog auditLog : auditPages.getContent()) {
				
				AuditLogDTO auditLogDTO = DTOMapper.toauditLogDTO(auditLog);
				auditLogDTOList.add(auditLogDTO);
			}

		} else {
			logger.info("There is no audit log...");
		}

		result.put(totalRecord, auditLogDTOList);

		return result;
	}
	
	@Override
    public Map<Long, List<AuditLogDTO>> searchAuditLogs(String activityType, String userId, Pageable pageable) {
        try {
            Page<AuditLog> auditLogsResult;
            if (activityType != null && !activityType.isEmpty() && userId != null && !userId.isEmpty()) {
                // Case 1: Search by both activityType and userId
                auditLogsResult = auditLogRepository.findByActivityTypeAndUserId(activityType, userId, pageable);
            } else if (activityType != null && !activityType.isEmpty()) {
                // Case 2: Search by activityType only
                auditLogsResult = auditLogRepository.findByActivityType(activityType, pageable);
            } else if (userId != null && !userId.isEmpty()) {
                // Case 3: Search by userId only
                auditLogsResult = auditLogRepository.findByUserId(userId, pageable);
            } else {
                // If no parameters provided, return empty result
                logger.warn("No valid search parameters provided.");
                return new HashMap<>();
            }

            long totalRecord = auditLogsResult.getTotalElements();
            Map<Long, List<AuditLogDTO>> result = new HashMap<>();
            List<AuditLogDTO> auditDTOList = new ArrayList<>();

            if (totalRecord > 0) {
                for (AuditLog auditLog : auditLogsResult.getContent()) {
                    AuditLogDTO auditLogDTO = DTOMapper.toauditLogDTO(auditLog);
                    auditDTOList.add(auditLogDTO);
                }
            }

            logger.info("Total record in searchAuditLogs: {}", totalRecord);
            result.put(totalRecord, auditDTOList);
            return result;

        } catch (Exception ex) {
            logger.error("Exception in searchAuditLogs: {}", ex.toString());
            throw ex;
        }
    }

	

}
