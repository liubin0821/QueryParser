/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package com.myhexin.qparser.tokenize;

import java.util.regex.Pattern;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.onto.ClassNodeFacade;

/**
 * the Class SortParser
 * 解析问句中的排序节点，主要处理两类事情：1）识别可能的排序节点；2）合并补全排序节点的参数值。
 * 例如：2012年分红最高的前30家。1）排序识别，将“前30家”识别为排序节点 2）排序合并， “最高”与“前30家”合并，
 * 形成“最高的前30家”，最终变为："2012年" "分红" "最高的前30家"。
 *
 */
public class SortParser {
	private static Pattern PTN_LEFT_SORT_WORDS = Pattern.compile("前|第|后");
	private static Pattern PTN_RIGHT_SORT_WORDS = Pattern.compile("名");
	private Query query;
	
	/**
	 * @param query
	 */
	public SortParser(Query query){
		this.query = query;
	}
	
	/**
	 * 具体处理“排序节点的识别”以及“排序节点的合并”两部分类容。<br>
	 * 例如：2012年分红最高的前30家。1）排序识别，将“前30家”识别为排序节点 2）排序合并，
	 * “最高”与“前30家”合并，形成“最高的前30家”。
	 */
	public void parse(){
		genSortFromSeqcialNum();
		tryMegerMulSorts2One();
	}
	
    /**
     * 根据强规则识别排序节点，检查问句中单位为%，或无单位且起止数字均为整的数字前，是否有排序提示词，如“前30%”或“前3”。
     * 若有，则将该数字节点及其排序提示词转化为排序节点。 
     */
	private void genSortFromSeqcialNum() {
		for(int idx = 1; idx < query.getNodes().size(); idx++){
			SemanticNode tmpNode = query.getNodes().get(idx);
			if(tmpNode.type != NodeType.NUM){
				continue ;
			}
			String left = query.getNodes().get(idx - 1).getText();
			String right = (idx < query.getNodes().size()-1)?query.getNodes().get(idx+1).getText():"";
            NumNode numNode = (NumNode) tmpNode;
            if(numNode.getNuminfo() == null){
            	continue ;
            }else if(PTN_LEFT_SORT_WORDS.matcher(left).matches()) {
            	Unit unit = numNode.getUnit();
    			if(unit == Unit.JIA || unit == Unit.ZHI
    					|| unit == Unit.GE || unit == Unit.HU || unit == Unit.GU
    					|| unit == Unit.PERCENT || unit == Unit.WEI || unit == Unit.UNKNOWN){
    				String text = left + tmpNode.getText();
    				double k = numNode.getFrom();
    				String rangeType = numNode.getRangeType();
    				if (NumUtil.isLessType(rangeType)) {
    					k = numNode.getTo();
    				}
    				
    				SortNode sortNode = new SortNode(text);
    				sortNode.k_ = unit == Unit.PERCENT ? k / 100 : k;
    				sortNode.setDescending_(left.matches("前|第"));
    				sortNode.isTopK_ = !left.matches("第")||
    						left.matches("第")&&(unit != Unit.PERCENT)&&k==1.0;
    	            query.getNodes().set(idx - 1, sortNode);
    	            query.getNodes().remove(idx--);
    			}
            }else if(isSortNumMeetRightCond(idx)){
            	
            	String text = tmpNode.getText() + right;
    			double k = numNode.getFrom();
    			String rangeType = numNode.getRangeType();
    			if (NumUtil.isLessType(rangeType)) {
    				k = numNode.getTo();
    			}
    			Unit unit = numNode.getUnit();
    			SortNode sortNode = new SortNode(text);
    			sortNode.k_ = unit == Unit.PERCENT ? k / 100 : k;
    			sortNode.setDescending_(true);
    			sortNode.isTopK_ = true;
                query.getNodes().set(idx, sortNode);
                query.getNodes().remove(idx+1);
            	
            }
		}
	}
	
	private boolean isSortNumMeetRightCond(int numIndex) {
		assert(query.getNodes().get(numIndex).type == NodeType.NUM);
		NumNode numNode = (NumNode) query.getNodes().get(numIndex);
		Unit unit = numNode.getUnit();
		boolean isSortUnit = unit == Unit.UNKNOWN;
		if(!isSortUnit) return false;
		boolean isLeftIndex = query.getNodes().get(numIndex -1).type == NodeType.CLASS;
		if(!isLeftIndex) return false;
		ClassNodeFacade ClassNodeFacade = (ClassNodeFacade)query.getNodes().get(numIndex -1);
		if(!ClassNodeFacade.isNumIndex()) return false;
		String right = (numIndex < query.getNodes().size()-1)?query.getNodes().get(numIndex+1).getText():"";
		boolean isRightSortWord = PTN_RIGHT_SORT_WORDS.matcher(right).matches();
		
		return isLeftIndex && isSortUnit && isRightSortWord;
	}

