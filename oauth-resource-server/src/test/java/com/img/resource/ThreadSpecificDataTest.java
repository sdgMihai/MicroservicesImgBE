package com.img.resource;

import com.img.resource.utils.Image;
import com.img.resource.utils.ThreadSpecificData;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class ThreadSpecificDataTest {
    @Test
    public void testEncapsulation() {
        Image input = new Image(1, 1);
        Image output = new Image(1, 1);
        int PARALLELISM = 1;
        ThreadSpecificData capsule = new ThreadSpecificData(
                PARALLELISM
                , input
                , output
        );
        assertEquals(input, capsule.getImage());
        assertEquals(output, capsule.getNewImage());
        assertEquals(PARALLELISM, capsule.getPARALLELISM());
    }
}
