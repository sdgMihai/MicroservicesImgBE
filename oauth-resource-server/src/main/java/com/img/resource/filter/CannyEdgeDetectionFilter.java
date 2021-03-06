package com.img.resource.filter;


import com.img.resource.utils.Image;
import com.img.resource.utils.Pixel;
import com.img.resource.utils.ThreadSpecificDataT;

import java.util.concurrent.BrokenBarrierException;

public class CannyEdgeDetectionFilter extends Filter {
    private int rank;
    private int numtasks;
    private int chunk;
    private static float[][] auxTheta;

    public CannyEdgeDetectionFilter() {
        this.filter_additional_data = null;
    }
    public CannyEdgeDetectionFilter(FilterAdditionalData filter_additional_data) {
        this.filter_additional_data = filter_additional_data;
    }
    /**
     * @param image referinta catre imagine
     * @param newImage referinta catre obiectul tip Image
     *          care va contine imaginea rezultata in urma
     *          aplicarii filtrului.
     */
    @Override
    public void applyFilter(Image image, Image newImage) throws BrokenBarrierException, InterruptedException {
        ThreadSpecificDataT tData = (ThreadSpecificDataT) filter_additional_data;
        int slice = (image.height - 2) / tData.NUM_THREADS;//imaginea va avea un rand de pixeli deasupra si unul dedesubt
        //de aici '-2' din ecuatie
        int start = Math.max(1, tData.threadID * slice);
        int stop = (tData.threadID + 1) * slice;
        if (tData.threadID + 1 == tData.NUM_THREADS) {
            stop = Math.max((tData.threadID + 1) * slice, image.height - 1);
        }

        for (int i = start; i < stop; ++i) {
            for (int j = 0; j < image.width - 1; ++j) {
                int gray = (int) (0.2126 * image.matrix[i][j].r +
                        0.7152 * image.matrix[i][j].g +
                        0.0722 * image.matrix[i][j].b);
                gray = Math.min(gray, 255);
                newImage.matrix[i][j] = new Pixel((char) gray, (char) gray, (char) gray, image.matrix[i][j].a);
            }
        }

        BlackWhiteFilter step1 = new BlackWhiteFilter(tData);
        step1.applyFilter(image, newImage);
        tData.barrier.await();

        GaussianBlurFilter step2 = new GaussianBlurFilter(tData);
        step2.applyFilter(newImage, image);
        tData.barrier.await();

        GradientFilter step3 = new GradientFilter(tData);
        step3.applyFilter(image, newImage);
        if (tData.threadID == 0) {
            auxTheta = step3.theta;
        }
        tData.barrier.await();

        NonMaximumSuppressionFilter step4 = new NonMaximumSuppressionFilter(auxTheta, step3.thetaHeight, step3.thetaWidth, tData);
        step4.applyFilter(newImage, image);
        tData.barrier.await();

        DoubleThresholdFilter step5 = new DoubleThresholdFilter(tData);
        step5.applyFilter(image, newImage);
        tData.barrier.await();

        EdgeTrackingFilter step6 = new EdgeTrackingFilter(tData);
        step6.applyFilter(newImage, image);
        tData.barrier.await();

        for (int i = start; i < stop; ++i) {
            final Pixel[] swp = image.matrix[i];
            image.matrix[i] = newImage.matrix[i];
            newImage.matrix[i] = swp;
            for (int j = 1; j < image.width - 1; ++j) {
                if (newImage.matrix[i][j].r < 100) {
                    newImage.matrix[i][j] = new Pixel((char) 0, (char) 0, (char) 0, newImage.matrix[i][j].a);
                }
            }
        }
    }
}
