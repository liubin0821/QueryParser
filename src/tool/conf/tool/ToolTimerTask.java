package conf.tool;

import java.util.Timer;
import java.util.TimerTask;

public class ToolTimerTask extends AbstracToolTimerTask {

	private static Timer timer;

	public static class Builder {
		// required parameters
		private AbstracToolTimerTask.TimerType timerType;

		// optional parameters
		private int value = 0;
		private long delay = 0;
		private long period = 1000;

		// required parameters initialize
		public Builder(AbstracToolTimerTask.TimerType timerType) {
			this.timerType = timerType;
		}

		public Builder(long delay, long period) {
			this.delay = delay;
			this.period = period;
		}

		// optional parameters initialize
		public Builder value(int value) {
			this.value = value;
			return this;
		}

		public Builder delay(long delay) {
			this.delay = delay;
			return this;
		}

		public Builder period(long period) {
			this.period = period;
			return this;
		}

		// build
		public ToolTimerTask build() {
			return new ToolTimerTask(this);
		}
	}

	// private constructor
	private ToolTimerTask(Builder builder) {
		this.timerType = builder.timerType;
		this.value = builder.value;
		this.delay = builder.delay;
		this.period = builder.period;
		this.date = this.toTimer(timerType, value);
	}

	private static Timer getTimer() {
		if (timer == null)
			timer = new Timer();
		return timer;
	}

	/***
	 * 执行任务
	 * 
	 * @param task
	 *            - 将要执行的任务
	 * @param flg
	 *            - true 安排在指定的时间执行指定的任务 false 安排指定的任务在指定的时间开始进行重复的固定延迟执行。
	 *            延迟时间默认为0，重复时间默认为1s
	 */
	public void schedule(TimerTask task, boolean flg) {
		if (flg)
			getTimer().schedule(task, this.date);
		else
			getTimer().schedule(task, delay, period);
	}
}
