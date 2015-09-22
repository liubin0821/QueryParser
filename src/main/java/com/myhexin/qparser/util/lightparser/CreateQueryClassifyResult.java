package com.myhexin.qparser.util.lightparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myhexin.DB.mybatis.mode.QueryClass;
import com.myhexin.DB.mybatis.mode.SearchIdPropertyConfig;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.resource.ResourceInst;

public class CreateQueryClassifyResult{
  
  public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(CreateQueryClassifyResult.class.getName());
  
  private final static String SPLIT_STR001 = "\\001";
  private final static String SPLIT_STR002 = "\\002";
  private final static String SPLIT_SPACE = " ";
  private final static String TEXT = "NormRawQuery"+SPLIT_STR001;
  private final static String SCORE ="Score_Count"+SPLIT_STR001;
  private final static String CHANNEL = "Channel_Enum"+SPLIT_STR001;
  private final static String TYPE = "MachineType_Enum"+SPLIT_STR001;
  private final static String SUBTYPE = "SubMachineType_Enum"+SPLIT_STR001;
  private final static String INDEX = "indexname"+SPLIT_STR001;
  private final static String STOCK = "Stock_Enum"+SPLIT_STR001;
  private final static String QUERY_PARSER = "QueryParser_Enum"+SPLIT_STR001;
  private final static String STOCK_CNT = "Stock_Count"+SPLIT_STR001;
  private final static String NODE_TYPE = "NodeTypeList_Enum"+SPLIT_STR001;
  private final static String CHAR_CNT = "Char_Count"+SPLIT_STR001;
  private final static String WORD_CNT = "Word_Count"+SPLIT_STR001;
  private final static String QS = "Qs_Enum"+SPLIT_STR001;
  private final static String NODE_LP_QUERY = "NormRawLPQuery"+SPLIT_STR001;
  private final static String NODE_CNT = "Nodes_Count"+SPLIT_STR001;
  private final static String NODE_SUBTYPE_LIST = "NodeSubTypeList_Enum"+SPLIT_STR001;
  private final static String NODE_SEG_COUNT = "Node_Seg_Count"+SPLIT_STR001;
  private final static String INFO_LIST = "InfoList_Enum"+SPLIT_STR001;
  private final static String EVENTS_LIST = "EventsList"+SPLIT_STR001;
  private final static String COMMON_PROP = "CommonProp_Enum"+SPLIT_STR001;
  
  private final static String[] QP_REPLACE_SYMB =
      new String[]{">", "<", "="};
  private final static String[] QP_INVALID_SYMB =
      new String[]{":", "+", "-", "%", "\""};
  private final static String[] COMMONPROP_IGNORE_TYPES = 
      new String[]{"ASTOCK", "HKSTOCK", "BSTOCK", "FUND", "USSTOCK"};
  private final static String[] COMMONPROP_IGNORE_PROP = 
      new String[]{"_指数名称"};

  public static String[] getResult(ArrayList<SemanticNode> nodes, LightParserTemplate template, LightParserServletParam requestParam){
    String qs = requestParam.getQS();
    JSONArray nodeTypeList = getNodeList(template.node_list);
    String labelList = getQueryClassify(nodes, template, nodeTypeList, qs);
    String idList = requestLabelResult(labelList);
    //idList = "{\"types\":[{\"id\":\"135857\"},{\"id\":\"138506\"},{\"id\":\"53265\"},{\"id\":\"53031\"}]}";
    idList = postProcessIdList(idList, requestParam);
    labelList = labelList.replaceAll("\\\\", "|");
    String[] result = {labelList,idList};
    return result;
  }
  
