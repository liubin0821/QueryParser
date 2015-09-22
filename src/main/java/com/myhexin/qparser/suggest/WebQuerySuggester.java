package com.myhexin.qparser.suggest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import com.myhexin.qparser.node.*;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.fuzzy.FuzzyUtil;

public class WebQuerySuggester {
  private ArrayList<SentNode> list_;
  private HashMap<String, ArrayList<SentNode>> i2sMap_;
  private WebTempCreater tempCreater_;
  // 这个映射表用来存放不同长度的查询匹配模板所能够接受的分值
  // key是模糊查询的temple长度,value是该长度所能对应的接受分值
  public static HashMap<Integer, Double> acceptMap_ = new HashMap<Integer, Double>();
  public static double defaultAcceptScore_ = 0;// 默认的可接受分值
  public static final int MAX_SENT_COUNT = 10;

  private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
      .getLogger(QuerySuggest.class.getName());

  static {
    // acceptMap_.put( 1, 1000.0);
    // acceptMap_.put( 2, 443.0);
    // acceptMap_.put( 3, 411.0);
    // acceptMap_.put( 4, 411.0);
    // acceptMap_.put(5, 374.0);
    // acceptMap_.put(6, 332.0);
    acceptMap_.put(1, 0.0);
    acceptMap_.put(2, 0.0);
    acceptMap_.put(3, 0.0);
    acceptMap_.put(4, 0.0);
    acceptMap_.put(5, 0.0);
    acceptMap_.put(6, 0.0);

  }

  public WebQuerySuggester(final ArrayList<SentNode> list,
      final HashMap<String, ArrayList<SentNode>> i2sMap,
      final WebTempCreater tempCreater) {
    list_ = list;
    i2sMap_ = i2sMap;
    tempCreater_ = tempCreater;
  }

  // 问句推荐的主要逻辑
  private ArrayList<String> makeSuggestBody(final Query query) {
    String queryStr = query.text;
    ArrayList<String> res = new ArrayList<String>();
    // 首先产生问句模板
    String templeStr = tempCreater_.createTempStr(queryStr);
    // 用来放随机问句的list
    ArrayList<SentNode> rdmList = new ArrayList<SentNode>();
    // 根据问句模板进行打分
    ArrayList<SentNode> candList = fuzzyFilt(templeStr,
        new ArrayList<SentNode>(), rdmList);
    // 根据打分进行排序
    SentNodeSortByScoreCom comparator = new SentNodeSortByScoreCom();
    Collections.sort(candList, comparator);
    // 进行去重
    candList = suggestListDE(candList, query);
    // 构建输出
    Iterator<SentNode> iterator = candList.iterator();
    SentNode sentNode = null;
    String suggStr = null;
    while (iterator.hasNext()) {
      sentNode = iterator.next();
      if (sentNode.sentence_.equals(queryStr)) {
        continue;
      }
      suggStr = sentNode.sentence_;
      res.add(suggStr);
      if (res.size() < MAX_SENT_COUNT && iterator.hasNext()) {
        // continue
      } else {
        break;
      }
    }
    // 如果最后选出的问句不足10个的话，我们凑足10个问句
    // 首先我们生成10不重复的随机数
    if (res.size() < MAX_SENT_COUNT) {
      for (SentNode node : rdmList) {
        res.add(node.sentence_);
        if (res.size() >= MAX_SENT_COUNT) {
          break;
        }
      }
    }
    return res;
  }

  /**
   * 推荐问句的统一入口函数，从这开始，将来我们会根据不同的query类型进行不同的扩展与推荐
   * 
   * @param query
   *          问句解析的结果
   * @return
   */
  public ArrayList<String> makeSuggest(Query query) {
    // 如果纯粹是股票，我们调用股票问句推荐扩展
    if ((query.getNodes().size() == 1) && NodeUtil.isStockNode(query.getNodes().get(0))) {
      return makeSuggestExpendStock(query);
    }
    if (containIndex(query)) {
      return makeSuggestByIndexGroup(query);
    } else {
      return makeSuggestBody(query);
    }
  }

