package com.example.musicbrowser;

public class RecordItem {
    int originalPos;
    boolean listened;
    String musicFilePath;
    
    public RecordItem(int originalPos, String musicFilePath) {
        this.originalPos = originalPos;
        this.listened = false;
        this.musicFilePath = musicFilePath;
    }
    
    public int getOriginalPos() {
        return originalPos;
    }
    
    public boolean getListened() {
        return listened;
    }
    
    public String getMusicFilePath() {
        return musicFilePath;
    }
    
    public void setListened(boolean listened) {
    	this.listened = listened;
    }
}