package sg.edu.nus.iss.edgp.audit.logging.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.nus.iss.edgp.audit.logging.dto.AuditLogDTO;
import sg.edu.nus.iss.edgp.audit.logging.entity.AuditLog;
import sg.edu.nus.iss.edgp.audit.logging.service.AuditLogService;
import sg.edu.nus.iss.edgp.audit.logging.utility.DTOMapper;

@WebMvcTest(AuditLogController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuditLogControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private AuditLogService auditLogService;
	
	private static List<AuditLogDTO> mockAuditLog = new ArrayList<>();
	
	private static final Date testDate = new Date();
	
	private static AuditLog auditLogData1 = new AuditLog(1, "200", "12345", "mocktest_data_001", "login", "User log into the system",
			"/api/login","Success", "POST", testDate, testDate,"Login successful");
	
	private static AuditLog auditLogData2 = new AuditLog(2, "200", "12345", "mocktest_data_002", "login", "User log into the system",
			"/api/login","Failed", "POST", testDate, testDate,"Login attempt is failed");

	
	@BeforeAll
	static void setUp() {
		mockAuditLog.add(DTOMapper.toauditLogDTO(auditLogData1));
		mockAuditLog.add(DTOMapper.toauditLogDTO(auditLogData2));
	}

	@Test
	void testRetrieveAllAuditLogs() throws Exception {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());
		Map<Long, List<AuditLogDTO>> mockAuditDataMap = new HashMap<>();
		mockAuditDataMap.put(0L, mockAuditLog);

		Mockito.when(auditLogService.retrieveAllAuditLogs(pageable)).thenReturn(mockAuditDataMap);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/retrieveAllAuditLogs").param("page", "0").param("size", "10")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data[0].auditId").value(1))
				.andDo(print());
	}
	
	
	@Test
	void testGetAllAuditLogsEmptyResult() throws Exception {
		int page = 0;
		int size = 10;

		Pageable pageable = PageRequest.of(page, size, Sort.by("lastupdatedDate").ascending());
		Mockito.when(auditLogService.retrieveAllAuditLogs(pageable)).thenReturn(Collections.emptyMap());

		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/audit/retrieveAllAuditLogs").param("page", String.valueOf(page))
						.param("size", String.valueOf(size)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false));
	}

	@Test
	void testGetAllAuditLogsException() throws Exception {
		int page = 0;
		int size = 10;

		Pageable pageable = PageRequest.of(page, size, Sort.by("lastupdatedDate").ascending());
		Mockito.when(auditLogService.retrieveAllAuditLogs(pageable))
				.thenThrow(new RuntimeException("Unexpected error"));

		 assertThrows(Exception.class, () -> 
	        mockMvc.perform(get("/api/audit/retrieveAllAuditLogs")
	                .param("page", String.valueOf(page))
	                .param("size", String.valueOf(size))
	                .contentType(MediaType.APPLICATION_JSON)));
	}
	
	@Test
	void testGetAllAuditLogsWhenAuditLogListIsEmptyReturnsNotFound() throws Exception {
	    long totalRecord = 5L;
	    Map<Long, List<AuditLogDTO>> mockResult = new HashMap<>();
	    mockResult.put(totalRecord, new ArrayList<>());

	    when(auditLogService.retrieveAllAuditLogs(any(Pageable.class)))
	        .thenReturn(mockResult);

	    mockMvc.perform(get("/api/audit/retrieveAllAuditLogs")
	            .param("page", "0")
	            .param("size", "10")
	            .accept(MediaType.APPLICATION_JSON))
	        .andExpect(status().isNotFound())
	        .andExpect(jsonPath("$.success").value(false));
	}

	
	@Test
	void testSearchAuditLogsWithActivityTypeAndUserId() throws Exception {
	    Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());
	    Map<Long, List<AuditLogDTO>> mockAuditDataMap = new HashMap<>();
	    mockAuditDataMap.put(0L, mockAuditLog);

	    Mockito.when(auditLogService.searchAuditLogs("login", "12345", pageable)).thenReturn(mockAuditDataMap);

	    mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/searchByParams")
	            .param("activityType", "login")
	            .param("userId", "12345")
	            .param("page", "0")
	            .param("size", "10")
	            .contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isOk())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	        .andExpect(jsonPath("$.success").value(true))
	        .andDo(print());
	}

	@Test
	void testSearchAuditLogsWithActivityTypeOnly() throws Exception {
	    Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());
	    Map<Long, List<AuditLogDTO>> mockAuditDataMap = new HashMap<>();
	    mockAuditDataMap.put(0L, mockAuditLog); 

	    Mockito.when(auditLogService.searchAuditLogs("login", null, pageable)).thenReturn(mockAuditDataMap);

	    mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/searchByParams")
	            .param("activityType", "login")
	            .param("page", "0")
	            .param("size", "10")
	            .contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isOk())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	        .andExpect(jsonPath("$.success").value(true))
	        .andDo(print());
	}

	@Test
	void testSearchAuditLogsWithUserIdOnly() throws Exception {
	    Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());
	    Map<Long, List<AuditLogDTO>> mockAuditDataMap = new HashMap<>();
	    mockAuditDataMap.put(1L, mockAuditLog.subList(1, 2)); 

	    Mockito.when(auditLogService.searchAuditLogs(null, "12345", pageable)).thenReturn(mockAuditDataMap);

	    mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/searchByParams")
	            .param("userId", "12345")
	            .param("page", "0")
	            .param("size", "10")
	            .contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isOk())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	        .andExpect(jsonPath("$.success").value(true))
	        .andDo(print());
	}

	@Test
	void testSearchAuditLogsNoParams() throws Exception {
	    Pageable pageable = PageRequest.of(0, 10, Sort.by("lastupdatedDate").ascending());

	    Mockito.when(auditLogService.searchAuditLogs(null, null, pageable)).thenReturn(new HashMap<>());

	    mockMvc.perform(MockMvcRequestBuilders.get("/api/audit/searchByParams")
	            .param("page", "0")
	            .param("size", "10")
	            .contentType(MediaType.APPLICATION_JSON))
	        .andExpect(MockMvcResultMatchers.status().isNotFound())
	        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	        .andExpect(jsonPath("$.success").value(false))
	        .andExpect(jsonPath("$.message").value("No audit logs found."))
	        .andDo(print());
	}
	

	@Test
	void testSearchAuditLogsInternalServerError() throws Exception {
		Mockito.when(auditLogService.searchAuditLogs(any(), any(), any(Pageable.class)))
				.thenThrow(new RuntimeException("Unexpected error"));

		mockMvc.perform(get("/api/audit/searchByParams").param("page", "0").param("size", "10")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(false)).andDo(print());
	}

	@Test
	void testAddAuditLog() throws Exception {
		AuditLogDTO auditLogDTO = new AuditLogDTO();
		auditLogDTO.setUserId("user123");

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(auditLogDTO);

		mockMvc.perform(post("/api/audit/execute").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(content().string("Audit log saved successfully!"));

	}


}
