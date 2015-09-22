package com.myhexin.qparser.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef;
import com.myhexin.qparser.define.EnumDef.ModifyNumType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.time.tool.TimeUtil;
import com.myhexin.qparser.util.Util;

/*
 * 特别说明, 本文件只是添加时间支持事移动过来, 以后要删除
 */

public class ModifyDefInfo {

    
    private static void load(Document doc, Type type)  {
        HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp = new HashMap<String, ArrayList<ModifyInfo>>();
        Element root = doc.getDocumentElement();
        NodeList infoNodes = root.getChildNodes();
        ArrayList<String> warningMsg = new ArrayList<String>();
        if (infoNodes.getLength() == 0) {
            return;
        }
        for (int i = 0; i < infoNodes.getLength(); i++) {
            Node modifyNodeI = infoNodes.item(i);
            if (modifyNodeI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            try {
                if (modifyNodeI.getNodeName().equals("modifier_def")) {
                    parseModifierDefInfo(modifyInfoTmp, modifyNodeI, type);
                } else if (modifyNodeI.getNodeName().equals("index_modify_def")) {
                    parseIndexModifyDefInfo(modifyInfoTmp, modifyNodeI, type);
                } else {
                    warningMsg.add(String.format("expect modify node while got %s",modifyNodeI.getNodeName()));
                }
            } catch (QPException e) {
                
            }
        }
        setData(modifyInfoTmp, type);
        if (warningMsg.size() > 0) {
            
        }
    }

    private static void parseIndexModifyDefInfo(
            HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp,
            Node modifyNodeI, Type type) throws UnexpectedException,
            NotSupportedException {
        NamedNodeMap namedNodeMap = modifyNodeI.getAttributes();
        String label = namedNodeMap.getNamedItem("label") == null ? ""
                : namedNodeMap.getNamedItem("label").getNodeValue();
        if (label.isEmpty()) {
            throw new UnexpectedException("指标名称为空");
        }
        /*try {
            MemOnto.getOnto(label, ClassOnto.class, type);
        } catch (QPException e) {
            throw new UnexpectedException("指标未定义：%s", label);
        }*/
        parseModifyDefInfo(modifyInfoTmp, label, modifyNodeI.getChildNodes(),
                type);
    }

    private static void parseModifierDefInfo(
            HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp,
            Node modifyNodeI, Type type) throws UnexpectedException,
            NotSupportedException {
        String indexName = def_index_name;
        parseModifyDefInfo(modifyInfoTmp, indexName, modifyNodeI.getChildNodes(), type);
    }

    private static void parseModifyDefInfo(
            HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp,
            String indexName, NodeList childNodes, Type type)
            throws UnexpectedException, NotSupportedException {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node modifyNodeI = childNodes.item(i);
            if (modifyNodeI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (modifyNodeI.getNodeName().equals("vague_num")) {
                parseNumModifyDefInfo(modifyInfoTmp, indexName,
                        modifyNodeI.getChildNodes(), type);
            } else if (modifyNodeI.getNodeName().equals("vague_date")) {
                parseDateModifyDefInfo(modifyInfoTmp, indexName,
                        modifyNodeI.getChildNodes(), type);
            } else if (modifyNodeI.getNodeName().equals("vague_time")) {
                parseTimeModifyDefInfo(modifyInfoTmp, indexName,
                        modifyNodeI.getChildNodes(), type);
            }else {
                throw new UnexpectedException("节点名称未识别：%s",
                        modifyNodeI.getNodeName());
            }
        }
    }

    private static void parseTimeModifyDefInfo(
            HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp,
            String indexName, NodeList childNodes, Type type) throws UnexpectedException {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node timeModifyNodeI = childNodes.item(i);
            if (timeModifyNodeI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (!timeModifyNodeI.getNodeName().equals("vague")) {
                throw new UnexpectedException("节点名称未识别：%s",
                        timeModifyNodeI.getNodeName());
            }
            NamedNodeMap namedNodeMap = timeModifyNodeI.getAttributes();
            String text = namedNodeMap.getNamedItem("text") == null ? ""
                    : namedNodeMap.getNamedItem("text").getNodeValue();
            String reportTypeStr = namedNodeMap.getNamedItem("report_type") == null ? ""
                    : namedNodeMap.getNamedItem("report_type").getNodeValue();
            String valueStr = namedNodeMap.getNamedItem("value") == null ? ""
                    : namedNodeMap.getNamedItem("value").getNodeValue();
            if (text.isEmpty()) {
                throw new UnexpectedException("指标“%s”的一个时间(Time)修饰词为空", indexName);
            } else if (valueStr.isEmpty()) {
                throw new UnexpectedException("指标“%s”的时间(Time)修饰词“%s”没有对应值",
                        indexName, text);
            } else if (indexName == def_index_name && reportTypeStr.isEmpty()) {
                throw new UnexpectedException("时间(Time)修饰词“%s”的默认修饰报告期类型没有对应值", text);
            }
            String[] modifyWords = text.split("\\|");
            ArrayList<String> warning = new ArrayList<String>();
            for (int k = 0; k < modifyWords.length; k++) {
                String curModifyWord = modifyWords[k];
                TimeModifyInfo addTimeModifyInfo = new ModifyDefInfo().new TimeModifyInfo(
                        curModifyWord, indexName,type);
                try {
                    addTimeModifyValueToModifyInfo(addTimeModifyInfo, type,
                            reportTypeStr, valueStr);
                } catch (NotSupportedException e) {
                    
                }
                addModifyInfo(modifyInfoTmp, addTimeModifyInfo);
            }
        }
    }

    private static void addTimeModifyValueToModifyInfo(
            TimeModifyInfo addTimeModifyInfo, Type type, String reportTypeStr,
            String valueStr) throws UnexpectedException, NotSupportedException {/*
        ReportType reportType = parseReportType(reportTypeStr);
        ClassOnto index = addTimeModifyInfo.getIndexName() == def_index_name ? null
                : MemOnto.getOnto(addTimeModifyInfo.getIndexName(),
                        ClassOnto.class, type);
        boolean notMatch = index != null && index.getReportType() != null;
        notMatch = notMatch && reportType != null
                && index.getReportType() != reportType;
        if (notMatch) {
            throw new UnexpectedException(
                    "指标“%s”的报告周期与其时间修饰词“%s”在文件中指定的报告期类型不一致",
                    addTimeModifyInfo.getIndexName(),
                    addTimeModifyInfo.getLabel());
        }
        reportType = reportType == null && index != null ? index
                .getReportType() : reportType;
        addTimeModifyInfo.setReportType(reportType);
        // 先尝试解析，保证配置文件里的时间可被解析;但不可把当下解析的时间作为替换值。
        // 每次提取替换值都要重新解析
        String[] timeStrs = valueStr.split("\\|");
        List<TimeNode> addTimeVals = new ArrayList<TimeNode>();
        ArrayList<String> notSupportedTimes = new ArrayList<String>();
        for (int i = 0; i < timeStrs.length; i++) {
            String curTimeStr = timeStrs[i];
            try {
                TimeNode addTimeValue = TimeUtil.makeTimeNodeFromStr(
                        curTimeStr, type);
                addTimeValue = new TimeNode(curTimeStr);
                addTimeVals.add(addTimeValue);
            } catch (NotSupportedException e) {
                notSupportedTimes.add(curTimeStr);
                continue;
            }
        }
        addTimeModifyInfo.setModifyTimeValues(addTimeVals);
        if (!notSupportedTimes.isEmpty()) {
            throw new NotSupportedException("[%s]的修饰定义：[%s]暂不可解析，请尝试其他定义",
                    addTimeModifyInfo.getLabel(), notSupportedTimes.toString());
        }
    */}

    private static void parseDateModifyDefInfo(
            HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp,
            String indexName, NodeList childNodes, Type type)
            throws UnexpectedException, NotSupportedException {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node dateModifyNodeI = childNodes.item(i);
            if (dateModifyNodeI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (!dateModifyNodeI.getNodeName().equals("vague")) {
                throw new UnexpectedException("节点名称未识别：%s",
                        dateModifyNodeI.getNodeName());
            }
            NamedNodeMap namedNodeMap = dateModifyNodeI.getAttributes();
            String text = namedNodeMap.getNamedItem("text") == null ? ""
                    : namedNodeMap.getNamedItem("text").getNodeValue();
            String reportTypeStr = namedNodeMap.getNamedItem("report_type") == null ? ""
                    : namedNodeMap.getNamedItem("report_type").getNodeValue();
            String valueStr = namedNodeMap.getNamedItem("value") == null ? ""
                    : namedNodeMap.getNamedItem("value").getNodeValue();
            if (text.isEmpty()) {
                throw new UnexpectedException("指标“%s”的一个时间修饰词为空", indexName);
            } else if (valueStr.isEmpty()) {
                throw new UnexpectedException("指标“%s”的时间修饰词“%s”没有对应值",
                        indexName, text);
            } else if (indexName == def_index_name && reportTypeStr.isEmpty()) {
                throw new UnexpectedException("时间修饰词“%s”的默认修饰报告期类型没有对应值", text);
            }
            String[] modifyWords = text.split("\\|");
            ArrayList<String> warning = new ArrayList<String>();
            for (int k = 0; k < modifyWords.length; k++) {
                String curModifyWord = modifyWords[k];
                DateModifyInfo addDateModifyInfo = new ModifyDefInfo().new DateModifyInfo(
                        curModifyWord, indexName,type);
                try {
                    addDateModifyValueToModifyInfo(addDateModifyInfo, type,
                            reportTypeStr, valueStr);
                } catch (NotSupportedException e) {
                    
                }
                addModifyInfo(modifyInfoTmp, addDateModifyInfo);
            }
        }
    }

    private static void addDateModifyValueToModifyInfo(
            DateModifyInfo addDateModifyInfo, Type type, String reportTypeStr,
            String valueStr) throws UnexpectedException, NotSupportedException {/*
        ReportType reportType = parseReportType(reportTypeStr);
        ClassOnto index = addDateModifyInfo.getIndexName() == def_index_name ? null
                : MemOnto.getOnto(addDateModifyInfo.getIndexName(),
                        ClassOnto.class, type);
        boolean notMatch = index != null && index.getReportType() != null;
        notMatch = notMatch && reportType != null
                && index.getReportType() != reportType;
        if (notMatch) {
            throw new UnexpectedException(
                    "指标“%s”的报告周期与其时间修饰词“%s”在文件中指定的报告期类型不一致",
                    addDateModifyInfo.getIndexName(),
                    addDateModifyInfo.getLabel());
        }
        reportType = reportType == null && index != null ? index
                .getReportType() : reportType;
        addDateModifyInfo.setReportType(reportType);
        // 先尝试解析，保证配置文件里的时间可被解析;但不可把当下解析的时间作为替换值。
        // 每次提取替换值都要重新解析
        String[] dateStrs = valueStr.split("\\|");
        List<DateNode> addDateVals = new ArrayList<DateNode>();
        ArrayList<String> notSupportedDates = new ArrayList<String>();
        for (int i = 0; i < dateStrs.length; i++) {
            String curDateStr = dateStrs[i];
            try {
                DateNode addDateValue = DateUtil
                        .makeDateNodeFromStr(curDateStr);
                addDateValue = new DateNode(curDateStr);
                addDateVals.add(addDateValue);
            } catch (NotSupportedException e) {
                notSupportedDates.add(curDateStr);
                continue;
            }
        }
        addDateModifyInfo.setModifyDateValues(addDateVals);
        if (!notSupportedDates.isEmpty()) {
            throw new NotSupportedException("[%s]的修饰定义：[%s]暂不可解析，请尝试其他定义",
                    addDateModifyInfo.getLabel(), notSupportedDates.toString());
        }
    */}

    private static ReportType parseReportType(String reportTypeStr)
            throws UnexpectedException {
        if (reportTypeStr == null | reportTypeStr.isEmpty()) {
            return null;
        } else if (reportTypeStr.matches("报告期")) {
            return ReportType.QUARTER;
        } else if (reportTypeStr.matches("交易日期")) {
            return ReportType.TRADE_DAILY;
        } else if (reportTypeStr.matches("自然日")) {
            return ReportType.NATURAL_DAILY;
        } else if (reportTypeStr.matches("年报")) {
            return ReportType.YEAR;
        } else if (reportTypeStr.matches("半年报")) {
            return ReportType.HALF_YEAR;
        } else if (reportTypeStr.matches("预测_交易日期")) {
            return ReportType.FUTURE_DAILY;
        } else if (reportTypeStr.matches("预测_报告期")) {
            return ReportType.FUTURE_QUARTER;
        } else if (reportTypeStr.matches("预测_年报")) {
            return ReportType.FUTURE_YEAR;
        } else if (reportTypeStr.matches("分时")) {
            return ReportType.TIME;
        } else {
            throw new UnexpectedException("Unexpected report type:%s",
                    reportTypeStr);
        }
    }

    private static void parseNumModifyDefInfo(
            HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp,
            String indexName, NodeList childNodes, Type type)
            throws UnexpectedException, NotSupportedException {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node numModifyNodeI = childNodes.item(i);
            if (numModifyNodeI.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            } else if (!numModifyNodeI.getNodeName().equals("vague")) {
                throw new UnexpectedException("节点名称未识别：%s",
                        numModifyNodeI.getNodeName());
            }
            NamedNodeMap namedNodeMap = numModifyNodeI.getAttributes();
            String text = namedNodeMap.getNamedItem("text") == null ? ""
                    : namedNodeMap.getNamedItem("text").getNodeValue();
            String plusStr = namedNodeMap.getNamedItem("plus_value") == null ? ""
                    : namedNodeMap.getNamedItem("plus_value").getNodeValue();
            String minusStr = namedNodeMap.getNamedItem("minus_value") == null ? ""
                    : namedNodeMap.getNamedItem("minus_value").getNodeValue();
            if (text.isEmpty()) {
                throw new UnexpectedException("指标“%s”的一个数值修饰词为空", indexName);
            }
            String[] modifyWords = text.split("\\|");
            for (int k = 0; k < modifyWords.length; k++) {
                String curModifyWord = modifyWords[k];
                NumModifyInfo addNumModifyInfo = new ModifyDefInfo().new NumModifyInfo(
                        curModifyWord, indexName,type);
                addNumModifyValueToModifyInfo(addNumModifyInfo, plusStr,
                        minusStr);
                addModifyInfo(modifyInfoTmp, addNumModifyInfo);
            }
        }
    }

