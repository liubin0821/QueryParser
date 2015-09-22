package com.myhexin.qparser.phrase.util;

import java.util.HashSet;
import java.util.Set;

public class NodeWeightLevel {

  public static Set<String> StrNodeSubType = new HashSet<String>();
  static {
    StrNodeSubType.add("_股票简称");
    StrNodeSubType.add("_股票代码");
    StrNodeSubType.add("_基金简称");
    StrNodeSubType.add("_基金代码");
    StrNodeSubType.add("_b股");
    StrNodeSubType.add("_a股");
    StrNodeSubType.add("ASTOCK");
    StrNodeSubType.add("BSTOCK");
    StrNodeSubType.add("NEWTB");
    StrNodeSubType.add("HKSTOCK");
    StrNodeSubType.add("USSTOCK");
    StrNodeSubType.add("_港股");
    StrNodeSubType.add("_美股");
    StrNodeSubType.add("_私募");
    StrNodeSubType.add("_保险");
    StrNodeSubType.add("_银行");
    StrNodeSubType.add("_交易所");
    StrNodeSubType.add("_券商");
    StrNodeSubType.add("_信托");
    StrNodeSubType.add("_信托产品");
    StrNodeSubType.add("_理财产品");
    StrNodeSubType.add("_政府机构");
    StrNodeSubType.add("_泛概念");
    StrNodeSubType.add("_所属概念");
    StrNodeSubType.add("_主营产品名称");
    StrNodeSubType.add("_泛产品");
    StrNodeSubType.add("_所属申万行业");
    StrNodeSubType.add("_国家主席");
    StrNodeSubType.add("_国家");
    StrNodeSubType.add("_国家和地区");
    StrNodeSubType.add("_姓名");
  }
  
  public final static int USELESS_LEVEL = 0;
  public final static int LOW_LEVEL = 1;
  public final static int MIDUM_LEVEL = 2;
  public final static int HIGH_LEVEL = 3;
  public final static int KEY_LEVEL = 4;
  
}
