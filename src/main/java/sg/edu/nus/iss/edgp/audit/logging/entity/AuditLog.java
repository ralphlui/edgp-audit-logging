package sg.edu.nus.iss.edgp.audit.logging.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="audit_log")
public class AuditLog {
	
	public AuditLog() {
       super();
    }
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "audit_id")
    private Integer auditId;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    private String username;

    @Column(name = "activity_type")
    private String activityType;

    @Column(name = "activity_description")
    private String activityDescription;

    @Column(name = "request_action_endpoint")
    private String requestActionEndpoint;

    @Column(name = "response_status")
    private String responseStatus;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "insert_date")
    private Date insertDate;

    @Column(name = "lastupdated_date")
    private Date lastupdatedDate;

    @Column(name = "remarks")
    private String remarks;

	public AuditLog(Integer auditId, String statusCode, String userId, String username, String activityType,
			String activityDescription, String requestActionEndpoint, String responseStatus, String requestType,
			Date insertDate, Date lastupdatedDate, String remarks) {
		super();
		this.auditId = auditId;
		this.statusCode = statusCode;
		this.userId = userId;
		this.username = username;
		this.activityType = activityType;
		this.activityDescription = activityDescription;
		this.requestActionEndpoint = requestActionEndpoint;
		this.responseStatus = responseStatus;
		this.requestType = requestType;
		this.insertDate = insertDate;
		this.lastupdatedDate = lastupdatedDate;
		this.remarks = remarks;
	}

}