    private static void addNumModifyValueToModifyInfo(
            NumModifyInfo addNumModifyInfo, String plusStr, String minusStr)
            throws UnexpectedException, NotSupportedException {
        if (plusStr.isEmpty() && minusStr.isEmpty()) {
            throw new UnexpectedException("指标“%s”的数值修饰词“%s”未定义",
                    addNumModifyInfo.getIndexName(),
                    addNumModifyInfo.getLabel());
        }
        List<SemanticNode> plusValues = new ArrayList<SemanticNode>();
        List<SemanticNode> minusValues = new ArrayList<SemanticNode>();
        String[] plusStrs = plusStr == null || plusStr.isEmpty() ? null
                : plusStr.split("\\|");
        String[] minusStrs = minusStr == null || minusStr.isEmpty() ? null
                : minusStr.split("\\|");
        try{
            plusValues = getNumAndSortListFrom(plusStrs);
            minusValues = getNumAndSortListFrom(minusStrs);
        }catch(QPException e){
            ;//No Op
        }
        if (plusValues.isEmpty()) {
            plusValues = getReversalListFrom(minusValues);
        }
        if (minusValues.isEmpty()) {
            minusValues = getReversalListFrom(plusValues);
        }
        addNumModifyInfo.setMinusModifyValues(minusValues);
        addNumModifyInfo.setPlusModifyValues(plusValues);
    }

