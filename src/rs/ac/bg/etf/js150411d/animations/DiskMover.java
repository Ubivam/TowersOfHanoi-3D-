package rs.ac.bg.etf.js150411d.animations;

import javafx.animation.Animation;
import rs.ac.bg.etf.js150411d.GameWindow;
import rs.ac.bg.etf.js150411d.HanoiGroup;
import rs.ac.bg.etf.js150411d.gameflow.DiskMove;
import rs.ac.bg.etf.js150411d.gameobjects.Disk;
import rs.ac.bg.etf.js150411d.gameobjects.Rod;

import java.util.List;

public abstract class DiskMover {

    public static final double DELFAULT_DURATION = 1;

    protected double animationDuration = DELFAULT_DURATION;

    private  int currentMoveIndex = -1;
    protected final HanoiGroup hanoiGroup;
    private final DiskMove[] diskMoves;
    private Animation currentAnimation;

    protected DiskMover(HanoiGroup hanoi, DiskMove... diskMoves) {
        this.hanoiGroup = hanoi;
        this.diskMoves = diskMoves;
    }
    protected DiskMover(HanoiGroup hanoi, List<DiskMove> diskMoves){
        this.hanoiGroup=hanoi;
        this.diskMoves = diskMoves.toArray(new DiskMove[0]);
    }

    protected  abstract Animation getDiskMoveAnimation(DiskMove move, Action doAfterEachMove);


    public final void start (Action doAfterLastMove)
    {
        currentMoveIndex++;
        if(currentMoveIndex < diskMoves.length){
            DiskMove currentMove = diskMoves[currentMoveIndex];
            Disk disk = currentMove.getDisk();
            Rod destinationRod = currentMove.getDestinationRod();

            disk.setSelected(true);
            destinationRod.setSelected(true);

            currentAnimation = getDiskMoveAnimation(currentMove, () -> {
                disk.getRod().pop();
                disk.setSelected(false);

                destinationRod.push(disk);
                destinationRod.setSelected(false);

                start(doAfterLastMove);
            });
            currentAnimation.setAutoReverse(false);
            currentAnimation.setCycleCount(0);
            currentAnimation.play();
        } else {
            doAfterLastMove.action();
        }
    }

    public final void start() {
        start(()->{});
    }
    public final void setAnimationDuration(double animationDuration) {
        this.animationDuration = animationDuration;
    }
    public final void pause(){
        currentAnimation.pause();
    }
    public final void resume(){
        currentAnimation.play();
    }
    public final void stop(){
        currentAnimation.stop();
        diskMoves[currentMoveIndex].getDisk().setSelected(false);
        diskMoves[currentMoveIndex].getDestinationRod().setSelected(false);
        currentMoveIndex = 0;
    }
    public int getCurrentMoveIndex(){
        return currentMoveIndex;
    }
    public int getNumberOfDiskMoves(){
        return diskMoves.length;
    }
    public interface Action{
        void action();
    }

}
