package rs.ac.bg.etf.js150411d.animations;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import rs.ac.bg.etf.js150411d.GameWindow;
import rs.ac.bg.etf.js150411d.HanoiGroup;
import rs.ac.bg.etf.js150411d.gameflow.DiskMove;
import rs.ac.bg.etf.js150411d.gameobjects.Disk;
import rs.ac.bg.etf.js150411d.gameobjects.Rod;

import java.security.Key;
import java.util.List;

public class DiskFlipper extends DiskMover{

    public  DiskFlipper(HanoiGroup hanoi, DiskMove... diskMoves){
        super(hanoi,diskMoves);
    }
    public DiskFlipper(HanoiGroup hanoi, List<DiskMove> diskMoves){
        super(hanoi,diskMoves);
    }
    @Override
    protected Animation getDiskMoveAnimation(DiskMove move, Action doAfterEachMove) {
        Disk disk = move.getDisk();
        Rod destinationRod = move.getDestinationRod();

        Disk topDiskDestination = destinationRod.peek();
        double topDiskDestinationTranslateY = topDiskDestination == null ? 0.5 * disk.getBoundsInParent().getHeight() : topDiskDestination.getTranslateY();

        Rotate rotation = new Rotate(0, (destinationRod.getTranslateX() - disk.getRod().getTranslateX()) / 2, 0 , 0, Rotate.Z_AXIS);
        disk.getTransforms().add(rotation);

        double angle = destinationRod.getTranslateX() - disk.getRod().getTranslateX() > 0 ? 180 : -180;

        Animation animation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(disk.translateYProperty(), disk.getTranslateY()),
                        new KeyValue(rotation.angleProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(animationDuration /3),
                        new KeyValue(disk.translateYProperty(), -(HanoiGroup.ROD_HEIGHT + disk.getBoundsInParent().getHeight() / 2)),
                        new KeyValue(rotation.angleProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(2 * animationDuration / 3),
                        new KeyValue(disk.translateYProperty(), -(HanoiGroup.ROD_HEIGHT + disk.getBoundsInParent().getHeight() / 2)),
                        new KeyValue(rotation.angleProperty(),angle)
                ),
                new KeyFrame(Duration.seconds(animationDuration),
                        new KeyValue(disk.translateYProperty(), topDiskDestinationTranslateY- disk.getBoundsInParent().getHeight()),
                        new KeyValue(rotation.angleProperty(), angle)
                )
        );
        animation.setOnFinished(event -> {
            disk.getTransforms().add(new Rotate(-angle,Rotate.Z_AXIS));
            doAfterEachMove.action();
        });
        return animation;
    }
}
