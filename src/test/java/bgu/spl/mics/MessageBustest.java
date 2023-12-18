package bgu.spl.mics;

import bgu.spl.mics.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;

class MessageBusImplTest {

    private static MessageBusImpl messageBus;
    private static MicroService m;
    private static ExampleEvent e;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        Student temp = new Student("a", 1,"CCofC", "MSc");
        LinkedList<Model> temp2 = new LinkedList<>();
        m = new StudentService(temp);
        e = new ExampleEvent("sugar");
    }

    @Test
    void subscribeEvent() {
        messageBus.register(m);
        messageBus.subscribeEvent(ExampleEvent.class, m);
        assertTrue(messageBus.isSubscribeEvent(ExampleEvent.class,m));
    }

    @Test
    void subscribeBroadcast() {
        messageBus.register(m);
        messageBus.subscribeBroadcast(ExampleBroadcast.class,m);
        assertTrue(messageBus.isSubscribeBroadcast(ExampleBroadcast.class,m));
    }

    @Test
    void complete() {
        messageBus.register(m);
        messageBus.subscribeEvent(ExampleEvent.class, m);
        messageBus.sendEvent(e);
        messageBus.complete(e,"");
        assertEquals( messageBus.getResult(e).get(),"");

    }

    @Test
    void sendBroadcast() throws InterruptedException {
        messageBus.register(m);
        messageBus.subscribeBroadcast(ExampleBroadcast.class,m);
        ExampleBroadcast b = new ExampleBroadcast("1");
        messageBus.sendBroadcast(b);
        assertEquals(messageBus.awaitMessage(m),b);
    }

    @Test
    void sendEvent() throws InterruptedException {
        messageBus.register(m);
        messageBus.subscribeEvent(ExampleEvent.class,m);
        messageBus.sendEvent(e);
        assertEquals(messageBus.awaitMessage(m),e);

    }



    @Test
    void awaitMessage() throws InterruptedException {
        messageBus.register(m);
        messageBus.subscribeEvent(ExampleEvent.class,m);
        messageBus.sendEvent(e);
        assertEquals(messageBus.awaitMessage(m),e);

    }
}