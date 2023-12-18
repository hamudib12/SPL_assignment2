package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private  int curr_tick;
    private ConfrenceInformation conference;

    public ConferenceService(ConfrenceInformation conference) {
        super("ConferenceService");
        this.conference = conference;


    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast b) -> { //get the time
            this.curr_tick = b.gettick();
            if(curr_tick == conference.getDate()){
                this.sendBroadcast(new PublishConfrenceBroadcast(conference.getList()));// publish the good results
                this.terminate();
            }
        });
        this.subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent pre) -> {// receive results
            conference.getList().add(pre.getModel());
            complete(pre, pre.getModel());
        });
        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast e) ->
        {
            this.terminate();
        });

    }
}