    private static List<SemanticNode> getReversalListFrom(
            List<SemanticNode> values) throws UnexpectedException {
        if (values==null||values.isEmpty()) {
            return Collections.emptyList();
        }
        List<SemanticNode> rtn = new ArrayList<SemanticNode>();
        for (int i = 0; i < values.size(); i++) {
            SemanticNode addVal = reversalSortOrNum(values.get(i));
            rtn.add(addVal);
        }
        return rtn;
    }

    private static List<SemanticNode> getNumAndSortListFrom(String[] valStrs)
            throws UnexpectedException, NotSupportedException {
        List<SemanticNode> rtn = new ArrayList<SemanticNode>();
        if(valStrs==null||valStrs.length==0){
            throw new UnexpectedException("ValStrs is EMPTY");
        }
        for (int i = 0; i < valStrs.length; i++) {
            /*SemanticNode addVal = NumUtil.parseStrAsSortOrNum(valStrs[i]);
            rtn.add(addVal);*/
        }
        return rtn;
    }

    private static SemanticNode reversalSortOrNum(SemanticNode value)
            throws UnexpectedException {
        SemanticNode reversalValue =  NodeUtil.copyNode(value); //.copy();
        if (reversalValue.type == NodeType.SORT) {
            ((SortNode) reversalValue)
                    .setDescending_(!((SortNode) reversalValue).isDescending_());
        } else if (reversalValue.type == NodeType.NUM) {
            NumUtil.changeNRByOp((NumNode) reversalValue,
                    OperatorType.MULTIPLY, -1);
        } else {
            throw new UnexpectedException("Unexpected node type:%s", value.type);
        }
        return reversalValue;
    }

