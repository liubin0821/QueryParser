package com.myhexin.qparser.phrase.parsePostPlugins;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.util.lightparser.CreateQueryClassifyResult;
import com.myhexin.qparser.util.lightparser.LightParserRedisState;

public class PhraseParserPluginEventsRedisReader extends PhraseParserPluginAbstract {

  private static String[] EventsNames = {
    "NoticePub",
    "QualityWeibo",
    "AnsInvestQA",
    "ReportPub",
    "ImpNews"
  };
  private static String CODEEVENTCONN = "__";
  
  private LightParserRedisState lprs;
  private ExecutorService executor;
  private int timeout = 100;
  
  @Override
  public void init() {
    Map<String, Object> conf = new HashMap<String, Object>();

    conf.put("host", Param.getMAINSEARCH_EVENTS_REDIS_HOST());
    conf.put("port", Param.getMAINSEARCH_EVENTS_REDIS_PORT());
    conf.put("db", Param.getMAINSEARCH_EVENTS_REDIS_DB());
    
    lprs = new LightParserRedisState(conf);
    executor = Executors.newSingleThreadExecutor();
  }
  
  public PhraseParserPluginEventsRedisReader() {
    super("EventsRedisReader");
  }
  
  @Override
  public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
    final ArrayList<SemanticNode> nodes = annotation.getNodes();
    final JSONArray jsonNodes = CreateQueryClassifyResult.getNodeList(nodes);
    Callable<ArrayList<ArrayList<SemanticNode>>> task = new Callable<ArrayList<ArrayList<SemanticNode>>>() {
      public ArrayList<ArrayList<SemanticNode>> call() throws Exception {
        return processRedisReader(nodes, jsonNodes);
      }
    }; 
    Future<ArrayList<ArrayList<SemanticNode>>> future = executor.submit(task);
    try{
      return future.get(timeout, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      e.printStackTrace();
      future.cancel(true);
      executor.shutdown();
      executor = Executors.newSingleThreadExecutor();
      ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>(1);
      rlist.add(nodes);
      return rlist;
    }
  }

  private ArrayList<ArrayList<SemanticNode>> processRedisReader(ArrayList<SemanticNode> nodes, JSONArray jsonNodes) {
    if (nodes == null || nodes.size() == 0)
      return null;
    
    Map<String, Object> textEventsMap = getNodeEvents(jsonNodes);

    if (textEventsMap != null && !textEventsMap.isEmpty()) {
      for (SemanticNode node : nodes) {
        String text = node.getText();
        if (textEventsMap.containsKey(text)) {
          Set<String> events = (Set<String>) textEventsMap.get(text);
          node.setEventsSet(events);
        }
      }
    }

    ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    rlist.add(nodes);
    return rlist;
  }
  
  private Map<String, Object> getNodeEvents(JSONArray jsonNodes) {
    String todayDateStr = getTodayDateStr(0l);
    Map<String, Object> eventsMap = null;
    for (int i = 0; i < jsonNodes.length(); i++) {
      JSONObject jsonNode = jsonNodes.getJSONObject(i);
      String type = jsonNode.getString("type");
      
      if ("ASTOCK".equals(type)) {
        String id = jsonNode.getString("id");
        String text = jsonNode.getString("text");
        
        for (String eventName : EventsNames) {
          String codeEvent = id + CODEEVENTCONN + eventName;
          String eventJSONStr = lprs.getData(todayDateStr, codeEvent);
          
          if ("NoticePub".equals(eventName) && eventJSONStr == null) {
            String tomorrowDateStr = getTodayDateStr(24l);
            eventJSONStr = lprs.getData(tomorrowDateStr, codeEvent);
          }
          
          if (eventJSONStr != null) {
            if (eventsMap == null) {
              eventsMap = new HashMap<String, Object>();
            }
            
            if (!eventsMap.containsKey(text)) {
              eventsMap.put(text, new HashSet<String>());
            }
            
            ((Set<String>) eventsMap.get(text)).add(eventName);

          }
        }
        
      }
    }

    return eventsMap;
  }
  
  private String getTodayDateStr(long aheadHour) {
    try {
      long currentTime = System.currentTimeMillis() + aheadHour*3600000;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String result = sdf.format(currentTime);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
