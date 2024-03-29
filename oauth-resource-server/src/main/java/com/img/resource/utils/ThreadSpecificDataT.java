package com.img.resource.utils;


import com.img.resource.filter.FilterAdditionalData;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ThreadSpecificDataT implements FilterAdditionalData {
    public int threadID;
    public Barrier barrier;
    public final Object mutex;
    public int NUM_THREADS;
    public DataInit dataInit;
}
