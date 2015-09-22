package com.myhexin.qparser.fuzzy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FuzzyAnalysis {
  private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
      .getLogger(FuzzySearcher.class.getName());

  public static String STR_START = "<BOF>";
  public static String STR_END = "<EOF>";

  // 全角转半角
  public static String QtoB(String input) {
    char c[] = input.toCharArray();
    for (int i = 0; i < c.length; i++) {
      if (c[i] == '\u3000') {
        c[i] = ' ';
      } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
        c[i] = (char) (c[i] - 65248);
      }
    }
    return new String(c);
  }

  public static boolean isChinese(char uchar) {
    // check if this uchar is a chinese char
    if ((uchar >= '\u4e00') && (uchar <= '\u9fa5')) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isNumber(char uchar) {
    // check if this uchar is a number
    return Character.isDigit(uchar);
  }

  public static boolean containAlphabet(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (isAlphabet(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * check if this uchar is a alphabet
   * 
   * @param uchar
   * @return
   */
  public static boolean isAlphabet(char uchar) {
    if (((uchar >= '\u0041') && (uchar <= '\u005a'))
        || ((uchar >= '\u0061') && (uchar <= '\u007a'))) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isOther(char uchar) {
    // check if this uchar is not chinese or number or alphabet
    if (isChinese(uchar) || isNumber(uchar) || isAlphabet(uchar)) {
      return false;
    } else {
      return true;
    }
  }

  public static String stringWithSpace(String ustr) {
    ArrayList<String> list = stringToList(ustr);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      sb.append(" ");
      sb.append(list.get(i));
    }
    sb.append(" ");
    return sb.toString();
  }

  public static ArrayList<String> stringToList(String ustr) {
    ArrayList<String> res = new ArrayList<String>();
    StringBuilder tmp = new StringBuilder();
    for (int i = 0; i < ustr.length(); i++) {
      char uchar = ustr.charAt(i);
      if (isOther(uchar)) {
        if (tmp.length() == 0) {
          continue;
        } else {
          res.add(tmp.toString());
          tmp = new StringBuilder();
        }
      } else {
        if (isChinese(uchar)) {
          if (tmp.length() > 0) {
            res.add(tmp.toString());
            tmp = new StringBuilder();
          }
          res.add(Character.toString(uchar));
        } else {
          tmp.append(uchar);
        }
      }
    }
    if (tmp.length() > 0) {
      res.add(tmp.toString());
    }
    return res;
  }

  /**
   * Try to find if jump
   * 
   * @param item1
   *          :first item to compare
   * @param item2
   *          :second item to compare
   * @return 0: no jump, 1 higher, -1 lower
   */
  public int jump(FuzzyProductionModelItem item1, FuzzyProductionModelItem item2) {
    double s1 = item1.getScore();
    double s2 = item2.getScore();
    int sl1 = item1.getScoreLevel();
    int sl2 = item2.getScoreLevel();

    if ((s1 == 0.0)) {
      if (s2 > 0.0) {
        return 1;
      } else {
        return 0;
      }
    }
    if (s2 == 0.0) {
      if (s1 > 0.0) {
        return -1;
      } else {
        return 0;
      }
    }

    if (sl2 - sl1 >= 2) {
      return 1;
    } else if (sl1 - sl2 >= 2) {
      return -1;
    } else {
      return 0;
    }
  }

  /**
   * 判断当前稳定状态是否是0稳态 如果是0稳态，返回ture。说明当前不在识别范围内 如果不是0稳态，返回false。说明是有效稳态，即在识别范围内
   * 
   * @param item1
   *          : now item
   * @param item2
   *          : next item
   * @return if the float type is zero float
   */
  public boolean isZeroFloat(FuzzyProductionModelItem item1,
      FuzzyProductionModelItem item2) {
    double s1 = item1.getScore();
    double s2 = item2.getScore();
    int sl1 = item1.getScoreLevel();
    int sl2 = item2.getScoreLevel();

    if ((sl1 == 0) && (sl2 == 0)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 
   * @param queryStr
   * @return
   */
  public ArrayList<FuzzyProductionModelItem> productAnalyse(String queryStr) {
    ArrayList<FuzzyProductionModelItem> list = productAnalyseScore(queryStr);
    return productAnalyseConstruct(list);
  }

  private ArrayList<FuzzyProductionModelItem> productAnalyseConstruct(
      ArrayList<FuzzyProductionModelItem> list) {
    int ps = 0;
    int pe = 0;
    int consistJumpCount = 1;
    int consistZeroFloatCount = 1;
    int consistAvalibleFloatCount = 1;

    ArrayList<FuzzyProductionModelItem> res = new ArrayList<FuzzyProductionModelItem>();

    // temp test
    // for( int i = 0; i < list.size(); i++ ){
    // logger_.info( list.get(i).toString() );
    // }

    for (int i = 0; i < list.size(); i++) {
      logger_.debug(list.get(i).toString());
    }

    for (int pnow = 0; pnow < list.size() - 1; pnow++) {
      // check if it will jump
      int jumpState = jump(list.get(pnow), list.get(pnow + 1));
      if (0 == jumpState) {
        consistJumpCount = 0;
        if (isZeroFloat(list.get(pnow), list.get(pnow + 1))) {
          consistZeroFloatCount++;
        } else {
          consistAvalibleFloatCount++;
        }
        continue;
      }
      if (1 == jumpState) {
        consistJumpCount++;
        if ((consistJumpCount >= 2) || (consistZeroFloatCount >= 1)) {
          ps = pnow + 1;
        }
        consistZeroFloatCount = 0;
        consistAvalibleFloatCount = 0;
        continue;
      }
      if (-1 == jumpState) {
        consistJumpCount++;
        if (consistAvalibleFloatCount >= 1) {
          pe = pnow;
          StringBuilder sb = new StringBuilder();
          double prescore = 0.0;

          for (int i = ps; i <= pe; i++) {
            sb.append(list.get(i).getWordPair());
            prescore += list.get(i).getScore();
          }
          double postscore = prescore / consistAvalibleFloatCount;
          FuzzyProductionModelItem newItem = new FuzzyProductionModelItem(
              sb.toString(), postscore);
          res.add(newItem);

        }
        ps = pnow + 1;
        pe = pnow + 1;
        consistZeroFloatCount = 0;
        consistAvalibleFloatCount = 0;
        continue;
      }
    }
    SortByProductionItemScore sorter = new SortByProductionItemScore();
    Collections.sort(res, sorter);
    return res;
  }

  private ArrayList<FuzzyProductionModelItem> productAnalyseScore(String ustr) {
    HashMap<String, FuzzyProductionModelItem> modelL2 = FuzzyModel.FUZZY_PROD_MODEL.prodModelL2Map;
    HashMap<String, FuzzyProductionModelItem> modelL1 = FuzzyModel.FUZZY_PROD_MODEL.prodModelL1Map;
    ArrayList<String> ustrList = new ArrayList<String>();
    ustrList.add(STR_START);
    ustrList.addAll(stringToList(ustr));
    ustrList.add(STR_END);

    HashMap<String, Double> scoreMap = new HashMap<String, Double>();
    for (String now : ustrList) {
      scoreMap.put(now, 0.0);
    }

    for (int i = 0; i < ustrList.size(); i++) {
      if (ustrList.get(i) == STR_START) {
        continue;
      }
      if (ustrList.get(i) == STR_END) {
        break;
      }
      if (modelL1.containsKey(ustrList.get(i))) {
        for (int j = i + 1; j < ustrList.size(); j++) {
          if (ustrList.get(j) == STR_START) {
            continue;
          }
          if (ustrList.get(j) == STR_END) {
            break;
          }
          String wordpair = String.format("%s%s", ustrList.get(i),
              ustrList.get(j));
          if (modelL2.containsKey(wordpair)) {
            double both_score = modelL2.get(wordpair).getScore();
            double value_temp = scoreMap.get(ustrList.get(i)) + both_score;
            scoreMap.put(ustrList.get(i), value_temp);
            value_temp = scoreMap.get(ustrList.get(j)) + both_score;
            scoreMap.put(ustrList.get(j), value_temp);
          }
        }
      }
    }

    ArrayList<FuzzyProductionModelItem> res = new ArrayList<FuzzyProductionModelItem>();
    for (String now : ustrList) {
      FuzzyProductionModelItem tmp = new FuzzyProductionModelItem(now,
          scoreMap.get(now));
      // if( Param.DEBUG ){
      // logger_.warn(String.format("%s:%s", now, scoreMap.get(now)));
      // }
      res.add(tmp);
    }
    return res;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }
}
