package com.myhexin.qparser;

public class QuerySplitInfo {
    private String value;
    private double prob = -1;
    
    public QuerySplitInfo(String value, double prob){
        this.setValue(value);
        this.setProb(prob);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public double getProb() {
        return prob;
    }  

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("value:").append(value).append(";prob:").append(prob);
        return sb.toString();
    }

    /**
     * 通过分数检测该chunk是否可用,可在配置文件中修改
     * @return
     */
    public boolean canUse() {
        return this.prob > Param.CRF_MIN_PROB;
    }
}
