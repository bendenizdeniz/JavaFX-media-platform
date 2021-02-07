package sample.model;

public class Album {
    private int IDAlbum;
    private String Name;
    private int AlbumSingerID;

    public int getIDAlbum() {
        return IDAlbum;
    }

    public void setIDAlbum(int IDAlbum) {
        this.IDAlbum = IDAlbum;
    }

    public String getName() {
        return Name;
    }

    public void setName(String nameAlbum) {
        Name = nameAlbum;
    }

    public int getAlbumSingerID() {
        return AlbumSingerID;
    }

    public void setAlbumSingerID(int albumSingerID) {
        AlbumSingerID = albumSingerID;
    }

}
