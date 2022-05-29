package com.img.resource.web.controller;

import com.img.resource.filter.Filters;
import com.img.resource.persistence.model.ImageUploadResponse;
import com.img.resource.persistence.model.ImgBin;
import com.img.resource.service.ImageFormatIO;
import com.img.resource.persistence.repository.ImageRepository;
import com.img.resource.service.ImgSrv;
import com.img.resource.utils.Image;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/")
public class Controller {
//    private static final Logger log = LogManager.getLogger(Controller.class);
    private static int imgID = 0;
    private final ImageRepository imageRepository;
    private final ImgSrv imgSrv;
    private final ImageFormatIO imageFormatIO;

    @Autowired
    public Controller(ImageRepository imageRepository, ImgSrv imgSrv, ImageFormatIO imageFormatIO) {
        this.imageRepository = imageRepository;
        this.imgSrv = imgSrv;
        this.imageFormatIO = imageFormatIO;
    }

    @CrossOrigin(origins = "http://localhost:8089")
    @PostMapping(value = "/filter", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> filterImage(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> itr = request.getFileNames();
        log.debug("logging parsed answer");
        log.debug("it has params in it: " + itr.hasNext());
        MultipartFile file;

        if (itr.hasNext()) {
            final String paramName = itr.next();
            log.debug("paramName : " + paramName);
            file = request.getFile(paramName);
            assert file != null;
            final byte[] image = file.getBytes();

            final String filter = request.getParameter("filter");
            List<String> argv = new java.util.ArrayList<>(List.of(filter));

            // filter is { brightness | contrast }
            if (filter.toLowerCase(Locale.ROOT)
                    .equals(Filters.BRIGHTNESS.toString().toLowerCase(Locale.ROOT))
                || filter.toLowerCase(Locale.ROOT)
                    .equals(Filters.CONTRAST.toString().toLowerCase(Locale.ROOT))) {
                argv.add(request.getParameter("level"));
                log.debug("level added: " + request.getParameter("level"));
                final double level = Double.parseDouble(request.getParameter("level"));
                log.debug("level added(double): " + argv.get(1));
            }

            assert (image.length != 0);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));

            final Image input = imageFormatIO.bufferedToModelImage(bufferedImage);
            final Image res = imgSrv.process(input, argv);
            final BufferedImage image1 = imageFormatIO.modelToBufferedImage(res);
            final byte[] bytes = imageFormatIO.bufferedToByteArray(image1);
            return ResponseEntity.ok(bytes);
        }

        return null;
    }

    @CrossOrigin("http://localhost:8089")
    @GetMapping(value = "/test")
    public ResponseEntity<String> filterImage() {
        return ResponseEntity.ok("test");
    }

}