  /**
   * 根据输入的query，找到其中所包含的指标， 在找到与所包含指标相关的指标，再根据相关指标找到其对应问句，推荐出来
   * 
   * @param query
   * @return
   */
  private ArrayList<String> makeSuggestByIndexGroup(Query query) {
    int rdmCount = 1;// 每一个相关指标随机的出现的问句的个数
    ArrayList<String> rlt = new ArrayList<String>();
    ArrayList<String> sentIndexList = new ArrayList<String>();
    // 获取query中所包含的所有指标
    for (SemanticNode node : query.getNodes()) {
      if (NodeType.CLASS == node.type) {
        sentIndexList.add(node.getText());
      }
    }
    HashSet<String> relIndexSet = new HashSet<String>();
    // 根据query包含指标获取相关指标
    for (String istr : sentIndexList) {
      if (QuerySuggest.indexInCommonUseMap.containsKey(istr)) {
        relIndexSet.addAll(QuerySuggest.indexInCommonUseMap.get(istr));
      }
    }
    // 根据相关指标找到相关问句
    Iterator<String> si = relIndexSet.iterator();
    while (si.hasNext()) {
      // 根据相关指标名称获取问句集，随机向rlt中添加句子
      addRdmRelSent(rlt, si.next(), rdmCount);
    }
    // 如果不足10个相关问句，我们还是需要随机补足
    if (rlt.size() < MAX_SENT_COUNT) {
      rdmComplete(rlt);
    }
    return rlt;
  }

  /**
   * 随机补足推荐问句
   * 
   * @param rlt
   */
  private void rdmComplete(ArrayList<String> rlt) {
    int dCount = MAX_SENT_COUNT - rlt.size();
    HashSet<Integer> pos = new HashSet<Integer>();
    // 需要补足的问句数多于推荐问句总数，这种情况应该不会发生
    if (dCount > list_.size()) {
      return;
    }
    Random rdm = new Random();
    while (pos.size() < dCount) {
      int ri = rdm.nextInt(list_.size());
      // 要避免重复
      if (rlt.contains(list_.get(ri).sentence_)) {
        continue;
      }
      pos.add(ri);
    }
    Iterator<Integer> it = pos.iterator();
    while (it.hasNext()) {
      rlt.add(list_.get(it.next()).sentence_);
    }
  }

  /**
   * 根据传入的随机个数
   * 
   * @param rlt
   *          ：需要存放推荐问句的list
   * @param si
   *          ：指标名称
   * @param rdmCount
   *          ：需要获取的随机问句个数
   * @throws UnexpectedException
   */
  private void addRdmRelSent(ArrayList<String> rlt, String si, int rdmCount) {
    Random rdm = new Random();
    // 获取包含指标 si 的推荐问句列表
    ArrayList<SentNode> slist = i2sMap_.get(si);
    if (slist == null) {
      logger_.error("未在指标问句映射map中找到：%s", si);
      return;
    }
    // 如果获得的问句列表长度小于需随机出来的问句个数，直接填充
    if (slist.size() <= rdmCount) {
      for (SentNode node : slist) {
        rlt.add(node.sentence_);
      }
      return;
    }

    // 从si对应的推荐问句列表当中随机挑选rdmCount个
    int[] rdmPosArray = new int[slist.size()];
    for (int i = 0; i < rdmPosArray.length; i++) {
      rdmPosArray[i] = i;
    }
    int j = 0;
    int temp = 0;
    for (int i = 0; i < rdmCount; i++) {
      j = rdm.nextInt(rdmCount);
      temp = rdmPosArray[j];
      rdmPosArray[j] = rdmPosArray[i];
      rdmPosArray[i] = temp;
    }

    for (int i = 0; i < rdmCount; i++) {
      rlt.add(slist.get(rdmPosArray[i]).sentence_);
    }
  }

  /**
   * 判断是否可以使用相关指标进行问句推荐 需满足： 1.问句中有指标
   * 2.问句中的指标必须在QuerySuggest.indexInCommonUseMap中作为key
   * 
   * @param query
   * @return
   */
  private boolean containIndex(Query query) {
    for (SemanticNode node : query.getNodes()) {
      if (NodeType.CLASS == node.type
          && QuerySuggest.indexInCommonUseMap.containsKey(node.getText())) {
        return true;
      }
    }
    return false;
  }

  /**
   * 对输入的推荐问句进行去重
   * 
   * @param list
   * @return
   */
  private ArrayList<SentNode> suggestListDE(ArrayList<SentNode> list,
      final Query query) {
    ArrayList<SentNode> rltList = list;
    // 我们根据pattern模板进行去重
    rltList = suggestListSimiDE(rltList, query);
    return rltList;
  }

