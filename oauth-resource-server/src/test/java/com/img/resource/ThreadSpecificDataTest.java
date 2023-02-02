package com.img.resource;

import com.img.resource.utils.Barrier;
import com.img.resource.utils.DataInit;
import com.img.resource.utils.Image;
import com.img.resource.utils.ThreadSpecificData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;

public class ThreadSpecificDataTest {


    @Test
    public void testEncapsulation() {
        int threadID = 0;
        Barrier cyclicBarrier = new Barrier(1);
        Lock lock  = new ReentrantLock();
        Image input = new Image(1, 1);
        Image output = new Image(1, 1);
        int nrFilters = 1;
        int NUM_THREADS = 1;
        List<String> filterList = new ArrayList<>();
        DataInit dataInit = new DataInit();
        ThreadSpecificData capsule = new ThreadSpecificData(threadID
                , cyclicBarrier
                , lock
                , input
                , output
                , nrFilters
                , NUM_THREADS
                , filterList
                , dataInit
        );
        Assertions.assertEquals (threadID, capsule.getThread_id());
        Assertions.assertEquals(cyclicBarrier, capsule.getBarrier());
        Assertions.assertEquals(lock, capsule.getLock());
        Assertions.assertEquals(input, capsule.getImage());
        Assertions.assertEquals(output, capsule.getNewImage());
        Assertions.assertEquals(nrFilters, capsule.getNrFilters());
        Assertions.assertEquals(NUM_THREADS, capsule.getNUM_THREADS());
        Assertions.assertEquals(filterList, capsule.getFilters());
        Assertions.assertEquals(dataInit, capsule.getDataInit());
    }
}
