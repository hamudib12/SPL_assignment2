package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.LinkedList;
/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    int cores;
    Cluster cluster;
    Collection<DataBatch> data;
    public CPU(int cores){
        this.cores = cores;
        cluster = Cluster.getInstance();
        data = new LinkedList<DataBatch>();
    }
    public int getCores(){
        return cores;
    }

    public Collection<DataBatch> getData() {
        return data;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public int getProcessingTime(DataBatch databatch){
        if(databatch.getData().getType().equals(Data.Type.Images) ){
            return  (32/ cores)*4;
        }
        else if(databatch.getData().getType().equals(Data.Type.Text) ){
            return  (32/ cores)*2;
        }
        else {
            return  (32/ cores);
        }
    }
    public int getTime_CPU_Used(){
        return cluster.getTime_CPU_Used();
    }

}
