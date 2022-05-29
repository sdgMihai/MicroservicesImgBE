package com.img.resource;

import com.img.resource.service.ImageFormatIO;
import com.img.resource.service.ImgSrv;
import com.img.resource.utils.Image;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.*;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

@State(Benchmark)
@ExtendWith(SpringExtension.class)
@Import(
        value = {
                ImageFormatIO.class
        }
)
@Disabled
public class PerformanceTest {
    Image input;
    Image output;
    Image result;
    private final ImageFormatIO imageFormatIO = new ImageFormatIO();

    @Setup(Level.Invocation)
    public void init() throws IOException {
        File inputFile = new ClassPathResource("noise.png").getFile();
        byte[] image = Files.readAllBytes(inputFile.toPath());
        assert (image.length != 0);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
        input = imageFormatIO.bufferedToModelImage(bufferedImage);
        assertNotNull(input);
        output = new Image(input.width - 2, input.height - 2);
        assertNotNull(output);

        File outputResult = new ClassPathResource("respnoise.png").getFile();
        byte[] resultBytes = Files.readAllBytes(outputResult.toPath());
        result = imageFormatIO.bufferedToModelImage(
                ImageIO.read(new ByteArrayInputStream(
                        resultBytes
                ))
        );
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 2)
    @Measurement(iterations = 4)
    public void testCannyEdgeDetectionFilter() {
        ImgSrv imgSrv = new ImgSrv();
        output = imgSrv.process(input, List.of("canny-edge-detection"));
    }

    @TearDown(Level.Invocation)
    public void checkResult() {
        assertEquals(input.width, output.width);
        assertEquals(input.height, output.height);
        assertEquals(result, output);
    }

}
