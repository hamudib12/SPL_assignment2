package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    public StudentService(Student student) {
        super("StudentService");
        this.student = student;
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(PublishConfrenceBroadcast.class, (PublishConfrenceBroadcast pcb) -> {
            for(Model model: pcb.getList()){
                if(!student.getName().equals(model.getStudent().getName())){
                    student.incrementPapersRead();
                }
                else {
                    student.incrementPublications();
                }
            }
        });
        Thread t = new Thread(()-> {
            for (Model model : student.getModelsList()) {
                try {
                    Boolean gotTrained = sendEvent(new TrainModelEvent(model)).get();
                    if (gotTrained != null && gotTrained) {
                        Future<Model.ResultsType> futureTest = sendEvent(new TestModelEvent(model));
                        Model.ResultsType temp = futureTest.get();
                        model.setStatus(Model.StatusType.Tested);
                        if (temp == Model.ResultsType.Good) {
                            sendEvent(new PublishResultsEvent(model));
                        }
                        break;
                    }
                } catch (NullPointerException ne) {
                    break;
                }
            }
        });
        t.start();

        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast e) ->
        {
            Cluster.getInstance().setTerminate();
            this.terminate();
        });


    }
}
