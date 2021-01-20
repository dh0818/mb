package com.example.musicbrowser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.content.res.Resources;
import android.util.Log;

public class FileUtils {
	
	public static File[] FindFiles(String dir, Resources res) {
		File[] allMatchingFiles = FileUtils.getSubFiles(new File(dir), FindFilter(res));
		Vector<File> vectors = new Vector<File>();
		
		for (File f : allMatchingFiles) {
			if(f.listFiles() != null && f.listFiles().length == 0)
				;
			else
				vectors.add(f);
		}
		
		File[] arr = new File[vectors.size()];
		
		return vectors.toArray(arr);
	}
	
    public static List<String> FindFilesPath(File[] files) {
    	
    	List<String> filesPath = new ArrayList<String>();
    	
		for (File f : files)
			filesPath.add(f.getAbsolutePath());
		
		return filesPath;
    }
	
    public static FilenameFilter[] FindFilter(Resources res) {
		Resources resources = res;
		String[] imageTypes = resources.getStringArray(R.array.music);
		FilenameFilter[] filter = new FilenameFilter[imageTypes.length];

		int i = 0;
		for (final String type : imageTypes) {
			filter[i] = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith("." + type);
				}
			};
			i++;
		}
		
		return filter;
    }
    
	public static File[] getSubFiles(File directory, FilenameFilter[] filter) {
    	Vector<File> files = new Vector<File>();
    	File[] entries = directory.listFiles();
    	
		if (entries != null) {
			for (File entry : entries) {
				
				for (FilenameFilter filefilter : filter) {
					if (filter == null || filefilter.accept(directory, entry.getName())) {
						files.add(entry);
					}
				}
				
				if(entry.isDirectory())
					files.add(entry);
			}
		}
		
		File[] arr = new File[files.size()];
		return files.toArray(arr);
	}
	
	public static File[] appendFiles2ToFiles1(File[] files1, File[] files2) {
		Vector<File> v1 = filesToVector(files1);
		Vector<File> v2 = filesToVector(files2);
		
		Vector<File> merge = new Vector<File>();
		merge.addAll(v1);
		merge.addAll(v2);
		
		return VectorToFiles(merge);
	}
	
	public static Vector<File> filesToVector(File[] files) {
    	Vector<File> vec_files = new Vector<File>();
    	
    	for (File entry : files) {
    		vec_files.add(entry);
    	}
    	
		return vec_files;
	}
	
	public static File[] VectorToFiles(Vector<File> vec_files) {
		File[] files = new File[vec_files.size()];
		return vec_files.toArray(files);
	}
	
    public static File[] findDirectoryInFiles(File[] files) {
    	Vector<File> vectors = new Vector<File>();
    	
		for (File f : files) {
			if(f.isDirectory())
				vectors.add(f);
		}
		
		File[] directorys = new File[vectors.size()];
		
    	return vectors.toArray(directorys);
    }
    
    public static File[] findPlayableInFiles(File[] files) {
    	Vector<File> vectors = new Vector<File>();
    	
		for (File f : files) {
			if(!f.isDirectory())
				vectors.add(f);
		}
		
		File[] pics = new File[vectors.size()];
		
    	return vectors.toArray(pics);
    }
    
    public static String[] getAllFileName(File[] files) {
    	String[] strArray = new String[files.length];
    	int index = 0;
    	
    	for(File file: files) {
    		strArray[index] = file.getName();
    		index++;
    	}
    	
    	return strArray;
    }
    
    public static int checkfileSepearteNum(String filePath) {
    	String[] temp = filePath.split("/");
    	String merge = "";
    	
    	for(int i = 0; i < temp.length - 1; i++) {
    		merge = merge.concat(temp[i]);
    	}
    	
    	Log.e("", temp + "/" + filePath + "");
    	
    	return temp.length;
    }
    
    public static String parsePrevFilePath(String filePath) {
    	String[] temp = filePath.split("/");
    	String merge = "";
    	
    	for(int i = 0; i < temp.length - 1; i++) {
    		merge = merge.concat(temp[i]);
    	}
    	
    	return merge;
    }
    
    public static String parseFileNameFromFilePath(String filePath) {
    	String[] temp = filePath.split("/");

    	return temp[temp.length - 1];
    }
}