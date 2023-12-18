package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private int id; // every student have a unique id
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private List<Model> modelsList;
    private Object lock1;
    private Object lock2;
    public Student(String name, int id, String department, String statusString){
        this.name = name;
        this.id = id;
        this.department= department;
        if(statusString =="MSc") {
            this.status = Degree.MSc;
        }else{
            this.status = Degree.PhD;
        }
        this.modelsList = null;
        this.lock1 = new Object();
        this.lock2 = new Object();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Degree getStatus() {
        return status;
    }

    public String getDepartment() {
        return department;
    }

    public int getPapersRead() {
        synchronized (lock1){
            return papersRead;
        }
    }

    public int getPublications() {
        synchronized (lock2){
            return publications;
        }
    }

    public List<Model> getModelsList() {
        return modelsList;
    }

    public void setModelsList(List<Model> modelsList) {
        this.modelsList = modelsList;
    }

    public void incrementPapersRead() {
        synchronized (lock1){
            this.papersRead++;
        }

    }

    public void incrementPublications() {
        synchronized (lock2) {
            this.publications++;
        }
    }
}
