package com.tcs.xmlprocessor.util;

import org.springframework.stereotype.Component;

@Component
public class Utility {
	public String removeFileExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
	}
}
