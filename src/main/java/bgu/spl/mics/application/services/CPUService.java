package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.Pair;


/**
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private int curr_tick;
    private CPU cpu;
    private Pair<Integer, Pair<GPUService, DataBatch>> processingDataBatche;

    public CPUService(CPU cpu) {
        super("CPUService");
        this.cpu = cpu;
        processingDataBatche = null;

    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast b) -> { //get the time
            this.curr_tick = b.gettick();
            if (processingDataBatche != null) {
                cpu.getCluster().incrementTime_CPU_Used();
                if (curr_tick - processingDataBatche.getKey() >= cpu.getProcessingTime(processingDataBatche.getValue().getValue())) {
                    cpu.getCluster().incrementBatches_Processed_ByCPU();
                    cpu.getCluster().addGPU_dataBatches_map(processingDataBatche.getValue().getKey(), processingDataBatche.getValue().getValue());
                    processingDataBatche.getValue().getValue().getData().incrementProcessedForDataBatch();
                    processingDataBatche = null;

                }
            } else {
                Pair<GPUService, DataBatch> pair = cpu.getCluster().ifNotEmptyReturn();
                if (pair != null) {
                    processingDataBatche = new Pair<>(curr_tick, pair);
                }
            }
        });
        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast e) ->
        {
            this.terminate();
        });
    }
}
