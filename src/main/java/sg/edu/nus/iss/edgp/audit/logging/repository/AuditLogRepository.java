package sg.edu.nus.iss.edgp.audit.logging.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.edgp.audit.logging.entity.AuditLog;


@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

	//Page<AuditLog> retrieveAuditLogWith(Pageable pageable);
	
	@Query("SELECT auditLogData FROM AuditLog auditLogData")
    Page<AuditLog> retrieveAuditLogWith(Pageable pageable);
	
	@Query("SELECT auditLogData FROM AuditLog auditLogData WHERE auditLogData.userId = :userId")
	Page<AuditLog> findByUserId(@Param("userId") String userId, Pageable pageable);
	
	@Query("SELECT auditLogData FROM AuditLog auditLogData WHERE auditLogData.activityType = :activityType")
	Page<AuditLog> findByActivityType(@Param("activityType") String activityType, Pageable pageable);
	
	@Query("SELECT auditLogData FROM AuditLog auditLogData WHERE auditLogData.activityType = :activityType AND auditLogData.userId = :userId")
	Page<AuditLog> findByActivityTypeAndUserId(@Param("activityType") String activityType, @Param("userId") String userId, Pageable pageable);

}
