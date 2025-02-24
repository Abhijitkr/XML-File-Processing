package com.tcs.xmlprocessor.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {
	
	
	@Value("${app.input.directory}")
	private String INPUT_DIR;
	
	@Autowired
	XMLProcessorService xmlProcessor;
	

	@Scheduled(fixedRate = 1000)
	public void checkTriggerFiles() {
//		loggerUtil.logInfo("Scheduler Arrived "+ LocalDateTime.now());
		File inputFolder = new File(INPUT_DIR);
		File[] triggerFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".trig"));
		if (triggerFiles != null) {
			for (File triggerFile : triggerFiles) {
				xmlProcessor.preprocessXML(triggerFile);
			}
		}
	}

}
