package com.hospital.triage.model;

import java.time.LocalDateTime;

public class PriorityChangeRecord {
    private Integer oldPriority;
    private Integer newPriority;
    private String reason;
    private String operator;
    private LocalDateTime changeTime;

    public Integer getOldPriority() { return oldPriority; }
    public void setOldPriority(Integer oldPriority) { this.oldPriority = oldPriority; }
    public Integer getNewPriority() { return newPriority; }
    public void setNewPriority(Integer newPriority) { this.newPriority = newPriority; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public LocalDateTime getChangeTime() { return changeTime; }
    public void setChangeTime(LocalDateTime changeTime) { this.changeTime = changeTime; }
}
