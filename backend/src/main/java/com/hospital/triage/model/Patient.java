package com.hospital.triage.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Patient {
    private String id;
    private String name;
    private Integer age;
    private String gender;
    private String symptom;
    private String emergencyLevel;
    private Integer priority;
    private String status;
    private String departmentId;
    private String doctorId;
    private LocalDateTime registerTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<PriorityChangeRecord> priorityHistory = new ArrayList<>();
    private List<TransferRecord> transferHistory = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getSymptom() { return symptom; }
    public void setSymptom(String symptom) { this.symptom = symptom; }
    public String getEmergencyLevel() { return emergencyLevel; }
    public void setEmergencyLevel(String emergencyLevel) { this.emergencyLevel = emergencyLevel; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public LocalDateTime getRegisterTime() { return registerTime; }
    public void setRegisterTime(LocalDateTime registerTime) { this.registerTime = registerTime; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public List<PriorityChangeRecord> getPriorityHistory() { return priorityHistory; }
    public void setPriorityHistory(List<PriorityChangeRecord> priorityHistory) { this.priorityHistory = priorityHistory; }
    public List<TransferRecord> getTransferHistory() { return transferHistory; }
    public void setTransferHistory(List<TransferRecord> transferHistory) { this.transferHistory = transferHistory; }
}
