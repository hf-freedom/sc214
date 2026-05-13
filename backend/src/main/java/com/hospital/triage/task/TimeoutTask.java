package com.hospital.triage.task;

import com.hospital.triage.model.Patient;
import com.hospital.triage.service.TriageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimeoutTask {

    private static final Logger logger = LoggerFactory.getLogger(TimeoutTask.class);

    @Autowired
    private TriageService triageService;

    @Scheduled(fixedRate = 60000)
    public void checkTimeoutPatients() {
        List<Patient> timeoutPatients = triageService.getTimeoutPatients();
        if (!timeoutPatients.isEmpty()) {
            logger.warn("发现 {} 位超时候诊患者", timeoutPatients.size());
            for (Patient patient : timeoutPatients) {
                logger.warn("超时患者: ID={}, 姓名={}, 科室={}, 候诊时间已超过30分钟",
                        patient.getId(), patient.getName(), patient.getDepartmentId());
            }
        }
    }
}
