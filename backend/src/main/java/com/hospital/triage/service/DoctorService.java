package com.hospital.triage.service;

import com.hospital.triage.model.Department;
import com.hospital.triage.model.Doctor;
import com.hospital.triage.model.Patient;
import com.hospital.triage.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    public List<Doctor> getAllDoctors() {
        return DataStorage.DOCTORS.values().stream().collect(Collectors.toList());
    }

    public List<Doctor> getDoctorsByDepartment(String departmentId) {
        return DataStorage.DOCTORS.values().stream()
                .filter(d -> departmentId.equals(d.getDepartmentId()))
                .collect(Collectors.toList());
    }

    public Doctor getDoctorById(String id) {
        return DataStorage.DOCTORS.get(id);
    }

    public Patient startConsultation(String doctorId, String patientId) {
        Doctor doctor = DataStorage.DOCTORS.get(doctorId);
        Patient patient = DataStorage.PATIENTS.get(patientId);
        
        if (doctor == null || patient == null) {
            return null;
        }
        
        if (!"空闲".equals(doctor.getStatus())) {
            return null;
        }
        
        Department dept = DataStorage.DEPARTMENTS.get(patient.getDepartmentId());
        if (dept != null) {
            dept.getQueue().remove(patientId);
        }
        
        doctor.setStatus("诊疗中");
        doctor.setCurrentPatientId(patientId);
        
        patient.setStatus("诊疗中");
        patient.setDoctorId(doctorId);
        patient.setStartTime(LocalDateTime.now());
        
        return patient;
    }

    public Patient endConsultation(String doctorId, String patientId) {
        Doctor doctor = DataStorage.DOCTORS.get(doctorId);
        Patient patient = DataStorage.PATIENTS.get(patientId);
        
        if (doctor == null || patient == null) {
            return null;
        }
        
        doctor.setStatus("空闲");
        doctor.setCurrentPatientId(null);
        
        patient.setStatus("已完成");
        patient.setEndTime(LocalDateTime.now());
        
        return patient;
    }

    public Patient getNextPatient(String doctorId) {
        Doctor doctor = DataStorage.DOCTORS.get(doctorId);
        if (doctor == null) {
            return null;
        }
        
        Department dept = DataStorage.DEPARTMENTS.get(doctor.getDepartmentId());
        if (dept == null || dept.getQueue().isEmpty()) {
            return null;
        }
        
        String patientId = dept.getQueue().get(0);
        return DataStorage.PATIENTS.get(patientId);
    }
}