    private static void addModifyInfo(
            HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp,
            ModifyInfo addModifyInfo) throws UnexpectedException {
        checkReiteration(modifyInfoTmp, addModifyInfo);
        if (modifyInfoTmp.get(addModifyInfo.getLabel()) == null) {
            modifyInfoTmp.put(addModifyInfo.getLabel(),
                    new ArrayList<ModifyInfo>());
        }
        modifyInfoTmp.get(addModifyInfo.getLabel()).add(addModifyInfo);
    }

    private static void checkReiteration(
            HashMap<String, ArrayList<ModifyInfo>> modifyInfoTmp,
            ModifyInfo addModifyInfo) throws UnexpectedException {
        assert (modifyInfoTmp != null && addModifyInfo != null);
        ArrayList<ModifyInfo> all = modifyInfoTmp.get(addModifyInfo.getLabel());
        if (all == null || all.isEmpty()) {
            return;
        }
        ArrayList<ModifyInfo> modifyInfoOld = getModifyInfoByIndexName(all,
                addModifyInfo.getIndexName());
        boolean hasSameIndexInfo = modifyInfoOld != null
                && !modifyInfoOld.isEmpty();
        if (addModifyInfo.isNumModifyInfo() && hasSameIndexInfo) {
            throw new UnexpectedException("指标“%s”的数值修饰词“%s”定义有重复",
                    addModifyInfo.getIndexName(), addModifyInfo.getLabel());
        } else if (addModifyInfo.isDateModifyInfo() && hasSameIndexInfo) {
            if (!addModifyInfo.getIndexName().equals(def_index_name)) {
                throw new UnexpectedException("指标“%s”的是时间修饰词“%s”定义有重复",
                        addModifyInfo.getIndexName(), addModifyInfo.getLabel());
            }
            ArrayList<ModifyInfo> defModifyInfo = getModifyInfoByIndexName(all,
                    addModifyInfo.getIndexName());
            if (getDateModifyInfoByReportType(defModifyInfo,
                    ((DateModifyInfo) addModifyInfo).getReportType()) != null) {
                throw new UnexpectedException("指标“%s”的是时间修饰词“%s”定义在报告期“”下有重复",
                        addModifyInfo.getIndexName(), addModifyInfo.getLabel(),
                        ((DateModifyInfo) addModifyInfo).getReportType());
            }
        }
    }

