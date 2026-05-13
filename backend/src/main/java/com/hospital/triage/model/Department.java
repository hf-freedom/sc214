package com.hospital.triage.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String id;
    private String name;
    private Integer capacity;
    private Integer currentLoad;
    private List<String> symptoms = new ArrayList<>();
    private List<String> queue = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Integer currentLoad) { this.currentLoad = currentLoad; }
    public List<String> getSymptoms() { return symptoms; }
    public void setSymptoms(List<String> symptoms) { this.symptoms = symptoms; }
    public List<String> getQueue() { return queue; }
    public void setQueue(List<String> queue) { this.queue = queue; }
}
