package com.myhexin.qparser.fuzzy;

import java.util.HashSet;

import org.slf4j.Logger;

import com.myhexin.qparser.fuzzy.FuzzyItem.TYPE_CLASS;

public class FuzzyScoreFactory {

  private static final Logger LOG = org.slf4j.LoggerFactory
      .getLogger(FuzzyScoreFactory.class);

  public enum RESCORE_TYPE {
    LR, DENSITY;
  }

  public static FuzzyMidItem rescore(FuzzyMidItem midItem,
      RESCORE_TYPE rescoreType) {
    if (rescoreType == RESCORE_TYPE.LR) {
      // call LR rescore function
      rescoreLR(midItem);
    } else if (rescoreType == RESCORE_TYPE.DENSITY) {
      rescoreDensity(midItem);
    }
    return midItem;
  }

  public static boolean accept(FuzzyMidItem midItem, RESCORE_TYPE rescoreType) {
    LOG.debug("check middle item :" + midItem);

    if (rescoreType == RESCORE_TYPE.LR) {
      return acceptLR(midItem);
    } else if (rescoreType == RESCORE_TYPE.DENSITY) {
      return acceptDensity(midItem);
    }
    return false;
  }

  public static boolean acceptDensity(FuzzyMidItem midItem) {
    String lowerQueryStr = midItem.scoreQueryStr_.toLowerCase();
    String lowerMatchStr = midItem.scoreMatchStr_.toLowerCase();

    if (midItem.type_ == TYPE_CLASS.VALUE) {
      if (lowerQueryStr.length() <= 2) {
        return (lowerMatchStr.contains(lowerQueryStr) && (midItem.score_ > 500));
      } else if (lowerQueryStr.length() == 3) {
        if (midItem.score_ > 0.429) {
          return true;
        } else {
          return false;
        }
      } else if (lowerQueryStr.length() == 4) {
        // if( midItem.score_ > 333 ){
        if (midItem.score_ > 0.429) {
          return true;
        } else {
          return false;
        }
      } else if (lowerQueryStr.length() == 5) {
        // if( midItem.score_ > 300 ){
        if (midItem.score_ > 0.429) {
          return true;
        } else {
          return false;
        }
      } else if (lowerQueryStr.length() == 6) {
        // if( midItem.score_ > 286 ){
        if (midItem.score_ > 0.333) {
          return true;
        } else {
          return false;
        }
      } else {
        // if( midItem.score_ > 249 ){
        if (midItem.score_ > 0.300) {
          return true;
        } else {
          return false;
        }
      }
    } else if (midItem.type_ == TYPE_CLASS.INDEX) {
      if (midItem.scoreQueryStr_.length() <= 3) {
        if (midItem.score_ > 0.749) {
          return true;
        } else {
          return false;
        }
      } else if (midItem.scoreQueryStr_.length() == 4) {
        if (midItem.score_ > 0.499) {
          return true;
        } else {
          return false;
        }
      } else if (midItem.scoreQueryStr_.length() == 5) {
        if (midItem.score_ > 0.333) {
          return true;
        } else {
          return false;
        }
      } else if (midItem.scoreQueryStr_.length() == 6) {
        if (midItem.score_ > 0.332) {
          return true;
        } else {
          return false;
        }
      } else {
        if (midItem.score_ > 0.299) {
          return true;
        } else {
          return false;
        }
      }
    } else {
      return false;
    }
  }

