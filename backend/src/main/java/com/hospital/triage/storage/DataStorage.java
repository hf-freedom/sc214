package com.hospital.triage.storage;

import com.hospital.triage.model.Department;
import com.hospital.triage.model.Doctor;
import com.hospital.triage.model.Patient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStorage {
    public static final Map<String, Patient> PATIENTS = new ConcurrentHashMap<>();
    public static final Map<String, Department> DEPARTMENTS = new ConcurrentHashMap<>();
    public static final Map<String, Doctor> DOCTORS = new ConcurrentHashMap<>();

    static {
        initDepartments();
        initDoctors();
    }

    private static void initDepartments() {
        Department dept1 = new Department();
        dept1.setId("DEPT001");
        dept1.setName("急诊科");
        dept1.setCapacity(10);
        dept1.setCurrentLoad(0);
        dept1.getSymptoms().add("胸痛");
        dept1.getSymptoms().add("呼吸困难");
        dept1.getSymptoms().add("昏迷");
        dept1.getSymptoms().add("外伤");
        DEPARTMENTS.put(dept1.getId(), dept1);

        Department dept2 = new Department();
        dept2.setId("DEPT002");
        dept2.setName("内科");
        dept2.setCapacity(8);
        dept2.setCurrentLoad(0);
        dept2.getSymptoms().add("发热");
        dept2.getSymptoms().add("咳嗽");
        dept2.getSymptoms().add("头痛");
        dept2.getSymptoms().add("腹痛");
        DEPARTMENTS.put(dept2.getId(), dept2);

        Department dept3 = new Department();
        dept3.setId("DEPT003");
        dept3.setName("外科");
        dept3.setCapacity(6);
        dept3.setCurrentLoad(0);
        dept3.getSymptoms().add("骨折");
        dept3.getSymptoms().add("创伤");
        dept3.getSymptoms().add("肿块");
        DEPARTMENTS.put(dept3.getId(), dept3);

        Department dept4 = new Department();
        dept4.setId("DEPT004");
        dept4.setName("儿科");
        dept4.setCapacity(5);
        dept4.setCurrentLoad(0);
        dept4.getSymptoms().add("儿童发热");
        dept4.getSymptoms().add("儿童咳嗽");
        dept4.getSymptoms().add("儿童腹泻");
        DEPARTMENTS.put(dept4.getId(), dept4);
    }

    private static void initDoctors() {
        Doctor doc1 = new Doctor();
        doc1.setId("DOC001");
        doc1.setName("张医生");
        doc1.setDepartmentId("DEPT001");
        doc1.setStatus("空闲");
        DOCTORS.put(doc1.getId(), doc1);

        Doctor doc2 = new Doctor();
        doc2.setId("DOC002");
        doc2.setName("李医生");
        doc2.setDepartmentId("DEPT001");
        doc2.setStatus("空闲");
        DOCTORS.put(doc2.getId(), doc2);

        Doctor doc3 = new Doctor();
        doc3.setId("DOC003");
        doc3.setName("王医生");
        doc3.setDepartmentId("DEPT002");
        doc3.setStatus("空闲");
        DOCTORS.put(doc3.getId(), doc3);

        Doctor doc4 = new Doctor();
        doc4.setId("DOC004");
        doc4.setName("赵医生");
        doc4.setDepartmentId("DEPT003");
        doc4.setStatus("空闲");
        DOCTORS.put(doc4.getId(), doc4);

        Doctor doc5 = new Doctor();
        doc5.setId("DOC005");
        doc5.setName("刘医生");
        doc5.setDepartmentId("DEPT004");
        doc5.setStatus("空闲");
        DOCTORS.put(doc5.getId(), doc5);
    }
}
