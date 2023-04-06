package com.img.resource.filter;

import com.img.resource.utils.Image;
import com.img.resource.utils.Pixel;

public class EmbossFilter extends AbstractFilter {
    static final float[][] kernel = new float[][]{{0, 1, 0},
        {0, 0, 0},
        {0, -1, 0}};

    /**
     * @param image    input image reference.
     * @param newImage output image reference.
     * @param start    first line to be processed from input image.
     * @param stop     past last line to be processed from input image.
     */
    public void applyFilterPh1(Image image, Image newImage, int start, int stop){
        for (int i = start; i < stop; ++i) {
            for (int j = 1; j < image.width - 1; ++j) {
                float red, green, blue;
                red = green = blue = 0;

                for (int ki = -1; ki <= 1; ++ki) {
                    for (int kj = -1; kj <= 1; ++kj) {
                        red   += (float)(image.matrix[i + ki][j + kj].r) * kernel[ki + 1][kj + 1];
                        green += (float)(image.matrix[i + ki][j + kj].g) * kernel[ki + 1][kj + 1];
                        blue  += (float)(image.matrix[i + ki][j + kj].b) * kernel[ki + 1][kj + 1];
                    }
                }

                red = (red < 0) ? 0 : red;
                green = (green < 0) ? 0 : green;
                blue = (blue < 0) ? 0 : blue;
                newImage.matrix[i][j].r = (char)((red > 255) ? 255 : red);
                newImage.matrix[i][j].g = (char)((green > 255) ? 255 : green);
                newImage.matrix[i][j].b = (char)((blue > 255) ? 255 : blue);
                newImage.matrix[i][j].a = image.matrix[i][j].a;
            }
        }
    }
}
