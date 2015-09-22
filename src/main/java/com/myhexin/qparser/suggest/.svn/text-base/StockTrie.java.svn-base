package com.myhexin.qparser.suggest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.HashTrie;
import com.myhexin.qparser.util.Util;

public class StockTrie {
  public static final Random RAND = new Random(System.nanoTime());
  public static final int MAX_RETURN = 10;
  public static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
      .getLogger(StockTrie.class);
  public static final int MIN_LEN = 1;// query should at least be this long,
                                      // else we skip it.
  HashTrie<StockCode> trie;
  static {
    // we do this for sake of security... but nothing is promised.
    // try "./conf/queans/index_in_common_use.txt"
    String file = "./conf/queans/" + Param.IFIND_INDEX_IN_COMMON_USE;
    File conf = new File(file);
    LOG.info("StockTrie we've got luck, load temple file done!: [{}]", file);
    if (conf.exists()) {
      try {
        ArrayList<String> content = Util.readTxtFile(file);
        QuerySuggest.loadIndexInCommonUse(content);
      } catch (DataConfException e) {
        LOG.error("StockTrie try load common index file failed: [{}]", e);
        e.printStackTrace();
      }
    }
    LOG.error("StockTrie we've got luck, load common index file done!: [{}]",
        file);
  }

  /**
   * create a Stock Trie from a file.
   * <p>
   * each line should have the following format:
   * code<space>name<space>spelling<space>XXXX...
   * 
   * we take everything, separated by blank space[s] after the "spelling" part
   * of the line , as an equation of the stock.
   * 
   * @param file
   */
  public StockTrie(String file) {
    this.initialize(file);
  }

  synchronized void initialize(String file) {
    HashTrie<StockCode> tmpTrie = new HashTrie<StockCode>();
    int lineCnt = 0;
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        ++lineCnt;
        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
          LOG.info("not well formatted line in stock list file: [{}]", line);
          continue;
        }
        StockCode code = new StockCode(parts[0], parts[1],
            parts.length > 2 ? parts[2] : null);
        for (String path : parts) {
          if (path != null && path.length() > 0) {
            tmpTrie.add(path, code);
          }
        }
      }
    } catch (IOException e) {
      LOG.error("Failed to init the StockTrie, exception: " + e);
      e.printStackTrace();
      tmpTrie = null;
    }
    if (tmpTrie != null) {
      this.trie = tmpTrie;
      LOG.info("StockTrie initialized with file: {}, total lines read: {}",
          file, lineCnt);
    }
  }

  public void updateTrie(String file) {
    this.initialize(file);
  }

  /**
   * get suggestions for stock code
   * 
   * @param path
   * @return
   */
  public List<String> getSuggestion(String path) {
    LOG.info("StockTrie::getSuggestion got query: [{}].", path);
    if (path.getBytes().length < MIN_LEN) {
      LOG.info(
          "StockTrie::getSuggestion query [{}] too short(less than {}), skip it.",
          path, MIN_LEN);
      return null;
    }
    List<StockCode> codes = this.trie.getNodeOrAllLeaf(path);
    if (codes == null) {
      LOG.debug("no node found for [{}]", path);
      return null;
    }
    HashSet<String> allcodes = new HashSet<String>();
    if (path.matches("^\\d+$")) {
      // stock code like 300033
      for (StockCode stock : codes) {
        allcodes.add(stock.code);
      }
    } else {
      for (StockCode stock : codes) {
        allcodes.add(stock.name);
      }
    }
    // get 10 at most stock common indexes
    List<String> commonIdxes = WebQuerySuggester
        .getRandomMaxSentCountCommonUseIndex();
    if (commonIdxes == null || commonIdxes.size() <= 0) {
      return null;
    }
    // form the suggestions here
    ArrayList<String> result = new ArrayList<String>(MAX_RETURN);
    int count = 0;
    for (String code : allcodes) {
      for (String index : commonIdxes) {
        ++count;
        if (result.size() < MAX_RETURN) {
          result.add(code + index);
        } else {
          int idx = RAND.nextInt(count);
          if (idx < MAX_RETURN) {
            result.set(idx, code + index);
          }
        }
      }
    }
    return result;
  }

  /**
   * stock code node
   * 
   * @author Steven Zhuang (zhuangxin8448@gmail.com)
   */
  public class StockCode {
    StockCode(String cc, String nn, String ss) {
      this.name = nn;
      this.spelling = ss;
      this.code = cc;
    }

    @Override
    public String toString() {
      return this.name + " " + this.code + " " + this.spelling;
    }

    String name;
    String code;
    String spelling;
  }
}
