package com.hospital.triage.controller;

import com.hospital.triage.model.Doctor;
import com.hospital.triage.model.Patient;
import com.hospital.triage.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:3007")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/list")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Doctor>> getDoctorsByDepartment(@PathVariable String departmentId) {
        return ResponseEntity.ok(doctorService.getDoctorsByDepartment(departmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable String id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return doctor != null ? ResponseEntity.ok(doctor) : ResponseEntity.notFound().build();
    }

    @PostMapping("/start")
    public ResponseEntity<Patient> startConsultation(@RequestBody Map<String, String> request) {
        String doctorId = request.get("doctorId");
        String patientId = request.get("patientId");
        Patient patient = doctorService.startConsultation(doctorId, patientId);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.badRequest().build();
    }

    @PostMapping("/end")
    public ResponseEntity<Patient> endConsultation(@RequestBody Map<String, String> request) {
        String doctorId = request.get("doctorId");
        String patientId = request.get("patientId");
        Patient patient = doctorService.endConsultation(doctorId, patientId);
        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/next/{doctorId}")
    public ResponseEntity<Patient> getNextPatient(@PathVariable String doctorId) {
        Patient patient = doctorService.getNextPatient(doctorId);
        return ResponseEntity.ok(patient);
    }
}
