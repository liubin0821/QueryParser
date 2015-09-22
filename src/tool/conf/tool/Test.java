package conf.tool;

import java.util.TimerTask;

public class Test {

	/**
	 * @rm.param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AbstracToolTimerTask.TimerType min = AbstracToolTimerTask.TimerType.MIN ;
		ToolTimerTask  toolTask = new ToolTimerTask.Builder(min).delay(0).period(3000).build() ; 
		Task1 t1 = new Task1() ;
		toolTask.schedule(t1 ,false) ;
		
		Task2 t2 = new Task2() ;
		toolTask.schedule(t2, false ) ;
	}
	
	static class Task1 extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("task1");
		}
		
	}
	static class Task2 extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("task2");
		}
		
	}
	
}
