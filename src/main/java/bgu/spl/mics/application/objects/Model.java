package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    public enum StatusType {PreTrained, Training, Trained, Tested}
    public enum ResultsType {None, Good, Bad}
    private String name;
    private Data data;
    private Student student;
    private StatusType status;
    private ResultsType results;
    public Model(String name, Data data, Student student){
        this.name= name;
        this.data = data;
        this.student = student;
        this.status = StatusType.PreTrained;
        results = ResultsType.None;
    }

    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public ResultsType getResults() {
        return results;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public void setResults(ResultsType results) {
        this.results = results;
    }
    public Boolean isTrainedOrTested(){
        return status== StatusType.Trained | status== StatusType.Tested;
    }
}
