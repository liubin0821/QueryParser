package com.myhexin.qparser.fuzzy;

import java.util.Comparator;

class SortByMidItemScore implements Comparator<Object> {
  @Override
  public int compare(Object arg0, Object arg1) {
    FuzzyMidItem i1 = (FuzzyMidItem) arg0;
    FuzzyMidItem i2 = (FuzzyMidItem) arg1;
    if (i1.score_ < i2.score_) {
      return 1;
    } else {
      return 0;
    }
  }
}

public class FuzzyMidItem {
  public String queryStr_;// 使用空格隔开的query string
  public String scoreQueryStr_;// 没有使用空格隔开的query string
  public String scoreMatchStr_;// 匹配到的字符串
  public String propName_;
  public String objName_;
  public double score_;
  public FuzzyItem.TYPE_CLASS type_;

  @Override
  public String toString() {
    return "FuzzyMidItem:{" + "\nqueryStr_:" + queryStr_ + "\nscoreQueryStr_"
        + scoreQueryStr_ + "\nscoreMatchStr_:" + scoreMatchStr_
        + "\npropName_:" + propName_ + "\nobjName_:" + objName_ + "\nscore_:"
        + score_ + "\ntype_:" + type_ + "}";
  }

  public FuzzyMidItem(String queryStr, String propName, String objName,
      double score, FuzzyItem.TYPE_CLASS type) {
    queryStr_ = queryStr;
    scoreQueryStr_ = queryStr.replaceAll(" ", "");
    if (objName == null || objName == "") {
      scoreMatchStr_ = propName;
    } else {
      scoreMatchStr_ = objName;
    }
    propName_ = propName;
    objName_ = objName;
    score_ = score;
    type_ = type;
  }
}
