package com.img.resource.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
//@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ImageEntity {
    @Id
    @GeneratedValue
    private Long id;
//    https://stackoverflow.com/questions/53520035/save-image-in-db-with-entity
    @Lob
    @Column
    private byte[] content;

    @Column
    private String userId;
}
