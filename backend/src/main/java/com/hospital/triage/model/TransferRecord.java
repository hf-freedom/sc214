package com.hospital.triage.model;

import java.time.LocalDateTime;

public class TransferRecord {
    private String fromDepartmentId;
    private String toDepartmentId;
    private String reason;
    private String operator;
    private LocalDateTime transferTime;

    public String getFromDepartmentId() { return fromDepartmentId; }
    public void setFromDepartmentId(String fromDepartmentId) { this.fromDepartmentId = fromDepartmentId; }
    public String getToDepartmentId() { return toDepartmentId; }
    public void setToDepartmentId(String toDepartmentId) { this.toDepartmentId = toDepartmentId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public LocalDateTime getTransferTime() { return transferTime; }
    public void setTransferTime(LocalDateTime transferTime) { this.transferTime = transferTime; }
}
