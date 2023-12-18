package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.List;

public class PublishConfrenceBroadcast implements Broadcast {
    List<Model> list;
    public PublishConfrenceBroadcast(List<Model> list){
        this.list = list;
    }

    public List<Model> getList() {
        return list;
    }
}
