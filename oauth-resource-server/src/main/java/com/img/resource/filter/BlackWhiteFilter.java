package com.img.resource.filter;

import com.img.resource.utils.Image;
import com.img.resource.utils.Pixel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executor;

@Slf4j
public class BlackWhiteFilter extends AbstractFilter{

    public void applyFilterPh1(Image image, Image newImage, int start, int stop) {
        for (int i = start; i < stop; ++i) {
            for (int j = 0; j < image.width - 1; ++j) {
                int gray = (int) (0.2126 * image.matrix[i][j].r +
                        0.7152 * image.matrix[i][j].g +
                        0.0722 * image.matrix[i][j].b);
                gray = Math.min(gray, 255);
                newImage.matrix[i][j] = new Pixel((char) gray, (char) gray, (char) gray, image.matrix[i][j].a);
            }
        }
        log.debug("bw filter ph1:" + start);
    }
}
