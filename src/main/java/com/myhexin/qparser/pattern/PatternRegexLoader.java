package com.myhexin.qparser.pattern;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.EnumDef.FakeNumType;
import com.myhexin.qparser.define.EnumDef.HiddenType;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.FakeNumNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.OperatorNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechOpNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.onto.UserClassNodeFacade;
import com.myhexin.qparser.util.Pair;

/**
 * 从pattern配置文件导入pattern资源，并将pattern中的词语添加到SpecialWords中。在导入时，
 * 进行严格错误识别。主要分四部分内容，一、label词语；二、pattern匹配节点序列的导入泛化；三、替换类型的
 * 识别；四、pattern替换节点序列的导入与表示。<br>
 * 注意：由于，pattern匹配序列的导入时，各类节点的泛化表示方式要与具体问句泛化格式一致，因此要特别注意。
 * 即，严格要求模板泛化{@link #makePatternInfo(String[], PatternInfo, Query.Type)}与
 * Query泛化{@link SemanticPattern#getQueryPatternText(ArrayList, int)}保持一致。
 */
public class PatternRegexLoader {
    /** 在抛出DataConfException时使用，虽非线程安全，但出错概率较小且仅影响提示信息 */
    private static String confFileName = null;
    private static int lineNo = -1;
    private static Map<String, EnumSet<SpecialWordType>> ptnSecialWords =null; 
    
    /**
     * @param lines
     * @throws DataConfException
     */
    public static void loadStockPtnRegex(List<String> lines) throws DataConfException{
        confFileName = Param.STOCK_PATTERN_FILE;
        ptnSecialWords = new HashMap<String, EnumSet<SpecialWordType>>();
        PtnRegexRuleInfo info = loadData(lines, Query.Type.STOCK);
        SemanticPattern.setLabel2PatternInfo(info.ptnInfo, Query.Type.STOCK);
        ExtendedRegex.setRegexInfo(info.regexInfo, Query.Type.STOCK);
        SpecialWords.addPtnRuleWordTypes(ptnSecialWords);
    }
    
    /**
     * @param lines
     * @throws DataConfException
     */
    public static void loadFundPtnRegex(List<String> lines) throws DataConfException{
        confFileName = Param.FUND_PATTERN_FILE;
        PtnRegexRuleInfo info = loadData(lines, Query.Type.FUND);
        SemanticPattern.setLabel2PatternInfo(info.ptnInfo, Query.Type.FUND);
        ExtendedRegex.setRegexInfo(info.regexInfo, Query.Type.FUND);
    }

    /** used for storing infos that are being constructed. Once all done,  
     * they are transfered to their counter-part class members. This can
     * reduce the risk of thread-safe problem
     */
    private static class PtnRegexRuleInfo {
        public HashMap<String, ArrayList<PatternInfo>> ptnInfo = 
            new HashMap<String, ArrayList<PatternInfo>>();
        public ArrayList<Pair<Pattern, String>> regexInfo =
            new ArrayList<Pair<Pattern, String>>();
    }

    private static PtnRegexRuleInfo loadData(List<String> lines, Query.Type qtype)
    throws DataConfException {
        try {
            PtnRegexRuleInfo rtn = new PtnRegexRuleInfo();
            for (int iLine = 0; iLine < lines.size(); iLine++) {
                lineNo = iLine + 1;
                String line = lines.get(iLine).trim();
                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(":",2);
                if (parts.length != 2) {
                    throw new DataConfException(confFileName,
                            iLine, "文件中格式错误: 行未以:分隔");
                }

                String infoType = parts[0].trim();//信息类型
                String infos = parts[1].trim();
                if (infoType.equals("regex")) {
                    parseRegex(rtn.regexInfo, infos, qtype);
                } else if (infoType.equals("stop_char")) {
                    TransInfo.IFIND_SUGGEST_STOP_CHARS = line.trim();
                } else if(infoType.equals("pattern")){
                    parsePattern(rtn.ptnInfo, infos, qtype);
                } else if(infoType.equals("rule")){
                	//先放在IndexRuleLoader里面
//                    parseRule(rtn.ruleInfo, infos, qtype);
                } else {
                    throw new DataConfException(confFileName, iLine,
                            "“%s”类型错误：未识别信息类型，请更正 ", line);
                }
            }
            return rtn;
        } catch (UnexpectedException e) {
            throw new DataConfException(confFileName, lineNo, e.getLogMsg());
        }
    }

    /**
     * 对PatternLine进行Pattern解析，以备以后进行匹配。解析主要包括三部分：
     * Pattern、替换类型、替换后节点序列。
     * @param patternLine
     * @param lineNo 
     * @throws DataConfException 
     * @throws UnexpectedException 
     */
    private static void parsePattern(HashMap<String, ArrayList<PatternInfo>> ptnInfo,
            String patternLine, Query.Type qtype)
    throws DataConfException, UnexpectedException {
    	patternLine = patternLine.toLowerCase();
        String[] patInfos = patternLine.split(";");
        if (!patInfos[0].startsWith("label=")) {
            String errMsg =  String.format( "“%s”格式错误:label未找到，请更正 ", patternLine);
            throw new DataConfException(confFileName, lineNo, errMsg);
        }
        
        String label = (patInfos[0].split("=", 2))[1];
        String[] labels = label.split("\\|");
        for(String word : labels){
        	ptnSecialWords.put(word, EnumSet.of(SpecialWordType.PATTERN_WORD));
        }
        
        for (int i = 1; i < patInfos.length; i++) {
            String[] infoI = patInfos[i].split("lab=>");
            boolean isRepAll = false;
            if(infoI.length != 2){
                infoI = patInfos[i].split("ptn=>");
                isRepAll = true;
            }
            
            if (infoI.length != 2) {
                String errMsg = String.format(
                        "“%s”格式错误:没有替换分隔符号REP_ALL或者REP_LAB，请更正 ", patternLine);
                throw new DataConfException(confFileName, lineNo, errMsg);
            }
            
            String[] patNodes = infoI[0].trim().split(" ");
            String[] repInfos = infoI[1].trim().split(" ");
            
            PatternInfo aPtn = new PatternInfo();
            aPtn.setText("pattern:label="+label+";"+patInfos[i].trim());
            aPtn.setIsRepAll(isRepAll);
            
            makePatternInfo(patNodes, aPtn, qtype);
            makeReplaceNodes(repInfos, aPtn, qtype);
            addPatternInfo(ptnInfo, labels, aPtn, qtype);
        }
    }

