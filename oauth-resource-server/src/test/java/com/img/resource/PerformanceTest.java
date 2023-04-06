package com.img.resource;

import com.img.resource.filter.Filter;
import com.img.resource.filter.FilterFactory;
import com.img.resource.service.ImageFormatIO;
import com.img.resource.service.ImgSrv;
import com.img.resource.utils.Image;
import com.img.resource.utils.ThreadSpecificData;
import com.img.resource.web.controller.SpringAsyncConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

@State(Benchmark)
@SpringBootTest
@SpringJUnitConfig(classes = OauthResourceServerApplication.class)
//@Import(
//        value = {
//                ImageFormatIO.class
//        }
//)
@Slf4j
public class PerformanceTest {
    private static final int PARALLELISM = 4;
    private final ImageFormatIO imageFormatIO = new ImageFormatIO();
    Image input;
    Image output;
    Image result;

    public Executor executor;

    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(PARALLELISM);
        executor.setMaxPoolSize(PARALLELISM);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(200);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("Browser-");

        executor.initialize();
        return executor;
    }

    @Setup(Level.Invocation)
    public void init() throws IOException {
        String pwd = System.getProperty("user.dir") + "\n";

        log.debug("pwd->"+pwd);
        File inputFile = new ClassPathResource("noise.png").getFile();

        byte[] image = Files.readAllBytes(inputFile.toPath());
        assert (image.length != 0);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
        input = imageFormatIO.bufferedToModelImage(bufferedImage);
        assertNotNull(input);
        output = new Image(input.width - 2, input.height - 2);
        assertNotNull(output);
        executor = taskExecutor();

//        File outputResult = new ClassPathResource("respnoise.png").getFile();
//        byte[] resultBytes = Files.readAllBytes(outputResult.toPath());
//        result = imageFormatIO.bufferedToModelImage(
//                ImageIO.read(new ByteArrayInputStream(
//                        resultBytes
//                ))
//        );
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 0)
    @Measurement(iterations = 1)
    public void testCannyEdgeDetectionFilter()  {


        final CompletableFuture<Image> imageCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Image newImage = new Image(input.width - 2, output.height - 2);
            final Filter filter = FilterFactory.filterCreate("canny-edge-detection");

            ImgSrv.applyFilter
                    .accept(List.of(filter), new ThreadSpecificData(PARALLELISM, input, output, executor));
            return newImage;
        });

        imageCompletableFuture.join();
    }

    @TearDown(Level.Invocation)
    public void checkResult() {
        assertEquals(input.width, output.width);
        assertEquals(input.height, output.height);
//        assertEquals(result, output);
        ((ThreadPoolTaskExecutor)executor).shutdown();
    }

}
