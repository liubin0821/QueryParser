package com.myhexin.qparser.number;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.FakeNumNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.NumNode.MoveType;
import com.myhexin.qparser.node.SemanticNode;

public class NumUtil {

    static Pattern FORM_P1 = Pattern.compile("^(\\d+?\\.?\\d*?)([十百千万亿仟佰拾]+?)$");

    /**
     * 把中文数字解析为阿拉伯数字(Integer)
     * @param chineseNumber 中文数字
     * @return 阿拉伯数字(Integer),如果是无法识别的中文数字则返回-1
     * @throws NotSupportedException 
     * @throws UnexpectedException
     */
    public static long parseChineseNumber(String chineseNumber) throws NotSupportedException{
        chineseNumber = chineseNumber.replace("两", "二");
        chineseNumber = chineseNumber.replace("仟", "千");
        chineseNumber = chineseNumber.replace("佰", "百");
        chineseNumber = chineseNumber.replace("拾", "十");
        chineseNumber = chineseNumber.replace("玖", "九");
        chineseNumber = chineseNumber.replace("捌", "八");
        chineseNumber = chineseNumber.replace("柒", "七");
        chineseNumber = chineseNumber.replace("陆", "六");
        chineseNumber = chineseNumber.replace("伍", "五");
        chineseNumber = chineseNumber.replace("肆", "四");
        chineseNumber = chineseNumber.replace("叁", "三");
        chineseNumber = chineseNumber.replace("贰", "二");
        chineseNumber = chineseNumber.replace("壹", "一");
        if (chineseNumber.startsWith("百") || chineseNumber.startsWith("千")
                || chineseNumber.startsWith("万")
                || chineseNumber.startsWith("亿")) {
            chineseNumber = "一" + chineseNumber;
        }
        return parseChineseNumber(chineseNumber, 1);
    }