  /**
   * 根据问句解析所产生的pattern来进行去重
   * 
   * @param list
   * @param query
   * @return
   */
  private ArrayList<SentNode> suggestListSimiDE(ArrayList<SentNode> list,
      Query query) {
    ArrayList<SentNode> rltList = new ArrayList<SentNode>();
    HashSet<String> existPatternTemp = new HashSet<String>();
    String qtmp = WebTempCreater.createTempPatternBuyQuery(query);
    existPatternTemp.add(qtmp);
    for (SentNode node : list) {
      if (!existPatternTemp.contains(node.patternTemple_)) {
        existPatternTemp.add(node.patternTemple_);
        rltList.add(node);
      }
    }
    return rltList;
  }

  public static ArrayList<String> getRandomMaxSentCountCommonUseIndex() {
    ArrayList<String> res = new ArrayList<String>();
    Random random = new Random();
    int groupCountOfIndexInCommonUse = QuerySuggest.indexInCommonUse.size();
    // 用来存放已经遇到的指标组ID，如果全都遇到了都没有凑够10个问句，仍然要退出循环
    // 获取一个随机的指标组ID的数组，我们采用Knuth的随机打乱数组的方式
    int[] groupIDArray = new int[groupCountOfIndexInCommonUse];
    for (int i = 0; i < groupCountOfIndexInCommonUse; i++) {
      groupIDArray[i] = i;
    }
    int j = 0;
    int temp = 0;
    for (int i = 0; i < groupCountOfIndexInCommonUse; i++) {
      j = random.nextInt(groupCountOfIndexInCommonUse);
      temp = groupIDArray[j];
      groupIDArray[j] = groupIDArray[i];
      groupIDArray[i] = temp;
    }
    ArrayList<String> nowGroupList = null;
    for (int k = 0; (k < groupIDArray.length) && (res.size() < MAX_SENT_COUNT); k++) {
      nowGroupList = QuerySuggest.indexInCommonUse.get(groupIDArray[k]);
      for (String line : nowGroupList) {
        res.add(line);
        if (res.size() >= MAX_SENT_COUNT) {
          break;
        }
      }
    }
    return res;
  }

  /**
   * 如果用户输入的是纯粹的股票名称或者股票代码的话 我们随机的从常用候选指标集当中挑选出常用指标，和其拼接，用于扩展
   * 
   * @param query
   * @return
   */
  private ArrayList<String> makeSuggestExpendStock(Query query) {
    ArrayList<String> res = new ArrayList<String>();
    // 在此处的query应该纯粹的是股票名称或股票代码
    String stockCodeOrName = query.text;
    ArrayList<String> indexList = getRandomMaxSentCountCommonUseIndex();
    if ((null == indexList) || indexList.isEmpty()) {
      return res;
    }
    Iterator<String> iterator = indexList.iterator();
    String newSentence = null;
    while (iterator.hasNext()) {
      newSentence = stockCodeOrName + iterator.next();
      res.add(newSentence);
    }
    return res;
  }

