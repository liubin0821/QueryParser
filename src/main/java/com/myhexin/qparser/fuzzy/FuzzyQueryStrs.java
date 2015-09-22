package com.myhexin.qparser.fuzzy;

import java.util.ArrayList;

import com.myhexin.qparser.define.MiscDef;

public class FuzzyQueryStrs {
  private static final String LINE_SEPARTOR = System
      .getProperty("line.separator");
  private static final String LS = LINE_SEPARTOR;
  private static final String INDICATOR_BASE_SPARQL = MiscDef.SPARQL_PREFIX
      + LINE_SEPARTOR + " select   str(?lx) str(?scorex) " + LINE_SEPARTOR
      + " { " + LINE_SEPARTOR
      + "    (?lx ?scorex ) pf:poTextMatch(\"%s\" %s %s ). " + LINE_SEPARTOR
      + "    ?x rdfs:label ?lx." + LINE_SEPARTOR
      + "    ?x rdfs:subPropertyOf base:ifind_indicator." + LINE_SEPARTOR
      + " }" + LINE_SEPARTOR + " limit %s " + LINE_SEPARTOR + " offset %s "
      + LINE_SEPARTOR;

  private static final String OBJ_BASE_SPARQL = MiscDef.SPARQL_PREFIX
      + LINE_SEPARTOR + " select distinct (str(?lp) as ?llp) (str(?lx) as ?llx) (str(?scorex) as ?sc) "
      + LINE_SEPARTOR + " { " + LINE_SEPARTOR
      + "      (?lx ?scorex ) pf:poTextMatch (\"%s\" %s %s )." + LINE_SEPARTOR
      + "      ?x rdfs:label ?lx. " + LINE_SEPARTOR + "      ?s ?p ?x. "
      + LINE_SEPARTOR + "      ?s rdf:type base:Stock." + LINE_SEPARTOR
      + "      ?p rdfs:label ?lp." + LINE_SEPARTOR + " }" + LINE_SEPARTOR
      + " limit %s " + LINE_SEPARTOR + " offset %s " + LINE_SEPARTOR;

  public static String getSearchIndicatorSparql(String queryStr, double score,
      int maxMatches, int limit, int offset) {
    return String.format(INDICATOR_BASE_SPARQL, queryStr, score, maxMatches,
        limit, offset);
  }

  public static String getSearchIndByObjSparql(String queryStr, double score,
      int maxMatches, int limit, int offset) {
    return String.format(OBJ_BASE_SPARQL, queryStr, score, maxMatches, limit,
        offset);
  }

  /**
   * 仅使用一个字符串拼装查询Sparql的函数 在返回的每一行中，如果?lp为空，则?lx存放指标的label；如果?lp非空
   * ?lp存放的是指标的label，而?lx存放的是实例的label；?scorex 永远存放匹配的label和查询的label在larq中的匹配程度
   * 
   * @param str
   *          要模糊查询的字符串
   * @param scroe
   *          larq中对label匹配度的限定分值 ：当前暂时不使用
   * @param maxMatches
   *          对于一个label，最大的匹配数
   * @param limit
   *          对于整个的sparql所返回的所有元组数限制
   * @param offset
   *          对于整个sparql返回的所有元组的偏离度
   * @return 返回构建完成的sparql
   */
  public static String getSearchSparqlBySingleEntity(final String str,
      final double scroe, final int maxMatches, final int limit,
      final int offset) {
    if (str == null || str.trim() == "") {
      return "";
    }

    String sparqlStr = "";
    // String spaceStr = FuzzyAnalysis.stringWithSpace( str );//真正用于查询的字符串
    StringBuilder sb = new StringBuilder();
    sb.append(MiscDef.SPARQL_PREFIX);
    sb.append("select distinct (str(?lp) as ?llp) (str(?lx) as ?llx) (str(?scorex) as ?sc)");
    sb.append("where{");
    sb.append(LS);
    sb.append(String.format("(?lx ?scorex) pf:poTextMatch( \"%s\" %s ). ", str,
        maxMatches));
    sb.append(LS);
    sb.append("?x rdfs:label ?lx.optional{?s ?p ?x.?p rdfs:label ?lp.}optional{?x rdfs:domain base:Stock.}" +
    		"?s a base:Stock.}");	//tao added
    sb.append(LS);
    sb.append(String.format("limit %s %s offset %s %s", limit, LS, offset, LS));
    sparqlStr = sb.toString();
    return sparqlStr;
  }