    public static final String def_index_name = "DEFAULT";
    
    private static Map<Query.Type, HashMap<String, ArrayList<ModifyInfo>>> domainToModifyInfos = new HashMap<Query.Type, HashMap<String, ArrayList<ModifyInfo>>>();
    
    private static void setData(
            HashMap<String, ArrayList<ModifyInfo>> changeInfos, 
            Type type) {
        domainToModifyInfos.put(type, changeInfos);
    }

    private static HashMap<String, ArrayList<ModifyInfo>> getData(Type type){
        return domainToModifyInfos.get(type);
    }
    
    private static ArrayList<ModifyInfo> getModifyInfoByModifyLabel(
            String modifyStr, Type type) {
        HashMap<String, ArrayList<ModifyInfo>> infoSrc = getData(type);
        ArrayList<ModifyInfo> all = infoSrc == null ? null : infoSrc.get(modifyStr);
        return all;
    }

    public static boolean isModify(String modifyStr, Type type) {
        ArrayList<ModifyInfo> all = getModifyInfoByModifyLabel(modifyStr, type);
        return all != null && !all.isEmpty();
    }

    public static boolean isDateModify(String modifyStr, Type type) {
        if (!isModify(modifyStr, type)) {
            return false;
        }
        ArrayList<ModifyInfo> all = getModifyInfoByModifyLabel(modifyStr, type);
        return all.get(0).isDateModifyInfo();
    }
    
    public static boolean isTimeModify(String modifyStr, Type type) {
        if (!isModify(modifyStr, type)) {
            return false;
        }
        ArrayList<ModifyInfo> all = getModifyInfoByModifyLabel(modifyStr, type);
        return all.get(0).isTimeModifyInfo();
    }

    public static boolean isNumModify(String modifyStr, Type type) {
        if (!isModify(modifyStr, type)) {
            return false;
        }
        ArrayList<ModifyInfo> all = getModifyInfoByModifyLabel(modifyStr, type);
        return all.get(0).isNumModifyInfo();
    }