    /**
     * 把中文数字解析为阿拉伯数字(Integer)
     * 
     * @param preNumber 第二大的位数
     * @param chineseNumber 中文数字
     * @throws NotSupportedException 
     * @throws UnexpectedException
     */
    private static long parseChineseNumber(String chineseNumber, int preNumber) throws NotSupportedException{
        long ret = 0;
        if (chineseNumber.indexOf("零") == 0) {
            int index = 0;
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1);
        } else if (chineseNumber.indexOf("亿") != -1) {
            int index = chineseNumber.indexOf("亿");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 100000000
                    + parseChineseNumber(postfix, 10000000);
        } else if (chineseNumber.indexOf("万") != -1) {
            int index = chineseNumber.indexOf("万");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 10000
                    + parseChineseNumber(postfix, 1000);
        } else if (chineseNumber.indexOf("千") != -1) {
            int index = chineseNumber.indexOf("千");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 1000
                    + parseChineseNumber(postfix, 100);
        } else if (chineseNumber.indexOf("百") != -1) {
            int index = chineseNumber.indexOf("百");
            int end = chineseNumber.length();
            String prefix = chineseNumber.substring(0, index);
            String postfix = chineseNumber.substring(index + 1, end);
            ret = parseChineseNumber(prefix, 1) * 100
                    + parseChineseNumber(postfix, 10);
        } else if (chineseNumber.indexOf("十") != -1) {
            if (chineseNumber.indexOf("十") != 0) {
                int index = chineseNumber.indexOf("十");
                int end = chineseNumber.length();
                String prefix = chineseNumber.substring(0, index);
                String postfix = chineseNumber.substring(index + 1, end);
                ret = parseChineseNumber(prefix, 1) * 10
                        + parseChineseNumber(postfix, 1);
            } else {
                chineseNumber = "一" + chineseNumber;
                int index = chineseNumber.indexOf("十");
                int end = chineseNumber.length();
                String prefix = chineseNumber.substring(0, index);
                String postfix = chineseNumber.substring(index + 1, end);
                ret = parseChineseNumber(prefix, 1) * 10
                        + parseChineseNumber(postfix, 1);
            }
        } else if (chineseNumber.equals("一")) {
            ret = 1 * preNumber;
        } else if (chineseNumber.equals("二")) {
            ret = 2 * preNumber;
        } else if (chineseNumber.equals("三")) {
            ret = 3 * preNumber;
        } else if (chineseNumber.equals("四")) {
            ret = 4 * preNumber;
        } else if (chineseNumber.equals("五")) {
            ret = 5 * preNumber;
        } else if (chineseNumber.equals("六")) {
            ret = 6 * preNumber;
        } else if (chineseNumber.equals("七")) {
            ret = 7 * preNumber;
        } else if (chineseNumber.equals("八")) {
            ret = 8 * preNumber;
        } else if (chineseNumber.equals("九")) {
            ret = 9 * preNumber;
        } else if (chineseNumber.equals("")) {
            ret = 0;
        } else {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_NUM_FMT,
                    chineseNumber);
        }
        return ret;
    }

    /**
     * 将中文数字转为阿拉伯数字
     * @param String chineseNumber 中文数字
     * @return String ArabicNumber 阿拉伯数字，若无法转换，返回-1或原始字符串
     * @throws UnexpectedException
     * @throws NotSupportedException 
     */
    public static String getArabic(String chineseNumber)
            throws NotSupportedException {
        chineseNumber = chineseNumber.replace("个", "");
        if (chineseNumber.indexOf("十") == -1
                && chineseNumber.indexOf("拾") == -1
                && chineseNumber.indexOf("百") == -1
                && chineseNumber.indexOf("佰") == -1
                && chineseNumber.indexOf("千") == -1
                && chineseNumber.indexOf("仟") == -1
                && chineseNumber.indexOf("万") == -1
                && chineseNumber.indexOf("亿") == -1) {
            chineseNumber = chineseNumber.replace("玖", "9");
            chineseNumber = chineseNumber.replace("捌", "8");
            chineseNumber = chineseNumber.replace("柒", "7");
            chineseNumber = chineseNumber.replace("陆", "6");
            chineseNumber = chineseNumber.replace("伍", "5");
            chineseNumber = chineseNumber.replace("肆", "4");
            chineseNumber = chineseNumber.replace("叁", "3");
            chineseNumber = chineseNumber.replace("贰", "2");
            chineseNumber = chineseNumber.replace("壹", "1");
            chineseNumber = chineseNumber.replace("零", "0");
            chineseNumber = chineseNumber.replace("九", "9");
            chineseNumber = chineseNumber.replace("八", "8");
            chineseNumber = chineseNumber.replace("七", "7");
            chineseNumber = chineseNumber.replace("六", "6");
            chineseNumber = chineseNumber.replace("五", "5");
            chineseNumber = chineseNumber.replace("四", "4");
            chineseNumber = chineseNumber.replace("三", "3");
            chineseNumber = chineseNumber.replace("两", "2");
            chineseNumber = chineseNumber.replace("二", "2");
            chineseNumber = chineseNumber.replace("一", "1");
        } else if (FORM_P1.matcher(chineseNumber).matches()) {
            Matcher mid = FORM_P1.matcher(chineseNumber);
            mid.matches();
            Double head = Double.valueOf(mid.group(1));
            String chinesepart = chineseNumber.replace(mid.group(1), "一");
            chineseNumber = String.format("%.0f", head * parseChineseNumber(chinesepart));
        } else {
            chineseNumber = chineseNumber.replace("0", "零");
            chineseNumber = chineseNumber.replace("9", "九");
            chineseNumber = chineseNumber.replace("8", "八");
            chineseNumber = chineseNumber.replace("7", "七");
            chineseNumber = chineseNumber.replace("6", "六");
            chineseNumber = chineseNumber.replace("5", "五");
            chineseNumber = chineseNumber.replace("4", "四");
            chineseNumber = chineseNumber.replace("3", "三");
            chineseNumber = chineseNumber.replace("2", "二");
            chineseNumber = chineseNumber.replace("1", "一");
            chineseNumber = String.valueOf(parseChineseNumber(chineseNumber));
        }
        return chineseNumber;
    }

    public static Long getArabicAsLong(String chineseNumber) throws NotSupportedException
              {
        Long chineseVal = Long.valueOf(getArabic(chineseNumber));
       
        return chineseVal;
    }

    public static void main(String[] args) {
        String test = "1.7亿";
        String result = null;
        try {
            result = getArabic(test);
        } catch (NotSupportedException e) {
            System.out.println(e.getLogMsg());
        }
        System.out.println(result);
    }

    /**
     * 根据操作修改nr
     * 
     * @param opN 被修改NumNode
     * @param ot 操作类型
     * @param opNum 操作数
     * @return
     * @throws UnexpectedException
     */
    public static NumNode changeNRByOp(NumNode opN, OperatorType ot,
            double opNum) throws UnexpectedException {
        String rangeType = opN.getRangeType();
        NumRange nr = opN.getNuminfo();
        if (nr == null) {
            return null;
        }
        if (NumUtil.isGreaterType(rangeType)) {
            // 若为大于，则只处理From
            nr.setFrom(changeDoubleByOp(nr.getDoubleFrom(), ot, opNum));
        } else if (NumUtil.isLessType(rangeType)) {
            // 若为小于，则只处理To
            nr.setTo(changeDoubleByOp(nr.getDoubleTo(), ot, opNum));
        } else if (NumUtil.isEQType(rangeType)||NumUtil.isInType(rangeType)) {
            Double from = changeDoubleByOp(nr.getDoubleFrom(), ot, opNum);
            Double to = changeDoubleByOp(nr.getDoubleTo(), ot, opNum);
            nr.setNumRange(from, to);
        } else {
            throw new UnexpectedException("Unexpected Number Type :%s",
                    rangeType);
        }
        opN.setNuminfo(nr);
        return opN;
    }

    private static Double changeDoubleByOp(Double num, OperatorType ot,
            Double opNum) {
        assert (ot != OperatorType.RATE);
        Double result = null;
        switch (ot) {
        case ADD:
            result = num + opNum;
            break;
        case SUBTRACT:
            result = num - opNum;
            break;
        case MULTIPLY:
            result = num * opNum;
            break;
        case DIVIDE:
            result = num / opNum;
            break;
        default:
            break;
        }
        assert (result != null);
        return result;
    }

    public static void changeBeiToPercent(NumNode nn)
            throws UnexpectedException {
        if (nn == null || nn.getUnit() != Unit.BEI) {
            throw new UnexpectedException("Unexpected NumNode:%s",
                    nn == null ? null : nn.getText());
        }
        String rangeType = nn.getRangeType();
        NumRange nr = nn.getNuminfo();
        nr.setUnit("%");
        double from = nr.getDoubleFrom();
        double to = nr.getDoubleTo();
        from = NumUtil.isLessType(rangeType) ? from : from * 100;
        to = NumUtil.isGreaterType(rangeType) ? to : to * 100;
        nr.setNumRange(from, to);
        nr.setUnit("%");
        nn.setNuminfo(nr);
        return;
    }

    /**
     * 为ChangeNode.type_为Decrease的更改NumNode
     * 
     * @param nn
     *            需要更改的NumNode
     */
    public static void changeNumForDecrease(NumNode nn) {
        String numType = nn.getRangeType();
        NumRange numRange = nn.getNuminfo();
        boolean notNeedChange = (NumUtil.isLessType(numType) || NumUtil
                .isEQType(numType)) && numRange.getDoubleTo() <= 0;
        notNeedChange |= NumUtil.isEQType(numType)
                && numRange.getDoubleFrom() <= 0;
        if (notNeedChange) {
            // 跌幅小于-20%/跌了-20%/跌了-20%到40%
            return;
        }
        if (NumUtil.isLessType(numType) && numRange.getDoubleTo() > 0) {
            // 跌幅小于20%
            numRange.setNumRange(-numRange.getDoubleTo(), 0.0);
            numRange.setIncludeFrom(numRange.isIncludeTo());
        } else if (NumUtil.isGreaterType(numType)
                && numRange.getDoubleFrom() <= 0) {
            // 跌幅大于-20%
            numRange.setNumRange(numRange.getDoubleFrom(), 0.0);
        } else if (NumUtil.isGreaterType(numType)
                && numRange.getDoubleFrom() > 0) {
            // 跌幅大于20%
            numRange.setNumRange(NumRange.MIN_, -numRange.getDoubleFrom());
            numRange.setIncludeTo(numRange.isIncludeFrom());
        } else if (NumUtil.isEQType(numType) && numRange.getDoubleTo() > 0) {
            // 跌了20%
            numRange.setNumRange(-numRange.getDoubleTo(),
                    -numRange.getDoubleTo());
            numRange.setIncludeFrom(numRange.isIncludeTo());
        } else if (NumUtil.isInType(numType) && numRange.getDoubleFrom() > 0) {
            // 跌了20%到40%
            numRange.setNumRange(-numRange.getDoubleTo(),
                    -numRange.getDoubleFrom());
            numRange.transposeInclude();
        } else {
            ;// No Op
        }
    }

    public static NumNode makeNumNodeFromStr(String numStr)
            throws NotSupportedException, UnexpectedException {
        NumNode numVal = new NumNode(numStr);
        NumRange numRange = NumParser.getNumRangeFromStr(numStr);
        if (numRange == null) {
            throw new NotSupportedException(MsgDef.NOT_SUPPORTED_NUM_FMT,
                    numStr);
        }
        numVal.setNuminfo(numRange);
        return numVal;
    }

    /**
     * 根据浮动范围对数值节点的数值范围进行修改
     * 
     * @param valNum
     *            原始数值节点
     * @param moveVal
     *            浮动值
     * @param moveUnit
     *            浮动单位
     * @param moveType
     *            浮动类型
     * @param isLongVal 
     * @throws UnexpectedException
     */
    public static void changeNRForMovableRange(NumNode valNum, double moveVal,
            Unit moveUnit, MoveType moveType, boolean isLongVal) throws UnexpectedException {
        if (!valNum.getRangeType().equals(OperDef.QP_EQ)) {
            // 只对确定值进行浮动
            throw new UnexpectedException("Unexpected Number Range Type:%s",
                    valNum.getRangeType());
        }
        NumRange valRange = valNum.getNuminfo();
        double numFrom = valNum.getFrom();
        double numTo = valNum.getTo();
        
        if (moveUnit == Unit.PERCENT) {
            // 若浮动单位为“%”，则按百分比浮动
            numFrom = moveType == MoveType.UP ? numFrom : numFrom
                    * (1 - moveVal / 100);
            numTo = moveType == MoveType.DOWN ? numTo : numTo
                    * (1 + moveVal / 100);
        } else {
            numFrom = moveType == MoveType.UP ? numFrom : numFrom - moveVal;
            numTo = moveType == MoveType.DOWN ? numTo : numTo + moveVal;
        }
        numFrom =isLongVal? Math.floor(numFrom):numFrom;
        numTo = isLongVal?Math.ceil(numTo):numTo;
        valRange.setNumRange(numFrom, numTo);
    }

    /**
     * 判断范围类型是否为Less或LessEqual
     * @param rangeType
     * @return
     */
    public static boolean isLessType(String rangeType){
        if(rangeType==null){
            return false;
        }
        return rangeType.equals(OperDef.QP_LT) || rangeType
        .equals(OperDef.QP_LE);
    }
    
    /**
     * 判断范围类型是否为In或NotIn
     * @param rangeType
     * @return
     */
    public static boolean isInType(String rangeType){
        if(rangeType==null){
            return false;
        }
        return rangeType.equals(OperDef.QP_IN) || rangeType
        .equals(OperDef.QP_NI);
    }

    /**
     * 判断范围类型是否为Greater或GreaterEqual
     * @param rangeType
     * @return
     */
    public static boolean isGreaterType(String rangeType){
        if(rangeType==null){
            return false;
        }
        return rangeType.equals(OperDef.QP_GT) || rangeType
        .equals(OperDef.QP_GE);
    }
    
    public static boolean checkAsYMD(String yearStr, String monthStr,
            String dayStr) throws UnexpectedException {
        boolean wrongStr = !NumPatterns.ONLY_YEAR.matcher(yearStr).matches()
                || !NumPatterns.ONLY_MONTH.matcher(monthStr).matches()
                || !NumPatterns.ONLY_DAY.matcher(dayStr).matches();
        if (wrongStr) {
            throw new UnexpectedException("Wrong String : %s %s %s", yearStr,
                    monthStr, dayStr);
        }
        int year = Integer.valueOf(yearStr);
        int month = Integer.valueOf(monthStr);
        int day = Integer.valueOf(dayStr);
        if (day > DateUtil.getMonthDayCount(year, month)) {
            return false;
        }
        return true;
    }
    
    /**
     * 判断范围类型是否为Equal或NotEqual
     * @param rangeType
     * @return
     */
    public static boolean isEQType(String rangeType){
        if(rangeType==null){
            return false;
        }
        return rangeType.equals(OperDef.QP_EQ) || rangeType
        .equals(OperDef.QP_NE);
    }

    public static OperatorType getOperatorTypeByStr(String operaTypeStr){
        OperatorType operaT = null;
        if (operaTypeStr.matches("add|Add|ADD|加")) {
            operaT = OperatorType.ADD;
        } else if (operaTypeStr.matches("sub|Sub|SUB|减")) {
            operaT = OperatorType.SUBTRACT;
        } else if (operaTypeStr.matches("mul|Mul|MUL|乘")) {
            operaT = OperatorType.MULTIPLY;
        } else if (operaTypeStr.matches("div|Div|DIV|除")) {
            operaT = OperatorType.DIVIDE;
        } else if (operaTypeStr.matches("rate|Rate|RATE|比率")) {
            operaT = OperatorType.RATE;
        }
        return operaT;
    }
    
    /**
     * 获得当前节点的左边或右边的节点，忽略空白节点
     * @param pos
     * @param nodes
     * @param toLeft
     * @return
     */
    public static SemanticNode getLastOrNextNode(int pos,
            ArrayList<SemanticNode> nodes, boolean toLeft) {
        int i = pos;
        while(toLeft?--i>-1:++i<nodes.size()){
            if(NumPatterns.BLANK.matcher(nodes.get(i).getText()).matches()){
                continue;
            }
            return nodes.get(i);
        }
        return null;
    }
    
    
    /**
     * 获得当前节点的左边或右边的节点，忽略空白节点
     * @param pos
     * @param words
     * @param toLeft
     * @return
     */
    public static int getLastOrNextNode1(int pos,
    		ArrayList<SemanticNode> words, boolean toLeft) {
        int i = pos;
        while(toLeft?--i>-1:++i<words.size()){
            if(NumPatterns.BLANK.matcher(words.get(i).getText()).matches()){
                continue;
            }
            return i;
        }
        return -1;
    }
    
    /**
     * 获得当前节点的左边或右边第N个非空白节点位置，忽略空白节点
     * @param pos
     * @param nodes
     * @param toLeft
     * @return
     */
    public static int getLastOrNextNNodePos(int pos, int n,
            ArrayList<SemanticNode> nodes, boolean toLeft) {
    	if (n <= 0)
    		return -1;
        int i = pos;
        while(toLeft ? --i>-1 : ++i<nodes.size()){
            if(NumPatterns.BLANK.matcher(nodes.get(i).getText()).matches()){
                continue;
            } else {
            	n--;
            }
            if (n == 0)
            	return i;
        }
        return -1;
    }

    public static NumNode changeNumForReverval(NumNode numNode) {
        if (numNode.isFake()) {
                FakeNumNode fakeNumNode = (FakeNumNode) numNode;
                fakeNumNode.exchangeFakeNumType();
            return numNode;
        }
        NumUtil.changeNumForDecrease(numNode);
        return numNode;
    }
    
	public static boolean big(NumNode n1, NumNode n2) {
		if (n1.getFrom() > n2.getTo()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void convertChineseToArabic(List<SemanticNode> nodes) {
		for (SemanticNode node : nodes) {
			String text = node.getText();
			String arbic = "";
			if (NumPatterns.CHINESE_NUM.matcher(text).matches()) {
				try {
					arbic = getArabic(text);
				} catch (NotSupportedException e) {
					e.printStackTrace();
				}
				if (!arbic.isEmpty()) {
					node.setText(arbic);
				}

			}
		}

	}

}
