package com.img.resource.persistence.model;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "images")
public record ImgBin(@Id String id, String user, Binary binary) {

}
