/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-10-29 上午10:51:25
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.DateNumModify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.dao.FakedateinfoMapper;
import com.myhexin.DB.mybatis.mode.Fakedateinfo;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.PhraseUtil;

public class DateNumModify {
	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	private static HashMap<String, ArrayList<Fakedateinfo>> dateModifyInfo = null;// 单例模式
	static {
		loadData();
		
	}

	public static void process(ArrayList<SemanticNode> nodes, int start, int end) {
		ArrayList<FocusNode> fnl = new ArrayList<FocusNode>();
		//把所有的指标抓出来,方便后面处理

		//处理日期
		for (int i = start; i <= end; i++) {
			//处理日期
			if (dateModifyInfo.containsKey(nodes.get(i).getText())) {//匹配
				if (fnl.size() == 0) {//大部分时候不匹配, 所以有匹配的时候才抓取index
					catchOutIndex(fnl, nodes, start, end);
				}

				
				for (FocusNode fn : fnl) {
					ClassNodeFacade index = fn.getIndex();
					List<PropNodeFacade> propList = index.getAllProps();
					if(changeFakeDateToRealDate(nodes, i, propList))
						return;
				}
				
				if(fnl.size()==0)
					if(changeFakeDateToRealDate(nodes, i, null))
						return;
				
				
			}
		}
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-7-25 下午3:54:55
     * @description:   	
     * @param nodes
     * @param i
     * @param propList
     * @return
     * 
     */
    private static boolean changeFakeDateToRealDate(
            ArrayList<SemanticNode> nodes, int i, List<PropNodeFacade> propList) {
	    String oldTime = nodes.get(i).getText();
	    String newTime = getProNeededTime(propList,
	    		dateModifyInfo.get(oldTime));
	    //该指标不需要 这种时间
	    if (newTime == null)
	        return false;
	    
	    DateNode dn = new DateNode(oldTime);
	    dn.setFake(true);
	    try {
	    	DateRange dr = DateCompute.getDateInfoFromStr(newTime, null);
	    	if (dr != null) {
	    		dn.setDateinfo(dr);
	    		dn.oldNodes.add(nodes.get(i));
	    		nodes.set(i, dn);
	    		return true;
	    	}
	    } catch (NotSupportedException e) {
	    	e.printStackTrace();
	    }
	    return false;
    }

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-10-29 下午8:10:13
	 * @description:   	
	 * @param propList
	 * @param arrayList
	 * @return
	 * 
	 */
	private static String getProNeededTime(List<PropNodeFacade> propList,
	        ArrayList<Fakedateinfo> dmiList) {

		if (propList != null)
			for (PropNodeFacade pn : propList) {
				if (pn.isDateProp()) {
					String text = pn.getText();//指标属性名称
					for (Fakedateinfo dmi : dmiList) {
						if (dmi.getReporttype().equals(text))
							return dmi.getValue();
					}
				}
			}

		//获取默认时间
		for (Fakedateinfo dmi : dmiList)
			if (dmi.getReporttype().equals("*"))
				return dmi.getValue();

		return null;
	}

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-10-29 下午7:13:38
	 * @description:   	
	 * 
	 */
	private static void catchOutIndex(ArrayList<FocusNode> fnl,
			ArrayList<SemanticNode> nodes, int start, int end) {
		for (int i = start; i <= end; i++) {
			if (PhraseUtil.isIndex(nodes.get(i))) {
				fnl.add((FocusNode) nodes.get(i));
			}
		}

	}
	
	public static final void loadData() {
		if (dateModifyInfo == null) {
			reloadData();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void reloadData() {
		//现在数据库方式
		List<Fakedateinfo> dmiList = null;
		dmiList = mybatisHelp.getFakeDateIndoMapper().selectAll();

		// 把Arraylist中东西转存到HashMap中
		HashMap<String, ArrayList<Fakedateinfo>> temp = new HashMap<String, ArrayList<Fakedateinfo>>();
		for (Fakedateinfo dmi : dmiList) {
			if (temp.containsKey(dmi.getText())) {
				temp.get(dmi.getText()).add(dmi);
			} else {
				ArrayList<Fakedateinfo> tempList = new ArrayList<Fakedateinfo>();
				tempList.add(dmi);
				temp.put(dmi.getText(), tempList);
			}
		}
		dateModifyInfo=temp;
	}

	public static void main(String[] args) {
		reloadData();
	}
}
