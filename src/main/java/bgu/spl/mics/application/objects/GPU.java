package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.TrainModelEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    private final Type type;
    private Model model;
    private final Cluster cluster;
    private final int processing_time;
    private final int capcity;
    private Queue<TrainModelEvent> eventQueue;
    public GPU(String typeString){
        if(typeString.equals("RTX3090")){
            this.type = Type.RTX3090;
            processing_time = 1;
            capcity = 32;

        }else if(typeString.equals("RTX2080")){
            this.type = Type.RTX2080;
            processing_time = 2;
            capcity = 16;
        }else{
            this.type = Type.GTX1080;
            processing_time = 4;
            capcity= 8;
        }
        model = null;
        cluster = Cluster.getInstance();
        eventQueue = new LinkedList<>();
    }

    public Type getType(){
        return type;
    }

    public Model getModel() {
        return model;
    }


    public void setModel(Model model) {
        this.model = model;
    }

    public Cluster getCluster() {
        return cluster;
    }
    public int getTime_GPU_Used(){
        return cluster.getTime_GPU_Used();
    }

    public int getProcessingTime(){
        return processing_time;
    }
    public int getCapcity(){
        return capcity;
    }

    public Queue<TrainModelEvent> getEventQueue() {
        return eventQueue;
    }

    public void dataBatchesCreate(){


    }

    public void trainModel(){

    }
}
