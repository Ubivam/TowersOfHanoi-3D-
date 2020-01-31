package rs.ac.bg.etf.js150411d.gameflow;

import rs.ac.bg.etf.js150411d.HanoiGroup;

import java.util.List;

public interface GameSolver {

    public void solve();

    public List<DiskMove> getDiskMoves();

}