    private static ArrayList<ModifyInfo> getModifyInfo(String modifyStr,
            String indexName, Type type) {
        ArrayList<ModifyInfo> modifyInfos = new ArrayList<ModifyInfo>();
        ArrayList<ModifyInfo> all = getModifyInfoByModifyLabel(modifyStr, type);
        modifyInfos = all == null || all.isEmpty() ? modifyInfos
                : getModifyInfoByIndexName(all, indexName);
        return modifyInfos;
    }

    private static ArrayList<ModifyInfo> getModifyInfoByIndexName(
            ArrayList<ModifyInfo> modifyInfos, String indexName) {
        ArrayList<ModifyInfo> modifyInfoRtn = new ArrayList<ModifyInfo>();
        for (int i = 0; i < modifyInfos.size(); i++) {
            if (modifyInfos.get(i).getIndexName().equals(indexName)) {
                modifyInfoRtn.add(modifyInfos.get(i));
            }
        }
        return modifyInfoRtn;
    }

    public static NumModifyInfo getModifyNumInfo(String modifyStr,
            String indexName, Type type) throws UnexpectedException {
        ArrayList<ModifyInfo> modifyInfos = getModifyInfo(modifyStr, indexName,
                type);
        if (modifyInfos.isEmpty()) {
            return null;
        } else if (modifyInfos.size() > 1) {
            // TODO:可加交互
        }
        ModifyInfo modifyInfo = modifyInfos.get(0);
        if (!modifyInfo.isNumModifyInfo()) {
            throw new UnexpectedException("字符串定义非数字修饰：%s", modifyStr);
        }
        return (NumModifyInfo) modifyInfo;
    }

    public static DateModifyInfo getModifyDateInfo(String modifyStr,
            String indexName, ReportType reportType, Type type)
            throws UnexpectedException {
        ArrayList<ModifyInfo> modifyInfos = getModifyInfo(modifyStr, indexName,
                type);
        if (modifyInfos.isEmpty()) {
            return null;
        }
        checkDateModifyInfo(modifyInfos);
        if (modifyInfos.size() == 1) {
            return (DateModifyInfo) modifyInfos.get(0);
        }
        DateModifyInfo modifyInfo = getDateModifyInfoByReportType(modifyInfos,
                reportType);
        DateModifyInfo cloneRtn = modifyInfo == null?null:modifyInfo.clone();
        return cloneRtn;
    }
    
    public static TimeModifyInfo getModifyTimeInfo(String modifyStr,
            String indexName, ReportType reportType, Type type)
            throws UnexpectedException {
        ArrayList<ModifyInfo> modifyInfos = getModifyInfo(modifyStr, indexName,
                type);
        TimeModifyInfo modifyInfo = null;
        if (modifyInfos.isEmpty()) {
            // 当指标无法找到对应修饰信息，根据给出的报告期寻找默认修饰信息
            /*ClassOnto classOnto = MemOnto.getOnto(indexName, ClassOnto.class,
                    type);
            modifyInfo = ModifyDefInfo.getModifyTimeInfo(modifyStr,
                    ModifyDefInfo.def_index_name, classOnto.getReportType(),
                    type);*/
        } else {
            checkTimeModifyInfo(modifyInfos);
            if (modifyInfos.size() == 1) {
                modifyInfo = (TimeModifyInfo) modifyInfos.get(0);
            } else {
                modifyInfo = getTimeModifyInfoByReportType(modifyInfos,
                        reportType);
            }
        }
        return modifyInfo == null ? null : modifyInfo.clone();
    }

    private static TimeModifyInfo getTimeModifyInfoByReportType(
            ArrayList<ModifyInfo> modifyInfos, ReportType reportType) {
        for (int i = 0; i < modifyInfos.size(); i++) {
            ModifyInfo curModifyInfo = modifyInfos.get(i);
            if (curModifyInfo.isTimeModifyInfo()
                    && ((TimeModifyInfo) curModifyInfo).getReportType() == reportType) {
                return (TimeModifyInfo) curModifyInfo;
            }
        }
        return null;
    }

    private static DateModifyInfo getDateModifyInfoByReportType(
            ArrayList<ModifyInfo> dateModifyInfos, ReportType reportType) {
        for (int i = 0; i < dateModifyInfos.size(); i++) {
            ModifyInfo curModifyInfo = dateModifyInfos.get(i);
            if (curModifyInfo.isDateModifyInfo()
                    && ((DateModifyInfo) curModifyInfo).getReportType() == reportType) {
                return (DateModifyInfo) curModifyInfo;
            }
        }
        return null;
    }
    