    private static void makeReplaceNodes(String[] repInfoNodes, PatternInfo ptnInfo,
            Query.Type qtype)
    throws DataConfException, UnexpectedException {
        ArrayList<SemanticNode> repInfos = new ArrayList<SemanticNode>();
        for(int index = 0; index < repInfoNodes.length; index++){
            String repNode = repInfoNodes[index];
            Matcher m ;
            if((m = PTN_REP_INDEX.matcher(repNode)).matches()){
                String text = m.group(1);
                SemanticNode indexNode = MemOnto.getOnto(text, ClassNodeFacade.class, qtype);
                if(indexNode == null){
                    String errMsg = String.format(
                            "“%s”没有该指标，请更正 ", repNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                indexNode.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(indexNode);
            }else if((m = PTN_REP_USER_INDEX.matcher(repNode)).matches()){
            	String text = m.group(1);
            	SemanticNode userIndexNode = MemOnto.getOnto(text, UserClassNodeFacade.class, qtype);
            	if(userIndexNode == null){
                    String errMsg = String.format(
                            "“%s”没有该指标，请更正 ", repNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                userIndexNode.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(userIndexNode);
            }else if((m = PTN_REP_PROP.matcher(repNode)).matches()){
                String text = m.group(1);
                SemanticNode PropNodeFacade = MemOnto.getOnto(text, PropNodeFacade.class, qtype);
                try{
                	PropNodeFacade = MemOnto.getOnto(text, PropNodeFacade.class, qtype);
                }catch (UnexpectedException e){
                	
                }
                
                if(PropNodeFacade == null && (PropNodeFacade = MemOnto.getOnto("_"+text, PropNodeFacade.class, qtype)) == null){
                    String errMsg = String.format("“%s”没有该指标，请更正 ", repNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                PropNodeFacade.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(PropNodeFacade);
            }else if((m = PTN_REP_NUM.matcher(repNode)).matches()){
                String text = m.group(1);
                SemanticNode numNode = null;
                try {
                    numNode = NumParser.getNumNodeFromStr(text);
                } catch (NotSupportedException e) {
                    throw new DataConfException(confFileName, lineNo, e.getMessage());
                }
                numNode.hiddenType = HiddenType.PATTERN_HIDDEN;;
                repInfos.add(numNode);
            }else if((m = PTN_REP_DATE.matcher(repNode)).matches()){
                String text = m.group(1);
                SemanticNode dateNode = null;
                try {
                    dateNode = DateUtil.getDateNodeFromStr(text, null);
                } catch (NotSupportedException e) {
                    throw new DataConfException(confFileName, lineNo, e.getMessage());
                }
                dateNode.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(dateNode); 
            }else if((m = PTN_REP_OPER.matcher(repNode)).matches()){
                String type = m.group(1).trim();
                OperatorNode operNode = new OperatorNode(type);
                operNode.operatorType = OperatorType.SUBTRACT;
                
                NumNode numNode = new NumNode(">0");
                NumRange numRange = new NumRange();
                numRange.setNumRange(0.0, NumRange.MAX_);
                numNode.setNuminfo(numRange);
                operNode.standard = numNode;
                
                if(type.equals("DIV|div|除")){
                    operNode.operatorType = OperatorType.DIVIDE;
                }else if(type.matches("MUL|mul|乘")){
                    operNode.operatorType = OperatorType.MULTIPLY;
                }else if(type.matches("ADD|add|加")){
                    operNode.operatorType = OperatorType.ADD;
                }else if(type.matches("RATE|rate|比率")){
                    operNode.operatorType = OperatorType.RATE;
                    operNode.isFromChange = true;
                    operNode.isBetween = false;
                    operNode.onOneProp = true;
                }
                operNode.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(operNode); 
            }else if((m = PTN_REP_OPER2.matcher(repNode)).matches()){
                String type = m.group(1).trim();
                String text = m.group(2).trim();
                OperatorNode operNode = new OperatorNode(text);
                operNode.operatorType = OperatorType.SUBTRACT;
                
                String numText ;
                NumRange numRange = new NumRange();
                if(!text.matches("(同比|环比|累计|大幅|复合|预测|预计)?(高|多|大|升|增|涨|上涨|升高|增多|增加|增长|增高|上升)")){
                	numText = "<0";
                	numRange.setNumRange(NumRange.MIN_ ,0.0);
                } else {
                	numText = ">0";
                	numRange.setNumRange(0.0, NumRange.MAX_);
                }
                NumNode numNode = new NumNode(numText);
                numNode.setNuminfo(numRange);
                operNode.standard = numNode;
                
                if(type.equals("DIV|div|除")){
                    operNode.operatorType = OperatorType.DIVIDE;
                }else if(type.matches("MUL|mul|乘")){
                    operNode.operatorType = OperatorType.MULTIPLY;
                }else if(type.matches("ADD|add|加")){
                    operNode.operatorType = OperatorType.ADD;
                }else if(type.matches("RATE|rate|比率")){
                    operNode.operatorType = OperatorType.RATE;
                    operNode.isBetween = false;
                    operNode.isFromChange = true;
                    operNode.onOneProp = true;
                }
                operNode.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(operNode); 
            }else if((m = PTN_REP_LOGIC.matcher(repNode)).matches()){
                String type = m.group(1);
                LogicNode logicNode = new  LogicNode(type);
                logicNode.logicType = LogicType.AND;
                if(type.matches("OR|or|或|或者")){
                    logicNode.logicType = LogicType.OR;
                }else if(type.matches("AND|and|且|并且")){
                	logicNode.logicType = LogicType.AND;
                }else{
                	String errMsg = String.format("逻辑节点“%s”类型不对", repNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                logicNode.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(logicNode); 
            }else if((m = PTN_REP_SORT.matcher(repNode)).matches()){
                String type = m.group(1);
                String text = m.group(2);
                SortNode sortNode = new SortNode(repNode);
                sortNode.setDescending_(false);
                sortNode.isTopK_ = true;
                if(type.matches("降序|DECR|decr")){
                    sortNode.setDescending_(true);
                }
                if(type.matches("升序|INCR|incr")){
                	sortNode.setDescending_(false);
                }
                if(text.matches("ALL|all")){
                    sortNode.isTopK_ = false;
                }else{
                    NumRange range;
                    try {
                        range = NumParser.getNumRangeFromStr(text);
                    } catch (NotSupportedException e) {
                        String errMsg = String.format("解析数字“%s”失败", text);
                        throw new DataConfException(confFileName, lineNo, errMsg);
                    }
                    sortNode.k_ = range.getDoubleFrom();
                }
                sortNode.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(sortNode);
            }else if((m = PTN_REP_STRVAL.matcher(repNode)).matches()){
                String type = m.group(1);
                String strval = m.group(2);
                String ofwhat = m.group(3);
                boolean isContails = false;
                
                String[] tmps = strval.split("\\|");
                if(type.matches("包含|\\+")){
                    strval = "+" + tmps[0];
                    isContails = true;
                }else if(type.matches("不包含|\\-")){
                    strval = "-" + tmps[0];
                    isContails = false;
                }else{
                    String errMsg = String.format("“%s”不知道strval的包含类型(包含|不包含)，请更正 ", repNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                for(int wdx = 1; wdx < tmps.length; wdx++){
                    if(isContails){
                        strval += MiscDef.STR_OR +tmps[wdx];
                    }else{
                        strval += MiscDef.STR_AND+tmps[wdx];
                    }
                }
                
                ArrayList<SemanticNode> ofWhats = new ArrayList<SemanticNode>();
                SemanticNode node = null;
                try{
                	node = MemOnto.getOnto(ofwhat, PropNodeFacade.class, qtype);
                }catch (UnexpectedException e){
                	node = MemOnto.getOnto("_"+ofwhat, PropNodeFacade.class, qtype);
                }
                
                if(node == null){
                    String errMsg = String.format("“%s”没有该指标，请更正 ", repNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                
                ofWhats.add(node);
                StrNode strNode = new StrNode(strval);
                //strNode.ofWhat = ofWhats;
                strNode.hiddenType = HiddenType.PATTERN_HIDDEN;
                repInfos.add(strNode);
            }else if((m = PTN_REP_COPY.matcher(repNode)).matches()){
                String group = m.group(1);
                CopyNode copyNode = new CopyNode(repNode);
                copyNode.groupIndex = Integer.parseInt(group);
                if(copyNode.groupIndex > ptnInfo.getPatSize() 
                		|| copyNode.groupIndex <= 0){
                    String errMsg = String.format("“%s”的复制节点group数错误，请更正 ", repNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                repInfos.add(copyNode);
            }else if((m = PTN_REP_CHNG_NUM.matcher(repNode)).matches()){
                String numIndex = m.group(1);
                String operType = m.group(2);
                String operhand = m.group(3);

                ChangeNumNode changeNumNode = new ChangeNumNode(repNode);
                if(operType.matches("ADD|add|加|ADD2|add2|反加")){
                    changeNumNode.operatorType = OperatorType.ADD;
                }else if(operType.matches("MUL|mul|乘|MUL2|mul2|反乘")){
                    changeNumNode.operatorType = OperatorType.MULTIPLY;
                }else if(operType.matches("SUB|sub|减")){
                    changeNumNode.operatorType = OperatorType.SUBTRACT;
                }else if(operType.matches("DIV|div|除")){
                    changeNumNode.operatorType = OperatorType.DIVIDE;
                }else if(operType.matches("SUB2|sub2|反减")){
                    changeNumNode.operatorType = OperatorType.SUBTRACT;
                    changeNumNode.isPassive = true;
                }else if(operType.matches("DIV2|div2|反除")){
                    changeNumNode.operatorType = OperatorType.DIVIDE;
                    changeNumNode.isPassive = true;
                }else{
                    String errMsg = String.format("“%s”格式错误 ", repNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                
                changeNumNode.number = Double.parseDouble(operhand);
                changeNumNode.numNodePosition = Integer.parseInt(numIndex);
                repInfos.add(changeNumNode);
            }else if((m = PTN_REP_TECHOP.matcher(repNode)).matches()){
            	String text = m.group(1);
            	int num = Integer.parseInt(m.group(2));
            	TechOpNode techop = new TechOpNode(text);
            	techop.maxBindNum = num;
            	techop.hiddenType = HiddenType.PATTERN_HIDDEN;
            	repInfos.add(techop);
            }else if((m = PTN_REP_FAKENUM.matcher(repNode)).matches()){
            	String text = m.group(1);
            	FakeNumNode fakeNumNode = new FakeNumNode(text);
            	if(text.equals("less")){
            		fakeNumNode.setFakeNumType(FakeNumType.LESS);
            		fakeNumNode.setText("较低");
            	}else if(text.equals("more")){
            		fakeNumNode.setFakeNumType(FakeNumType.MORE);
            		fakeNumNode.setText("较高");
            	}else {
            		fakeNumNode.setFakeNumType(FakeNumType.FLAT);
            		fakeNumNode.setText("持平");
            	}
            	fakeNumNode.hiddenType = HiddenType.PATTERN_HIDDEN;
            	repInfos.add(fakeNumNode);
            }else if((m = PTN_REP_CHANGE_DEF.matcher(repNode)).find()){
            	assert(m.groupCount() == 3) ;
            	ChangeDefNode changeDefNode = new ChangeDefNode("") ;
            	String defclassText = m.group(1) ;
            	String textIndexStr = m.group(2) ;
            	String changeGroupIndexStr = m.group(3) ;
            	
            	// changeDefNode's defClass
            	SemanticNode defClassNode = MemOnto.getOnto(defclassText, ClassNodeFacade.class, qtype);
            	if(defClassNode == null || defClassNode.type != NodeType.CLASS){
            		String errMsg = String.format(
            				"“%s”没有该指标，请更正 ", repNode);
            		throw new DataConfException(confFileName, lineNo, errMsg);
            	}
            	changeDefNode.defClass_ = (ClassNodeFacade)defClassNode ;
            	
            	// changeDefNode's text
            	String[] textIndexStrArr = textIndexStr.split(",") ;
            	int[] textIndexArr = new int[textIndexStrArr.length] ;
            	for(int idx = 0; idx < textIndexStrArr.length ; ++ idx){
            		textIndexArr[idx] = Integer.parseInt(textIndexStrArr[idx]) ;
            	}
            	changeDefNode.textIndexArr = textIndexArr ;
            	
            	// changDefNode's change
            	changeDefNode.changeGroupIndex = Integer.parseInt(changeGroupIndexStr) ;
                changeDefNode.type = NodeType.CHANG_DEF ;
                repInfos.add(changeDefNode);
            }else {
                String errMsg = String.format("“%s”格式错误，不知道替换节点，请更正", repNode);
                throw new DataConfException(confFileName, lineNo, errMsg);
            }
        }
        ptnInfo.setRepInfos(repInfos);
    }

    private static void addPatternInfo(HashMap<String, ArrayList<PatternInfo>> ptnInfo,
            String[] labels, PatternInfo aPtn, Query.Type qtype) throws DataConfException {
        if(aPtn == null || aPtn.getPattern() == null ||
                aPtn.getRepInfos() == null){
            return ;
        }
        for(int labelIndex=0; labelIndex<labels.length; labelIndex++){
            String labelI = labels[labelIndex];
            ArrayList<PatternInfo> patternInfos = ptnInfo.get(labelI);
            
            if(patternInfos == null){
                patternInfos = new ArrayList<PatternInfo>();
                patternInfos.add(aPtn);
                ptnInfo.put(labelI, patternInfos);
            }else if(!hasPatternInfo(patternInfos, aPtn)){
                //保证优先级高的先匹配
                int k = 0;
                for(; k< patternInfos.size(); k++){
                    if(aPtn.compareTo(patternInfos.get(k)) >= 0){
                        break;
                    }
                }
                patternInfos.add(k, aPtn);
            }else{
                String warnStr = String.format("Label “%s” 有重复的Pattern信息，请改正", labelI);
                throw new DataConfException(confFileName, -1, warnStr);
            }
        }
    }

    /**
     * 从Pattern节点产生PatternInfo的正则式，注意严格要求其与具体Query的泛化
     * {@link SemanticPattern#getQueryPatternText(ArrayList, int)}保持一致。<br>
     * 另一点，如果想增强pattern的处理功能，譬如支持新的节点或对于某类节点匹配更多信息，则
     * 要求pattern资源文件、资源文件导入、Query的泛化三者同步进行！
     * 
     * @param patNodes
     * @param ptnInfo 
     * @throws DataConfException 
     * @throws UnexpectedException 
     */
    private static void makePatternInfo(String[] patNodes, PatternInfo ptnInfo,
            Query.Type qtype) throws DataConfException, UnexpectedException {
        String regex = "";
        int labelCnt = 0;
        for(int index = 0; index < patNodes.length; index++){
            String keyNode = patNodes[index];
            
            if(keyNode.startsWith("[")){
            	regex += makeMultiRegex(keyNode, qtype, ptnInfo) ;
            }else if(keyNode.startsWith("index")){
            	regex += makeIndexRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("user_index")){
            	regex += makeUserIndexRegex(keyNode, qtype, ptnInfo) ;
            }else if(keyNode.startsWith("prop")){
            	regex += makePropRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("num")){
            	regex += makeNumRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("date")){
            	regex += makeDateRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("sort")){
            	regex += makeSortRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("logic")){
            	regex += makeLogicRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("qword")){
            	regex += makeQwordRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("oper")){
            	regex += makeOperRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("str")){
            	regex += makeStrRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("avg")){
                regex += makeAvgRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("techop")){
            	regex += makeTechopRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("whatever")){
            	regex += makeWhateverRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("techperiod")){
            	regex += makeTechperiodRegex(keyNode, qtype, ptnInfo);
            }else if(keyNode.startsWith("change")){
            	regex += makeChangeRegex(keyNode, qtype, ptnInfo) ;
            }else if(keyNode.startsWith("label")){
                labelCnt++;
                regex += makeLabelRegex(keyNode, qtype, ptnInfo);
            }else if(STR_KEY_START.equals(keyNode)){
                if(index != 0){
                    String errMsg = String.format(
                            "“%s”格式错误:start应该出现在Pattern的最前面", keyNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
                ptnInfo.setHasStart(true);
                regex += makeStartRegex(keyNode, qtype, ptnInfo);
			} else if (STR_KEY_END.equals(keyNode)) {
				if (index != patNodes.length - 1) {
					String errMsg = String.format(
							"“%s”格式错误:end应该出现在Pattern的最末尾", keyNode);
					throw new DataConfException(confFileName, lineNo, errMsg);
				}
				ptnInfo.setHasEnd(true);
				regex += makeEndRegex(keyNode, qtype, ptnInfo);
			}
            else{
                String errMsg = String.format( "“%s”格式错误，不知道其类型，请更正 ", keyNode);
                throw new DataConfException(confFileName, lineNo, errMsg);
            }
        }
        
        if(labelCnt != 1){
            String errMsg = "格式错误，“label”必须出现且只出现一次，请更正 ";
            throw new DataConfException(confFileName, lineNo, errMsg);
        }
        ptnInfo.setPatSize(patNodes.length);
        ptnInfo.setPattern(Pattern.compile(regex));
    }
    
    /**
     * 为了达到代码公用的目的，将具体节点的泛化与可选信息相分离处理。对于指标节点而言，只需要对指标的信息进行泛化处理
     * 然后再根据可选信息一起决定最终表达式的书写匹配。<font color=green>注意：各类节点的泛化格式，要与具体问句的泛化保持一致。</font>
     * @param keyNode
     * @param qtype
     * @param ptnInfo
     * @return
     * @throws DataConfException
     * @throws UnexpectedException
     */
    private static String makeIndexRegex(String keyNode, Query.Type qtype, PatternInfo ptnInfo) throws DataConfException, UnexpectedException {
    	//分离出节点信息和可选信息，例如“index(总股本)!”分为：节点信息“index(总股本)”和可选信息“!”
    	assert(PTN_KEY_INDEX.matcher(keyNode).matches());
    	String indexNode = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			indexNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String nodeRegex = makeIndexNodeRegex(indexNode, qtype, ptnInfo);
		return makeNodeRegexOption(nodeRegex, option, ptnInfo);
	}
    
    /**
     * 根据indexNode的参数，形成匹配相应指标的正则表达式字符串。注意这里的指标指点，
     * 是不含“!”和“?”这两个可选符号的。
     * 一般形式的例子：index(收盘价|最新价)、index()、index、
     * index(number|date|string|bool)、index(元|%|倍|股|户|手|无)等。
     * 以上三个节点依次表示具体某些指标，或值类型为数值、日期、字符串、布尔值，或指标的值单位为元、%、倍等单位。
     * 当然，还可以增加index的参数内容，以匹配更多信息，例如：报告期周期类型（年、季度、日）的指标等。<br>
     * <font color=red>注意1：在进行正则式泛化时，需要根据节点的参数信息进行优先级调整<br>
     * 注意2：各类节点的泛化格式，要与具体问句的泛化保持一致。</font>
     * @param indexNode 
     * @param qtype 
     * @param ptnInfo 
     * @return 指标正则表达式
     * @throws DataConfException 
     * @throws UnexpectedException 
     */
    private static String makeIndexNodeRegex(String indexNode, Query.Type qtype, PatternInfo ptnInfo) throws DataConfException, UnexpectedException{
    	String indexParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(indexNode)).find()){
	    	indexParam = m.group(1);
	    }
	    
    	String[] parms= indexParam.split("\\|");
        boolean isType = false;
        boolean isText = false;
        boolean isUnit = false;
        for(int idx =0; idx < parms.length; idx++){
            if(parms[idx].equals("number")){
                parms[idx] = "N";
                isType = true;
            }else if(parms[idx].equals("date")){
                parms[idx] = "D";
                isType = true;
            }else if(parms[idx].equals("string")){
            	parms[idx] = "S";
                isType = true;
            }else if(parms[idx].equals("bool")){
            	parms[idx] = "B";
                isType = true;
            }else if(parms[idx].matches("元|%|倍|个|股|手|家|户|支|只|无")){
            	if(parms[idx].equals("支")){
            		parms[idx] = "只";
            	}
            	isUnit = true;
            }else if(parms[idx].length() > 0){
                isText = true;
                if(MemOnto.getOnto(parms[idx], ClassNodeFacade.class, qtype) == null){
                    String errMsg = String.format("“%s”没有该指标，请更正 ", indexNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
            }
        }
        
        if((isType||isUnit) && isText){
            String errMsg =  String.format("“%s”指标类别与具体指标同时配置不规范，请更正 ", indexNode);
            throw new DataConfException(confFileName, lineNo, errMsg);
        }else if(isType && isUnit){
        	 String errMsg =  String.format("“%s”指标类别与具体单位同时配置不规范，请更正 ", indexNode);
             throw new DataConfException(confFileName, lineNo, errMsg);
        }
        
        String indexTextRegex = STR_ITEM_REGEX;
        String indexTypeRegex = STR_ITEM_REGEX;
        String indexUnitRegex = STR_ITEM_REGEX;
        
        //由于只要给定了具体指标，其他信息已经确定，所以不需要类型和单位
        if(isText){
        	indexParam = indexParam.replace("(", "\\(");
        	indexParam = indexParam.replace(")", "\\)");
        	indexTextRegex = indexParam;
        	ptnInfo.textPriority();
        //由于给定了单位，就知道是数值指标或时间指标，所以不需要类型
        }else if(isUnit){
        	indexUnitRegex = parms[0];
        	for(int idx = 1; idx < parms.length; idx++){
        		indexUnitRegex += "|" + parms[idx];
        	}
        	ptnInfo.attrPriority();
        //指标值类型相对单位更泛
        }else if(isType){
        	indexTypeRegex = parms[0];
        	for(int idx = 1; idx < parms.length; idx++){
        		indexTypeRegex += "|" + parms[idx];
        	}
        	ptnInfo.typePriority();
        }else{//index或index()形式的没有任何参数信息
        }
        
        String indexRegex = "INDEX\\d+" + STR_ITEM_SEP + "(?:" + indexTextRegex +")"
        		+ STR_ITEM_SEP + "(?:" + indexTypeRegex +")"
        		+ STR_ITEM_SEP + "(?:" + indexUnitRegex +")";
    	return indexRegex;
    }
    
    private static String makeUserIndexRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException, UnexpectedException {
    	assert(PTN_KEY_USER_INDEX.matcher(keyNode).matches());
    	String userindexNode = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			userindexNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String nodeRegex = makeUserIndexNodeRegex(userindexNode, qtype, ptnInfo);
		return makeNodeRegexOption(nodeRegex, option, ptnInfo);
    }
    
    private static String makeUserIndexNodeRegex(String indexNode, Query.Type qtype, PatternInfo ptnInfo) throws DataConfException, UnexpectedException{
    	String userIndexParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(indexNode)).find()){
	    	userIndexParam = m.group(1);
	    }
	    
    	String[] parms= userIndexParam.split("\\|");
        boolean isText = false;
        for(int idx =0; idx < parms.length; idx++){
            if(parms[idx].length() > 0){
            	isText = true;
            	ClassNodeFacade IndexfromOnto = MemOnto.getOnto(parms[idx], UserClassNodeFacade.class, qtype) ;
                if(IndexfromOnto == null){
                    String errMsg = String.format("“%s”没有该指标，请更正 ", indexNode);
                    throw new DataConfException(confFileName, lineNo, errMsg);                    
                } else if(!IndexfromOnto.isFake()) {
                	String errMsg = String.format("“%s”该指标不为UserIndex，请更正 ", indexNode);
                	throw new DataConfException(confFileName, lineNo, errMsg);         
                } 
            }
        }
        String userIndexTextRegex = STR_ITEM_REGEX;
        if(isText){
        	userIndexParam = userIndexParam.replace("(", "\\(");
        	userIndexParam = userIndexParam.replace(")", "\\)");
        	userIndexTextRegex = userIndexParam;
        	ptnInfo.textPriority();
        }
        
        String userIndexRegex = "USERINDEX\\d+" + STR_ITEM_SEP + "(?:" + userIndexTextRegex +")" ;
    	return userIndexRegex;
    }
    

	private static String makePropRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException, UnexpectedException {
    	assert(PTN_KEY_PROP.matcher(keyNode).matches());
    	String PropNodeFacade = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			PropNodeFacade = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String propNodeRegex = makePropNodeRegex(PropNodeFacade, qtype, ptnInfo);
		return makeNodeRegexOption(propNodeRegex, option, ptnInfo);
	}
	
    /**
     * 根据propNode的参数，形成匹配相应属性的正则表达式字符串。注意这里的属性指点，
     * 是不含“!”和“?”这两个可选符号的。
     * 一般形式的例子：prop(_浮点型数值|日期)、prop(number|date|string)等。
     * 以上两个节点依次表示具体某些属性，或值类型为数值、日期、字符串、布尔值。
     * 当然，还可以增加prop的参数内容，以匹配更多信息。<br>
     * <font color=green>注意1：在进行正则式泛化时，需要根据节点的参数信息进行优先级调整<br>
     * 注意2：各类节点的泛化格式，要与具体问句的泛化保持一致。</font>
     * @param PropNodeFacade 
     * @param qtype 
     * @param ptnInfo 
     * @return 属性正则表达式
     * @throws DataConfException 
     * @throws UnexpectedException 
     */
    private static String makePropNodeRegex(String PropNodeFacade, Type qtype, PatternInfo ptnInfo) throws DataConfException, UnexpectedException {
    	Matcher m = null;
    	String propParam = "";
	    if((m = PTN_NODE_PARAM.matcher(PropNodeFacade)).find()){
	    	propParam = m.group(1);
	    }
	    
	    String[] params= propParam.split("\\|");
        boolean isType = false;
        boolean isText = false;
        for(int idx =0; idx < params.length; idx++){
            if(params[idx].equals("number")){
                params[idx] = "N";
                isType = true;
            }else if(params[idx].equals("date")){
                params[idx] = "D";
                isType = true;
            }else if(params[idx].equals("string")){
            	params[idx] = "S";
            	isType = true;
            }else if(params[idx].equals("bool")){
            	params[idx] = "B";
            	isType = true;
            //因为具体属性确定了属性的其他信息
            }else if(params[idx].length() > 0){
                isText = true;
                SemanticNode tmpNode = null;
                try{
                	tmpNode = MemOnto.getOnto(params[idx], PropNodeFacade.class, qtype);
                	if(PropNodeFacade == null){
                        params[idx] = "_" + params[idx];
                        tmpNode = MemOnto.getOnto(params[idx], PropNodeFacade.class, qtype);
                    }
                }catch (UnexpectedException e){
                	if(!params[idx].startsWith("_")){
                		params[idx] = "_" + params[idx];
                    	tmpNode = MemOnto.getOnto(params[idx], PropNodeFacade.class, qtype);
                	}
                }
                if(tmpNode == null){
                    String errMsg =String.format("“%s”没有该属性，请更正 ", PropNodeFacade);
                    throw new DataConfException(confFileName, lineNo, errMsg);
                }
            }
        }
        
        if(isType && isText){
            String errMsg =  String.format("“%s”属性类别与具体属性同时配置不规范，请更正 ", PropNodeFacade);
            throw new DataConfException(confFileName, lineNo, errMsg);
        }
        
        String propTextRegex = STR_ITEM_REGEX;
        String propTypeRegex = STR_ITEM_REGEX;
        if(isText){
        	propTextRegex = params[0];
            for(int wdx = 1; wdx < params.length; wdx++){
            	propTextRegex += "|" + params[wdx];
            }
            ptnInfo.textPriority();
        }else if(isType){
        	propTypeRegex = params[0];
        	for(int wdx = 1; wdx < params.length; wdx++){
        		propTypeRegex += "|" + params[wdx];
            }
            ptnInfo.typePriority();
        }else{//prop或prop()形式，无任何属性参数
        }
        String propRegex = "PROP\\d+" + STR_ITEM_SEP + "(?:" + propTextRegex +")"
        		+ STR_ITEM_SEP + "(?:" + propTypeRegex +")";
        return propRegex;
    }

	private static String makeNumRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		assert(PTN_KEY_NUM.matcher(keyNode).matches());
		String numNode = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			numNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String numNodeRegex = makeNumNodeRegex(numNode, qtype, ptnInfo);
		return makeNodeRegexOption(numNodeRegex, option, ptnInfo);
	}

	/**
     * 根据numNode的参数，形成匹配相应数值的正则表达式字符串。注意这里的数值指点，
     * 是不含“!”和“?”这两个可选符号的。
     * 一般形式的例子：num(元|%|倍|股|户|手|无)等。
     * 当然，还可以增加prop的参数内容，以匹配更多信息。<br>
     * <font color=green>注意1：在进行正则式泛化时，需要根据节点的参数信息进行优先级调整<br>
     * 注意2：各类节点的泛化格式，要与具体问句的泛化保持一致。</font>
     * @param PropNodeFacade 
     * @param qtype 
     * @param ptnInfo 
     * @return 数值正则表达式
     * @throws DataConfException 
     * @throws UnexpectedException 
     */
	private static String makeNumNodeRegex(String numNode, Type qtype,
			PatternInfo ptnInfo) throws DataConfException {
		Matcher m = null;
    	String numParam = "";
	    if((m = PTN_NODE_PARAM.matcher(numNode)).find()){
	    	numParam = m.group(1);
	    }
	    
	    String numTextRegex = "";
	    String[] params= numParam.split("\\|");
	    for(int idx =0; idx < params.length; idx++){
            if(params[idx].matches("%|百分比|百分之")){
                params[idx] = "" + Unit.PERCENT;
            }else if(params[idx].matches("yuan|YUAN|元|块|角")){
                params[idx] = "" + Unit.YUAN;
            }else if(params[idx].matches("hkd|HKD|港元|港币|港圆")){
                params[idx] = "" + Unit.HKD;
            }else if(params[idx].matches("usd|USD|美元|美金|美分")){
                params[idx] = "" + Unit.USD;
            }else if(params[idx].matches("unknown|UNKNOWN|其他|其它")){
                params[idx] = "" + Unit.UNKNOWN;
            }else if(params[idx].matches("bei|BEI|倍|倍数")){
                params[idx] = "" + Unit.BEI;
            }else if(params[idx].matches("zhi|ZHI|只|支")){
                params[idx] = "" + Unit.ZHI;
            }else if(params[idx].matches("jia|JIA|家|家数")){
                params[idx] = "" + Unit.JIA;
            }else if(params[idx].matches("shou|SHOU|手")){
                params[idx] = "" + Unit.SHOU;
            }else if(params[idx].matches("gu|GU|股|股数")){
                params[idx] = "" + Unit.GU;
            }else if(params[idx].matches("ge|GE|个")){
                params[idx] = "" + Unit.GE;
            }else if(params[idx].matches("hu|HU|户|户数")){
                params[idx] = "" + Unit.HU;
            }else if(params[idx].matches("sui|SUI|岁")){
                params[idx] = "" + Unit.SUI;
            }else if(params[idx].matches("wei|WEI|位")){
                params[idx] = "" + Unit.WEI;
            }else if(params[idx].matches("year|YEAR|年|年数|年份")){
                params[idx] = "" + Unit.YEAR;
            }else if(params[idx].matches("month|MONTH|月|月份|月数")){
                params[idx] = "" + Unit.MONTH;
            }else if(params[idx].matches("季度|季")){
                params[idx] = "" + Unit.QUARTER;
            }else if(params[idx].length() > 0){
                String errMsg = String.format("“%s”数字单位不存在，请更正 ", numNode);
                throw new DataConfException(confFileName, lineNo, errMsg);
            }
            
            if(params[idx].length() > 0){
            	if(idx == 0){
    	    		numTextRegex = params[idx];
    	    	}else {
    	    		numTextRegex += "|" + params[idx];
    	    	}
            }
        }
	    
	    if(numTextRegex.isEmpty()){
	    	numTextRegex = STR_ITEM_REGEX ;
	    }else {
	    	ptnInfo.typePriority() ;
	    }
	    
	    return "NUM\\d+" + STR_ITEM_SEP + STR_ITEM_REGEX 
	    		+ STR_ITEM_SEP + "(?:" + numTextRegex +")";
	}

	private static String makeDateRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		assert(PTN_KEY_DATE.matcher(keyNode).matches());
		String dateNode = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			dateNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String dateNodeRegex = makeDateNodeRegex(dateNode, qtype, ptnInfo);
		return makeNodeRegexOption(dateNodeRegex, option, ptnInfo);
	}
	
	/**
     * 根据dateNode的参数，形成匹配相应时间的正则表达式字符串。注意这里的时间指点，
     * 是不含“!”和“?”这两个可选符号的。
     * 一般形式的例子：date(年|季|月|周|日)，date(连续|非连续)，date(时间点|时间段)，
     * date(年|季|月|周|日,连续|非连续)，date(年|季|月|周|日,非连续,时间点|时间段)等。
     * 当然，还可以增加date的参数内容，以匹配更多信息。<br>
     * <font color=green>
     * 注意1：在进行正则式泛化时，需要根据节点的参数信息进行优先级调整<br>
     * 注意2：各类节点的泛化格式，要与具体问句的泛化保持一致。<br>
     * 注意3：date(连续)一定是date(时间段|多周期)</font>
     * @param dateNode 
     * @param qtype 
     * @param ptnInfo 
     * @return 时间正则表达式
     * @throws DataConfException 
     * @throws UnexpectedException 
     */
	private static String makeDateNodeRegex(String dateNode, Type qtype,
			PatternInfo ptnInfo) throws DataConfException {
		Matcher m = null;
    	String dateParam = "";
	    if((m = PTN_NODE_PARAM.matcher(dateNode)).find()){
	    	dateParam = m.group(1);
	    }
	    dateParam = dateParam.replace(',', '|');
	    String[] params= dateParam.split("\\|");
	    
        String dateUnitText = "";
        String dateLianxuText = "";
        String dateCycleText = "";
        String dateFrequenceText = "" ;
        
        for(int wdx = 0; wdx < params.length; wdx++){
            if(params[wdx].matches("year|YEAR|年|年数|年份")){
            	if(!dateUnitText.equals(""))dateUnitText+="|";
            	dateUnitText += "Y";
            }else if(params[wdx].matches("month|MONTH|月|月份|月数")){
            	if(!dateUnitText.equals(""))dateUnitText+="|";
            	dateUnitText += "M";
            }else if(params[wdx].matches("quarter|QUARTER|季|季度")){
            	if(!dateUnitText.equals(""))dateUnitText+="|";
            	dateUnitText += "Q";
            }else if(params[wdx].matches("week|WEEK|周|周数")){
            	if(!dateUnitText.equals(""))dateUnitText+="|";
            	dateUnitText += "W";
            }else if(params[wdx].matches("day|DAY|天|日|交易日")){
            	if(!dateUnitText.equals(""))dateUnitText+="|";
            	dateUnitText += "D";
            }else if(params[wdx].equals("连续")){
            	if(!dateLianxuText.equals(""))dateLianxuText+="|";
            	dateLianxuText += "S";
            }else if(params[wdx].equals("非连续")){
            	if(!dateLianxuText.equals(""))dateLianxuText+="|";
            	dateLianxuText += "N";
            }else if(params[wdx].matches("时间点|单周期")){
            	if(!dateCycleText.equals(""))dateCycleText+="|";
            	dateCycleText += "S";
            }else if(params[wdx].matches("时间段|多周期")){
            	if(!dateCycleText.equals(""))dateCycleText+="|";
            	dateCycleText += "M";
            }else if(params[wdx].matches("频度")){
            	//频度：5天内有两天涨停
            	if(!dateFrequenceText.equals(""))dateFrequenceText+="|";
            	dateFrequenceText += "F";
            }else if(params[wdx].matches("非频度")){
            	if(!dateFrequenceText.equals(""))dateFrequenceText+="|";
            	dateFrequenceText += "N";
            }else if(params[wdx].length() > 0){
                String errMsg = confFileName + String.format(
                        "“%s”日期单位不正确，请更正 ", dateNode);
                throw new DataConfException(confFileName, lineNo, errMsg);
            }
        }
        if(dateUnitText.equals("")){
        	dateUnitText = STR_ITEM_REGEX;
        } else { 
        	ptnInfo.typePriority() ;
        }
        
        if(dateLianxuText.equals("")){
        	dateLianxuText = STR_ITEM_REGEX;
        } else {
        	ptnInfo.typePriority() ;
        }
        
        if(dateCycleText.equals("")){
        	dateCycleText = STR_ITEM_REGEX;
        } else {
        	ptnInfo.typePriority() ;
        }
        if(dateFrequenceText.equals("")){
        	dateFrequenceText = STR_ITEM_REGEX ;
        } else {
        	ptnInfo.typePriority() ;
        }
	    
	    return "DATE\\d+" + STR_ITEM_SEP + STR_ITEM_REGEX 
	    		+ STR_ITEM_SEP + "(?:" +dateUnitText +")"
	    		+ STR_ITEM_SEP + "(?:" + dateLianxuText +")"
	    		+ STR_ITEM_SEP + "(?:" + dateCycleText +")"
	    		+ STR_ITEM_SEP + "(?:" + dateFrequenceText +")";
	}

	private static String makeSortRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {

		assert(PTN_KEY_SORT.matcher(keyNode).matches());
    	String sortNode = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			sortNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String nodeRegex = makeSortNodeRegex(sortNode, qtype, ptnInfo);
		return makeNodeRegexOption(nodeRegex, option, ptnInfo);	
	}
	private static String makeSortNodeRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException{
		
		String sortParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(keyNode)).find()){
	    	sortParam = m.group(1);
	    }
	    
	    String sortType = STR_ITEM_REGEX ; 
	    if(sortParam.equals("升序")){
	    	sortType = "升"  ;
	    	ptnInfo.typePriority() ;
	    } else if(sortParam.equals("降序")){
	    	sortType = "降"  ;
	    	ptnInfo.typePriority() ;
	    }
	    
		return "SORT\\d" + STR_ITEM_SEP + STR_ITEM_REGEX 
	    		+STR_ITEM_SEP + sortType ;
	}
	
	
	/**
	 * 疑问词节点
	 * @param keyNode
	 * @param qtype
	 * @param ptnInfo
	 * @return
	 * @throws DataConfException
	 */
	private static String makeQwordRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		assert(PTN_KEY_QWORD.matcher(keyNode).matches()) ;
		String qwordNode = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			qwordNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String qwordRegex = makeQwordNodeRegex(qwordNode, qtype, ptnInfo) ;
		return makeNodeRegexOption(qwordRegex , option , ptnInfo) ;		
	}
	
	private static String makeQwordNodeRegex(String qwordNode, Type qtype, PatternInfo ptnInfo)throws DataConfException{
		String qwordParam = "";
		Matcher m = null;
		if ((m = PTN_NODE_PARAM.matcher(qwordNode)).find()) {
			qwordParam = m.group(1);
		}

		String qwordType = "";
		String[] params = qwordParam.split("\\|");

		for (int wdx = 0; wdx < params.length; wdx++) {
			if (params[wdx].matches("how_much|HOW_MUCH|价格")) {
				if (qwordType.isEmpty()) {
					qwordType = "价";
				} else {
					qwordType += "|价";
				}
			} else if (params[wdx].matches("what|WHAT|什么")) {
				if (qwordType.isEmpty()) {
					qwordType = "什么";
				} else {
					qwordType += "|什么";
				}
			} else if (params[wdx].matches("who|WHO|谁|人")) {
				if (qwordType.isEmpty()) {
					qwordType = "谁";
				} else {
					qwordType += "|谁";
				}
			} else if (params[wdx].matches("where|WHERE|哪里|哪儿|地点|地址")) {
				if (qwordType.isEmpty()) {
					qwordType = "哪";
				} else {
					qwordType += "|哪";
				}
			} else if (params[wdx].matches("when|WHEN|时间|何时")) {
				if (qwordType.isEmpty()) {
					qwordType = "何时";
				} else {
					qwordType += "|何时";
				}
			} else if (params[wdx].matches("which|WHICH|哪个")) {
				if (qwordType.isEmpty()) {
					qwordType = "哪个";
				} else {
					qwordType += "|哪个";
				}
			} else if (params[wdx].matches("unknown|UNKNOWN|其他|其它")) {
				if (qwordType.isEmpty()) {
					qwordType = "UN";
				} else {
					qwordType += "|UN";
				}
			} else if (!params[wdx].isEmpty()) {
				String errMsg = String.format("“%s”疑问词的类型不存在，请更正 ", qwordNode);
				throw new DataConfException(confFileName, lineNo, errMsg);
			}
		}
		if (qwordType.isEmpty()) {
			qwordType = STR_ITEM_REGEX;
		} else {
			ptnInfo.typePriority();
		}
		return "QWORD\\d+" + STR_ITEM_SEP + STR_ITEM_REGEX 
	    		+ STR_ITEM_SEP + "(?:" + qwordType + ")";
	}

	private static String makeOperRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		assert(PTN_KEY_OPER.matcher(keyNode).matches());
    	String operNode = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			operNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		
		String operNodeRegex = makeOperNodeRegex(operNode, qtype, ptnInfo) ;				
		return makeNodeRegexOption(operNodeRegex , option , ptnInfo) ;
	}
		
	private static String makeOperNodeRegex(String operNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		String operParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(operNode)).find()){
	    	operParam = m.group(1);
	    }
		
		String operatorType = "";
		String[] params = operParam.split("\\|");
		for (int wdx = 0; wdx < params.length; wdx++) {
			if (params[wdx].matches("SUB|sub|减")) {
				if (operatorType.isEmpty()) {
					operatorType = "减";
				} else {
					operatorType += "|减";
				}
			} else if (params[wdx].matches("ADD|add|加")) {
				if (operatorType.isEmpty()) {
					operatorType = "加";
				} else {
					operatorType += "|加";
				}
			} else if (params[wdx].matches("DIV|div|除")) {
				if (operatorType.isEmpty()) {
					operatorType = "除";
				} else {
					operatorType += "|除";
				}
			} else if (params[wdx].matches("MUL|mul|乘")) {
				if (operatorType.isEmpty()) {
					operatorType = "乘";
				} else {
					operatorType += "|乘";
				}
			} else if (params[wdx].matches("RATE|rate|比率|比例")) {
				if (operatorType.isEmpty()) {
					operatorType = "比";
				} else {
					operatorType += "|比";
				}
			} else if (!params[wdx].isEmpty()) {
				String errMsg = String.format("“%s”没有该类运算符，请更正 ", operNode);
				throw new DataConfException(confFileName, lineNo, errMsg);
			}
		}

		if (operatorType.isEmpty()) {
			operatorType = STR_ITEM_REGEX;
		} else {
			ptnInfo.typePriority();
		}
		return "OPERATOR\\d+" + STR_ITEM_SEP + STR_ITEM_REGEX 
	    		+ STR_ITEM_SEP + "(?:" + operatorType + ")";
	}


	private static String makeLogicRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		assert(PTN_KEY_LOGIC.matcher(keyNode).matches());
    	String logicNode = keyNode;
    	String option = "";
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			logicNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		
		String logicNodeRegex = makeLogicNodeRegex(logicNode, qtype, ptnInfo) ;				
		return makeNodeRegexOption(logicNodeRegex , option , ptnInfo) ;		
	}
	
	private static String makeLogicNodeRegex(String logicNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		String operParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(logicNode)).find()){
	    	operParam = m.group(1);
	    }
		
		String logicType = "";
		String[] params = operParam.split("\\|");
		for(int wdx = 0 ; wdx < params.length ; wdx++){
			if (params[wdx].matches("AND|and")) {
				if (logicType.isEmpty()) {
					logicType = "与";
				} else {
					logicType += "|与";
				}
			} else if(params[wdx].matches("OR|or")){
				if (logicType.isEmpty()) {
					logicType = "或";
				} else {
					logicType += "|或";
				}
			} else if(!params[wdx].isEmpty()){
				String errMsg =  String.format("“%s”排序节点书写不规范 ", logicNode);
				throw new DataConfException(confFileName, lineNo, errMsg);
			}
		}
		
		if (logicType.isEmpty()) {
			logicType = STR_ITEM_REGEX;
		} else {
			ptnInfo.typePriority();
		}
		
		return "LOGIC\\d+" + STR_ITEM_SEP + STR_ITEM_REGEX 
	    		+ STR_ITEM_SEP + "(?:" + logicType + ")";
	}

	private static String makeStrRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException, UnexpectedException {
		assert(PTN_KEY_STR.matcher(keyNode).matches()) ;
		String strNode = keyNode ;
		String option = "" ;
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			strNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String strNodeRegex = makeStrNodeRegex(strNode, qtype, ptnInfo) ;
		return makeNodeRegexOption(strNodeRegex , option , ptnInfo) ;
	}
	
	private static String makeStrNodeRegex(String strNode, Type qtype,
			PatternInfo ptnInfo) throws DataConfException, UnexpectedException {
		String strParam = "";
		Matcher m = null;
		if ((m = PTN_NODE_PARAM.matcher(strNode)).find()) {
			strParam = m.group(1);
		}

		int ofIndex = strParam.lastIndexOf(',');
		String strText = strParam;
		String strOfwhat = "";
		if (ofIndex >= 0) {
			strText = strParam.substring(0, ofIndex);
			strOfwhat = strParam.substring(ofIndex + 1);
		}
		String[] words = strText.split("\\|");
		for (String word : words) {
			ptnSecialWords.put(word, EnumSet.of(SpecialWordType.PATTERN_WORD));
		}
		if (strText.trim().equals("")) {
			strText = STR_ITEM_REGEX;
		} else {
			ptnInfo.textPriority();
		}
		
		String ofwhatRegex = "";
		String[] ofwhats = strOfwhat.split("\\|");
		for (int idx = 0; idx < ofwhats.length; idx++) {
			String tmpWhat = ofwhats[idx];
			if(tmpWhat.isEmpty()){
				continue ;
			}
			SemanticNode PropNodeFacade = null;
			try {
				// 从本体中检查
				PropNodeFacade = MemOnto.getOnto(tmpWhat, PropNodeFacade.class, qtype);
				if (PropNodeFacade == null && !tmpWhat.startsWith("_")) {
					ofwhats[idx] = "_" + ofwhats[idx];
					PropNodeFacade = MemOnto.getOnto(ofwhats[idx], PropNodeFacade.class,
							qtype);
				}
			} catch (UnexpectedException e) {
				if (!tmpWhat.startsWith("_")) {
					ofwhats[idx] = "_" + ofwhats[idx];
					PropNodeFacade = MemOnto.getOnto(ofwhats[idx], PropNodeFacade.class,
							qtype);
				}
			}

			if (PropNodeFacade == null) {
				String errMsg = String.format("字符串“%s”中属性配置有误，没有该属性，请更正 ",
						strNode);
				throw new DataConfException(confFileName, lineNo, errMsg);
			}
			if (idx == 0) {
				ofwhatRegex = ofwhats[idx];
			} else {
				ofwhatRegex += "|" + ofwhats[idx];
			}
		}

		if (ofwhatRegex.isEmpty()) {
			ofwhatRegex = STR_ITEM_REGEX;
		} else {
			ptnInfo.attrPriority();
			ofwhatRegex = STR_ITEM_REGEX + "(?:" + ofwhatRegex + ")+" + STR_ITEM_REGEX;
		}
		
		return "STR\\d+" + STR_ITEM_SEP + "(?:" + strText + ")" + STR_ITEM_SEP
				+ "(?:" + ofwhatRegex + ")";
	}

	private static String makeAvgRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		assert(PTN_KEY_AVG.matcher(keyNode).matches()) ;
		String avgNode = keyNode ;
		String option = "" ;
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			avgNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String avgNodeRegex = makeAvgNodeRegex(avgNode, qtype, ptnInfo) ;
		return makeNodeRegexOption(avgNodeRegex , option , ptnInfo) ;
	}
	
	private static String makeAvgNodeRegex(String avgNode, Type qtype, PatternInfo ptnInfo) throws DataConfException{
		String avgParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(avgNode)).find()){
	    	avgParam = m.group(1);
	    }
	    
	    String avgText = avgParam ; 
	    if(avgText.isEmpty()){
	    	avgText = STR_ITEM_REGEX ;
	    }else {
	    	ptnInfo.textPriority() ;
	    }
	    
		return "AVG\\d+" + STR_ITEM_SEP + "(?:" + avgText + ")" ;
	}

	private static String makeTechopRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		assert(PTN_KEY_TECHOP.matcher(keyNode).matches()) ;
		String techopNode = keyNode ;
		String option = "" ;
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			techopNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String techopNodeRegex = makeTechopNodeRegex(techopNode, qtype, ptnInfo) ;
		return makeNodeRegexOption(techopNodeRegex , option , ptnInfo) ;
	}
	
	private static String makeTechopNodeRegex(String techopNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		String techopParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(techopNode)).find()){
	    	techopParam = m.group(1);
	    }
	    
