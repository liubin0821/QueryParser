package com.myhexin.qparser.phrase.parsePrePlugins;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Query;

import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.util.Consts;

public class PhraseParserPrePluginWordSegmentPostTreat extends PhraseParserPrePluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPrePluginWordSegmentPostTreat.class.getName());
	private final static Pattern NUM_AND_LETTER = Pattern.compile("^(\\d+)([a-zA-Z]+)/$");
    private final static Pattern DATE_UNIT_AND_INDEX = Pattern.compile("^(周|月|年)(振幅|成交量|成交额|换手率|平均换手率|最低收盘价|最高收盘价|最低价|最高价|最低收盘价日|最高收盘价日|均价|最高价日|最低价日|开盘价|收盘价|前收盘价|涨跌|涨跌幅)/onto_class:$");
    
    
    
	public PhraseParserPrePluginWordSegmentPostTreat() {
        super("Word_Segment_Post_Treat");
    }
    
    @Override
    public void processSingle(ParserAnnotation annotation) {
    	String segmentedText = annotation.getSegmentedText();
    	String result = getLtpPostTreatRlt(segmentedText); // 获得后处理结果
    	//.trim()滥用
    	if (result != null && result.length() > 0) {
    		annotation.setSegmentedText(result);
    	}
    }
    
    private final static String STR_RM = "_人名";
    private final static String STR_XM = "_姓名";
    
    /*
     * 返回分词结果
     */
    private String getLtpPostTreatRlt(String ltpRlt) {
        //ltpRlt = ltpRlt.replace("_泛产品", "_泛产品|_主营产品名称");
        ltpRlt = ltpRlt.replace(STR_RM, STR_XM);
        String[] ltpRlts = ltpRlt.split(Consts.STR_NEW_LINE);
        if(ltpRlts!=null && ltpRlts.length>1){
        	for(int i = 1; i < ltpRlts.length; i ++) {
                String tempLine = ltpRlts[i];
                String[] fromTo = tempLine.trim().split("≌");
                if(fromTo!=null && fromTo.length >= 2){
                    if (SpecialWords.hasWord(fromTo[1], SpecialWordType.IGNORE_TRANS)){
                        continue;
                    }
                    //query_.getLog().logTransWord(fromTo[0], fromTo[1]);
                }
            }
        }
        
        String line = ltpRlts[0]; // 获取分词第一行，不包括之后的转换行≌
        line = dealWithNumAndLetter(line); // 处理数字+英文字母的问题：2010pe
        line = dealWithDateUnitAndIndex(line); // 处理数字+时间单位+指标的问题：10年涨跌幅
        line = dealWithPartedIndex(line, Query.Type.ALL); // 处理w&r且wr为指标但分词时被切开的情况
        return line;
    }
    
    /**
     * 处理分词时10pe这种“数字+英文字符串”的问题
     * 拆分为10/onto_num:	pe
     * @param 分词结果line
     * @return
     */
    public static String dealWithNumAndLetter(String line) {
        StringBuilder sb = new StringBuilder();
        for (String token : line.split(Consts.STR_TAB)) {
        	Matcher numAndLetter = NUM_AND_LETTER.matcher(token);
        	if (numAndLetter.matches()) {
        		String num = numAndLetter.group(1)+"/onto_num:";
        		String letter = numAndLetter.group(2)+Consts.CHAR_SLASH;
        		sb.append(num).append(Consts.STR_TAB).append(letter).append(Consts.STR_TAB);
        	} else
        		sb.append(token).append(Consts.STR_TAB);
        }
        return sb.toString();
    }
    
    /**
     * 处理分词时10年涨跌幅这种“数字+时间单位+指标”的问题
     * 拆分为10/onto_num:	年/	年涨跌幅/onto_class:
     * @param 分词结果line
     * @return
     */
    public static String dealWithDateUnitAndIndex(String line) {
    	 StringBuilder sb = new StringBuilder();
         boolean isLastNum = false;
         for (String token : line.split(Consts.STR_TAB)) {
         	Matcher dateUnitAndIndex = DATE_UNIT_AND_INDEX.matcher(token);
         	if (isLastNum && dateUnitAndIndex.matches()) {
         		String dateUnit = dateUnitAndIndex.group(1)+Consts.CHAR_SLASH;
         		String index = dateUnitAndIndex.group(2)+"/onto_class:";
         		sb.append(dateUnit).append(Consts.STR_TAB).append(index).append(Consts.STR_TAB);
         	} else
         		sb.append(token).append(Consts.STR_TAB);
         	Pattern NUM = Pattern.compile("^(\\d+)/(onto_num:)?$");
         	Pattern CHINESE_NUM = Pattern.compile("^[\\d零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+/(onto_num:)?$");
         	if (NUM.matcher(token).matches() || CHINESE_NUM.matcher(token).matches())
         		isLastNum = true;
         	else
         		isLastNum = false;
         }
         return sb.toString();
    }
    
    private final static Pattern LETTER = Pattern.compile("^([a-zA-Z]+)/$");
    private final static Pattern CONNECTOR = Pattern.compile("^(&)/$");
    /**
     * 处理分词时w&r这种“英文字母+&+英文字母”为指标的问题
     * 合并为：wr/onto_class:
     * @param 分词结果line
     * @return
     */
    public static String dealWithPartedIndex(String line, Query.Type qType) {
    	StringBuilder sb = new StringBuilder();
    	String[] tokens = line.split(Consts.STR_TAB);
        for (int i=0; i < tokens.length; i++) {
        	String token = tokens[i];
        	if (i >= tokens.length-2) {
        		sb.append(token).append(Consts.STR_TAB);
        		continue;
        	}
        	
        	boolean isPartedLetters = LETTER.matcher(tokens[i]).matches()
        			&& CONNECTOR.matcher(tokens[i+1]).matches()
        			&& LETTER.matcher(tokens[i+2]).matches();
        	if (!isPartedLetters) {
        		sb.append(token).append(Consts.STR_TAB);
        		continue;
        	}
        	String partedIndex = tokens[i].substring(0, tokens[i].length()-1) + tokens[i+2].substring(0, tokens[i+2].length()-1);
    		Collection<ClassNodeFacade> collection = MemOnto.getOntoC(partedIndex, ClassNodeFacade.class, qType);
    		boolean isPartedIndex = (collection != null && collection.isEmpty() == false) ? true : false;
        	if (isPartedIndex) {
        		i++;
        		i++;
        		sb.append(partedIndex+"/onto_class:\t");
        	} else
        		sb.append(token).append(Consts.STR_TAB);
        }
        return sb.toString();
    }
    
    @Override
    public String getLogResult(ParserAnnotation annotation ) {
    	String queryText = annotation.getQueryText();
    	String querySegText = annotation.getSegmentedText();
    	if(queryText!=null)
    		return String.format("[%s] 处理后 : %s\n", queryText, querySegText);
    	else
    		return null;
    }
}

