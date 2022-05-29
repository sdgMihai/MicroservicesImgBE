package com.img.resource.persistence.model;

import lombok.Value;

@Value
public class ImageUploadResponse {
    String result = "OK";
    String id;
    Integer numberOfImages = 1;
    String message = "upload successful";

    public ImageUploadResponse(String id) {
        this.id = id;
    }
}