	/**
	 * 对最、排、从高到底等排序的节点进行处理，包括合并其他排序节点，查找该列点的排序范围。
	 * 主要根据，数字的单位、位置信息等潜规则进行。例如：股价跌幅前50的，90家股价最高的融资融券股票等
	 */
	private void tryMegerMulSorts2One() {
		for(int idx = 0; idx < query.getNodes().size();idx++ ){
			SemanticNode tmpNode = query.getNodes().get(idx);
			if(tmpNode.type != NodeType.SORT){
				continue ;
			}
			
			SortNode sortNode = (SortNode)tmpNode;
			//只对可能与其他数字或排序联合起来的排序说法进行合并，xx最好的xx只的，xx家xx最好的
			if(!sortNode.getText().matches("(最|从|排|至|降|升).{1,4}")){
				continue ;
			}
			//如果该类排序已经找到相应的排序参数则跳过处理
			if(sortNode.k_ > 0 && sortNode.k_ != 1){
				continue ;
			}
			int passUnknownCount = 0;
			boolean findSortK = false;
			//排序数字在后时，一般是近邻排序数字
			for(int j = idx + 1; j < query.getNodes().size(); j++){
				SemanticNode tmpNode2 = query.getNodes().get(j);
				if(tmpNode2.type == NodeType.SORT){
					SortNode sortNode2 = (SortNode)tmpNode2;
					sortNode.k_ = sortNode2.k_;
					sortNode.setDescending_(sortNode.isDescending_() 
							&& sortNode2.isDescending_());
					String text = "";
					for(int k= j; k>idx; k--){
						text = query.getNodes().get(k).getText() + text;
						//删除后面的节点(只含有unknown节点或sort节点)
						query.getNodes().remove(k);
					}
					sortNode.setText(sortNode.getText() + text);
					findSortK = true;
					break;
				}else if(tmpNode2.type == NodeType.NUM){
					NumNode numNode = (NumNode)tmpNode2;
					Unit unit = numNode.getUnit();
					if(unit == Unit.HU || unit == Unit.ZHI || unit == Unit.JIA || unit == Unit.GE || unit == Unit.WEI
							|| (unit == Unit.UNKNOWN && passUnknownCount<=2 && OperDef.QP_EQ.equals(numNode.getRangeType()))){
						sortNode.k_ = numNode.getFrom();
						String text = "";
						for(int k= j; k>idx; k--){
							text = query.getNodes().get(k).getText() + text;
							//删除后面的节点(只含有unknown节点或sort节点)
							query.getNodes().remove(k);
						}
						sortNode.setText(sortNode.getText() + text);
						findSortK = true;
					}
					break;
				}else if(tmpNode2.type == NodeType.UNKNOWN){
					passUnknownCount++;
					continue ;
				}else{
					break ;
				}
			}
			
			if(!findSortK){
				int passIndexNum = 0;
				//排序数字在前时，与Sort节点可以相隔多个
				int left = idx - 1;
				int right = idx + 1;
				if(right >= query.getNodes().size()){
					right=-1;
				}
				boolean isLeft = true;
				for(int j = isLeft? (left>=0?left:right):(right>=0?right:left)
						; j >= 0 && j < query.getNodes().size(); isLeft = !isLeft, 
						j = isLeft? (left>0?--left:++right):(right>=0?++right:--left)){
					SemanticNode tmpNode2 = query.getNodes().get(j);
					if(tmpNode2.type == NodeType.NUM){
						NumNode numNode = (NumNode)tmpNode2;
						Unit unit = numNode.getUnit();
						if(unit == Unit.HU || unit == Unit.ZHI ||
								unit == Unit.JIA){
							sortNode.k_ = numNode.getFrom();
							if(j < idx ){
								sortNode.setText(tmpNode2.getText() + tmpNode.getText() );
							}else{
								sortNode.setText(tmpNode.getText() + tmpNode2.getText() );
							}
							break ;
						}
					}else if(tmpNode2.type == NodeType.SORT){
						if(passIndexNum <= 1 && ((SortNode)tmpNode2).k_ > 1){
							sortNode.k_ = ((SortNode)tmpNode2).k_;
							sortNode.setDescending_(sortNode.isDescending_() 
									&& ((SortNode)tmpNode2).isDescending_());
							query.getNodes().set(j, new UnknownNode(tmpNode2.getText()));
							sortNode.setText(tmpNode2.getText() + query.getNodes().get(idx).getText() );
						 }
						break;
					}else if(tmpNode2.type == NodeType.CLASS){
						passIndexNum++ ;
					}
				}
			}
		}
	}
}
