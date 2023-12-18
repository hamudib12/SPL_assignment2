package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    String typeString;
    private int processed;
    private int size;
    private Object lock;

    public Data(int size , String typeString){
        this.size = size;
        this.typeString = typeString;
        if(typeString =="Images"){
            this.type = Type.Images;

        }else if(typeString =="Text"){
            this.type = Type.Text;
        }else{
            this.type = Type.Tabular;
        }
        processed = 0;
        this.lock = new Object();
    }

    public Type getType(){
        return type;
    }

    public int getSize(){
        return size;
    }

    public String getTypeString() {
        return typeString;
    }

    public int getProcessed(){
        synchronized (lock){
            return processed;
        }
    }

    public void incrementProcessedForDataBatch() {
        synchronized (lock){
            processed = processed + 1000;
        }
    }
}