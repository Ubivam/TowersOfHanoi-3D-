package rs.ac.bg.etf.js150411d.gameobjects;

import javafx.scene.shape.Cylinder;
import rs.ac.bg.etf.js150411d.HanoiGroup;
import rs.ac.bg.etf.js150411d.Shading;

import java.util.EmptyStackException;
import java.util.Stack;

public class Rod extends Cylinder implements Selectable {
    private Stack<Disk> disk_stack = new Stack<>();
    private final HanoiGroup hanoi;
    private Shading material;
    private boolean selected;

    public Rod(double radius, double height, HanoiGroup hanoi) {
        super(radius,height);
        this.hanoi = hanoi;

        this.setOnMouseEntered((event -> {
            if(hanoi.getInteractionEnabled()){
                if(isSelectable()){
                    if(!selected){
                        setMaterial(material.getSelectMaterial());
                    } else {
                        setMaterial(material.getUnselectableMaterial());
                    }
                }
            }
        }));
        this.setOnMouseExited((event -> {
            if(hanoi.getInteractionEnabled()){
                if(!selected){
                    setMaterial(material.getMaterial());
                } else {
                    setMaterial(material.getSelectMaterial());
                }
            }
        }));
    }

    //Stack Interaction
    public void push(Disk disk){
        disk_stack.push(disk);
        disk.setRod(this);
    }
    public Disk pop(){
        Disk topDisk =null;
        try{
            topDisk = disk_stack.pop();
        }catch (EmptyStackException e){}
        if(topDisk != null){
            topDisk.setRod(null);
        }
        return topDisk;
    }
    public Disk peek(){
        try{
            return disk_stack.peek();
        }catch (EmptyStackException e)
        {
            return null;
        }
    }
    public void clear(){
        disk_stack.clear();
    }

    //Gettters and informations
    public Disk[] getDisks(){
        return (Disk[]) disk_stack.toArray();
    }

    public int getNumberOfDisks() {
        return disk_stack.size();
    }
    public void getDiskFrom(Rod rod){
        Stack<Disk> temp = disk_stack;
        disk_stack = rod.disk_stack;
        for(Disk disk : disk_stack) {
            disk.setRod(this);
        }
        rod.disk_stack = temp;
    }
    public  HanoiGroup getHanoi() {
        return hanoi;
    }
    public boolean isSelectable(){
        var  exists = hanoi.getSelectedDisk() != null;
        var selectable = disk_stack.isEmpty() || disk_stack.peek().getOuterRadius() > hanoi.getSelectedDisk().getOuterRadius();
        return exists && selectable;
    }
    //Set Materials
    public void setSelected(boolean selected){
        this.selected = selected;
        if(selected){
            setMaterial(material.getSelectMaterial());
        } else {
            setMaterial(material.getMaterial());
        }
    }
    public void setShading(Shading material){
        this.material = material;
        if(!selected){
            this.setMaterial(material.getMaterial());
        }
    }
}
