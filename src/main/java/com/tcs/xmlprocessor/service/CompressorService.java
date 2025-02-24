package com.tcs.xmlprocessor.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.xmlprocessor.model.Metadata;
import com.tcs.xmlprocessor.repository.MetadataRepository;
import com.tcs.xmlprocessor.util.LoggerUtil;
import com.tcs.xmlprocessor.util.Utility;

@Component
public class CompressorService {
	@Autowired
	LoggerUtil logger;
	
	@Autowired
	Utility utils;
	
	@Autowired
	MetadataRepository metadataRepo;
	
	public void zipFile(File file, String zipFilePath, LocalDateTime startTime) throws IOException {
		String fileName = file.getName();
		String fileNameWithoutExtension = utils.removeFileExtension(fileName);
		String[] parts = fileNameWithoutExtension.split("_");
		if (parts.length != 4) {
			logger.logError("Invalid file format: " + fileName);
			return;
		}
		
		String accountName = parts[0];
		String empId = parts[1];
		String countryCode = parts[2];
		String deptId = parts[3];
//		String fileId = UUID.randomUUID().toString();
		
		
		try (FileOutputStream fos = new FileOutputStream(zipFilePath); ZipOutputStream zos = new ZipOutputStream(fos)) {
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);
			Files.copy(file.toPath(), zos);
			zos.closeEntry();
			logger.logInfo("Zipped file: " + zipFilePath);
			LocalDateTime endTime = LocalDateTime.now();
			Metadata metadata = new Metadata(accountName, empId, countryCode, deptId, startTime.toString(),
					endTime.toString());
			metadataRepo.save(metadata);
		}
	}
}
