package com.temples.in.logmonitor.services;

import java.util.List;

public class Results {

	String fileName;
	List<String> messages;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public void print() {
		if (messages.size() > 0) {
			System.out.println("File Name: " + fileName);

			for (String string : messages) {
				System.out.println(string);
			}
		} else {
			//System.out.println("No errors reported...");
		}
	}
}
