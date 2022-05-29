package com.img.resource.utils;


import com.img.resource.filter.FilterAdditionalData;
import lombok.AllArgsConstructor;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;

@AllArgsConstructor
public class ThreadSpecificDataT implements FilterAdditionalData {
    public int threadID;
    public CyclicBarrier barrier;
    public final Lock mutex;
    public int NUM_THREADS;
}
