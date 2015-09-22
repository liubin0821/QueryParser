package com.myhexin.qparser.tokenize;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

import org.springframework.stereotype.Component;

import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TriggerNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.OntoXmlReader;
import com.myhexin.qparser.onto.PropNodeFacade;

/**
 * 对字符串进行识别、合并、ofwhat属性改造。主要根据trigger、字符串型指标进行未识别词到StrVal词语的转换，
 * 以及StrVal的属性改变，并对相邻的相同属性的字符串进行合并处理。
 * 
 */
@Component
public class TriggerParser {
	//private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(TriggerParser.class.getName());
	    

    public ArrayList<SemanticNode> parse(ArrayList<SemanticNode> nodes) {
    	//ArrayList<SemanticNode> nodes = query_.nodes();
    	ArrayList<SemanticNode> words = nodes;
        for(int i = 0; i < words.size(); i++){
            if(words.get(i).type == NodeType.TRIGGER){
                removeExcessTriggerByTriggerProp(words, i);
            }
        }
        
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).type == NodeType.TRIGGER) {
            	if(tagStrOrUnknownByTrigger(words, i, null)){
            		// 当该trigger为冗余时,删除.如: "水泥概念",在"水泥"tag后,删除"概念"节点
            		if(isDuplicateTrigger(words, i)){
            			words.remove(i) ;
            		} else {
            			//String oldText = words.get(i).text;
            			for (String st : ((TriggerNode)words.get(i)).subType) {
            				@SuppressWarnings("unchecked")
                            List<PropNodeFacade> props = (List<PropNodeFacade>) OntoXmlReader.subTypeToPropGet(st);
            				if(props.size()>0)
            					words.set(i, props.get(0));
						}
            			
            			//words.set(i, ((TriggerNode)words.get(i)).trigProp);
            			//query_.getLog().logTransWord(oldText, words.get(i).text);
            		}
            	}
            }else if(isTriggerIndex(words, i)){
            	PropNodeFacade valPropNode = ((ClassNodeFacade)words.get(i)).getPropOfValue();
            	ArrayList<PropNodeFacade> ofWhats = new ArrayList<PropNodeFacade>(1);
            	ofWhats.add(valPropNode);
            	TriggerNode triggerNode = new TriggerNode("");
            	triggerNode.ofWhat_ = ofWhats;
            	triggerNode.trigProp = valPropNode;
            	doTagStrOrUnknown(words, i, Direction.RIGHT,triggerNode);
            }
        }
        
        return nodes;

    }
    
    /***
     * 判断一个trigger是否为冗余的trigger
     * @param nodes
     * @param triggerPos - trigger pos
     * @return
     */
    private boolean isDuplicateTrigger(ArrayList<SemanticNode> nodes, int triggerPos){
    	if(triggerPos >= nodes.size() || triggerPos < 0)
    		return false ;
    	SemanticNode curNode = nodes.get(triggerPos) ;
    	if(curNode.type != NodeType.TRIGGER)
    		return false ;
    	TriggerNode trigger = (TriggerNode)curNode ;
    	
    	int strPos = triggerPos ; 
    	SemanticNode preNode = null;
    	while(true){
    		strPos = ( trigger.direction == Direction.LEFT ?  strPos-1 : strPos+1 ) ; 
    		if(strPos >= nodes.size() || strPos < 0)
        		return false ;
    		preNode = nodes.get(strPos) ;
    		if(preNode.type == NodeType.STR_VAL)
    			break ;
    		else if(preNode.type == NodeType.UNKNOWN && trigger.skipList_ != null && trigger.skipList_.contains(preNode.getText())){
    			continue ;
    		} else {
    			return false ;
    		}    		
    	}
    	
    	if(preNode.type != NodeType.STR_VAL)
    		return false ;
    	StrNode strNode = (StrNode)preNode ;
    	if(strNode.isTagedBySelf == true)
    		return true ;
    	return false ;
    }
    
	private boolean isTriggerIndex(ArrayList<SemanticNode> nodes, int i) {
		if(nodes.get(i).type != NodeType.CLASS){
			return false;
		}
		
		ClassNodeFacade ClassNodeFacade = (ClassNodeFacade)nodes.get(i);
		PropNodeFacade valPropNode = ClassNodeFacade.getPropOfValue();
		return valPropNode != null && valPropNode.isStrProp() && !MiscDef.BOOL_VAL_PROP.equals(valPropNode.getText());
		    
	}

	/**
     * 清除多余的TriggerNode<br>
     * 若一个TriggerNode后还有TriggerProp相同的TriggerNode，将多余的TriggerNode删除
     */
    private void removeExcessTriggerByTriggerProp(ArrayList<SemanticNode> nodes,
            int pos) {
        TriggerNode trigger = (TriggerNode) nodes.get(pos);
        int curPos = pos;
        while (++curPos < nodes.size()) {
            SemanticNode nodeI = nodes.get(curPos);
            if (nodeI.type == NodeType.STR_VAL 
                    || nodeI.type == NodeType.UNKNOWN) {
                continue;
            } else if (nodeI.type != NodeType.TRIGGER) {
                break;
            }
            TriggerNode tnI = (TriggerNode) nodeI;
            boolean needRemove = tnI.trigProp == trigger.trigProp;
            // 若两个Trigger的TriggerProp一致，则删掉后面的那个
            if (needRemove) {
                if(tnI.isIndex_ && !trigger.isIndex_) {
                    nodes.set(pos, tnI); //若现在的不产生指标而待删除的要产生指标，则替换现在的
                } 
                nodes.remove(curPos);
                curPos--;
                // pos = direction == Direction.LEFT ? pos-- : pos;
                // 暂对位置没有改动
            }
        }
    }
    
 
    
    /**
     * 为了保险起见，对于前面是字符串、后面是未识别词并且该词语的长度大于2的trigger进行跳过
     * （因为至少有一个明确的字符串值，被识别出来。如果还需要识别后面的未识别词，则通过添加词典做到）
     * 此外，之所以放过长度为1的unknown词，是因为这些词有很多产品只包含一个字，或一些特殊符号：
     * 例如，主营业务包括钛、钨、锑、稀土，对于“、”可以跳过
     * @param nodes
     * @param from
     * @param to
     * @param logic
     */
    private boolean doTagStrOrUnknown(List<SemanticNode> nodes, int startPos, Direction dir, 
    		 TriggerNode triggerNode){
    
    	if(dir == Direction.LEFT){
    		if(startPos <= 0){
    			return false;
    		}
    	}else if(startPos >= nodes.size() -1){
    		return false;
    	}
    	
    	boolean addForce = false;
    	if(dir == Direction.RIGHT){
    		//如果trigger或者字符串指标紧跟，包含、等于这类词语，则强制将其作为StrNode的属性
    		SemanticNode currNode = nodes.get(startPos+1);
    		if(currNode.getText().matches(MiscDef.TRIGGER_WORD_SET)){
    			addForce = true;
    			if(currNode.type == NodeType.OPERATOR){
    				nodes.set(startPos+1, new UnknownNode(currNode.getText()));
    			}
    		}
    	}
    	int curPos = startPos;
    	boolean goLeft = dir == Direction.LEFT;
        boolean rtn = false;
    	// 检查紧邻的是否可忽略
        String next = goLeft ? nodes.get(startPos - 1).getText() : nodes
                .get(startPos + 1).getText();
        if (triggerNode.skipList_ != null && triggerNode.skipList_.contains(next)) {
            curPos = goLeft ? startPos - 1 : startPos + 1;
            boolean canTryAgain = goLeft ? curPos > 0
                    : curPos < nodes.size() - 1;
            String nextString = canTryAgain ? goLeft ? nodes.get(curPos - 1).getText()
                    : nodes.get(curPos + 1).getText()
                    : null;
            if (canTryAgain && triggerNode.skipList_.contains(nextString)) {
                canTryAgain = false;
                curPos = goLeft ? startPos - 2 : startPos + 2;
            }
        }

        int nTagged = 0;
        final int MAX_TAG_ = 1;// 最多可标记的节点数
        boolean isPreStr = false;
        while ((goLeft ? --curPos > -1 : ++curPos < nodes.size())
                && nTagged < MAX_TAG_) {
            SemanticNode curNode = nodes.get(curPos);
            boolean isStrOrUnknownNode = curNode.type == NodeType.UNKNOWN
                    || curNode.type == NodeType.STR_VAL;
            
            boolean isStopSign = curNode.type != NodeType.STR_VAL &&  triggerNode.stopList_ != null && triggerNode.stopList_.contains(curNode.getText());
            isStopSign |= curNode.type != NodeType.STR_VAL && SpecialWords.hasWord(curNode.getText(),
                    SpecialWordType.TRIGGER_STOP);
            isStopSign |= dir == Direction.LEFT 
            		&& curNode.getText().matches(MiscDef.TRIGGER_WORD_SET);
            boolean isSkipSign = curNode.type != NodeType.STR_VAL && curNode.getText().matches(MiscDef.TRIGGER_WORD_SET)
            		&& dir == Direction.RIGHT;
            //例如生产飞机但是不包含火车的
            if (!isStrOrUnknownNode || isStopSign) {
                break;
            }
            if(isSkipSign){
            	continue ;
            }
            //对于StrNode来说，强制添加该属性
            if (curNode.type == NodeType.STR_VAL) {
            	isPreStr = true;
                StrNode sn = (StrNode) curNode;
                
                /****** 此代码暂时不用   *******
                boolean isOverlapTrigger = false ;
                SemanticNode startPosNode = nodes.get(startPos) ;
                if(startPosNode.type == NodeType.TRIGGER){
                	isOverlapTrigger = ((TriggerNode)startPosNode).isOverlap() ;
                }
                ****************************/
                // 标记返回值
                int canTagValue = canTagStr(sn, triggerNode.subType, triggerNode.ofWhat_, false) ;
                if ( canTagValue > 0 || addForce ) {
                    rtn = true;
                    sn.isTag = true;
                    nTagged++;
                    // 使用STR自身ofWhat,不再作标记
                    if(canTagValue != 2) {
                    	//sn.addOfWhat(triggerNode.trigProp);
                    	sn.subType.addAll(triggerNode.subType);
                    	sn.isTag = true;                 
                    } 
                    
                } else { // 遇到不可标记的StrNode，则不再标记
                    break;
                }
            } else { // 处理UnknownNode
                UnknownNode un = (UnknownNode) curNode;
                if (un.isTag) {
                    break;
                } // 已被标记过，不再向前标记
                un.isTag = true;
                if (SpecialWords.hasWord(un.getText(),
                        SpecialWordType.TRIGGER_STOP)) {
                    break;
                }else if(isPreStr && un.getText().length() >= 2){
                	break;
                }
                isPreStr = false;
                if (SpecialWords.hasWord(un.getText(), SpecialWordType.TRIGGER_SKIP)) {
                    continue;
                }
                // 成功标记，将其转成StrNode
                rtn = true;
                StrNode sn = new StrNode(un.getText());
                if(triggerNode.subType != null && triggerNode.subType.size()>0)
                	sn.subType = triggerNode.subType;
                sn.isTag = true;
                //测试无误后删除 wyh 20114.11.
                //sn.ofWhat.add(triggerNode.trigProp);        
                nodes.set(curPos, sn);
                nTagged++;
            }
        }
        return rtn;
    }
    
    /**
     * 根据TriggerNode标记未识别词条，或有歧义的StrNode
     * TriggerNode的isIndex_为true,即本身也是指标的，若未标记成功，则推后到pattern处理之后
     * TODO: 为什么推后？
     */
    private boolean tagStrOrUnknownByTrigger(List<SemanticNode> nodes,
            int triggerPos, Direction dir) {
        TriggerNode triggerNode = (TriggerNode) nodes.get(triggerPos);
        if (dir == null) {
            dir = triggerNode.direction;
        }
        boolean goLeft = dir == Direction.LEFT && triggerPos > 0;
        boolean goRight = dir == Direction.RIGHT
                && triggerPos < nodes.size() - 1;
        boolean rtn = false;
        if (dir == Direction.BOTH) {
            // 可以两个方向标记的，先标记左侧。若未标记成功，则标记右侧
            rtn = doTagStrOrUnknown(nodes, triggerPos, Direction.LEFT,
            		triggerNode);
            if (!rtn) {
            	doTagStrOrUnknown(nodes, triggerPos, Direction.RIGHT,
                		triggerNode);
            }
            return rtn;
        } else if (!goLeft && !goRight) {
            return false;
        }

        return doTagStrOrUnknown(nodes, triggerPos, dir, triggerNode);
    }
    
	private int canTagStr(StrNode nodeK, LinkedHashSet<String> subType,
	        List<PropNodeFacade> triggerOfWhat, boolean isOverlapTrigger) {

		if(triggerOfWhat == null)
			return -1;
		StrNode sn = (StrNode) nodeK;
		//ArrayList<SemanticNode> snOfWhat = sn.ofWhat;
		
		boolean isTag = sn.isTag;
		if (isTag) {
			// 若已被标记，则不再标记
			return -1;
		} /*else if (containProp(snOfWhat,subType.text)) {
			// 若可绑定，则确认可标记. 使用trigger的prop标记
			return 2;//strNode已经存在该Prop
		}*/
  		return 1;
	}
	
	private boolean containProp(ArrayList<SemanticNode> snOfWhat, String prop){
		for (SemanticNode sn : snOfWhat) {
			if (sn.type == NodeType.PROP && sn.getText().equals(prop)) {
				return true;
			}
		}
		return false;
	}
}
