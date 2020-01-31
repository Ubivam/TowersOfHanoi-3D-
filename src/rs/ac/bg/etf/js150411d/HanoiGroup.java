package rs.ac.bg.etf.js150411d;

import javafx.scene.Group;
import javafx.scene.input.PickResult;
import javafx.scene.shape.Shape3D;
import rs.ac.bg.etf.js150411d.animations.DiskFlipper;
import rs.ac.bg.etf.js150411d.animations.DiskMover;
import rs.ac.bg.etf.js150411d.gameflow.DiskMove;
import rs.ac.bg.etf.js150411d.gameobjects.Disk;
import rs.ac.bg.etf.js150411d.gameobjects.Platform;
import rs.ac.bg.etf.js150411d.gameobjects.Rod;


public class HanoiGroup extends Group {

    public  static final Shading DEFAULT_MATERIAL = Shading.WOOD;
    public static final  double ROD_HEIGHT = 150;
    public static final double PLATFORM_WIDTH = 450;
    private static final double PLATFORM_HEIGHT = 10;
    private static final double PLATFORM_DEPTH = 120;
    public static final double ROD_DISTANCE = 0.25 * PLATFORM_WIDTH;

    private  static final double DISK_RADIUS = 50;
    private static final int DEFAULT_NUM_DISKS  = 3;


    private  final Platform platform;
    private  final Rod[] rods;
    private int numberOFDisks;
    private Disk[] disks;
    private Shading material;
    private double heightFactor = (numberOFDisks / 15)/(Math.sqrt((1+(numberOFDisks / 15) * (numberOFDisks / 15))));

    private Disk selectedDisk;
    private Rod selectedRod;
    private boolean interactionEnabled;

    public HanoiGroup(Shading material, int numberOFDisks, GameWindow gameWindow){
        this.material = material;
        this.numberOFDisks = numberOFDisks <= 0 ? 1 : numberOFDisks;

        int index = -1;
        platform = new Platform(PLATFORM_WIDTH, PLATFORM_HEIGHT, PLATFORM_DEPTH);
        platform.setTranslateY(0.5 * PLATFORM_HEIGHT);

        super.getChildren().add(platform);

        rods = new Rod[3];
        for(int i = 0 ; i< rods.length; i++){
            rods[i] = new Rod(5, ROD_HEIGHT,this);
            rods[i].setTranslateY(-0.5 * ROD_HEIGHT);
            rods[i].setTranslateX((index++) * ROD_DISTANCE);
            super.getChildren().add(rods[i]);
        }
        instantiateDisks();

        this.setOnMouseClicked(event -> {
            if(!interactionEnabled){
                return;
            }
            PickResult pickResult = event.getPickResult();
            if(pickResult != null && pickResult.getIntersectedNode() instanceof Shape3D){
                var shape  = (Shape3D) pickResult.getIntersectedNode();
                if(shape instanceof Disk){
                    if(!(((Disk)shape).isSelectable())){
                        return;
                    }
                    if(selectedDisk != null && selectedDisk != shape){
                        selectedDisk.setSelected(false);
                        selectedDisk = (Disk) shape;
                        selectedDisk.setSelected(true);
                    } else if (selectedDisk == null) {
                        selectedDisk = (Disk) shape;
                        selectedDisk.setSelected(true);
                    }
                } else if (shape instanceof Rod) {
                    Rod rod = (Rod) shape;
                    if(selectedDisk != null){
                        if(selectedDisk.getRod() == rod){
                            return;
                        }
                        if(!rod.isSelectable()){
                            return;
                        }
                        if(selectedRod != null && selectedRod != shape){
                            selectedRod.setSelected(false);
                            selectedRod = rod;
                            selectedRod.setSelected(true);
                        } else if (selectedRod == null) {
                            selectedRod = rod;
                            selectedRod.setSelected(true);
                        }
                    }
                    if(selectedDisk != null && selectedRod !=null){
                        gameWindow.getButtonGameReset().setDisable(true);

                        interactionEnabled = false;

                        new DiskFlipper(this, new DiskMove(selectedDisk, selectedRod)).start(()->{

                            gameWindow.getButtonGameReset().setDisable(false);

                            selectedRod = null;
                            selectedDisk = null;
                            if(isSolved()){
                                //Get label
                                gameWindow.getButtonGameReset().setText("Game Reset");
                            }
                            else {
                                interactionEnabled = true;
                            }

                        });
                    }
                }
            }
        });

    }
    public HanoiGroup(int numberOFDisks, GameWindow gameWindow){
        this(DEFAULT_MATERIAL, numberOFDisks,gameWindow);
    }
    public HanoiGroup(GameWindow gameWindow){
        this(DEFAULT_MATERIAL,DEFAULT_NUM_DISKS,gameWindow);
    }
    public void reset(){
        for(Rod rod : rods){
            rod.clear();
        }
        for(int i = 0; i<disks.length; i++){
            disks[i].getTransforms().clear();
            disks[i].setTranslateY(-0.5 * ((heightFactor * ROD_HEIGHT)/numberOFDisks) - i * ((heightFactor * ROD_HEIGHT)/numberOFDisks));
            disks[i].setTranslateX(-0.25 * PLATFORM_WIDTH);
            rods[0].push(disks[i]);
        }
    }
    public int getNumberOFDisks()
    {
        return numberOFDisks;
    }
    public void setNumberOFDisks(int numberOFDisks){
        for(var rod: rods){
            for(var disk : rod.getDisks()){
                super.getChildren().remove(disk);
            }
            rod.clear();
        }
        this.numberOFDisks = numberOFDisks;
        instantiateDisks();
    }
    private void instantiateDisks(){
        double diskWidthFactor = 1;
        double widthFactorDecrement = 0.8 / numberOFDisks;
        heightFactor = (numberOFDisks / 15d) / (Math.sqrt(1 + (numberOFDisks / 15d) * (numberOFDisks / 15d))); // Sigmoid function for determining disk's height
        disks = new Disk[numberOFDisks];
        for (int i = 0; i < disks.length; i++) {
            disks[i] = new Disk(5, diskWidthFactor * DISK_RADIUS, (float)(heightFactor * ROD_HEIGHT) / numberOFDisks);
            disks[i].setTranslateY(-0.5 * ((heightFactor * ROD_HEIGHT) / numberOFDisks) - i * ((heightFactor * ROD_HEIGHT) / numberOFDisks));
            disks[i].setTranslateX(-0.25 * PLATFORM_WIDTH);
            disks[i].setShading(material);
            rods[0].push(disks[i]);

            super.getChildren().add(disks[i]);
            diskWidthFactor -= widthFactorDecrement;
        }
    }
    public Rod[] getRods(){
        return rods;
    }
    public Disk[] getDisks(){
        return disks;
    }
    public void setShading(Shading material){
        this.material = material;
        platform.setMaterial(material.getMaterial());
        for(var rod: rods){
            rod.setShading(material);
        }
        for(var disk : disks){
            disk.setShading(material);
        }
    }
    public Disk getSelectedDisk() {
        return selectedDisk;
    }
    public Shading getShading(){
        return material;
    }
    public boolean isSolved(){
        return rods[2].getNumberOfDisks() == numberOFDisks;
    }
    public boolean getInteractionEnabled(){
        return interactionEnabled;
    }
    public  void setInteractionEnabled(boolean interactionEnabled){
        this.interactionEnabled = interactionEnabled;
    }
    public void setSelectedDisk(Disk selectedDisk){
        this.selectedDisk = selectedDisk;
    }

}
