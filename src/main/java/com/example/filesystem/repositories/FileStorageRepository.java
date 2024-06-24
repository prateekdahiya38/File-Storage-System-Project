package com.example.filesystem.repositories;

import com.example.filesystem.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface FileStorageRepository extends JpaRepository<File, UUID> {

    Optional<File> findByName(String fileName);
    List<File>findByNameOrderByVersionDesc(String fileName);
    Optional<File> findByNameAndVersion(String fileName, int version);
}
