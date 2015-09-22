package com.myhexin.qparser.fuzzy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.fuzzy.FuzzyScoreFactory.RESCORE_TYPE;

public class FuzzyMidResult {
  public static SortByMidItemScore sorter_ = new SortByMidItemScore();
  public ArrayList<FuzzyMidItem> midResult_ = new ArrayList<FuzzyMidItem>();

  /**
   * 更改算法之后还应该修改相应的过滤策略 以下三个函数可以联合使用，rescoreFil
   */
  public void rescore() {
    for (int i = 0; i < midResult_.size(); i++) {
      FuzzyScoreFactory.rescore(midResult_.get(i), RESCORE_TYPE.DENSITY);
    }
  }

  public void sort() {
    Collections.sort(midResult_, sorter_);
  }

  /**
   * check the basic confidence, if score less than Param.ONTO_PROD_SCORE,
   * delete it
   */
  public void scoreFilter() {
    for (Iterator<FuzzyMidItem> iterator = this.midResult_.iterator(); iterator
        .hasNext();) {
      FuzzyMidItem item = iterator.next();
//      if (item.score_ < Param.ONTO_PROD_SCORE
//          && item.propName_.equals("主营产品名称")) {
//        iterator.remove();
//      }      
      if(item.score_ < Param.ONTO_PROD_SCORE 
          && item.type_ == FuzzyItem.TYPE_CLASS.INDEX){
          iterator.remove();
          continue;
      }      
      if(item.score_ < Param.ONTO_PROD_SCORE 
          && (item.propName_.equals("主营产品名称")||item.propName_.equals("所属概念"))){
          iterator.remove();
          continue;
      }
    }
  }

  /**
   * WTF is this? rescore the result from TDB...
   */
  public void rescoreFilter() {
    ArrayList<FuzzyMidItem> tempResult = new ArrayList<FuzzyMidItem>();
    int maxLayerCount = 4;
    int nowCount = 0;
    double preScoreLayer = -1.0;
    double nowScoreLayer = -1.0;
    this.sort();
    for (int i = 0; i < midResult_.size(); i++) {
      if (FuzzyScoreFactory.accept(midResult_.get(i), RESCORE_TYPE.DENSITY)) {
        if (preScoreLayer < 0) {
          preScoreLayer = midResult_.get(i).score_;
          nowScoreLayer = preScoreLayer;
        } else {
          nowScoreLayer = midResult_.get(i).score_;
        }

        tempResult.add(midResult_.get(i));
        if (nowScoreLayer < preScoreLayer) {
          nowCount++;
        }
        preScoreLayer = nowScoreLayer;
        if (nowCount > maxLayerCount) {
          break;
        }
      }
    }
    midResult_ = tempResult;
  }
}