    public static List<DateModifyInfo> getDateModifyInfosByReportType(
            String modifyStr, ReportType reportType, Type type) {
        ArrayList<ModifyInfo> dateModifyInfos = getModifyInfoByModifyLabel(
                modifyStr, type);
        List<DateModifyInfo> rtn = new ArrayList<DateModifyInfo>();
        for (int i = 0; i < dateModifyInfos.size(); i++) {
            ModifyInfo curModifyInfo = dateModifyInfos.get(i);
            if (curModifyInfo.isDateModifyInfo()
                    && ((DateModifyInfo) curModifyInfo).getReportType() == reportType) {
                rtn.add(((DateModifyInfo) curModifyInfo).clone());
            }
        }
        return rtn;
    }
    
    public static List<TimeModifyInfo> getTimeModifyInfosByReportType(
            String modifyStr, ReportType reportType, Type type) {
        ArrayList<ModifyInfo> timeModifyInfos = getModifyInfoByModifyLabel(
                modifyStr, type);
        List<TimeModifyInfo> rtn = new ArrayList<TimeModifyInfo>();
        for (int i = 0; i < timeModifyInfos.size(); i++) {
            ModifyInfo curModifyInfo = timeModifyInfos.get(i);
            if (curModifyInfo.isTimeModifyInfo()
                    && ((TimeModifyInfo) curModifyInfo).getReportType() == reportType) {
                rtn.add(((TimeModifyInfo) curModifyInfo).clone());
            }
        }
        return rtn;
    }

    private static void checkDateModifyInfo(ArrayList<ModifyInfo> modifyInfos)
            throws UnexpectedException {
        for (int i = 0; i < modifyInfos.size(); i++) {
            ModifyInfo modifyInfo = modifyInfos.get(i);
            if (!modifyInfo.isDateModifyInfo()) {
                throw new UnexpectedException("字符串定义有非时间修饰：%s", modifyInfos
                        .get(i).getLabel());
            }
        }
    }
    
    private static void checkTimeModifyInfo(ArrayList<ModifyInfo> modifyInfos)
            throws UnexpectedException {
        for (int i = 0; i < modifyInfos.size(); i++) {
            ModifyInfo modifyInfo = modifyInfos.get(i);
            if (!modifyInfo.isTimeModifyInfo()) {
                throw new UnexpectedException("字符串定义有非时间(Time)修饰：%s",
                        modifyInfos.get(i).getLabel());
            }
        }
}

    public class ModifyInfo implements Cloneable{
        
        public ModifyInfo(String label, String indexName,Type type) {
            this.label = label;
            this.type = type;
            this.indexName = indexName == null || indexName.isEmpty() ? this.indexName
                    : indexName;
        }

        public boolean isTimeModifyInfo() {
            return this instanceof TimeModifyInfo;
        }

        public boolean isNumModifyInfo() {
            return this instanceof NumModifyInfo;
        }

        public boolean isDateModifyInfo() {
            return this instanceof DateModifyInfo;
        }

        public String getLabel() {
            return label;
        }

        public String getIndexName() {
            return indexName;
        }

        public ModifyInfo clone() {
            try {
                ModifyInfo rtn = (ModifyInfo) super.clone();
                return rtn;
            } catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
        }
        
        private String label;
        protected Type type;
        private String indexName;
    }

    public class NumModifyInfo extends ModifyInfo {
        public NumModifyInfo(String label, String indexName,Type type) {
            super(label, indexName,type);
        }

        public List<SemanticNode> getModifyValuesByModifyNumType(
                ModifyNumType type) {
            List<SemanticNode> cloneFrom = type == ModifyNumType.PLUS ? plusModifyValues
                    : minusModifyValues;
            List<SemanticNode> rtn = new ArrayList<SemanticNode>();
            for (int i = 0; i < cloneFrom.size(); i++) {
                rtn.add(  NodeUtil.copyNode(cloneFrom.get(i)));//.copy());
            }
            return rtn;
        }
        
        public SemanticNode getModifyValueByModifyNumType(ModifyNumType type) {
            return getModifyValuesByModifyNumType(type).get(0);
        }

        public void setPlusModifyValues(List<SemanticNode> plusValues)
                throws UnexpectedException {
            if (plusValues == null || plusValues.isEmpty()) {
                throw new UnexpectedException(
                        "[%s]的修饰信息[%s]:plusValues is EMPTY",
                        this.getIndexName(), this.getLabel());
            }
            for (SemanticNode plusValue : plusValues) {
                if (plusValue.type != NodeType.NUM
                        && plusValue.type != NodeType.SORT) {
                    throw new UnexpectedException(
                            "[%s]的修饰信息[%s]:plusValue [%s] type is :[%s]",
                            this.getIndexName(), this.getLabel(),
                            plusValue.getText(), plusValue.type);
                }
            }
            this.plusModifyValues = plusValues;
        }

