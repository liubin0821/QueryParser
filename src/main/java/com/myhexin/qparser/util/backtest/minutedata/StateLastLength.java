package com.myhexin.qparser.util.backtest.minutedata;


/**
 * 
 * 用于保存 状态的 持续时间， 比如 连续5分钟 股价低于均价 ，对于这样的分时问句， 状态是  股价低于均价，然后持续时间为5分钟。
 * 
 * 这是一个 Option 属性， 对于多数问句 可能没有这个属性。
 *
 */
public class StateLastLength {

    private int timeLength;

    public StateLastLength(int lastLength) {
        this.timeLength = lastLength;
    }
    
    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

}
