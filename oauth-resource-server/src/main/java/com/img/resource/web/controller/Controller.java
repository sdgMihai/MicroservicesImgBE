package com.img.resource.web.controller;

import com.img.resource.service.ImageFormatIO;
import com.img.resource.service.ImgSrv;
import com.img.resource.utils.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/api/")
public class Controller {
    private final ImgSrv imgSrv;
    private final ImageFormatIO imageFormatIO;

    @Autowired
    public Controller(ImgSrv imgSrv, ImageFormatIO imageFormatIO) {
        this.imgSrv = imgSrv;
        this.imageFormatIO = imageFormatIO;
    }

    @Async("executorOne")
    @PostMapping(value = "/filter", produces = MediaType.IMAGE_PNG_VALUE)
    public CompletableFuture<ResponseEntity<byte[]>> filterImage(MultipartHttpServletRequest request, @AuthenticationPrincipal Jwt principal) { //
        log.debug("debug message in image filter !");

        Iterator<String> itr = request.getFileNames();

        String userId = principal.getClaimAsString("sub");

        MultipartFile file;

        if (itr.hasNext()) {
            file = request.getFile(itr.next());
            assert file != null;
            final byte[] image;
            final CompletableFuture<ResponseEntity<byte[]>> internalServerError = CompletableFuture.completedFuture(
                    ResponseEntity.internalServerError().body(new byte[0])
            );
            try {
                image = file.getBytes();

                String[] filterNames = null;
                if (request.getParameter("filter") != null) {
                    filterNames = request.getParameter("filter").split(",");
                }
                String[] filterParams = null;
                if (request.getParameter("level") != null) {
                    filterParams = request.getParameter("level").split(",");
                }
                assert (image.length != 0);
                BufferedImage bufferedImage;
                bufferedImage = ImageIO.read(new ByteArrayInputStream(image));

                final Image input = imageFormatIO.bufferedToModelImage(bufferedImage);
                final CompletableFuture<Image> res;
                res = imgSrv.process(input, filterNames, filterParams);
                BufferedImage image1;

                final Image image2 = res.get(24, TimeUnit.SECONDS);
                log.debug("future of res obtained");
                image1 = imageFormatIO.modelToBufferedImage(image2);
                log.debug("buffered result image obtained");

                final byte[] bytes;
                bytes = imageFormatIO.bufferedToByteArray(image1);
                log.debug("bytes result image obtained");


                log.debug("imaged has been saved");
                return CompletableFuture.completedFuture(ResponseEntity.ok(bytes));
            } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                log.debug(e.getMessage());
                return internalServerError;
            }
        }

        return CompletableFuture.completedFuture(
                ResponseEntity.badRequest()
                        .body(new byte[0])
        );

    }

}
