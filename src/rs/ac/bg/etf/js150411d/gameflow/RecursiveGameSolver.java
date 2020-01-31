package rs.ac.bg.etf.js150411d.gameflow;

import rs.ac.bg.etf.js150411d.HanoiGroup;
import rs.ac.bg.etf.js150411d.gameobjects.Disk;
import rs.ac.bg.etf.js150411d.gameobjects.Rod;

import java.util.ArrayList;
import java.util.List;

public class RecursiveGameSolver implements GameSolver {

    private  final HanoiGroup hanoi;
    private final List<DiskMove> moves = new ArrayList<>();

    public RecursiveGameSolver(HanoiGroup hanoi){
        this.hanoi = hanoi;
    }

    public void move(int n, Rod sourceRod, Rod targetRod, Rod auxiliaryRod){
        if(n > 0){
            move(n-1,sourceRod, auxiliaryRod, targetRod);

            moves.add(new DiskMove(sourceRod.peek(),targetRod));
            targetRod.push(sourceRod.pop());
            move(n-1,auxiliaryRod,targetRod,sourceRod);
        }
    }
    @Override
    public void solve() {
        moves.clear();
        move(hanoi.getNumberOFDisks(),hanoi.getRods()[0],hanoi.getRods()[2],hanoi.getRods()[1]);
        hanoi.getRods()[0].getDiskFrom(hanoi.getRods()[2]);
    }

    @Override
    public List<DiskMove> getDiskMoves() {
        return moves;
    }
}
