package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.time.info.TimePoint;
import com.myhexin.qparser.time.info.TimeRange;

public class TimeNode extends SemanticNode{

    private static final long serialVersionUID = -3637094957626018093L;

    public TimeNode(String text) {
        super(text);
        this.type = NodeType.TIME;
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Type qtype)
            throws BadDictException {
    }
    
    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setExpandedRange(TimeRange expandedRange) {
        this.expandedRange = expandedRange;
    }

    public TimeRange getExpandedRange() {
        return expandedRange;
    }

    public TimeRange getRealRange(){
        return expandedRange == null?timeRange:expandedRange;
    }
    
    public boolean isFake() {
        return this instanceof FakeTimeNode;
    }
    public String toString() {
        return String.format("NodeType:TIME  %s(Range:%s)", this.text,getRealRange());
    }
    
    /*public TimeNode clone() {
        TimeNode rtn = (TimeNode) (super.clone());
        if (timeRange != null) {
            rtn.timeRange = timeRange.clone();
        }
        if (expandedRange != null) {
            rtn.expandedRange = expandedRange.clone();
        }
        return rtn;
    }*/
    
    public boolean isSuccessfullyParsed() {
        return timeRange!=null;
    }
    
    public String getRangeType() {
        TimeRange range = this.getRealRange();
        return range == null ? null : range.getRangeType();
    }
    /**
     * 原始解析出的范围
     */
    private TimeRange timeRange;
    /**
     * 调整后的范围
     */
    private TimeRange expandedRange;

    public boolean isLength() {
        TimeRange range = this.getRealRange();
        return range == null ? false : range.isLength();
    }

    public boolean canBeAdjust() {
        TimeRange range = this.getRealRange();
        return range == null ? false : !range.isCanNotAdjust();
    }

	@Override
	protected TimeNode copy() {
		TimeNode rtn = new TimeNode(super.text);
		rtn.expandedRange=expandedRange;
		rtn.timeRange=timeRange;


		super.copy(rtn);
		return rtn;
	}

	public int getHourDifference() {
		if (timeRange != null) {
			TimePoint from = timeRange.getFrom();
			TimePoint to = timeRange.getTo();
			int dif = to.getHour() - from.getHour();
			if (dif < 0) {
				dif += 24;
			}
			return dif;
		} else {
			return 0;
		}
	}

	public int getMinuteDifference() {
		if (timeRange != null) {
			TimePoint from = timeRange.getFrom();
			TimePoint to = timeRange.getTo();
			int dif = to.getMin() - from.getMin();
			if (dif < 0) {
				dif += 60;
			}
			return dif;
		} else {
			return 0;
		}
	}

}
