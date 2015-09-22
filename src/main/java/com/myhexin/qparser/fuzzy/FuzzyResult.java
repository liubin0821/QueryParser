package com.myhexin.qparser.fuzzy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class FuzzyResult {
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
      .getLogger(FuzzySearcher.class);

  private static SortByScore sortByScore = new SortByScore();

  private ArrayList<FuzzyItem> fuzzy_item_list_;

  public static FuzzyResult mergeFuzzyResult(ArrayList<FuzzyResult> results) {
    FuzzyResult res = new FuzzyResult();
    Iterator<FuzzyResult> iterator = results.iterator();
    while (iterator.hasNext()) {
      res.addResult(iterator.next());
    }
    return res;
  }

  public void fill(FuzzyMidResult fuzzyMidResult) {
    HashMap<String, FuzzyItem> fuzzyItemMap = new HashMap<String, FuzzyItem>();
    FuzzyMidItem fuzzyMidItem = null;
    FuzzyItem nowFuzzyItem = null;
    for (int i = 0; i < fuzzyMidResult.midResult_.size(); i++) {
      fuzzyMidItem = fuzzyMidResult.midResult_.get(i);
      String fuzzyItemKey = fuzzyMidItem.propName_
          + Double.toString(fuzzyMidItem.score_);
      if (!fuzzyItemMap.containsKey(fuzzyItemKey)) {
        FuzzyItem newFuzzyItem = new FuzzyItem();
        newFuzzyItem.score = fuzzyMidItem.score_;
        newFuzzyItem.content = fuzzyMidItem.propName_;
        newFuzzyItem.userQuery = fuzzyMidItem.queryStr_;
        newFuzzyItem.type = fuzzyMidItem.type_;
        fuzzyItemMap.put(fuzzyItemKey, newFuzzyItem);
        nowFuzzyItem = newFuzzyItem;
      } else {
        nowFuzzyItem = fuzzyItemMap.get(fuzzyItemKey);
      }

      if (nowFuzzyItem.score == fuzzyMidItem.score_) {
        nowFuzzyItem.addToFuzzyResult(fuzzyMidItem.objName_);
      }
    }
    Iterator<Entry<String, FuzzyItem>> iterator = fuzzyItemMap.entrySet()
        .iterator();
    while (iterator.hasNext()) {
      fuzzy_item_list_.add(iterator.next().getValue());
    }
  }

  public FuzzyResult() {
    fuzzy_item_list_ = new ArrayList<FuzzyItem>();
  }

  public FuzzyItem getItem(int pos) {
    return fuzzy_item_list_.get(pos);
  }

  public ArrayList<FuzzyItem> getList() {
    return fuzzy_item_list_;
  }

  public void addItem(FuzzyItem item) {
    fuzzy_item_list_.add(item);
  }

  public void addResult(FuzzyResult source) {
    fuzzy_item_list_.addAll(source.getList());
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fuzzy_item_list_.size(); i++) {
      sb.append(fuzzy_item_list_.get(i).toString());
      sb.append("\n");
    }
    return sb.toString();
  }

  public int size() {
    return fuzzy_item_list_.size();
  }

  /**
   * remove duplicated index-labels in fuzzy item list
   * 
   */
  public void unique() {
    if (this.fuzzy_item_list_ == null) {
      return;
    }
    HashSet<String> knownLabels = new HashSet<String>();
    for (Iterator<FuzzyItem> iterator = this.fuzzy_item_list_.iterator(); iterator
        .hasNext();) {
      FuzzyItem item = iterator.next();
      if (item.content != null) {
        // if it's an index, check if there is already a same index in result
        if (knownLabels.contains(item.content)) {
          iterator.remove();// skip this item
        } else {
          knownLabels.add(item.content);
        }
      }
    }
  }

  public void sort(boolean preSort) {
    Collections.sort(fuzzy_item_list_, sortByScore);
    if (!preSort) {
      for (int i = 0; i < fuzzy_item_list_.size(); i++) {
        fuzzy_item_list_.get(i).calAndSort();
      }
    }
  }
}
