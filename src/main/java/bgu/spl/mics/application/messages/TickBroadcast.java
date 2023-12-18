package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int tick;
    public TickBroadcast(int i) {
        tick=i;
    }
    public int gettick() {
        return tick;
    }
}