        public void setMinusModifyValues(List<SemanticNode> minusValues)
                throws UnexpectedException {
            if (minusValues == null || minusValues.isEmpty()) {
                throw new UnexpectedException(
                        "[%s]的修饰信息[%s]:minusValues is EMPTY",
                        this.getIndexName(), this.getLabel());
            }
            for (SemanticNode minusValue : minusValues) {
                if (minusValue.type != NodeType.NUM
                        && minusValue.type != NodeType.SORT) {
                    throw new UnexpectedException(
                            "[%s]的修饰信息[%s]:minusValue [%s] type is :[%s]",
                            this.getIndexName(), this.getLabel(),
                            minusValue.getText(), minusValue.type);
                }
            }
            this.minusModifyValues = minusValues;
        }

        public NumModifyInfo clone() {
            NumModifyInfo rtn = (NumModifyInfo) super.clone();
            return rtn;
        }
        
        private List<SemanticNode> plusModifyValues;
        private List<SemanticNode> minusModifyValues;
    }

    public class DateModifyInfo extends ModifyInfo {
        public DateModifyInfo(String label, String indexName,Type type) {
            super(label, indexName,type);
        }

        public void setReportType(ReportType reportType) {
            this.reportType = reportType;
        }

        public ReportType getReportType() {
            return reportType;
        }

        public void setModifyDateValues(List<DateNode> modifyValues)
                throws UnexpectedException {
            if (modifyValues==null||modifyValues.isEmpty()) {
                throw new UnexpectedException("[%s]的修饰信息[%s]:ModifyValues is EMPTY",this.getIndexName(),this.getLabel());
            }
            this.modifyValues = modifyValues;
        }

        public DateNode getModifyDateValue(DateInfoNode curDate) throws NotSupportedException,
                UnexpectedException {
            DateNode rtn = getModifyDateValues(curDate).get(0);
            return rtn;
        }
        
        public List<DateNode> getModifyDateValues(DateInfoNode curDate)
                throws NotSupportedException, UnexpectedException {
            List<DateNode> rtn = new ArrayList<DateNode>();
            for (int i = 0; i < modifyValues.size(); i++) {
                DateNode addDate = DateUtil.makeDateNodeFromStr(modifyValues.get(i).getText(), null);
                rtn.add(addDate);
            }
            return rtn;
        }

        public DateModifyInfo clone() {
            DateModifyInfo rtn = (DateModifyInfo) super.clone();
            return rtn;
        }
        private ReportType reportType = null;
        private List<DateNode> modifyValues = null;

    }
    
    public class TimeModifyInfo extends ModifyInfo {
        public TimeModifyInfo(String label, String indexName,Type type) {
            super(label, indexName,type);
        }

        public void setReportType(ReportType reportType) {
            this.reportType = reportType;
        }

        public ReportType getReportType() {
            return reportType;
        }

        public void setModifyTimeValues(List<TimeNode> modifyValues)
                throws UnexpectedException {
            if (modifyValues==null||modifyValues.isEmpty()) {
                throw new UnexpectedException("[%s]的修饰信息[%s]:ModifyValues is EMPTY",this.getIndexName(),this.getLabel());
            }
            this.modifyValues = modifyValues;
        }

        public TimeNode getModifyTimeValue() throws NotSupportedException,
                UnexpectedException {
            List<TimeNode> allValues = getModifyTimeValues();
            TimeNode rtn = allValues==null||allValues.isEmpty()?null:allValues.get(0);
            return rtn;
        }
        
        public List<TimeNode> getModifyTimeValues()
                throws NotSupportedException, UnexpectedException {
            List<TimeNode> rtn = new ArrayList<TimeNode>();
            for (int i = 0; i < modifyValues.size(); i++) {
                TimeNode addTime = TimeUtil.makeTimeNodeFromStr(
                        modifyValues.get(i).getText(),type);
                rtn.add(addTime);
            }
            return rtn;
        }

        public TimeModifyInfo clone() {
            TimeModifyInfo rtn = (TimeModifyInfo) super.clone();
            return rtn;
        }
        private ReportType reportType = null;
        private List<TimeNode> modifyValues = null;

    }

    
}
