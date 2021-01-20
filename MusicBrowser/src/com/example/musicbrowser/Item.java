package com.example.musicbrowser;

public class Item {
    String artist;
    String album;
    byte[] embeddedPicture;
    
    public Item(String artist, String album, byte[] embeddedPicture) {
        this.artist = artist;
        this.album = album;
        this.embeddedPicture = embeddedPicture;
    }
    
    public String getArtist() {
    	if(artist == null)
    		artist = "";
    	
        return artist;
    }
    
    public String getAlbum() {
    	if(album == null)
    		album = "";
    	
        return album;
    }
    
    public byte[] getPic() {
        return embeddedPicture;
    }
}