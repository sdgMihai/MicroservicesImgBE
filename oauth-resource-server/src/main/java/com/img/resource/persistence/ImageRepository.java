package com.img.resource.persistence;

import com.img.resource.persistence.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    List<ImageEntity> findByUserId(String userId);
}