  /**
   * 
   * 
   * 
   * @param nodes
   * @return
   */
  public static String getQueryClassify(ArrayList<SemanticNode> nodes, LightParserTemplate template, JSONArray nodeTypeList, String qs) {
    if (nodes == null || nodes.size() == 0)
        return null;
    
    String channel = template.channel;
    String type = template.type;
    String subtype = template.sub_type;
    String seg_num = template.seg_num;
    
    StringBuilder result = new StringBuilder();
    
    //得到问句的文本
    StringBuilder text = new StringBuilder();
    if(nodes.get(0).getType()==NodeType.ENV){
      Environment listEnv = (Environment) nodes.get(0);
      Environment qEnv = (Environment)listEnv.get("queryEnv", false);
      if(qEnv!=null) {
        String queryStr = (String) qEnv.get("query", false);
        text.append(queryStr);
      }
    }
    if(text.length()>0){//加一些格式符号
      result.append(TEXT).append(text);
      result.append(SPLIT_STR002).append(WORD_CNT).append(getWordCount(text));
      result.append(SPLIT_STR002).append(CHAR_CNT).append(getCharCount(text));
    }
    
    String score = getScoreCount(nodes.get(0));//解析分数
    if(score !=null && score.length()>0)
      result.append(SPLIT_STR002).append(SCORE).append(score);
    
    if(channel !=null && channel.length()>0)//频道
      result.append(SPLIT_STR002).append(CHANNEL).append(channel);
    
    if(type != null && type.length()>0)//类型
      result.append(SPLIT_STR002).append(TYPE).append(type);
    
    if(subtype != null && subtype.length()>0)//子类型
      result.append(SPLIT_STR002).append(SUBTYPE).append(subtype);
    
    String nodeTLE = getNodeTypeList(nodeTypeList);//NodeTypeList_Enum
      if(nodeTLE!=null && nodeTLE.length()>0)
        result.append(SPLIT_STR002).append(NODE_TYPE).append(nodeTLE);
    
    String nodeNormRawLP = getNormRawLPQuery(nodeTypeList); //NormRawLPQuery
    if (nodeNormRawLP != null && nodeNormRawLP.length() > 0)
      result.append(SPLIT_STR002).append(NODE_LP_QUERY).append(nodeNormRawLP);
      
    Integer nodescount = getNodesCount(nodeTypeList); //Nodes_Count
    if (nodescount != null)
      result.append(SPLIT_STR002).append(NODE_CNT).append(nodescount);
    
    String nodeSubTypeList = getNodeSubTypeList(nodes); //NodeSubTypeList_Enum
    if (nodeSubTypeList != null && nodeSubTypeList.length() > 0)
      result.append(SPLIT_STR002).append(NODE_SUBTYPE_LIST).append(nodeSubTypeList);
    
    if (seg_num != null && seg_num.length() > 0)
      result.append(SPLIT_STR002).append(NODE_SEG_COUNT).append(seg_num);
    
    String infoList = getInfoList(nodes);
    if (infoList != null && infoList.length() > 0)
      result.append(SPLIT_STR002).append(INFO_LIST).append(infoList);
    
    String eventsList = getEventsList(nodeTypeList);
    if (eventsList != null && eventsList.length() > 0)
      result.append(SPLIT_STR002).append(EVENTS_LIST).append(eventsList);
    
    String commonProp = getCommonProp(nodeTypeList);
    if (commonProp != null && commonProp.length() > 0)
      result.append(SPLIT_STR002).append(COMMON_PROP).append(commonProp);

    
    List<String> indexSet = new ArrayList<String>();
    List<String> stkcodeSet = new ArrayList<String>();
    
      for(SemanticNode node : nodes){
        
        if(node.isFocusNode()){//指标
          FocusNode focusNode = (FocusNode) node;
          ArrayList<FocusItem> focusItems = focusNode.getFocusItemList();
          boolean indexAdded = false;
          for(FocusItem focusItem : focusItems){
            if(!indexAdded){
              ClassNodeFacade classNodeFacade = focusItem.getIndex();
              if(classNodeFacade != null){
                indexSet.add(classNodeFacade.getText());
                indexAdded = true;
              }
            }
            StrNode strNode = focusItem.getStr();
            if(strNode != null){
              getStkCodeFromStrNode(strNode,stkcodeSet);
            }
          }
        }
        if(node.isStrNode()){//股票代码
          getStkCodeFromStrNode((StrNode) node,stkcodeSet);
        }
        
      }
      if(indexSet.size()>0){
        String index = setToString(indexSet);
        result.append(SPLIT_STR002).append(INDEX).append(index);
      }
      if(stkcodeSet.size()>0){
        String stkcode = setToString(stkcodeSet);
        result.append(SPLIT_STR002).append(STOCK).append(stkcode);
        result.append(SPLIT_STR002).append(STOCK_CNT).append(stkcodeSet.size());
      }
      
      Environment env = (Environment)nodes.get(0);
      if (env.containsKey("jsonResult")) {
        String queryPE = env.get("jsonResult", String.class, false);
        if(queryPE!=null && queryPE.length()>0){//QUERY_PARSER
            result.append(SPLIT_STR002).append(QUERY_PARSER).append(getQueryParserEnum(queryPE));
        }
      }
      
      if (qs != null && !qs.isEmpty())
        result.append(SPLIT_STR002).append(QS).append(qs);
      
//      System.out.println("result:"+result.toString());
      
    return result.toString();
  }
  
