package com.temples.in.logmonitor.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;

public class LogService {

	static String path = "/var/log/temples";
	String searchToken = "ERROR";
	static ExecutorService executorService;

	public static List<Future<Results>> read() throws IOException, InterruptedException{
		
		List<File> files = getFileList();
		
		System.out.println("Files to process: " + files.size());
		executorService = Executors.newFixedThreadPool(files.size());
		List<FileProcessor> fileProcessors = new ArrayList<FileProcessor>();
		
		for (File file : files) {
			fileProcessors.add(new FileProcessor(file));
		}

		System.out.println("***** Starting to process files ******");

		List<Future<Results>> outputList = executorService.invokeAll(fileProcessors);
		
		return outputList;
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<File> getFileList() throws IOException {
		File dir = new File(path);
		System.out.println("Getting all files in " + dir.getCanonicalPath() + " including those in subdirectories");
		String[] extns = {"log"};
		List<File> files = (List<File>) FileUtils.listFiles(dir, extns, true);
		return files;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		List<Future<Results>> futures = read();
		System.out.println("****** Printing output ******");
		for (Future<Results> future : futures) {
			Results res = future.get();
			res.print();
		}
		executorService.shutdown();
	}
}
