package rs.ac.bg.etf.js150411d.gameflow;

import rs.ac.bg.etf.js150411d.gameobjects.Disk;
import rs.ac.bg.etf.js150411d.gameobjects.Rod;

public class DiskMove {
    private final Disk disk;
    private final Rod destinationRod;

    public  DiskMove(Disk disk, Rod destinationRod){
        this.disk = disk;
        this.destinationRod = destinationRod;
    }

    public Disk getDisk(){
        return disk;
    }

    public Rod getDestinationRod(){
        return destinationRod;
    }
}
