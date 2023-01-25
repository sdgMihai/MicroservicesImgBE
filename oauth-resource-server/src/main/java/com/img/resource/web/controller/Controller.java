package com.img.resource.web.controller;

import com.img.resource.persistence.ImageRepository;
import com.img.resource.persistence.model.ImageEntity;
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
import org.springframework.web.bind.annotation.GetMapping;
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

@Slf4j
@RestController
@RequestMapping("/api/")
public class Controller {
    private final ImgSrv imgSrv;
    private final ImageFormatIO imageFormatIO;
    private final ImageRepository imgRepo;

    @Autowired
    public Controller(ImgSrv imgSrv, ImageFormatIO imageFormatIO, ImageRepository imgRepo) {
        this.imgSrv = imgSrv;
        this.imageFormatIO = imageFormatIO;
        this.imgRepo = imgRepo;
    }

    @Async
    @PostMapping(value = "/filter", produces = MediaType.IMAGE_PNG_VALUE)
    public CompletableFuture<ResponseEntity<byte[]>> filterImage(MultipartHttpServletRequest request, @AuthenticationPrincipal Jwt principal) {
        Iterator<String> itr = request.getFileNames();

        MultipartFile file;

        if (itr.hasNext()) {
            file = request.getFile(itr.next());
            assert file != null;
            final byte[] image;
            try {
                image = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
                return CompletableFuture.completedFuture(ResponseEntity.internalServerError()
                        .build());
            }

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
            try {
                bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
            } catch (IOException e) {
                e.printStackTrace();
                log.debug(e.getMessage());
                return CompletableFuture.completedFuture(ResponseEntity.internalServerError()
                        .build());
            }

            final Image input = imageFormatIO.bufferedToModelImage(bufferedImage);
            final CompletableFuture<Image> res;
            res = imgSrv.process(input, filterNames, filterParams);
            log.debug("future of res obtained");
            BufferedImage image1;
            try {
                image1 = imageFormatIO.modelToBufferedImage(res.get());
                log.debug("buffered result image obtained");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                log.debug(e.getMessage());
                return CompletableFuture.completedFuture(ResponseEntity.internalServerError()
                        .build());
            }
            final byte[] bytes;
            try {
                bytes = imageFormatIO.bufferedToByteArray(image1);
                log.debug("bytes result image obtained");
            } catch (IOException e) {
                e.printStackTrace();
                log.debug(e.getMessage());
                return CompletableFuture.completedFuture(ResponseEntity.internalServerError()
                        .build());
            }
            log.debug("ready result image");

            String userId = principal.getClaimAsString("sub");
            final ImageEntity imageEntity = new ImageEntity();
            log.debug("save res image to db");
            imageEntity.setContent(bytes);
            imageEntity.setUserId(userId);
            log.debug("image usedId:" + imageEntity.getUserId());
            log.debug("image content size:" + imageEntity.getContent().length);
            imgRepo.save(imageEntity);
            if (imgRepo.findByUserId(userId).size() < 1) {
                log.debug("cannot save image");
                return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
            }

            log.debug("imaged has been saved");
            return CompletableFuture.completedFuture(ResponseEntity.ok(bytes));
        }

        return null;
    }

    @Async
    @GetMapping(value = "/filter/ret")
    public CompletableFuture<ResponseEntity<byte[]>> returnImage( @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getClaimAsString("sub");
        final byte[] content = imgRepo.findByUserId(userId)
                .get(0)
                .getContent();
        log.debug("got image from db");
        return CompletableFuture.completedFuture(ResponseEntity.ok(content));
    }
}