  public static boolean acceptLR(FuzzyMidItem midItem) {
    if (midItem.score_ >= 94) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 算法有问题: 算公共子序列, 不用先把公告字符集找出来, 查询串不会很长, 要是怕内存消耗大, 可以改进一下LCS的存储,
   * 空间复杂度可以是O(min(m,n))的. 计算密度的时候, 除法那里, 1/一个大于1的整数始终是0的, 不知道这个是故意还是疏忽了(确实是疏忽了)
   * 
   * 
   * 密度算法计算第一个字符串相对于第二个字符串的相似度 因此, 两个相同字符串传入顺序不同, 其返回分值可能会不同 如果传入参数不合法, 会返回负值
   * 
   * @param first
   *          ：主字符串
   * @param second
   *          ：从字符串
   * @return 第一个字符串相对于第二个字符串的相似度分值
   */
  public static double rescoreDensity(String first, String second) {
    if (first == null || second == null) {
      return -1.0;
    }

    String lowerQueryStr = first.toLowerCase();
    String lowerMatchStr = second.toLowerCase();
    int ql = lowerQueryStr.length();
    int ml = lowerMatchStr.length();

    if (ql + ml == 0) {
      return -1.0;
    }

    HashSet<Character> querySet = new HashSet<Character>();
    HashSet<Character> comSet = new HashSet<Character>();

    // all char in query goes to queryset
    for (char ch : lowerQueryStr.toCharArray()) {
      querySet.add(ch);
    }
    // get joint char set : comSet
    for (char ch : lowerMatchStr.toCharArray()) {
      if (querySet.contains(ch)) {
        comSet.add(ch);
      }
    }
    //
    StringBuilder qSB = new StringBuilder();
    for (int i = 0; i < ql; i++) {
      char iChar = lowerQueryStr.charAt(i);
      if (comSet.contains(iChar)) {
        qSB.append(iChar);
      }
    }
    StringBuilder mSB = new StringBuilder();
    for (int i = 0; i < ml; i++) {
      char iChar = lowerMatchStr.charAt(i);
      if (comSet.contains(iChar)) {
        mSB.append(iChar);
      }
    }
    String comStr = FuzzyUtil.LCS(qSB.toString(), mSB.toString());
    //
    // 上面所有的都是为了算连个字符串的公共最长子序列(序列, 不是串), 还是重复计算了公共字符集, 不知道为啥
    //
    // 下面应该是算最长子序列在匹配串中"密度":
    // 对每个字, 计算他在匹配串中的分数, 出现一次算1分, 出现第i次的时候, 加上1/dis(i,i-1),
    // 其中dis(i, j)含义是在匹配串中字符距离.
    //
    // 这里貌似没有对浮点数的除法用对: 1/2在java里面会变成0

    int cl = comStr.length();
    double score = 0.0;
    int preJ = -1;
    for (int i = 0; i < cl; i++) {
      char iChar = comStr.charAt(i);
      double nowscore = 0.0;
      for (int j = preJ + 1; j < ml; j++) {
        char jChar = lowerMatchStr.charAt(j);
        if (iChar == jChar) {
          if (preJ == -1) {
            nowscore = 1.0;
            preJ = j;
            break;
          } else {
            double tempScore = (double)1 / (double)(j - preJ );
            nowscore += tempScore; // 要是紧邻, 则算一分, 否者零分???
            preJ = j;
            break;
          }
        }
      }
      score += nowscore;
    }

    double unionl = (double)(ql + ml - cl);
    double rlt = score / unionl;
    return rlt;
  }

  /**
   * 对模糊的中间结果按照密度方法进行重排序
   * 
   * @param midItem
   */
  public static void rescoreDensity(FuzzyMidItem midItem) {
    midItem.score_ = rescoreDensity(midItem.scoreQueryStr_,
        midItem.scoreMatchStr_);
  }

  public static void rescoreLR(FuzzyMidItem midItem) {
    // double w1 = 1.0;
    double mul = 100;
    // double minimum = 1/mul;
    double max = 100;
    double queryStrLen = (double) midItem.scoreQueryStr_.length();
    double objLen = (double) midItem.scoreMatchStr_.length();
    double delta = Math.abs(objLen - queryStrLen);
    String lowerQueryStr = midItem.scoreQueryStr_.toLowerCase();
    String lowerObjStr = midItem.scoreMatchStr_.toLowerCase();
    double base = (max - (double) FuzzyUtil.ld(lowerQueryStr, lowerObjStr));
    double res = base;
    midItem.score_ = Math.round(res);
  }
  
  public static void main(String[] args){

    String strA = "软件包开发商";
    
    String strB1 = "SimCreator 仿真软件包";
    String strB2 = "软件";
    String strB3 = "月成交量";
    String strB4 = "月最高价日";
    String strB5 = "月涨跌幅";
    
      double score1 = rescoreDensity(strA, strB1);
      double score2 = rescoreDensity(strA, strB2);
      double score3 = rescoreDensity(strA, strB3);
      double score4 = rescoreDensity(strA, strB4);
      double score5 = rescoreDensity(strA, strB5);
      
      System.out.println(strA);
      System.out.println( strB1 + ":" + String.valueOf(score1));
      System.out.println( strB2 + ":" + String.valueOf(score2));
      System.out.println( strB3 + ":" + String.valueOf(score3));
      System.out.println( strB4 + ":" + String.valueOf(score4));
      System.out.println( strB5 + ":" + String.valueOf(score5));
  }
  
}
