package com.hospital.triage.service;

import com.hospital.triage.model.Department;
import com.hospital.triage.model.Patient;
import com.hospital.triage.model.PriorityChangeRecord;
import com.hospital.triage.model.TransferRecord;
import com.hospital.triage.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TriageService {

    public Patient registerPatient(Patient patient) {
        patient.setId(UUID.randomUUID().toString());
        patient.setRegisterTime(LocalDateTime.now());
        patient.setStatus("候诊中");
        
        int priority = calculatePriority(patient);
        patient.setPriority(priority);
        
        String departmentId = assignDepartment(patient);
        patient.setDepartmentId(departmentId);
        
        DataStorage.PATIENTS.put(patient.getId(), patient);
        
        Department dept = DataStorage.DEPARTMENTS.get(departmentId);
        addToQueue(dept, patient);
        
        return patient;
    }

    private int calculatePriority(Patient patient) {
        int basePriority = 0;
        
        switch (patient.getEmergencyLevel()) {
            case "危急":
                basePriority = 100;
                break;
            case "紧急":
                basePriority = 70;
                break;
            case "普通":
                basePriority = 30;
                break;
            default:
                basePriority = 10;
        }
        
        if (patient.getAge() > 65 || patient.getAge() < 12) {
            basePriority += 20;
        }
        
        String symptom = patient.getSymptom();
        if (symptom.contains("胸痛") || symptom.contains("呼吸困难") || symptom.contains("昏迷")) {
            basePriority += 30;
        }
        
        return Math.min(basePriority, 100);
    }

    private String assignDepartment(Patient patient) {
        String symptom = patient.getSymptom();
        
        if (patient.getAge() < 14) {
            return "DEPT004";
        }
        
        for (Department dept : DataStorage.DEPARTMENTS.values()) {
            for (String s : dept.getSymptoms()) {
                if (symptom.contains(s)) {
                    return dept.getId();
                }
            }
        }
        
        return "DEPT002";
    }

    private void addToQueue(Department dept, Patient patient) {
        List<String> queue = dept.getQueue();
        int insertIndex = 0;
        for (String patientId : queue) {
            Patient p = DataStorage.PATIENTS.get(patientId);
            if (p != null && p.getPriority() >= patient.getPriority()) {
                insertIndex++;
            } else {
                break;
            }
        }
        queue.add(insertIndex, patient.getId());
    }

    public List<Patient> getAllPatients() {
        return DataStorage.PATIENTS.values().stream()
                .sorted((a, b) -> b.getPriority() - a.getPriority())
                .collect(Collectors.toList());
    }

    public List<Department> getAllDepartments() {
        return DataStorage.DEPARTMENTS.values().stream().collect(Collectors.toList());
    }

    public Patient getPatientById(String id) {
        return DataStorage.PATIENTS.get(id);
    }

    public Patient changePriority(String patientId, int newPriority, String reason, String operator) {
        Patient patient = DataStorage.PATIENTS.get(patientId);
        if (patient == null) {
            return null;
        }
        
        PriorityChangeRecord record = new PriorityChangeRecord();
        record.setOldPriority(patient.getPriority());
        record.setNewPriority(newPriority);
        record.setReason(reason);
        record.setOperator(operator);
        record.setChangeTime(LocalDateTime.now());
        patient.getPriorityHistory().add(record);
        
        patient.setPriority(newPriority);
        
        Department dept = DataStorage.DEPARTMENTS.get(patient.getDepartmentId());
        if (dept != null) {
            dept.getQueue().remove(patientId);
            addToQueue(dept, patient);
        }
        
        return patient;
    }

    public Patient transferPatient(String patientId, String toDepartmentId, String reason, String operator) {
        Patient patient = DataStorage.PATIENTS.get(patientId);
        if (patient == null) {
            return null;
        }
        
        String fromDepartmentId = patient.getDepartmentId();
        Department fromDept = DataStorage.DEPARTMENTS.get(fromDepartmentId);
        if (fromDept != null) {
            fromDept.getQueue().remove(patientId);
        }
        
        TransferRecord record = new TransferRecord();
        record.setFromDepartmentId(fromDepartmentId);
        record.setToDepartmentId(toDepartmentId);
        record.setReason(reason);
        record.setOperator(operator);
        record.setTransferTime(LocalDateTime.now());
        patient.getTransferHistory().add(record);
        
        patient.setDepartmentId(toDepartmentId);
        patient.setStatus("候诊中");
        patient.setDoctorId(null);
        
        Department toDept = DataStorage.DEPARTMENTS.get(toDepartmentId);
        if (toDept != null) {
            addToQueue(toDept, patient);
        }
        
        return patient;
    }

    public List<Patient> getTimeoutPatients() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        return DataStorage.PATIENTS.values().stream()
                .filter(p -> "候诊中".equals(p.getStatus()))
                .filter(p -> p.getRegisterTime().isBefore(threshold))
                .collect(Collectors.toList());
    }
}
