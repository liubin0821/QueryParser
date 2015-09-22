package com.myhexin.qparser.time;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.conf.ModifyDefInfo;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.HiddenType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FakeTimeNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.time.info.TimeRange;
import com.myhexin.qparser.time.parse.TimePatterns;
import com.myhexin.qparser.time.parse.TimeStrParser;
import com.myhexin.qparser.time.tool.TimeUtil;

@Component
public class TimeParser {    
    
	@Autowired(required = true)
	private TimeMerger timeMerger = null;
	
	
	
    public void setTimeMerger(TimeMerger timeMerger) {
    	this.timeMerger = timeMerger;
    }

	public ArrayList<SemanticNode> parse(ArrayList<SemanticNode> nodes){
       
        try {
        	turnTimeStrToTime(nodes);
            mergeTime(nodes);
            turnNumToTime(nodes);
            parseTimeRange(nodes);
            tagFakeTime(nodes);
            mergeFakeTime(nodes);
            checkTime(nodes);
        } catch (UnexpectedException e) {
                logger_.debug(e.getLogMsg());

        }
        return nodes;
    }
	
	/**首先把是time类型的节点转为TimeNode*/
	private void turnTimeStrToTime(ArrayList<SemanticNode> nodes){
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode curNode = nodes.get(i);
			boolean isFitTime = TimePatterns.LENGTH_HOUR.matcher(curNode.getText()).matches();//时间区间类型
            isFitTime = isFitTime|| TimePatterns.LENGTH_MIN.matcher(curNode.getText()).matches();
            isFitTime = isFitTime|| TimeUtil.isMatchTimePoint(curNode.getText());//时间点类型
			if(isFitTime){
				TimeNode timeNode = new TimeNode(curNode.getText());
				nodes.set(i, timeNode);
			}
		}
	}
	
	/**
     * 将剩余数字型分时信息根据句中信息转为分时节点，暂只按右侧是否有技术面相关信息进行判断。
     * @throws UnexpectedException
     */
    private void turnNumToTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
        for (int i = 0; i < nodes.size(); i++) {
            SemanticNode curNode = nodes.get(i);
            if(curNode.type == NodeType.TECH_PERIOD) continue;
            boolean isFitTime = TimePatterns.LENGTH_HOUR.matcher(curNode.getText())
                    .matches();
            isFitTime = isFitTime
                    || TimePatterns.LENGTH_MIN.matcher(curNode.getText())
                            .matches();
            isFitTime = isFitTime || TimeUtil.isMatchTimePoint(curNode.getText());
            isFitTime = isFitTime || TimePatterns.AM_OR_PM.matcher(curNode.getText()).matches();
            isFitTime = isFitTime || TimePatterns.STATES.matcher(curNode.getText()).matches();//尾盘
            if (!isFitTime)
                continue;
            boolean canTurnToTime = numCanTurnToTime(nodes,i);
            if (!canTurnToTime)
                continue;
            TimeNode repNode = new TimeNode(curNode.getText());
            nodes.set(i, repNode);
        }
    }

    private boolean numCanTurnToTime(ArrayList<SemanticNode> nodes, int curPos) throws UnexpectedException {
      boolean canTrun = !TimeUtil.nextHasTechNodes(nodes, curPos);
      if(!canTrun)return false;
      //TODO:就较特殊的问句进行处理，如 macd 60分钟
      for (SemanticNode sn : nodes) {
		if (sn.type==NodeType.FOCUS) {
			FocusNode fn=(FocusNode) sn;
			
			if (fn.hasIndex()) {
				ClassNodeFacade cn = fn.getIndex();
				
				if(cn!=null && cn.hasTechAnalyPeriodProp())
					return false;
			}
		}
      }

      return true;
    }

    private void checkTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
        for (int i = 0; i < nodes.size(); i++) {
            SemanticNode curNode = nodes.get(i);
            if (curNode.type != NodeType.TIME)
                continue;
            TimeNode curTimeNode = (TimeNode) curNode;
            if (curTimeNode.isFake())
                continue;
            TimeRange range = curTimeNode.getRealRange();
            if (range == null || !range.isLegal()) {
                /*int curChunkID = nodes.getChunkID(i);
                logChunkTimeErr(curChunkID, String.format(
                        MsgDef.NOT_SUPPORTED_TIME_FMT, curTimeNode.text));*/
            }
        }
    }

    public void deepParse(ArrayList<SemanticNode> nodes) {
		connectWithDate(nodes);
		try {
			inferDefDate(nodes);
		} catch (QPException e) {

			logger_.debug(e.getLogMsg());

		}
    }   
    

    /**
     * 若分时时间未与任何其他句中时间{@link DateNode}关联，则默认取用“今天”
     * @throws NotSupportedException 
     * @throws UnexpectedException 
     */
    private void inferDefDate(ArrayList<SemanticNode> nodes) throws UnexpectedException,
            NotSupportedException {
        for (int i = 0; i < nodes.size(); i++) {
        	if(nodes.get(i)!=null && nodes.get(i).isSkipOldDateParser()) continue;
        	
            SemanticNode curNode = nodes.get(i);
            if (curNode.type != NodeType.TIME)
                continue;
            TimeNode curTime = (TimeNode) curNode;
            DateNode addDate = DateUtil.makeDateNodeFromStr(MiscDef.TIME_INDEX_DEF_TIME, null);
            addDate.setText(curTime.getText() );//在新系统时间显示time
            addDate.hiddenType = HiddenType.FAKE_TIME_HIDDEN;
            addDate.setTime(curTime);
            nodes.set(i, addDate);
        }
    }

    /**
     * 与句中其他时间{@link DateNode}关联
     */
    private void connectWithDate(ArrayList<SemanticNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
        	if(nodes.get(i)!=null && nodes.get(i).isSkipOldDateParser()) continue;
        	
            SemanticNode curNode = nodes.get(i);
            if (curNode.type != NodeType.TIME)
                continue;
            TimeNode curTime = (TimeNode) curNode;
			if (curTime.getText().contains("分钟")) {
				//2015年7月4日30分钟这样的时间显然不能和日期接起来
				continue;
			}
            // 暂只关联最近的时间,只跨过空白
            int lastNodePos = NumUtil.getLastOrNextNode1(i, nodes, true);
            if (lastNodePos < 0)
                continue;
            SemanticNode lastNode = nodes.get(lastNodePos);
            if(lastNode.type != NodeType.DATE)
            	continue;
            DateNode lastDate = (DateNode) lastNode;
            boolean dateCanHasTime = dateCanHasTime(lastDate);
            if (!dateCanHasTime)
                continue;
            lastDate.setText( lastDate.getText() + curTime.getText() );
            lastDate.setTime(curTime);
            mergeAsGroup(nodes, i, lastNodePos);
            i--;
        }
    }
    
    /**
     * 将src节点加入到dest节点。src节点保存到dest中并从序列中移除，dest的text不发生变化。
     */
    public void mergeAsGroup(ArrayList<SemanticNode> nodes,int src, int dest) {
        
        SemanticNode snSrc = nodes.get(src), snDest = nodes.get(dest);
        snDest.addGroupNode(snSrc);
        nodes.remove(src);
    }


    private boolean dateCanHasTime(DateNode lastNode) {
        boolean canHasTime = true;
        try {
            canHasTime = !lastNode.isFake();
            canHasTime = canHasTime && !lastNode.isLength();
            canHasTime = canHasTime && lastNode.isExactlyOneDay();
            Unit unit = lastNode.getUnitOfDate();
            canHasTime = canHasTime && unit == Unit.DAY;
            canHasTime = canHasTime && lastNode.getTime() == null;
        } catch (UnexpectedException e) {
            canHasTime = false;
        }
        return canHasTime;
    }

    private void mergeFakeTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
        timeMerger.mergeFakeTime(nodes);
        
    }

    private void tagFakeTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
        for (int i = 0; i < nodes.size(); i++) {
            SemanticNode curNode = nodes.get(i);
            if (curNode.type != NodeType.UNKNOWN)
                continue;
            if (!ModifyDefInfo.isTimeModify(curNode.getText(), Type.STOCK)) {
                continue;
            }
            FakeTimeNode repTimeNode = new FakeTimeNode(curNode.getText());
            nodes.set(i, repTimeNode);
        }
    }

    private void parseTimeRange(ArrayList<SemanticNode> nodes) throws UnexpectedException {
        for (int i = 0; i < nodes.size(); i++) {
            SemanticNode curNode = nodes.get(i);
            if (curNode.type != NodeType.TIME)
                continue;
            TimeNode curTimeNode = (TimeNode) curNode;
            try {
                TimeRange range = TimeStrParser.parse(curNode.getText());
                curTimeNode.setTimeRange(range);
            } catch (QPException e) {
                
            }
        }
    }

    private void mergeTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
        timeMerger.merge(nodes);
    }


    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
    .getLogger(TimeParser.class.getName());
    
    

}
