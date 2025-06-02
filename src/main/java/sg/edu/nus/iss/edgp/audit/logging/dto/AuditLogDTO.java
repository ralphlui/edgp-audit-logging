package sg.edu.nus.iss.edgp.audit.logging.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogDTO {
	
	private Integer auditId;
	private String statusCode;
    private String userId;
    private String username;
    private String activityType;
    private String activityDescription;
    private String requestActionEndpoint;
    private String responseStatus;
    private String requestType;
    private Date insertDate;
    private Date lastupdatedDate;
    private String remarks;
	

}