	    String techopText = techopParam ; 
	    if(techopText.isEmpty()){
	    	techopText = STR_ITEM_REGEX ;
	    } else {
	    	ptnInfo.textPriority() ;
	    }
	    
		return "TECHOP\\d+" + STR_ITEM_SEP + "(?:" + techopText + ")";
	}

	private static String makeWhateverRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {

		assert(PTN_KEY_WHATEVER.matcher(keyNode).matches()) ;
		String whateverNode = keyNode ;
		String option = "" ;
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			whateverNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String whateverNodeRegex = makeWhateverNodeRegex(whateverNode, qtype, ptnInfo) ;
		return makeNodeRegexOption(whateverNodeRegex , option , ptnInfo) ;
	}
	
	private static String makeWhateverNodeRegex(String whateverNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		String whateverText = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(whateverNode)).find()){
	    	whateverText = m.group(1);
	    }
	    assert(!whateverText.isEmpty()) ;
		return "[A-Z]+\\d+" +STR_ITEM_SEP + "(?:" + whateverText + ")"
				+ STR_ONE_NODE_REST;
	}
	
	private static String makeTechperiodRegex(String keyNode, Type qtype, PatternInfo ptnInfo)  throws DataConfException{
		assert(PTN_KEY_TECHPERIOD.matcher(keyNode).matches()) ;
		String techperiodNode = keyNode ;
		String option = "" ;
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			techperiodNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String techperiodNodeRegex = makeTechperiodNodeRegex(techperiodNode, qtype, ptnInfo) ;
		return makeNodeRegexOption(techperiodNodeRegex , option , ptnInfo) ;		
	}
	
	private static String makeTechperiodNodeRegex(String techperiodNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		String techperiodParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(techperiodNode)).find()){
	    	techperiodParam = m.group(1);
	    }
	    
	    String techperiodType = "";
		String[] params = techperiodParam.split("\\|");
		// DAY|WEEK|MONTH|YEAR|MIN_1|MIN_5|MIN_15|MIN_30|MIN_60
		for(int wdx = 0 ; wdx < params.length ; wdx++){
			if (params[wdx].matches("DAY|day")) {
				if (techperiodType.isEmpty()) {
					techperiodType = "D";
				} else {
					techperiodType += "|D";
				}
			} else if(params[wdx].matches("WEEK|week")){
				if (techperiodType.isEmpty()) {
					techperiodType = "W";
				} else {
					techperiodType += "|W";
				}
			} else if(params[wdx].matches("MONTH|month")){
				if (techperiodType.isEmpty()) {
					techperiodType = "M";
				} else {
					techperiodType += "|M";
				}
			} else if(params[wdx].matches("YEAR|year")){
				if (techperiodType.isEmpty()) {
					techperiodType = "Y";
				} else {
					techperiodType += "|Y";
				}
			} else if(params[wdx].matches("MIN_1|min_1")){
				if (techperiodType.isEmpty()) {
					techperiodType = "m1";
				} else {
					techperiodType += "|m1";
				}
			} else if(params[wdx].matches("MIN_5|min_5")){
				if (techperiodType.isEmpty()) {
					techperiodType = "m5";
				} else {
					techperiodType += "|m5";
				}
			} else if(params[wdx].matches("MIN_15|min_15")){
				if (techperiodType.isEmpty()) {
					techperiodType = "m15";
				} else {
					techperiodType += "|m15";
				}
			} else if(params[wdx].matches("MIN_30|min_30")){
				if (techperiodType.isEmpty()) {
					techperiodType = "m30";
				} else {
					techperiodType += "|m30";
				}
			} else if(params[wdx].matches("MIN_60|min_60")){
				if (techperiodType.isEmpty()) {
					techperiodType = "m60";
				} else {
					techperiodType += "|m60";
				}
			} else if(!params[wdx].isEmpty()){
				String errMsg =  String.format("“%s”排序节点书写不规范 ", techperiodNode);
				throw new DataConfException(confFileName, lineNo, errMsg);
			}
		}
	    
	    String techperiodText = techperiodType ; 
	    if(techperiodText.isEmpty()){
	    	techperiodText = STR_ITEM_REGEX ;
	    } else {
	    	ptnInfo.textPriority() ;
	    }
	    
		return "TECHPERIOD\\d+" + STR_ITEM_SEP + "(?:" + techperiodText + ")";
	} 
	
	private static String makeChangeRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException{
		assert(PTN_KEY_CHANGE.matcher(keyNode).matches()) ;
		String changeNode = keyNode ;
		String option = "" ;
		if(PTN_NODE_OPTION.matcher(keyNode).matches()){
			changeNode = keyNode.substring(0, keyNode.length()-1);
    		option = keyNode.substring(keyNode.length()-1);
		}
		String changeNodeRegex = makeChangeNodeRegex(changeNode, qtype, ptnInfo) ;
		return makeNodeRegexOption(changeNodeRegex , option , ptnInfo) ;		
	}
	
	private static String makeChangeNodeRegex(String changeNode, Type qtype, PatternInfo ptnInfo) throws DataConfException{
		String changeParam = "";
		Matcher m = null;
	    if((m = PTN_NODE_PARAM.matcher(changeNode)).find()){
	    	changeParam = m.group(1);
	    }
	    String changeText = changeParam ;
	    if(changeText.isEmpty()){
	    	changeText = STR_ITEM_REGEX ;
	    } else {
	    	ptnInfo.textPriority() ;
	    }
		return "CHANGE\\d+" + STR_ITEM_SEP + "(?:" + changeText + ")";
	}
	
	private static String makeLabelRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		if(!STR_KEY_LABEL.equals(keyNode)){
			String errMsg =  String.format("“%s”节点书写不规范 ", keyNode);
			throw new DataConfException(confFileName, lineNo, errMsg);
		}
		return STR_NODE_SEP + "(LABEL\\d+)";
	}

	private static String makeStartRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		if(!STR_KEY_START.equals(keyNode)){
			String errMsg =  String.format("“%s”节点书写不规范 ", keyNode);
			throw new DataConfException(confFileName, lineNo, errMsg);
		}
		ptnInfo.startPriority();
		return "^("+ STR_NODE_SEP + "STR\\d+"+ STR_ITEM_SEP + STR_ITEM_REGEX +")*"; 
	}

	private static String makeEndRegex(String keyNode, Type qtype, PatternInfo ptnInfo) throws DataConfException {
		if(!STR_KEY_END.equals(keyNode)){
			String errMsg =  String.format("“%s”节点书写不规范 ", keyNode);
			throw new DataConfException(confFileName, lineNo, errMsg);
		}
		ptnInfo.endPriority();
		return "(" + STR_NODE_SEP +"STR\\d+"+ STR_ITEM_SEP + STR_ITEM_REGEX +")*$";
	}

	private static boolean isRegexExist(Pattern regex,
	        ArrayList<Pair<Pattern, String>> ptn2Rpl) {
	    for(Pair<Pattern, String> ps : ptn2Rpl) {
	        if(ps.first.equals(regex)) {
	            return true;
	        }
	    }
	    return false;
	}

	private static void parseRegex(ArrayList<Pair<Pattern, String>> regexInfo,
	        String info, Query.Type qtype)
	throws DataConfException {
	    String[] strs = info.split("REG=>");
	    if (strs.length != 2) {
	        throw new DataConfException(confFileName, lineNo,
	                "格式错误:[%s]“REG=>”分隔出字段数：%d", info, strs.length);
	    }
	    String pattern = strs[0].trim();
	    pattern = pattern.replace("NaN", "([^\\d零一二三四五六七八九十百两]+?|^|$)");//非数字
	    pattern = pattern.replace("NUM", "(-?[\\d零一二三四五六七八九十百两百千万亿]+\\.?\\d*)");
	    pattern = pattern.replace("UNIT", "(手|倍|股|元|块|块钱|美元|点|个|只|份|支|部|部分|家|户|盘|%|人|)");
	    String replace = strs[1].trim();
	    if(replace.endsWith(";")){
	    	replace = replace.substring(0, replace.length() -1);
	    }
	    Pattern pat = Pattern.compile(pattern);
	    if (isRegexExist(pat, regexInfo)) {
	        throw new DataConfException(confFileName, lineNo, "正则式重复定义");
	    }
	    regexInfo.add(new Pair<Pattern, String>(pat, replace));
	}
	
    /**
     * 根据节点信息（正则表达式）nodeRegex和节点可选参数option，书写匹配正则表达式。
     * 由于节点参数信息和可选信息，确定最终匹配节点，需根据匹配多少调整优先级别。<br>
     * <font color=green>注意：节点正则式+可选项，达代码公用的目的</font>
     * @param nodeRegex 匹配节点的表达式
     * @param option 可选信息
     * @param ptnInfo 
     * @return 最终正则表达式
     */
    private static String makeNodeRegexOption(String nodeRegex, String option, PatternInfo ptnInfo){
    	if(option.equals("?")){
    		ptnInfo.optionPriority();
            return STR_NODE_SEP +"?("+nodeRegex+")?";
        }else if(option.equals("!")){
        	ptnInfo.notMatchPriority();
        	return STR_NODE_SEP +"((?!"+ nodeRegex +")" + STR_ONE_NODE+")";
        }else{
        	ptnInfo.matchPriority();
        	return STR_NODE_SEP + "(" + nodeRegex +")";
        }
    }

	private static boolean hasPatternInfo(ArrayList<PatternInfo> pis,
	        PatternInfo pi) {
	    for(int i = 0;i<pis.size();i++){
	        PatternInfo oldPi = pis.get(i);
	        if(oldPi.equals(pi)){
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * 对多个节点的做!或? 
	 * 节点暂时只支持Index
	 * @param keyNode
	 * @param qtype
	 * @param ptnInfo
	 * @return
	 */
	private static String makeMultiRegex(String keyNode, Query.Type qtype, PatternInfo ptnInfo)throws DataConfException, UnexpectedException{
		assert(PTN_KEY_MULTI.matcher(keyNode).matches()) ;
		String multiRegex = "" ;
		ArrayList<String> subKeyNodes = new ArrayList<String>() ;
		String option = "" ;
		Matcher multiMatcher = PTN_KEY_MULTI_NODE.matcher(keyNode);	
		
		while(multiMatcher.find()){
			subKeyNodes.add(multiMatcher.group(1)) ;
		}
		
		if(keyNode.indexOf("]") == keyNode.length()-2)
			option = keyNode.substring(keyNode.indexOf("]")+1) ;

		// 还原比对
		StringBuffer reduction = new StringBuffer("[") ;
		boolean isFirst = true ;
		for(String s : subKeyNodes){
			if(isFirst == true){
				reduction.append(s) ;
				isFirst = false ;
			}else {
				reduction.append("|" + s) ;
			}
		}
		reduction.append("]" + option) ;
		
		if(reduction.toString().equals(keyNode) == false){						
			String errMsg =String.format("“%s”格式错误 /n" , keyNode);
            throw new DataConfException(confFileName, lineNo, errMsg);
		}
		
		multiRegex = makeNodeRegex(subKeyNodes.get(0), qtype, ptnInfo);
		for(int keyNodeIndex = 1; keyNodeIndex < subKeyNodes.size(); keyNodeIndex++){
			multiRegex += "|" + "(?:" + makeNodeRegex(subKeyNodes.get(keyNodeIndex), qtype, ptnInfo) + ")" ;
		}
		multiRegex = "(?:" + multiRegex + ")";
		return makeNodeRegexOption(multiRegex, option, ptnInfo);
	}
	
	private static String makeNodeRegex(String keyNode, Type qtype,
			PatternInfo ptnInfo) throws DataConfException, UnexpectedException {
		String regex  = "";
		if(keyNode.startsWith("index")){
        	regex = makeIndexNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("prop")){
        	regex = makePropNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("num")){
        	regex = makeNumNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("date")){
        	regex = makeDateNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("sort")){
        	regex = makeSortNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("logic")){
        	regex = makeLogicNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("qword")){
        	regex = makeQwordNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("oper")){
        	regex = makeOperNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("str")){
        	regex = makeStrNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("avg")){
            regex = makeAvgNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("techop")){
        	regex = makeTechopNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("whatever")){
        	regex = makeWhateverNodeRegex(keyNode, qtype, ptnInfo);
        }else if(keyNode.startsWith("techperiod")){
        	regex = makeTechperiodNodeRegex(keyNode, qtype, ptnInfo);
        }else {
        	 String errMsg = String.format( "“%s”multiNode书写格式错误", keyNode);
             throw new DataConfException(confFileName, lineNo, errMsg);
        }
		return regex;
	}
	
	public static String confFileName(){
		return confFileName ;
	}
	
    private static Pattern PTN_KEY_MULTI = 
			Pattern.compile("^\\[.*\\][?!]?$") ;
    private static Pattern PTN_KEY_INDEX = 
        Pattern.compile("^index(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_USER_INDEX = 
            Pattern.compile("^user_index(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_PROP =
            Pattern.compile("^prop(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_NUM = 
    		Pattern.compile("^num(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_DATE = 
            Pattern.compile("^date(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_SORT =
            Pattern.compile("^sort(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_LOGIC =
            Pattern.compile("^logic(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_QWORD =
            Pattern.compile("^qword(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_OPER =
            Pattern.compile("^oper(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_STR =
            Pattern.compile("^str(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_AVG =
            Pattern.compile("^avg(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_TECHOP =
    		Pattern.compile("^techop(\\(.*\\)|)([?|!]?)$");
    private static Pattern PTN_KEY_WHATEVER = 
    		Pattern.compile("^whatever\\((.+)\\)([?|!]?)$");
    private static Pattern PTN_KEY_TECHPERIOD = 
    		Pattern.compile("^techperiod\\((.*)\\)([?|!]?)$") ;
    private static Pattern PTN_KEY_CHANGE = 
    		Pattern.compile("^change\\((.*)\\)([?|!]?)$") ;
	private static Pattern PTN_KEY_MULTI_NODE = 
			Pattern.compile("([a-zA-Z]+(?:\\([^\\|\\(]*(?:\\([^\\(\\)\\|]+\\))?[^\\(|\\|]*(?:\\|[^\\|\\(]+(?:\\([^\\(\\)|\\|]+\\))?[^\\|\\(]*)*\\)|))") ;
	
	private static Pattern PTN_REP_INDEX =
			Pattern.compile("^index\\((.+)\\)$");
	private static Pattern PTN_REP_USER_INDEX =
			Pattern.compile("^user_index\\((.+)\\)$");
    private static Pattern PTN_REP_PROP =
            Pattern.compile("^prop\\((.+)\\)$");
    private static Pattern PTN_REP_NUM = 
            Pattern.compile("^num\\((.+)\\)$");
    private static Pattern PTN_REP_DATE =
            Pattern.compile("^date\\((.+)\\)$");
    private static Pattern PTN_REP_OPER =
            Pattern.compile("^oper\\(([^,]+)\\)$");
    private static Pattern PTN_REP_OPER2 =
            Pattern.compile("^oper\\(([^,]+),(.+)\\)$");
    private static Pattern PTN_REP_LOGIC =
            Pattern.compile("^logic\\((.+)\\)$");
    private static Pattern PTN_REP_STRVAL =
            Pattern.compile("^strval\\((包含|不包含|\\+|\\-),([^,]+),(.+)\\)$");
    private static Pattern PTN_REP_SORT =
            Pattern.compile("^sort\\((incr|decr|升序|降序),(.+)\\)$");
    private static Pattern PTN_REP_CHNG_NUM =
            Pattern.compile("^change_num\\((\\d+),([^,]+),(.*)\\)$");
    private static Pattern PTN_REP_COPY =
            Pattern.compile("^group\\((\\d+)\\)$");
    private static Pattern PTN_REP_TECHOP =
    		Pattern.compile("^techop\\(([^,]+),(\\d+)\\)$");
    private static Pattern PTN_REP_FAKENUM = 
    		Pattern.compile("^fakenum\\((.+)\\)$");
    private static Pattern PTN_REP_CHANGE_DEF = 
    		Pattern.compile("^change_def\\(defclass\\((.+)\\),text\\(group\\((.+)\\)\\),change\\(group\\((\\d+)\\)\\)\\)$");

    private static Pattern PTN_NODE_PARAM = Pattern.compile("\\((.*)\\)");
    private static Pattern PTN_NODE_OPTION = Pattern.compile("^(.*)([!|?])$");

    private static final String STR_ONE_NODE = "[^\1]+";
    private static final String STR_ONE_NODE_REST = "[^\1]*";
    private static final String STR_ITEM_REGEX = "[^\2\1]*";
    public static final String STR_NODE_SEP = "\1";
    public static final String STR_ITEM_SEP = "\2";
    private static String STR_KEY_LABEL = "label";
    private static String STR_KEY_START = "start";
    private static String STR_KEY_END = "end";
}
