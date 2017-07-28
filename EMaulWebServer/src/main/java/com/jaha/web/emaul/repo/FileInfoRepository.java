package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.FileInfo;

/**
 * Created by shavrani on 16. 5. 30..
 */
@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

    FileInfo findByFileKey(Long fileKey);

    FileInfo findByCategoryAndFileGroupKeyAndFileKey(String category, String fileGroupKey, Long fileKey);

    FileInfo findByCategoryAndFileGroupKeyAndTypeAndFileKey(String category, String fileGroupKey, String type, Long fileKey);

    List<FileInfo> findByCategoryAndFileGroupKey(String category, String fileGroupKey);

    List<FileInfo> findByCategoryAndFileGroupKeyAndType(String category, String fileGroupKey, String type);

}
