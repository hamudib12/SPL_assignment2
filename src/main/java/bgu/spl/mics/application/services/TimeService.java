package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	final private int speed;
	private int duration;
	private Timer timer ;
	private int current;
	private TimerTask timerTask;
	private static class SingletonHolder {
		private static TimeService instance ;
	}
	public static TimeService getInstance(){
		return SingletonHolder.instance;
	}
	public TimeService(int duration, int speed) {
		super("TimeService");
		this.duration=duration;
		current=1;
		this.speed=speed;
		SingletonHolder.instance = this;
	}

	@Override
	protected void initialize() {
		while (current<duration) {
			Cluster.getInstance().setCurr_tick(current);
			sendBroadcast(new TickBroadcast(current));
			try {
				Thread.sleep(speed);
				current++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendBroadcast(new TerminateBroadcast());

		terminate();
		
	}

}
