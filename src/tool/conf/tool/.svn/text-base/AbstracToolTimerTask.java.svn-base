package conf.tool;

import java.util.Calendar;
import java.util.Date;

public abstract class AbstracToolTimerTask {

	/* 执行任务的时间类型 */
	TimerType timerType;
	Date date;
	long delay = 0;
	long period = 1000;

	/*
	 * 执行任务细分开始时间，比如，当TimerType为HOUR时，value = 10, 
	 * 从每一小时的第10分钟开始执行，默认值为0
	 */
	int value = 0;

	public static enum TimerType {
		MIN, HOUR, DAY, WEEK, MONTH;
	}

	/***
	 * 将所有执行任务的时间要求转化为Date类型数据
	 * 
	 * @param type
	 *            - 执行定时任务的时间，小时、天、周 or 月
	 * @param value
	 *            - delay time
	 * @return
	 */
	public Date toTimer(TimerType type, int value) {

		Calendar ca = Calendar.getInstance();

		ca.set(Calendar.SECOND, 0);
		switch (type) {
		case MIN:
			break;
		case HOUR:
			ca.set(Calendar.MINUTE, value);
			break;
		case DAY:
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.HOUR_OF_DAY, value);
			break;
		case WEEK:
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.HOUR_OF_DAY, 0);
			ca.set(Calendar.DAY_OF_WEEK, value);
			break;
		case MONTH:
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.HOUR_OF_DAY, 0);
			ca.set(Calendar.DAY_OF_MONTH, value);
			break;
		default:
		}
		return ca.getTime();
	}
}
