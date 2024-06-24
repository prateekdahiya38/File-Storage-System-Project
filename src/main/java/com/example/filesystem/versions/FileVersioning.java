package com.example.filesystem.versions;

import com.example.filesystem.models.File;
import com.example.filesystem.repositories.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Component
public class FileVersioning {
    @Autowired
    private FileStorageRepository fileStorageRepository;
    public File saveVersion(File file){
        List<File> existingFiles = fileStorageRepository.findByNameOrderByVersionDesc(file.getName());
        int newVersion = existingFiles.isEmpty() ? 1 : existingFiles.get(0).getVersion() + 1;
        file.setVersion(newVersion);
        return fileStorageRepository.save(file);
    }
}
