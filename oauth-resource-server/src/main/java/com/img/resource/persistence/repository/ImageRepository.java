package com.img.resource.persistence.repository;

import com.img.resource.persistence.model.ImgBin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<ImgBin, String> {

}
