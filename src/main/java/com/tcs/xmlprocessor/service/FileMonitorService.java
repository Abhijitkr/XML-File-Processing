package com.tcs.xmlprocessor.service;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tcs.xmlprocessor.model.Metadata;

import com.tcs.xmlprocessor.repository.MetadataRepository;
import com.tcs.xmlprocessor.util.LoggerUtil;

@Service
public class FileMonitorService {
	private static final String SOURCE_DIR = "E:/Karat Preparation/xmlprocessor/source";
	private static final String INPUT_DIR = "E:/Karat Preparation/xmlprocessor/input";
	private static final String OUTPUT_DIR = "E:/Karat Preparation/xmlprocessor/output";

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
			String[] parts = fileNameWithoutExtension.split("_");
			if (parts.length != 4) {
				loggerUtil.logError("Invalid file format: " + fileName);
				return;
			}
			String accountName = parts[0];
			String empId = parts[1];
			String countryCode = parts[2];
			String deptId = parts[3];
			String fileId = UUID.randomUUID().toString();
			LocalDateTime startTime = LocalDateTime.now();

			// Copy XML file from source directory to input directory
			Path sourcePath = file.toPath();
			Path targetPath = Path.of(INPUT_DIR, fileName);
			Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

			// Create trigger file in the input directory
			Files.createFile(Path.of(INPUT_DIR, fileNameWithoutExtension + ".trig"));

			checkTriggerFiles();

			LocalDateTime endTime = LocalDateTime.now();
			Metadata metadata = new Metadata(fileId, accountName, empId, countryCode, deptId, startTime.toString(),
					endTime.toString());
			metadataRepository.save(metadata);

			loggerUtil.logInfo("Copied and processed file: " + fileName);
		} catch (IOException e) {
			loggerUtil.logError("Error processing file: " + file.getName());
//			loggerUtil.logError("Error processing file: " + file.getName(), e);
		}
	}

	@Scheduled(fixedRate = 10000)
	public void checkTriggerFiles() {
		File inputFolder = new File(INPUT_DIR);
		File[] triggerFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".trig"));
		if (triggerFiles != null) {
			for (File triggerFile : triggerFiles) {
				processTriggerFile(triggerFile);
			}
		}
	}

	private void processTriggerFile(File triggerFile) {
		try {
			String fileNameWithoutExtension = removeFileExtension(triggerFile.getName());
			File xmlFile = new File(INPUT_DIR, fileNameWithoutExtension + ".xml");
			if (!xmlFile.exists()) {
				loggerUtil.logError("Associated XML file not found for trigger: " + triggerFile.getName());
				return;
			}

			// Convert XML to pipe-separated .dat format
			File datFile = new File(INPUT_DIR, fileNameWithoutExtension + ".dat");

			String fileContent = Files.readString(xmlFile.toPath());
			String pipeSeparatedContent = convertXmlToPipeSeparated(fileContent);

			Files.write(datFile.toPath(), pipeSeparatedContent.getBytes());

			// Compress and move .dat file to output directory
			zipFile(datFile, OUTPUT_DIR + "/" + fileNameWithoutExtension + ".zip");

			Files.delete(triggerFile.toPath());
			loggerUtil.logInfo("Processed and zipped file: " + datFile.getName());
		} catch (IOException e) {
			loggerUtil.logError("Error processing trigger file: " + triggerFile.getName());
		}
	}

	private String convertXmlToPipeSeparated(String xmlContent) {
		Pattern pattern = Pattern.compile("<([^/][^>]*)>(.*?)</\\1>");
		Matcher matcher = pattern.matcher(xmlContent);
		StringBuilder result = new StringBuilder();

		while (matcher.find()) {
//	        result.append(matcher.group(2).trim()).append("|");
			result.append(matcher.group(1).trim()).append("|").append(matcher.group(2).trim()).append("|\n");
		}

		return result.length() > 0 ? result.substring(0, result.length() - 1) : "";
	}

	private void zipFile(File file, String zipFilePath) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(zipFilePath); ZipOutputStream zos = new ZipOutputStream(fos)) {
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);
			Files.copy(file.toPath(), zos);
			zos.closeEntry();
			loggerUtil.logInfo("Zipped file: " + zipFilePath);
		}
	}

	private String removeFileExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
	}

}
