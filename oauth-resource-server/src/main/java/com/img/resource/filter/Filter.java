package com.img.resource.filter;

import com.img.resource.utils.Image;

public interface Filter {
    void applyFilter(Image in, Image out, final int PARALLELISM);
}