  /**
   * 没有score参数的生成sparql语句的函数
   * @param str
   *     要模糊查询的字符串
   * @param maxMatches
   *     对于一个label，最大的匹配数
   * @param limit
   *     对于整个的sparql所返回的所有元组数限制
   * @param offset
   *     对于整个sparql返回的所有元组的偏离度
   * @return
   *     返回构建完成的sparql
   */
//  public static String getSearchSparqlBySingleEntity(final String str,
//          final int maxMatches, final int limit, final int offset){
//      if(str == null || str.trim() == ""){
//          return "";
//      }
//      
//      StringBuilder selectSB = new StringBuilder();
//      selectSB.append(MiscDef.SPARQL_PREFIX);
//      
//  }
  
  public static String getSearchSparql(String strs, double score,
      int maxMatches, int limit, int offset) {
    if (strs == null || strs == "") {
      return "";
    }
    String trimStrs = strs.trim();
    if (trimStrs == "") {
      return "";
    }

    StringBuilder selectSB = new StringBuilder();
    selectSB.append(MiscDef.SPARQL_PREFIX);
    selectSB.append("select distinct (str(?lp) as ?llp) (str(?lx) as ?llx) (str(?scorex) as ?sc)" + LS);
    selectSB.append("where{" + LS);

    String[] objects = trimStrs.split(" ");

    // 存放用于查询instance的String
    ArrayList<String> candidate = new ArrayList<String>();
    for (int i = 0; i < objects.length; i++) {
      if (objects[i].replaceAll("\\s", "").length() == 0) {
        continue;
      }
      if (candidate.size() > 0) {
        selectSB.append("union" + LS);
      }
      candidate.add(objects[i]);
      // String searchStr = FuzzyAnalysis.stringWithSpace( objects[i] );
      selectSB.append("{" + LS);
      selectSB.append(String.format(
          "(?lx ?scorex) pf:poTextMatch( \"%s\" %s %s  )." + LS, objects[i],
          score, maxMatches));
      selectSB.append("?x rdfs:label ?lx." + LS);
      selectSB.append("?s ?p ?x." + LS);
      selectSB.append("?s rdf:type base:Stock." + LS);
      selectSB.append("?p rdfs:label ?lp." + LS);
      selectSB.append("}" + LS);
    }

    // String searchStr = FuzzyAnalysis.stringWithSpace( trimStrs );
    if (!candidate.contains(trimStrs)) {
      selectSB.append("union" + LS);
      selectSB.append("{" + LS);
      selectSB.append(String.format("(?x ?scorex) pf:poTextMatch(\"%s\" %s %s)."
          + LS, trimStrs, score, maxMatches));
      selectSB.append("?x rdfs:label ?lx." + LS);
      selectSB.append("?s ?p ?x." + LS);
      selectSB.append("?s rdf:type base:Stock." + LS);
      selectSB.append("?p rdfs:label ?lp." + LS);
      selectSB.append("}" + LS);
    }

    // 添加查询property部分，对于proerty，我们要将字符串全都拼接起来查
    selectSB.append("union" + LS);
    selectSB.append("{" + LS);
    selectSB.append(String.format(
        "(?lx ?scorex) pf:poTextMatch( \"%s\" %s %s  ).", trimStrs, score,
        maxMatches)
        + LS);
    selectSB.append("?x rdfs:label ?lx." + LS);
    selectSB.append("?x rdfs:domain base:stock." + LS);
    selectSB.append("}" + LS);
    selectSB.append("}" + LS);
    selectSB.append(String.format("limit %s" + LS, limit));
    selectSB.append(String.format("offset %s" + LS, offset));

    return selectSB.toString();
  }

  public static void main(String args[]) {
    System.out.println(FuzzyQueryStrs.getSearchIndicatorSparql("市 净 率", 0.8d,
        100, 100, 0));
    System.out.println(FuzzyQueryStrs.getSearchIndByObjSparql("石 墨 烯 股 东",
        0.8d, 100, 100, 0));
  }
}
