/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-2-26 上午10:05:32
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.bind.stateMachine;

import java.util.ArrayList;
import java.util.HashSet;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;

public class PhraseParserPluginOneTripBindPropToIndex  extends PhraseParserPluginAbstract{

	    public PhraseParserPluginOneTripBindPropToIndex() {
	        super("## Bind Props to Index");
	    }
	    
		public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
			ArrayList<SemanticNode> nodes = annotation.getNodes();
	    	return bindPropToIndex(nodes);
	    }


	    private ArrayList<ArrayList<SemanticNode>> bindPropToIndex(ArrayList<SemanticNode> nodes)
	    {
	        ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>();
	        new OneTripBindPropToIndex(nodes).bind(NodeType.STR_VAL);
	        qlist.add(nodes);
	        return qlist;
	    }	
}

class OneTripBindPropToIndex{
	public static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(OneTripBindPropToIndex.class.getName());
	
	ArrayList<Integer> indexs = new ArrayList<Integer>(); //依次记录index在nodeList中的位置
	ArrayList<BoundaryToIndex> boundarys = new ArrayList<BoundaryToIndex>();//依次记录boundary在nodeList中的位置
	ArrayList<BindNode> nodeList = new ArrayList<BindNode>();
	HashSet<NodeType> types = new HashSet<NodeType>();//本次绑定需要绑定的类型
	private final int indexsLength;
	private final int boundarysLength;
	
    //机器本身包含所有的状态机  
    private State leftAndRightNotOverState;  //向左向右都没有超过boundary
    private State leftNotOverRightOverState;  //向左没有超过boundary , 向右超过了
    private State leftOverRightNotOverState;  //想左超过boundary, 向右没有
    private State leftAndRightOverState;    //向右向右都超过了boundary
  
    private State state; //机器的当前状态  
	
    
    public void changeToLeftAndRightNotOverState() {
    	this.state =  leftAndRightNotOverState;
    }


	public void changeToLeftNotOverRightOverState() {
		this.state = leftNotOverRightOverState;
    }


	public void changeToLeftOverRightNotOverState() {
		this.state = leftOverRightNotOverState;
    }


	public void changeToLeftAndRightOverState() {
		this.state = leftAndRightOverState;
    }
	
	public final int getRightIndexPos(int boundaryListPos,BindNode bn) {
		int indexPos = -1;
		if(bn.bindInfo.spandBoundary == 0)
			indexPos=bn.leftIndex;
		else
		 indexPos = boundarys.get(boundaryListPos-1).indexPosInIndexList;
		
	    return indexPos+bn.bindInfo.spandIndex;
    }
	
	public final int getLeftIndexPos(int boundaryListPos,BindNode bn) {
		int indexPos = -1;
		if(bn.bindInfo.spandBoundary == 0)
			indexPos=bn.leftIndex;
		else
		 indexPos = boundarys.get(boundaryListPos+1).indexPosInIndexList;

	    return indexPos-bn.bindInfo.spandIndex + 1;
    }



	public void bind(NodeType... type) {
    	if(type.length == 0) return;
    	
    	for (NodeType nodeType : type) {
    		types.add(nodeType);
		}

    	for (BindNode bn : nodeList) {
			if(types.contains(bn.sn.type)){//该类型属性进行绑定
				FocusNode fn = null;
				while((fn = getNextBindIndex(bn)) !=null){
					//System.out.println(bn.sn);
					//System.out.println(fn);
				}
			}
		}
    }
  
    
    /**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-26 下午2:43:30
     * @description:  得到下一个能应该绑定的指标 	
     * 
     */
    private final FocusNode getNextBindIndex(BindNode bn) {
    	int pos = getNextBindIndexPos(bn);
    	//前面有逻辑保证 indexs中都是index
    	if (pos == -1) {
			return null;
		} else if (pos == -2) {
			logger_.error("状态转换错误");
			return null;
		}
	    return (FocusNode) nodeList.get(indexs.get(pos)).sn;
    }
    
