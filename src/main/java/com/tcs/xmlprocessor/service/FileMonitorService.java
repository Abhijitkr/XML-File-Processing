package com.tcs.xmlprocessor.service;

import com.tcs.xmlprocessor.model.Metadata;
import com.tcs.xmlprocessor.repository.MetadataRepository;
import com.tcs.xmlprocessor.util.LoggerUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileMonitorService {
	private static final String SOURCE_DIR = "E:/Karat Preparation/xmlprocessor/source";
	private static final String INPUT_DIR = "E:/Karat Preparation/xmlprocessor/input";
	private final MetadataRepository metadataRepository;
	private final LoggerUtil loggerUtil;

	public FileMonitorService(MetadataRepository metadataRepository, LoggerUtil loggerUtil) {
		this.metadataRepository = metadataRepository;
		this.loggerUtil = loggerUtil;
	}

	public void monitorDirectory() {
		File folder = new File(SOURCE_DIR);
		File[] files = folder.listFiles((dir, name) -> name.endsWith(".xml"));
		if (files != null) {
			for (File file : files) {
				processFile(file);
			}
		}
	}

	private void processFile(File file) {
		try {
			String fileName = file.getName();
			 String fileNameWithoutExtension = removeFileExtension(fileName);
			String[] parts = fileName.split("_");
			if (parts.length != 4) {
				loggerUtil.logError("Invalid file format: " + fileName);
				return;
			}
			String accountName = parts[0];
			String empId = parts[1];
			String countryCode = parts[2];
			String deptId = parts[3].replace(".xml", "");
			String fileId = UUID.randomUUID().toString();
			LocalDateTime startTime = LocalDateTime.now();

			Path targetPath = Path.of(INPUT_DIR, fileName);
			Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			
			
			Files.createFile(Path.of(INPUT_DIR, fileNameWithoutExtension + ".trig"));


			LocalDateTime endTime = LocalDateTime.now();
			Metadata metadata = new Metadata(fileId, accountName, empId, countryCode, deptId, startTime.toString(),
					endTime.toString());
			metadataRepository.save(metadata);

			loggerUtil.logInfo("Processed file: " + fileName);
		} catch (IOException e) {
			loggerUtil.logError("Error processing file: " + file.getName());
		}
	}
	
	private String removeFileExtension(String fileName) {
	    int lastDotIndex = fileName.lastIndexOf(".");
	    return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
	}

}
