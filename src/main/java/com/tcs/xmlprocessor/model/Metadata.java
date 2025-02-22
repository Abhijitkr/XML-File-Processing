package com.tcs.xmlprocessor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Metadata {
	
	@Id
	private String fileId;
	private String accountName;
	private String empId;
	private String countryCode;
	private String deptId;
	private String startTime;
	private String endTime;
	
	public Metadata(String fileId, String accountName, String empId, String countryCode, String deptId,
			String startTime, String endTime) {
		super();
		this.fileId = fileId;
		this.accountName = accountName;
		this.empId = empId;
		this.countryCode = countryCode;
		this.deptId = deptId;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Metadata() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
}