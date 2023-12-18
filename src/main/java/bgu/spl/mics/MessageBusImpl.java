package bgu.spl.mics;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */



public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<Event,Future> FutureOfEvent = new ConcurrentHashMap<>();
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> QueuesOfMicros = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> BroadCastsMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> EventsMap = new ConcurrentHashMap<>();

	public boolean isSubscribeBroadcast(Class<ExampleBroadcast> exampleBroadcastClass, MicroService m) {
		return BroadCastsMap.containsKey(exampleBroadcastClass) && BroadCastsMap.contains(m);
	}

	public Future getResult(ExampleEvent e) {
		return this.FutureOfEvent.get(e);
	}

	private static class MessageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl () {}
	public static  MessageBusImpl getInstance(){
		return MessageBusHolder.instance;
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
			ConcurrentLinkedQueue<MicroService> MicroServicesQueue = new ConcurrentLinkedQueue();
			EventsMap.putIfAbsent(type, MicroServicesQueue);
			EventsMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		ConcurrentLinkedQueue<MicroService> MicroServicesQueue = new ConcurrentLinkedQueue();
		BroadCastsMap.putIfAbsent(type, MicroServicesQueue);
		BroadCastsMap.get(type).add(m);

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		FutureOfEvent.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		ConcurrentLinkedQueue<MicroService> MicrosQ;
		MicrosQ = BroadCastsMap.get(b.getClass());
		if (MicrosQ == null) {
			return;
		}
		MicrosQ.forEach(MicroService -> {
			LinkedBlockingQueue<Message> q2 = QueuesOfMicros.get(MicroService);
			if (q2 != null) {
				q2.add(b);
			}
		});
	}



	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		Future future = new Future();
		FutureOfEvent.put(e,future);
		ConcurrentLinkedQueue<MicroService> MicroServiceQ = EventsMap.get(e.getClass()); // get the micro for e.
		MicroService micro;
		if (MicroServiceQ == null)
			return null;
		synchronized (e.getClass()) { //round robin
			if (MicroServiceQ.isEmpty()) {
				return null;
			}
			micro = MicroServiceQ.poll();
			MicroServiceQ.add(micro);
		}
		synchronized (micro) {
			LinkedBlockingQueue<Message> MessageQ = QueuesOfMicros.get(micro); // adding e to the matching micro
			if (MessageQ == null) {
				return null;
			}
			MessageQ.add(e);
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub
		QueuesOfMicros.put(m,new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub
		for (Message e : QueuesOfMicros.get(m)){
			synchronized (m){
				if (e instanceof Event) {
					try {
						EventsMap.get(e).remove(m);
					}catch (NullPointerException ne){
					}

				}
				if(e instanceof Broadcast) {
					try {
						BroadCastsMap.get(e).remove(m);
					}catch (NullPointerException ne){
					}
				}
			}
		}
		LinkedBlockingQueue<Message> QOfMesssages;
		synchronized (m) {
			QOfMesssages = QueuesOfMicros.remove(m);
		}
		while (!QOfMesssages.isEmpty()) {
			Message me = QOfMesssages.poll();
			Future<?> future = FutureOfEvent.get(me);
			if (future != null)
				future.resolve(null);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return QueuesOfMicros.get(m).take();
	}
	public Boolean isSubscribeEvent(Class<? extends Event> eClass, MicroService m){
		return EventsMap.containsKey(eClass) && EventsMap.contains(m);

	}



}