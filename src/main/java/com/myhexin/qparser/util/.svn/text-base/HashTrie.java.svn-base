package com.myhexin.qparser.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class HashTrie<T> {
  Node<T> root = new Node<T>();

  /**
   * add new path string into current tree.
   * 
   * @param path
   */
  public void add(String path, T value) {
    Node<T> cur = this.root;
    for (char ch : path.toCharArray()) {
      cur = cur.addChild(ch);
    }
    cur.addValue(value);
  }

  /**
   * find value of given path, if none attached to the node, return all that
   * under it
   */
  public List<T> getNodeOrAllLeaf(String path) {
    Node<T> cur = this.root;
    for (char ch : path.toCharArray()) {
      cur = cur.advance(ch);
      if (cur == null) {
        return null;
      }
    }
    List<T> ret = cur.getValue();
    if (ret != null && ret.size() > 0) {
      return ret;
    }
    return cur.getAllLeaf();
  }

  /**
   * find all values under a given path
   */
  public List<T> getAllLeaf(String path) {
    Node<T> cur = this.root;
    for (char ch : path.toCharArray()) {
      cur = cur.advance(ch);
      if (cur == null) {
        return null;
      }
    }
    return cur.getAllLeaf();
  }

  /**
   * tree node
   * 
   * @author Steven Zhuang (zhuangxin8448@gmail.com)
   * 
   * @param <T>
   */

  class Node<T> {
    HashMap<Character, Node<T>> children;
    List<T> values;

    /**
     * return values attached to this node
     * 
     * @return
     */
    public List<T> getValue() {
      return this.values;
    }

    /**
     * all values attached to current sub tree
     * 
     * @return
     */
    public List<T> getAllLeaf() {
      LinkedList<T> ret = null;
      if (this.values != null) {
        ret = new LinkedList<T>();
        ret.addAll(this.values);
      }
      if (this.children != null) {
        ret = new LinkedList<T>();
        for (Node<T> node : this.children.values()) {
          List<T> vv = node.getAllLeaf();
          if (vv != null) {
            ret.addAll(vv);
          }
        }
      }
      return ret;
    }

    /**
     * set node flag
     */
    void addValue(T value) {
      if (this.values == null) {
        this.values = new LinkedList<T>();
      }
      this.values.add(value);
    }

    /**
     * get the speciifed child, null if there is none
     * 
     * @param ch
     * @return
     */
    public Node<T> advance(Character ch) {
      if (this.children != null) {
        return this.children.get(ch);
      }
      return null;
    }

    /**
     * get the child specified by the character.
     * 
     * @param ch
     * @return
     */
    public Node<T> addChild(Character ch) {
      if (this.children == null) {
        children = new HashMap<Character, Node<T>>();
      }
      // get the child
      Node<T> node = this.children.get(ch);
      if (node == null) {
        node = new Node<T>();
        this.children.put(ch, node);
      }
      return node;
    }
  }
}
