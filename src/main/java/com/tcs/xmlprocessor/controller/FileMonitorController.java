package com.tcs.xmlprocessor.controller;

import com.tcs.xmlprocessor.service.FileMonitorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class FileMonitorController {
	private final FileMonitorService fileMonitorService;

	public FileMonitorController(FileMonitorService fileMonitorService) {
		this.fileMonitorService = fileMonitorService;
	}

	@GetMapping("/start-monitoring")
	public String startMonitoring() {
		fileMonitorService.monitorDirectory();
		return "File monitoring started.";
	}

}
