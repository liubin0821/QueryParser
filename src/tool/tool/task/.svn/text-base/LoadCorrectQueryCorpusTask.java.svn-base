package tool.task;

import java.util.TimerTask;

import bench.BenchManager;


/***
 * 定时从正确问句库中更新问句
 * 
 * @author Administrator
 * 
 */
public class LoadCorrectQueryCorpusTask extends TimerTask {
	private BenchManager benchManager;

	LoadCorrectQueryCorpusTask(BenchManager benchManager) {
		this.benchManager = benchManager;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		benchManager.addNewComingStdQuery();
	}
}
