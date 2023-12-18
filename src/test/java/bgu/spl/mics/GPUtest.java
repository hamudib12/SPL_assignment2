package bgu.spl.mics;

import bgu.spl.mics.application.objects.GPU;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bgu.spl.mics.application.objects.Model;
import org.junit.jupiter.api.BeforeEach;


class GPUTest {
    private static GPU gpu;

    @BeforeEach
    void setUp() {
        gpu = new GPU("RTX3090");
    }



    @Test
    void getModel() {
        assertEquals( gpu.getModel(), null );
        Model a = new Model("momo",null,null);
        gpu.setModel(a);
        assertEquals(gpu.getModel(),a);
    }



}