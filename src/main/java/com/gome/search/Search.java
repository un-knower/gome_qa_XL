package com.gome.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiang-ds3 on 2016/9/7.
 */
public class Search {

    // 跟节点
    private Node root = new Node();
    // 生成fail,遍历用
    private List<String> keywords = new ArrayList<String>();

    /**
     * 添加关键词，生成一个查找树
     *
     * 1. 字典树的构造
     * @param keyword
     */
    public void add(String keyword) {
        if (keyword == null|| keyword.length() == 0) {
            return;
        }
        keywords.add(keyword);
        Node currNode = root;
        for (int i = 0, j = keyword.length(); i < j; i++) {
            char c = keyword.charAt(i);
            String value = String.valueOf(c);
            if (currNode.getSons().containsKey(value)) {
                Node selectNode = currNode.getSons().get(value);
                if (i == j - 1) {
                    selectNode.setOut(true);
                }
                currNode = selectNode;
            } else {
                Node sNode = new Node();
                sNode.setVal(value);
                sNode.setParent(currNode);
                currNode.addSon(value, sNode);
                if (i == j - 1) {
                    sNode.setOut(true);
                }
                currNode = sNode;
            }
        }

    }

    /**
     * 失败链的构造
     */
    public void build() {

        root.setFail(root);
        for (String keyword : keywords) {
            // 最大后缀表示
            Node fail = root;
            String prefix = "";
            for (char c : keyword.toCharArray()) {
                prefix += c;
                Node currNode = searchNode(prefix);
                String sval = String.valueOf(c);
                if (fail.getSons().containsKey(sval)) {
                    if (fail.getSons().get(sval) != currNode) {
                        fail = fail.getSons().get(sval);
                        currNode.setFail(fail);
                    } else {
                        fail = root;
                        currNode.setFail(fail);
                    }

                } else {
                    // 查找后缀，匹配最大
                    boolean hasfound = false;
                    for (int i = 1; i < prefix.length(); i++) {
                        String suffix = prefix.substring(i);
                        Node sufNode = searchNode(suffix);
                        if (sufNode != null) {
                            currNode.setFail(sufNode);
                            fail = sufNode;
                            hasfound = true;
                            break;
                        }
                    }
                    if (!hasfound) {
                        currNode.setFail(root);
                        fail = root;
                    }
                }
            }
        }
    }

    /**
     * 根据字符串 查找node
     *
     * @param keyword
     * @return
     */
    private Node searchNode(String keyword) {

        if (keyword.length() == 1) {
            return root.getSons().get(keyword);
        } else {
            Node tree = root.getSons().get(keyword.substring(0, 1));
            for (int i = 1, j = keyword.length(); i < j; i++) {
                char c = keyword.charAt(i);
                String value = String.valueOf(c);

                if (tree != null) {
                    if (tree.getSons().containsKey(value)) {
                        tree = tree.getSons().get(value);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }

            }
            return tree;
        }
    }

    // 查找关键词
    public List<String> search(String exp) {
        List<String> levelSet = new ArrayList<String>();
        Node pathNode = root;
        for (int i = 0, j = exp.length(); i < j; i++) {
            char c = exp.charAt(i);
            String value = String.valueOf(c);
            if (pathNode.getSons().containsKey(value)) {
                pathNode = pathNode.getSons().get(value);
                if (pathNode.isOut()) {
                    levelSet.add(pathNode.toString());
                }
            }
            else {
                do {
                    if (pathNode.isOut()) {
                        levelSet.add(pathNode.toString());
                    }
                    if (pathNode.getSons().containsKey(value)) {
                        pathNode = pathNode.getSons().get(value);
                        if (pathNode.isOut()) {
                            levelSet.add(pathNode.toString());
                        }
                        break;
                    }
                } while ((pathNode = pathNode.getFail()) != root);

                if (pathNode == root) {
                    if (pathNode.getSons().containsKey(value)) {
                        pathNode = pathNode.getSons().get(value);
                        if (pathNode.isOut()) {
                            levelSet.add(pathNode.toString());
                        }
                    }
                }
            }
        }
        return levelSet;
    }

    public Node getRoot() {
        return root;
    }
}
