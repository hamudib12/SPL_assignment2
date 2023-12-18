package bgu.spl.mics.application.objects;



import bgu.spl.mics.application.services.GPUService;


import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private LinkedList<GPU> GPU_Collection;
	private LinkedList<CPU> CPU_Collection;
	private Collection<String> modelsName_Collection;
	private AtomicInteger batches_Processed_ByCPU;
	private AtomicInteger time_CPU_Used;
	private AtomicInteger time_GPU_Used;
	private ConcurrentHashMap< GPUService, ConcurrentLinkedQueue<DataBatch> > GPU_dataBatches_map;
	private ConcurrentLinkedQueue<Pair<GPUService,DataBatch>> unprocessedDataBatches;
	private int curr_tick;
	private boolean terminate;
		/**
     * Retrieves the single instance of this class.
     */
	private static class ClusterHolder {
		private static Cluster instance = new Cluster();
	}
	private Cluster () {
		GPU_Collection = new LinkedList<>();
		CPU_Collection = new LinkedList<>();
		modelsName_Collection = new LinkedList<>();
		batches_Processed_ByCPU = new AtomicInteger(0);
		time_CPU_Used = new AtomicInteger(0);
		time_GPU_Used = new AtomicInteger(0);
		GPU_dataBatches_map = new ConcurrentHashMap<>();
		unprocessedDataBatches = new ConcurrentLinkedQueue<>();
		this.curr_tick = 1 ;
		terminate = false;
	}
	public static Cluster getInstance() {
		return Cluster.ClusterHolder.instance;
	}

	public int getTime_CPU_Used() {
		return time_CPU_Used.get();
	}
	public int getTime_GPU_Used() {
		return time_GPU_Used.get();
	}

	public ConcurrentHashMap<GPUService,ConcurrentLinkedQueue<DataBatch>> getGPU_dataBatches_map() {
		return GPU_dataBatches_map;
	}
	public void addGPU_dataBatches_map(GPUService gpuService, DataBatch dataBatch){
		GPU_dataBatches_map.putIfAbsent(gpuService, new ConcurrentLinkedQueue<>());
		GPU_dataBatches_map.get(gpuService).add(dataBatch);

	}

	public int getBatches_Processed_ByGPU() {
		return batches_Processed_ByCPU.get();
	}

	public LinkedList<CPU> getCPU_Collection() {
		return CPU_Collection;
	}

	public LinkedList<GPU> getGPU_Collection() {
		return GPU_Collection;
	}

	public ConcurrentLinkedQueue<Pair<GPUService,DataBatch>> getUnprocessedDataBatches() {
		return unprocessedDataBatches;
	}

	public int getBatches_Processed_ByCPU() {
		return batches_Processed_ByCPU.get();
	}

	public Collection<String> getModelsName_Collection() {
		return modelsName_Collection;
	}
	public void incrementBatches_Processed_ByCPU() {
		int val;
		do {
			val = getBatches_Processed_ByGPU();
		}while (!batches_Processed_ByCPU.compareAndSet(val, val +  1));

	}
	public void incrementTime_CPU_Used() {
		int val;
		do {
			val = getTime_CPU_Used();
		}while (!time_CPU_Used.compareAndSet(val, val +  1));
	}

	public void incrementTime_GPU_Used(){
		int val;
		do {
			val = getTime_GPU_Used();
		}while (!time_GPU_Used.compareAndSet(val, val +  1));

	}
	public Pair<GPUService,DataBatch> ifNotEmptyReturn(){
		synchronized (this){
			if (!unprocessedDataBatches.isEmpty()) {
				return unprocessedDataBatches.remove();
			}
		}
		return null;
	}

	public int getCurr_tick() {
		return curr_tick;
	}

	public void setCurr_tick(int curr_tick) {
		this.curr_tick = curr_tick;
	}
	public void setTerminate(){
		terminate = true;
	}

	public boolean isTerminate() {
		return terminate;
	}
}
