package com.myhexin.qparser.fuzzy;

import com.myhexin.datasrc.TdbClient;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

public class FuzzySearcher {
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
      .getLogger(FuzzySearcher.class);

  public static void loadFuzzyModel() {
    // we don't use model now
  }

  public FuzzySearcher() {
  }

  /**
   * check if given string is a valid string to search in TDB.
   * 
   * @param str
   * 
   * @return true if there is at least one valid character
   */
  public boolean searchAble(String str) {
    if (str == null) {
      return false;
    } else if (str == "") {
      return false;
    } else {
      for (int i = 0; i < str.length(); i++) {
        if (!FuzzyAnalysis.isOther(str.charAt(i))) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * 1. search the TDB
   * <p>
   * 2. calc the score
   * <p>
   * return result sorted by score
   * 
   * @param queryStr
   * @return
   */
  public FuzzyResult search(String queryStr) {
    // step I: 去除一下特殊字符
    StringBuilder querySB = new StringBuilder();
    for (int i = 0; i < queryStr.length(); i++) {
      char nowChar = queryStr.charAt(i);
      if (Character.isWhitespace(nowChar) || !FuzzyAnalysis.isOther(nowChar)) {
        querySB.append(nowChar);
      }
    }
    if (querySB.length() < 1) {
      LOG.warn("no need to fuzzy search for String: {}", queryStr);
      return null;// no valid char in given string
    }
    String clearQueryStr = querySB.toString();

    // step II: 进入搜索阶段
    LOG.debug("create new TDB client using [{}]", Param.JOSEKI_URL);
    TdbClient tdbClient = TdbClient.newTdbClient(Param.JOSEKI_URL);
    FuzzyResult res = searchInAllBySingleEntity(clearQueryStr, tdbClient);

    // step III: sort and return
    if (res == null) {
      LOG.debug(
          "Query [{}](cleaned-query:[{}]) gets null result from TDB search.",
          queryStr, clearQueryStr);
      return null;
    } else {
      LOG.debug("for query [{}] returned result(before sort): \n{}\n",
          queryStr, res);
      res.sort(true);
      LOG.debug("for query [{}] returned result(before unique): \n{}\n",
          queryStr, res);
      res.unique();
    }
    LOG.debug("for query [{}] returned result: \n{}\n", queryStr, res);

    return res;
  }

  public FuzzyResult searchInAllBySingleEntity(String queryStr,
      TdbClient tdbClient) {
    FuzzyResult res = null;
//    String querySparql = FuzzyQueryStrs.getSearchSparqlBySingleEntity(queryStr,
//        0.6d, 5, 50, 0);
    String querySparql = FuzzyQueryStrs.getSearchSparql(queryStr, 0, 5, 50, 0);

    if (querySparql == null || querySparql == "") {
      LOG.warn("cannot compose a valid sparql for query: [{}]", queryStr);
      return null;
    }

    LOG.debug("send to TDB the following sparql for query [{}]:\n{}\n",
        queryStr, querySparql);

    String csvStr = tdbClient.executeQuery(querySparql);
    if (csvStr == null) {
      LOG.warn("query [{}] gets null result from TDB, ", queryStr);
      return null;
    }
    LOG.debug("query [{}] gets result from TDB:\n{} ", queryStr, csvStr);

    try {
      String[] lineArray = csvStr.split(TdbClient.SPLIT_LINE);
      if (lineArray.length < 5) {
        LOG.debug("not valid result, "
            + "less than 5 lines in returned CSV string: []", csvStr);
        return null;
      }
      FuzzyMidResult fuzzyMidResult = new FuzzyMidResult();
      for (int i = 3; i < lineArray.length - 1; i++) {
        String[] fieldArray = lineArray[i].split(TdbClient.SPLIT_FIELD);
        if (fieldArray == null || fieldArray.length != 3) {
          // logger_.error("模糊查询返回结果field个数不符:%s" ,lineArray[i]);
          continue;
        }
        String nowPropName = "";
        String nowObjName = "";
        double nowScore = 0;
        FuzzyItem.TYPE_CLASS type = null;

        if (fieldArray[0].length() == 0) {
          // 第一个域为空，说明匹配的label是指标的label
          nowPropName = fieldArray[1];
          type = FuzzyItem.TYPE_CLASS.INDEX;
        } else {
          // 第一个域非空，说明匹配的label是实例的label
          nowPropName = fieldArray[0];
          nowObjName = fieldArray[1];
          type = FuzzyItem.TYPE_CLASS.VALUE;
        }

        nowScore = Double.parseDouble(fieldArray[2]);

        FuzzyMidItem nowFuzzyMidItem = new FuzzyMidItem(queryStr, nowPropName,
            nowObjName, nowScore, type);
        fuzzyMidResult.midResult_.add(nowFuzzyMidItem);
      }
      // TODO: double check if there is something we can add to the TDB result.
      // at present, just disable the questionable "re-score and filter"
      // process.
      //
      // fuzzyMidResult.rescore();
      // fuzzyMidResult.rescoreFilter();
      fuzzyMidResult.scoreFilter();
      //
      res = new FuzzyResult();
      res.fill(fuzzyMidResult);
      return res;
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(String.format("模糊查询错误:%s", e.getMessage()));
      return null;
    }
  }

  public FuzzyResult searchInAll(String queryStr, TdbClient tdbClient) {
    FuzzyResult res = null;
    String querySparql = FuzzyQueryStrs.getSearchSparql(queryStr, 0.6d, 5, 50,
        0);
    if (Param.DEBUG_FUZZY) {
      String outstr = String.format("\n%s", querySparql);
      LOG.warn(outstr);
      System.out.println(outstr);
    }
    if (querySparql == null || querySparql == "") {
      return null;
    }
    String csvStr = tdbClient.executeQuery(querySparql);
    if (csvStr == null) {
      return null;
    }
    try {
      String[] splArray = csvStr.split(TdbClient.SPLIT_LINE);
      int length = splArray.length;
      if (length > 4) {
        FuzzyMidResult fuzzyMidResult = new FuzzyMidResult();
        for (int i = 3; i < length - 1; i++) {
          String[] contentArray = splArray[i].split(TdbClient.SPLIT_FIELD);
          String nowPropName = contentArray[0];
          String nowObjName = "";
          FuzzyItem.TYPE_CLASS type = null;
          if (contentArray[1] == "") {
            nowObjName = "";
            type = FuzzyItem.TYPE_CLASS.INDEX;
          } else {
            nowObjName = contentArray[1];
            type = FuzzyItem.TYPE_CLASS.VALUE;
          }
          double nowScore = Double.parseDouble(contentArray[2]);

          FuzzyMidItem nowFuzzyMidItem = new FuzzyMidItem(queryStr,
              nowPropName, nowObjName, nowScore, type);
          fuzzyMidResult.midResult_.add(nowFuzzyMidItem);
        }
        fuzzyMidResult.rescore();
        fuzzyMidResult.rescoreFilter();
        res = new FuzzyResult();
        res.fill(fuzzyMidResult);
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(String.format("模糊查询错误:%s", e.getMessage()));
    }
    return res;
  }

  public static void main(String[] args) {
    org.apache.log4j.PropertyConfigurator.configure("./conf/log4j.properties");
    ApplicationContextHelper.loadApplicationContext();
    Param.DEBUG_FUZZY = true;
    FuzzySearcher searcher = new FuzzySearcher();

    String testIn = "三平底股票";
    testIn = "精油";
    testIn = "三角";
//    testIn = "房地产";
//    testIn = "工程";
    testIn = "员工";
//    testIn = "希望";

    FuzzyResult result = searcher.search(testIn);
    System.out.println("Fuzyy result: " + result);
  }
}
