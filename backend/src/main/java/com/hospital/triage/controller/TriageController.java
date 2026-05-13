package com.hospital.triage.controller;

import com.hospital.triage.model.Department;
import com.hospital.triage.model.Patient;
import com.hospital.triage.service.TriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/triage")
@CrossOrigin(origins = "http://localhost:3007")
public class TriageController {

    @Autowired
    private TriageService triageService;

    @PostMapping("/register")
    public ResponseEntity<Patient> registerPatient(@RequestBody Patient patient) {
        Patient registered = triageService.registerPatient(patient);
        return ResponseEntity.ok(registered);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(triageService.getAllPatients());
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        Patient patient = triageService.getPatientById(id);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(triageService.getAllDepartments());
    }

    @PutMapping("/patients/{id}/priority")
    public ResponseEntity<Patient> changePriority(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        int newPriority = (int) request.get("newPriority");
        String reason = (String) request.get("reason");
        String operator = (String) request.getOrDefault("operator", "系统");
        Patient patient = triageService.changePriority(id, newPriority, reason, operator);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }

    @PostMapping("/patients/{id}/transfer")
    public ResponseEntity<Patient> transferPatient(
            @PathVariable String id,
            @RequestBody Map<String, Object> request) {
        String toDepartmentId = (String) request.get("toDepartmentId");
        String reason = (String) request.get("reason");
        String operator = (String) request.getOrDefault("operator", "医生");
        Patient patient = triageService.transferPatient(id, toDepartmentId, reason, operator);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.notFound().build();
    }

    @GetMapping("/timeout")
    public ResponseEntity<List<Patient>> getTimeoutPatients() {
        return ResponseEntity.ok(triageService.getTimeoutPatients());
    }
}