  /**
   * 顺序遍历所有的问句，并为每个问句打分，如果分值大于阈值，则将其放入candList中去
   * 为了补足问句个数，我们在遍历的过程当中随机挑选出10不在candList中的问句，放在randomList中
   * 
   * @param templeStr
   *          需要进行匹配的字符串
   * @param candList
   *          候选问句列表
   * @param randomList
   *          随机问句列表
   * @return
   */
  private ArrayList<SentNode> fuzzyFilt(final String templeStr,
      ArrayList<SentNode> candList, ArrayList<SentNode> randomList) {
    Iterator<SentNode> iterator = list_.iterator();
    SentNode nowNode = null;
    SentNode newNode = null;
    HashSet<String> existTempleStrSet = new HashSet<String>();

    // 由于问句个数比较多，如果再使用打乱数组的方式来进行随机数获取，则会连续申请创建过多内存
    // 如果虚拟机来不及释放，则可能会导致内存溢出，所以我们采用多次尝试的方法凑够两倍MAX_SENT_COUNT
    HashSet<Integer> randomPositionSet = new HashSet<Integer>();
    int rdmJ = 0;
    Random random = new Random();
    int randomCount = 2 * MAX_SENT_COUNT;
    int sentenceCount = list_.size();
    // 一般循环次数会远少于所有问句个数次的
    for (int i = 0; i < sentenceCount; i++) {
      rdmJ = random.nextInt(sentenceCount);
      randomPositionSet.add(rdmJ);
      if (randomPositionSet.size() >= randomCount) {
        break;
      }
    }

    int i = 0;
    while (iterator.hasNext()) {
      nowNode = iterator.next();
      String nowTempStr = nowNode.temple_;

      double score = densityScore(templeStr, nowTempStr);

      double cmpScore = defaultAcceptScore_;
      if (acceptMap_.containsKey(templeStr.length())) {
        cmpScore = acceptMap_.get(templeStr.length());
      }

      boolean scoreOK = score > cmpScore;
      boolean randomOK = randomPositionSet.contains(i);
      if (scoreOK || randomOK) {
        if (existTempleStrSet.contains(nowNode.temple_)) {
          continue;
        }
        newNode = nowNode.clone();
        newNode.score_ = score;
        existTempleStrSet.add(nowNode.temple_);
        if (scoreOK) {
          candList.add(newNode);
        } else {
          randomList.add(newNode);
        }
      }
      i++;
    }
    return candList;
  }

  /**
   * 问句密度打分函数 如果输入非法参数，返回负值
   * 
   * @param queryStr
   * @param matchStr
   * @return
   */
  private double densityScore(final String queryStr, final String matchStr) {
    if (queryStr == null || matchStr == null) {
      return -1.0;
    }
    int ql = queryStr.length();
    int ml = matchStr.length();
    if (ql + ml == 0) {
      return -1.0;
    }

    HashSet<Character> querySet = new HashSet<Character>();
    HashSet<Character> comSet = new HashSet<Character>();

    // 首先将查询query的string中每一个字放入查询字符集
    for (int i = 0; i < ql; i++) {
      querySet.add(queryStr.charAt(i));
    }
    // 然后遍历要匹配的字符串，找到queryStr和matchStr中的公共字符放入comSet中
    for (int i = 0; i < ml; i++) {
      char iChar = matchStr.charAt(i);
      if (querySet.contains(iChar)) {
        comSet.add(iChar);
      }
    }
    // 再次遍历查询queryStr字符串，将所有在comSet中的字符按在queryStr中的顺序排列放入qSB
    StringBuilder qSB = new StringBuilder();
    for (int i = 0; i < ql; i++) {
      char iChar = queryStr.charAt(i);
      if (comSet.contains(iChar)) {
        qSB.append(iChar);
      }
    }
    // 再次遍历查询matchStr字符串，将所有在comSet中的字符按在matchStr中的顺序排列放入mSB
    StringBuilder mSB = new StringBuilder();
    for (int i = 0; i < ml; i++) {
      char iChar = matchStr.charAt(i);
      if (comSet.contains(iChar)) {
        mSB.append(iChar);
      }
    }
    // 调用最大公共子串计算函数，计算qSB和mSB的最大公共子串
    String comStr = FuzzyUtil.LCS(qSB.toString(), mSB.toString());
    int cl = comStr.length();
    double score = 0.0;
    // 查找每个comStr的字符在matchStr中的位置，并打分
    int preJ = -1;
    for (int i = 0; i < cl; i++) {
      char iChar = comStr.charAt(i);
      double nowscore = 0.0;
      // 计算每一个comStr字符与其后出现comStr字符的分值
      for (int j = preJ + 1; j < ml; j++) {
        char jChar = matchStr.charAt(j);
        if (iChar == jChar) {
          if (preJ == -1) {
            // 如果是第一个出现的comStr字符，默认分值为1.0
            nowscore = 1.0;
            preJ = j;
            break;
          } else {
            // 累加两个相邻comStr字符分值为该两字符
            nowscore += (double) 1 / (double) (j - preJ);
            preJ = j;
            break;
          }
        }
      }
      // 将当前的comStr的字符分值累加到最终的分值上去
      score += nowscore;
    }

    int mul = 1000;
    int unionl = ql + ml - cl;
    // 使用unionl尽量将分值归一化
    double resScore = Math.round(mul * score / unionl);
    return resScore;
  }
}
