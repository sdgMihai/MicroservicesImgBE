package com.img.resource.filter;


import com.img.resource.utils.Image;

import java.util.concurrent.BrokenBarrierException;

public abstract class Filter {
    public FilterAdditionalData filter_additional_data;
    /**
     * aplica un filtru pe imagine
     * @param image referinta catre imagine
     * @param newImage referinta catre obiectul tip Image
     *          care va contine imaginea rezultata in urma
     *          aplicarii filtrului.
     */
    public abstract void applyFilter(Image image, Image newImage) throws BrokenBarrierException, InterruptedException;
}