  public static String connectToURLAndGetResult(String url){
    
    URL getUrl = null;
    InputStream reader = null;
    HttpURLConnection connection = null;
    try {
      long start = System.currentTimeMillis();
      getUrl = new URL(url);
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setConnectTimeout(Param.CREATE_CONN_TIME_OUT);
      connection.setReadTimeout(Param.READ_CONTENT_TIME_OUT_DYNAMIC_RLT);
      connection.connect();
      
      reader = connection.getInputStream();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] buf = new byte[8192];
      int n=-1;
      while( (n=reader.read(buf))>0){
        bos.write(buf, 0, n);
      }
      
      long end = System.currentTimeMillis();
      long times = (end-start);
      if( times> Consts.TIMEOUT_QUERY_CLASSIFY) {
        logger_.error("[QUERY_CLASSIFY] timeout:" + times + "ms, " + url);
      }
      return new String(bos.toString());
    } catch (MalformedURLException e) {
      logger_.error("GetQueryClassifyException\t"+e.toString());
      return Consts.STR_BLANK;
    }catch(SocketTimeoutException ex){
      logger_.error("GetQueryClassify:SocketTimeoutException\t"+ex.toString());
      return Consts.STR_BLANK;
    }catch (IOException e) {
      logger_.error("GetQueryClassifyException\t"+e.toString());
      return Consts.STR_BLANK;
    } finally{
      if(reader != null){
        try {
          reader.close();
        } catch (IOException e) {
          logger_.error("GetQueryClassifyException\t"+e.toString());
        }
      }
    }
  }
  
  public static String requestLabelResult(String label){
    if(label == null || label.length() == 0)
      return Consts.STR_BLANK;
    String getURL=null;
    try {
      getURL = Param.GET_QUERY_CLASSIFY_URL_ONLINE + URLEncoder.encode(label,"utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    if(getURL!=null) {
      String result = connectToURLAndGetResult(getURL);
      return result;
    }else{
      return Consts.STR_BLANK;
    }
    
  }
  
  /**Events list*/
  private static String getEventsList(JSONArray types) {
    StringBuilder res = new StringBuilder();
    if (types != null && types.length() > 0) {
      for (int i = 0; i < types.length(); i++) {
        JSONObject node = types.getJSONObject(i);
        if (node.has("events")) {
          String events = node.getString("events");
          if(res.length() > 0)
            res.append(" ");
          res.append(events.replace(",", " "));
        }
      }
    }
    if (res.length() > 0) {
      return res.toString();
    }
    return null;
  }
  
  /**CommonProps list*/
  private static String getCommonProp(JSONArray types) {
    Set<String> commonProps = new HashSet<String>();
    if (types != null && types.length() > 0) {
      for (int i = 0; i < types.length(); i++) {
        JSONObject node = types.getJSONObject(i);
        if (node.has("type")) {
          String type = node.getString("type");
          if (Arrays.asList(COMMONPROP_IGNORE_TYPES).contains(type))
            continue;
        }
        if (node.has("prop")) {
          boolean ignore = false;
          List<String> propList  = null;
          String props = node.getString("prop");
          if (props.isEmpty())
            return null;
          
          String[] propSplits = props.split("\\|");
          propList = Arrays.asList(propSplits);

          for (String ignoreProp : COMMONPROP_IGNORE_PROP) {
            if (propList.contains(ignoreProp)) {
              ignore = true;
              break;
            }
          }
          
          if (ignore) 
            continue;
          
          if (commonProps.isEmpty()) {
            commonProps.addAll(propList);
          } else {
            commonProps.retainAll(propList);
          }
          
        } else {
          return null;
        }
      }
    }
    if (!commonProps.isEmpty()) {
      StringBuffer res = new StringBuffer();
      for (String prop : commonProps) {
        if (res.length() > 0)
          res.append(" ");
        res.append(prop);
      }
      return res.toString();
    }
    return null;
  }
  
  /**node type list*/
  private static String getNodeTypeList(JSONArray types){
    StringBuilder res = new StringBuilder();
    if (types != null && types.length() > 0){
      for(int i = 0; i < types.length(); i++){
        JSONObject node = types.getJSONObject(i);
        String type = node.getString("type");
        if(res.length() > 0)
          res.append(" ");
        res.append(type);
      }
      return res.toString();
    }
    return null;
  }
  
  /**NormLPQuery*/
  private static String getNormRawLPQuery(JSONArray types) {
    StringBuilder res = new StringBuilder();
    if (types != null && types.length() > 0){
      for(int i = 0; i < types.length(); i++){
        JSONObject node = types.getJSONObject(i);
        String type = node.getString("type");
        String text = node.getString("text");
        if ("TEXT".equals(type) || "UNKNOWN".equals(type)) {
          res.append(text);
        } else {
          res.append(type);
        }
      }
      return res.toString();
    }
    return null;
  }
  
  /**Nodes_Count */
  private static Integer getNodesCount(JSONArray types) {
    if(types!=null && types.length()>0){
      return types.length();
    }
    return null;
  }

  /**NodeSubTypeList_Enum*/
  private static String getNodeSubTypeList(ArrayList<SemanticNode> nodes) {
    StringBuilder res = new StringBuilder();
    if (nodes != null && nodes.size() > 0) {
      for (SemanticNode node : nodes) {
        if (node.isStrNode()) {
          
          if(res.length() > 0)
            res.append(" ");
          
          if (node.isStrNode()) {
            String subtype = ((StrNode) node).getDefaultIndexSubtype();
            res.append(subtype);
          } else if (node.isFocusNode()) {
            String subtype = ((FocusNode) node).getString().getDefaultIndexSubtype();
            res.append(subtype);
          }
          
        }
      }
      return res.toString();
    }
    return null;
  }
  
  /**query parser enum*/
  private static String getQueryParserEnum(String query){
    for (String symb : QP_REPLACE_SYMB) {
      query = query.replace(symb, "_");
    }
    for (String symb : QP_INVALID_SYMB) {
      query = query.replace(symb, "");
    }
    return query;
  }
  
  /** info list enum */
  private static String getInfoList(ArrayList<SemanticNode> nodes) {
    StringBuilder res = new StringBuilder();
    if (nodes != null && nodes.size() > 0) {
      for (SemanticNode node : nodes) {
        if (node.isStrNode()) {
          
          if(res.length() > 0)
            res.append(" ");
          
          if (node.isStrNode()) {
            String infolist = ((StrNode) node).getProps().replace("|", " ");
            res.append(infolist);
          } else if (node.isFocusNode()) {
            String infolist = ((FocusNode) node).getString().getProps().replace("|", " ");
            res.append(infolist);
          }
        }
      }
      return res.toString();
    }
    return null;
  }
  
  private final static String STK_CODE = "股票代码";
  private final static String STK_CODE_w = "_股票代码";
  
  private final static String STK_NAME = "股票简称";
  private final static String STK_NAME_w = "_股票简称";
  
  private static void getStkCodeFromStrNode(StrNode strNode,List<String> stkcodeList){
    LinkedHashSet<String> subType = strNode.subType;
    boolean stkcodeFlag = false;
    for(String atype : subType){
      if(atype.equals(STK_CODE) || atype.equals(STK_CODE_w) || atype.equals(STK_NAME) || atype.equals(STK_NAME_w)) {
        stkcodeFlag = true;
        break;
      }
    }
    if(!stkcodeFlag)
      return;
    
    String value1 = strNode.getId(STK_CODE_w);
    if(value1==null || value1.length()==0) value1 = strNode.getId(STK_CODE);
    if(value1==null || value1.length()==0) value1 = strNode.getId(STK_NAME_w);
    if(value1==null || value1.length()==0) value1 = strNode.getId(STK_NAME);
    
    if(value1!=null) {
      String[] vals = value1.split("\\|");
      for(String va:vals){
        if(va.matches("\\d{6}"))
          stkcodeList.add(va);
      }
    }
    
    //Map<String,String> map = strNode.getId();
    /*for(Entry<String, String>  en: map.entrySet()){
      if(en.getKey().contains("股票简称")||en.getKey().contains("股票代码")){
        String val = en.getValue();
        String[] vals = val.split("\\|");
        for(String va:vals){
          if(va.matches("\\d{6}"))
            stkcodeList.add(va);
        }
        break;
      }
    }*/
    /*String info = strNode.info;
    String[] infos = info.split("\\|");
    for(String inf:infos){
      if(inf.contains("股票代码")||inf.contains("股票简称")){
        String[] infs = inf.split(":");
        if(infs.length==2)
          return infs[1];
        else if(infs.length==3)
          return infs[2];
      }
    }*/
  }
  
  /**char count*/
  private static int getCharCount(StringBuilder q){
    //String query= q.toString();
    StringBuilder b = new StringBuilder();
    for(int i=0;i<q.length();i++) {
      char c = q.charAt(i);
      if(c!=' ' && c!='\t' && c!='\r' && c!='\n') {
        b.append(c);
      }
    }
    
    /*query = query.replaceAll(" ", "");
    query = query.replaceAll("\t", "");
    query = query.replaceAll("\r", "");
    query = query.replaceAll("\n", "");*/
    try {
      return b.toString().getBytes("UTF-8").length;
    } catch (UnsupportedEncodingException e) {
      return b.toString().getBytes().length;
    }
  }
  
  /**word count*/
  private static  int getWordCount(StringBuilder query){
    try {
      int cnt = 0;
      boolean isCh = false;
      for(int i=0;i<query.length();i++){
        String ch = String.valueOf(query.charAt(i));
        byte[] bytes;
        bytes = ch.getBytes("UTF-8");
        if(bytes.length==1){
          isCh = true;
          continue;
        }
        if(isCh){
          cnt += 2;
          isCh = false;
        }else{
          cnt += 1;
        }
      }
      if(isCh)
        cnt += 1;
      return cnt;
    } catch (UnsupportedEncodingException e) {
      return query.toString().getBytes().length;
    }
  }
  
  private final static String regex = ".*\"node_list\":\\[(.*?)\\].*";
  private final static Pattern pattern = Pattern.compile(regex);
  
  /**node list*/
  @SuppressWarnings("unused")
  private String getNodeList(SemanticNode node){
    String lightParserResult = node.getLightParserResult();
    lightParserResult = lightParserResult.replace(" ", "");
    lightParserResult = lightParserResult.replace("\r\n", "");
    
    Matcher matcher = pattern.matcher(lightParserResult);
    if(matcher.matches()){
      String node_list=matcher.group(1);
      return node_list;
    }
    return null;
  }
  
  /**解析分数*/
  private static String getScoreCount(SemanticNode node){
    return String.valueOf(node.score);
  }
  
  /**set转成字符串*/
  private static String setToString(List<String> set){
    StringBuilder str = new StringBuilder();
    for(String s : set){
      str.append(SPLIT_SPACE).append(s);
    }
    str.deleteCharAt(0);
    return str.toString();
  }
  
  private static String postProcessIdList(String idList, LightParserServletParam requestParam) {
    if (!Consts.STR_BLANK.equals(idList)) {
      JSONObject idListObj = new JSONObject(idList);
      if (idListObj.has("types")) {
        JSONArray idListTypes = idListObj.getJSONArray("types");
        if (requestParam.isShowId_Name()) {
          refillTagId("id", "name", 
              ResourceInst.getInstance().getQcMap(), idListTypes);
        }
        refillTagId("id", "priority", 
            ResourceInst.getInstance().getSearchIdPropertyMap(), idListTypes);
        refillTagId("id", "optime",
        ResourceInst.getInstance().getSearchIdPropertyMap(), idListTypes);
        idListTypes = reorderIdList(idListTypes);
        idListObj.put("types", idListTypes);
      }
      return idListObj.toString();
    }
    return idList;
  }
  
  private static JSONArray reorderIdList(JSONArray idListTypes) {
    List<JSONObject> sortList = new ArrayList<JSONObject>();
    for (int i = 0; i < idListTypes.length(); i++) {
      sortList.add(idListTypes.getJSONObject(i));
    }
    Collections.sort(sortList, new Comparator<JSONObject>() {
      @Override
      public int compare(JSONObject arg0, JSONObject arg1) {
        int priority0 = arg0.has("priority") ? arg0.getInt("priority") : 0;
        int priority1 = arg1.has("priority") ? arg1.getInt("priority") : 0;
        if (priority0 < priority1) {
          return 1;
        } else if (priority0 > priority1) {
          return -1;
        } else {
          long time0 = arg0.has("optime") ? ((Timestamp) arg0.get("optime")).getTime() : 0;
          long time1 = arg1.has("optime") ? ((Timestamp) arg1.get("optime")).getTime() : 0;
          if (time0 < time1) {
            return 1;
          } else if (time0 > time1) {
            return -1;
          } 
        }
        return 0;
      }
    });
    JSONArray sortedIdListTypes = new JSONArray(sortList);
    return sortedIdListTypes;
  }
  
  private static void refillTagId(String key, String newfield, Map map, JSONArray idListTypes) {
    for(int i = 0; i < idListTypes.length(); i++){
      JSONObject node = (JSONObject) idListTypes.get(i);
      String id = node.getString(key);
      if (map.containsKey(id)) {
        Object content = map.get(id);
        if (content != null) {
          if (content instanceof QueryClass) {
            node.put(newfield, ((QueryClass) content).getTagName());
          } else if (content instanceof SearchIdPropertyConfig) {
            SearchIdPropertyConfig propertyConfig = (SearchIdPropertyConfig) content;
            if ("priority".equals(newfield))
              node.put(newfield, propertyConfig.getPriority());
            else if ("optime".equals(newfield))
              node.put(newfield, propertyConfig.getOptime());
          }
        }
      }
    }
  }
  
  public static JSONArray getNodeList(List<SemanticNode> nodes) {
    try {
      Map<String, Object> nodeList = new HashMap<String, Object>();
      StringWriter sw = new StringWriter();
      nodeList.put("node_list", nodes);
      LightParser.createJson(nodeList, "node_list", sw);
      return new JSONArray(sw.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
    
    
    
  }
}