    public int getNextBindIndexPos(BindNode bn) {
	    return this.state.getNextIndexPos(bn);//计算出下一个index的位置
    }


	public OneTripBindPropToIndex(ArrayList<SemanticNode> nodes) {
		this.leftAndRightNotOverState = new LeftAndRightNotOverState(this);
		this.leftNotOverRightOverState = new LeftNotOverRightOverState(this);
		this.leftOverRightNotOverState = new LeftOverRightNotOverState(this);
		this.leftAndRightOverState = new LeftAndRightOverState(this);
		
		this.state = leftAndRightNotOverState;//初始状态
		
    	init(nodes);
    	indexsLength = indexs.size();
    	boundarysLength = boundarys.size();
    }

    

	public boolean isIndexInList(int indexPos){
		return  indexPos>=0 && indexPos<indexsLength;
	}
	
	public boolean isBoundaryInList(int boundaryPos){
		return  boundaryPos>=0 && boundaryPos<boundarysLength;
	}

	private void init(ArrayList<SemanticNode> nodesOrgin) {
    	ArrayList<SemanticNode> nodes = null;
    	nodes = getNodesWithOutDefaultNodes(nodesOrgin);//句式中会产生默认指标,默认值; 把这些默认值变成非默认表示
    	int size = nodes.size();
    	BoundaryToIndex bti = null;
    	for (int i = 0;i<size ;i++) {
    		SemanticNode sn = nodes.get(i);
			switch (sn.getType()) {
			case BOUNDARY:
				bti = new BoundaryToIndex();
				bti.boundaryPos = i;
				if (indexs.size()>0) 
					bti.indexPosInIndexList = indexs.size()-1;				
				boundarys.add(bti);
				addToNodeList(sn,indexs.size()-1,boundarys.size()-2);
				break;
			case FOCUS:
				FocusNode fn = (FocusNode)sn;
				if(fn.hasIndex())
					indexs.add(i);
				addToNodeList(sn,indexs.size()-2,boundarys.size()-1);
				break;	
			default:
				addToNodeList(sn,indexs.size()-1,boundarys.size()-1);
			}
		}
    }

    /**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-26 下午1:35:59
     * @description:  句式中会产生默认指标,默认值; 把这些默认值变成非默认表示 	
     * 
     */
    private ArrayList<SemanticNode> getNodesWithOutDefaultNodes(
            ArrayList<SemanticNode> nodes) {
	    ArrayList<SemanticNode> nodesNew = new ArrayList<SemanticNode>();
	    for (SemanticNode sn : nodes) {
			if (sn.type == NodeType.BOUNDARY && ((BoundaryNode)sn).isStart()) {
				nodesNew.add(sn);
				getDefaultNode(nodesNew, sn);
				continue;
			} 
			nodesNew.add(sn);
		}
	    return nodesNew;
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-2-26 下午1:49:46
     * @description:  得到默认节点 	
     * 
     */
    private final void getDefaultNode(ArrayList<SemanticNode> nodesNew,
            SemanticNode sn) {
	    BoundaryNode.SyntacticPatternExtParseInfo info = ((BoundaryNode)sn).getSyntacticPatternExtParseInfo(false);
	    ArrayList<Integer> elelist;
	    for (int j = 1; (elelist = info.getElementNodePosList(j)) != null; j++) {
	    	for (int pos : elelist) {
	    		if (pos == -1) {
	    			SemanticNode defaultNode = info.absentDefalutIndexMap.get(j);
	    			if(defaultNode != null)
	    				nodesNew.add(defaultNode);
	    		}
	    	}
	    }
    }

	private final void addToNodeList(SemanticNode sn, int leftIndex, int leftBoundary) {    	
    	BindNode bn = new BindNode(sn,leftIndex,leftBoundary);
    	nodeList.add(bn);
    }	
}
 class BoundaryToIndex{
	 int boundaryPos = -1;
	 int indexPosInIndexList = -1;
 }
