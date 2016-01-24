package com.temples.in.logmonitor.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class FileProcessor implements Callable<Results> {

	File file;

	public FileProcessor(File file) {
		super();
		this.file = file;
	}


	@Override
	public Results call() throws Exception {
		System.out.println("Processing file: " + file.getAbsolutePath());
		List<String> filteredLines = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("ERROR")) {
					filteredLines.add(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		
		Results res = new Results();
		res.setFileName(file.getAbsolutePath());
		res.setMessages(filteredLines);
		return res;
	}

}
