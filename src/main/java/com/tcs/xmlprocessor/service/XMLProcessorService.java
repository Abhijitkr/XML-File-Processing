package com.tcs.xmlprocessor.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tcs.xmlprocessor.util.LoggerUtil;
import com.tcs.xmlprocessor.util.Utility;

@Component
public class XMLProcessorService {
	
	@Autowired
	Utility utils;
	
	@Autowired
	LoggerUtil logger;
	
	@Autowired
	CompressorService compressor;
	
	@Value("${app.input.directory}")
	private String INPUT_DIR;
	
	@Value("${app.output.directory}")
	private String OUTPUT_DIR;
	
	public void preprocessXML(File triggerFile) {
		try {

			
			LocalDateTime startTime= LocalDateTime.now();
			
			String fileNameWithoutExtension = utils.removeFileExtension(triggerFile.getName());
			File xmlFile = new File(INPUT_DIR, fileNameWithoutExtension + ".xml");
			if (!xmlFile.exists()) {
				logger.logError("Associated XML file not found for trigger: " + triggerFile.getName());
				return;
			}

			// Convert XML to pipe-separated .dat format
			File datFile = new File(INPUT_DIR, fileNameWithoutExtension + ".dat");

			String fileContent = Files.readString(xmlFile.toPath());
			
			String pipeSeparatedContent = convertToDat(fileContent);

			Files.write(datFile.toPath(), pipeSeparatedContent.getBytes());

			// Compress and move .dat file to output directory
			compressor.zipFile(datFile, OUTPUT_DIR + "/" + fileNameWithoutExtension + ".zip", startTime);

			Files.delete(triggerFile.toPath());
			logger.logInfo("Processed and zipped file: " + datFile.getName());
		} catch (IOException e) {
			logger.logError("Error processing trigger file: " + triggerFile.getName());
		}
	}
	
	public String convertToDat(String xmlContent) {
	    Pattern employeePattern = Pattern.compile("<employee>(.*?)</employee>", Pattern.DOTALL);
	    Pattern fieldPattern = Pattern.compile("<([^/][^>]*)>(.*?)</\\1>");

	    Matcher employeeMatcher = employeePattern.matcher(xmlContent);
	    StringBuilder result = new StringBuilder();

	    while (employeeMatcher.find()) {
	        String employeeData = employeeMatcher.group(1);
	        Matcher fieldMatcher = fieldPattern.matcher(employeeData);
	        StringBuilder employeeLine = new StringBuilder();

	        while (fieldMatcher.find()) {
	            employeeLine.append(fieldMatcher.group(2).trim()).append("|");
	        }

	        // Remove trailing '|' and add a new line
	        if (employeeLine.length() > 0) {
	            employeeLine.setLength(employeeLine.length() - 1);
	        }

	        result.append(employeeLine).append("\n");
	    }

	    return result.toString().trim();
	}
}
