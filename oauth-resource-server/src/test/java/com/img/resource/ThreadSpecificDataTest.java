package com.img.resource;

import com.img.resource.utils.Image;
import com.img.resource.utils.ThreadSpecificData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.Assert.assertEquals;

public class ThreadSpecificDataTest {


    @Test
    public void testEncapsulation() {
        Image input = new Image(1, 1);
        Image output = new Image(1, 1);
        int PARALLELISM = 1;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        ThreadSpecificData capsule = new ThreadSpecificData(
                PARALLELISM
                , input
                , output
                , executor
        );
        Assertions.assertEquals(input, capsule.getImage());
        Assertions.assertEquals(output, capsule.getNewImage());
        Assertions.assertEquals(PARALLELISM, capsule.getPARALLELISM());
        Assertions.assertEquals(executor, capsule.getExecutor());
    }
}
