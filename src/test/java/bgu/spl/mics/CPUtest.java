package bgu.spl.mics;

import bgu.spl.mics.application.objects.CPU;

import bgu.spl.mics.application.objects.GPU;

import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;



import static org.junit.jupiter.api.Assertions.*;

class CPUTest {

    private static CPU cpu;


    @BeforeEach
    void setUp() {
        cpu = new CPU(32);
    }


    @Test
    void getCores() {
        assertEquals(cpu.getCores(),32);
    }




}