package com.example.filesystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class File extends BaseModel{
    private String name;
    private String type;
    private int version;
    @Lob
    @Column(name = "file_data", length = 1000)
    private byte[] data;
    private Instant uploadedAt;
    @ManyToOne
    private User user;
}
