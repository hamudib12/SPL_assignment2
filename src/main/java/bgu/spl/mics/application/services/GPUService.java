
package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private int curr_tick;
    private GPU gpu;
    private Queue<Pair<DataBatch, GPUService>> unprocessedDataBatchesQueue;
    private List<Pair<Integer, DataBatch>> processedDataBatchesQueue;
    private int ProcessedDataReceived;
    private Object lock1;
    private int unTrainedDataBatches;
    private TrainModelEvent trainEventCurrWorkingOn;


    public GPUService(GPU gpu) {
        super("GPUService");
        this.gpu = gpu;
        unprocessedDataBatchesQueue = new ConcurrentLinkedQueue<>();
        processedDataBatchesQueue = new Vector<>();
        ProcessedDataReceived = 0;
        this.lock1 = new Object();

    }

    public int getProcessedDataReceivedAndReset() {
        int val;
        synchronized (lock1) {
            val = ProcessedDataReceived;
            ProcessedDataReceived = 0;
        }
        return val;
    }

    public void incrementProcessedDataReceived() {
        synchronized (lock1) {
            ProcessedDataReceived++;
        }
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast b) -> { //get the time
            this.curr_tick = b.gettick();
            if (!processedDataBatchesQueue.isEmpty()) {
                gpu.getCluster().incrementTime_GPU_Used();
            }
            if (gpu.getModel() == null && !gpu.getEventQueue().isEmpty()) {
                trainEventCurrWorkingOn = gpu.getEventQueue().remove();
                getProcessedDataReceivedAndReset();
                gpu.setModel(trainEventCurrWorkingOn.getModel());// set the model in the gpu
                int size = trainEventCurrWorkingOn.getModel().getData().getSize();
                unTrainedDataBatches = ((int) Math.ceil(size / 1000));// num of batches
                for (int i = 0; i < unTrainedDataBatches; i++) {// dived to batches
                    unprocessedDataBatchesQueue.add(new Pair<>(new DataBatch(trainEventCurrWorkingOn.getModel().getData(), i * 1000), this));
                }
                for (int j = 0; j < 2 * gpu.getCapcity() & !unprocessedDataBatchesQueue.isEmpty(); j++) { // first sent two
                    Pair<DataBatch, GPUService> pair = unprocessedDataBatchesQueue.remove();        //times the gpu capacity
                    gpu.getCluster().getUnprocessedDataBatches().add(new Pair<>(pair.getValue(), pair.getKey()));
                }
                gpu.getModel().setStatus(Model.StatusType.Training);
            }

            for (Pair<Integer, DataBatch> pair : processedDataBatchesQueue) {
                if (curr_tick - pair.getKey() >= gpu.getProcessingTime()) {
                    processedDataBatchesQueue.remove(pair);
                    unTrainedDataBatches--;
                }
            }
            if (gpu.getCluster().getGPU_dataBatches_map().get(this) != null) {
                while (!gpu.getCluster().getGPU_dataBatches_map().get(this).isEmpty() && processedDataBatchesQueue.size() < gpu.getCapcity()) {
                    processedDataBatchesQueue.add(new Pair<>(curr_tick, gpu.getCluster().getGPU_dataBatches_map().get(this).remove()));
                    incrementProcessedDataReceived();
                }
            }
            int received = getProcessedDataReceivedAndReset();
            for (int i = 0; i < received & !unprocessedDataBatchesQueue.isEmpty(); i++) { // we sent as much as we received
                Pair<DataBatch, GPUService> pair = unprocessedDataBatchesQueue.remove();
                gpu.getCluster().getUnprocessedDataBatches().add(new Pair<>(pair.getValue(), pair.getKey()));
            }
            if (unTrainedDataBatches == 0 & trainEventCurrWorkingOn != null) {
                gpu.getModel().setStatus(Model.StatusType.Trained);
                gpu.getCluster().getModelsName_Collection().add(trainEventCurrWorkingOn.getModel().getName());
                gpu.setModel(null);
                complete(trainEventCurrWorkingOn, true);
                trainEventCurrWorkingOn = null;
            }
        });
        this.subscribeEvent(TrainModelEvent.class, (TrainModelEvent tme) -> {
            gpu.getEventQueue().add(tme);
        });
        this.subscribeEvent(TestModelEvent.class, (TestModelEvent tme) -> {
            Random rand = new Random();
            int rand_int = rand.nextInt(10);
            if (tme.getModel().getStudent().getStatus() == Student.Degree.MSc) {
                if (rand_int < 6) {
                    tme.getModel().setResults(Model.ResultsType.Good);
                    complete(tme, Model.ResultsType.Good);
                } else {
                    tme.getModel().setResults(Model.ResultsType.Bad);
                    complete(tme, Model.ResultsType.Bad);
                }

            } else {
                if (rand_int < 8) {
                    tme.getModel().setResults(Model.ResultsType.Good);
                    complete(tme, Model.ResultsType.Good);
                } else {
                    tme.getModel().setResults(Model.ResultsType.Bad);
                    complete(tme, Model.ResultsType.Bad);
                }
            }

        });
        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast e) ->
        {
            this.terminate();
            TrainModelEvent lol = trainEventCurrWorkingOn ;
            if(lol != null){
                complete(lol, false);
            }
            for(TrainModelEvent eq : gpu.getEventQueue()){
                complete(eq, false);
            }
        });
    }
}