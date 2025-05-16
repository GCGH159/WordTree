package com.slixils.v2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
// 定义 WordTree 类
@Slf4j
public class WordTree implements Serializable {

    private static final long serialVersionUID = 1L;
    private TrieNode root;

    /**
     * 获取根节点
     * @return 根节点
     */
    public TrieNode getRoot() {
        return root;
    }

    /**
     * WordTree 构造函数
     */
    public WordTree() {
        this.root = new TrieNode(null, null, null,null);
    }

    /**
     * 插入新单词及其翻译
     * @param newWord 新单词
     * @param translation 翻译
     * @param ushone 美式发音
     * @param ukphone 英式发音
     */
    public void insert(String newWord, String translation,String ushone,String ukphone) {
        TrieNode newNode = new TrieNode(newWord, translation,ushone,ukphone);

        List<TrieNode> nodes = getAllNodes();
        nodes.sort((n1, n2) -> {
            int len1 = n1.word != null ? n1.word.length() : 0;
            int len2 = n2.word != null ? n2.word.length() : 0;
            return -Integer.compare(len1, len2);
        });

        for (TrieNode node : nodes) {
            if (newWord.equals(node.word)) {
                log.error("单词已经存在！:{}-{}",newWord,translation);
//                throw new RuntimeException("单词已经存在！"+newWord+"-{"+translation+"}");
            }

            if (isSubword(node.word, newWord)) {
                TrieNode existingChild = node.findChild(newWord);
                if (existingChild == null) {
                    node.addChild(newNode);
                }
                newNode.addParent(node);
            } else if (isSubword(newWord, node.word)) {
                TrieNode existingChild = newNode.findChild(node.word);
                if (existingChild == null) {
                    newNode.addChild(node);
                }
                node.addParent(newNode);
            }
        }

        if (newNode.parents.isEmpty()) {
            TrieNode existingChild = root.findChild(newWord);
            if (existingChild == null) {
                root.addChild(newNode);
            }
        }
    }

    /**
     * 判断 w1 是否是 w2 的子串
     * @param w1 字符串1
     * @param w2 字符串2
     * @return 如果 w1 是 w2 的子串则返回 true，否则返回 false
     */
    private boolean isSubword(String w1, String w2) {
        if (w1 == null || w2 == null) {
            return false;
        }
        return w2.contains(w1);
    }

    /**
     * 获取所有节点
     * @return 所有节点的列表
     */
    private List<TrieNode> getAllNodes() {
        List<TrieNode> result = new ArrayList<>();
        dfs(root, result);
        return result;
    }

    /**
     * 深度优先搜索遍历节点
     * @param node 当前节点
     * @param result 结果列表
     */
    private void dfs(TrieNode node, List<TrieNode> result) {
        if (node != null) {
            result.add(node);
            for (TrieNode child : node.children) {
                dfs(child, result);
            }
        }
    }

    /**
     * 根据单词查找节点
     * @param word 要查找的单词
     * @return 找到的节点，否则返回 null
     */
    public TrieNode findNode(String word) {
        for (TrieNode node : getAllNodes()) {
            if (word.equals(node.word)) {
                return node;
            }
        }
        return null;  // 如果找不到该单词，返回 null
    }

    /**
     * 查询单词的父节点和子节点
     * @param word 要查询的单词
     */
    public void query(String word) {
        TrieNode node = findNode(word);
        if (node == null) {
            System.out.println("未找到单词: " + word);
            return;
        }

        System.out.println("查询单词: " + word);
        System.out.println("父节点: ");
        for (TrieNode parent : node.parents) {
            System.out.println("    " + parent.word + " - " + parent.translation);
        }

        System.out.println("子节点: ");
        for (TrieNode child : node.children) {
            System.out.println("    " + child.word + " - " + child.translation);
        }
    }

    /**
     * 将 WordTree 转换为格式化的 JSON 字符串
     * @return JSON 字符串
     */
    public String toPrettyJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(buildJson(root));
    }

    /**
     * 构建节点的 JSON 对象
     * @param node 当前节点
     * @return JSON 对象
     */
    private JsonObject buildJson(TrieNode node) {
        JsonObject jsonObject = new JsonObject();
        if (node.word != null) {
            jsonObject.addProperty("word", node.word);
            jsonObject.addProperty("translation", node.translation);
            jsonObject.addProperty("ushone", node.ushone);
            jsonObject.addProperty("ukphone", node.ukphone);
        }
        JsonArray childrenArray = new JsonArray();
        for (TrieNode child : node.children) {
            childrenArray.add(buildJson(child));
        }
        if (childrenArray.size() > 0) {
            jsonObject.add("children", childrenArray);
        }
        return jsonObject;
    }

    /**
     * 查询单词 (版本1)
     * @param word 要查询的单词
     */
    public void queryv1(String word) {
        List<String> parents = new ArrayList<>();
        TrieNode node = findNodev1(word, root, parents);
        if (node == null) {
            System.out.println("未找到单词: " + word);
            return;
        }

        // 打印父节点
        System.out.println("查询单词: " + word);
        System.out.println("父节点: ");
        for (String parent : parents) {
            System.out.println("    " + parent);
        }

        // 打印子节点
        System.out.println("子节点: ");
        printChildrenv1(node, 1);
    }

    /**
     * 打印子节点 (版本1)
     * @param node 当前节点
     * @param level 层级
     */
    /**
     * 递归打印所有子节点
     * @param node 当前节点
     * @param level 层级深度
     */
    private void printChildrenv1(TrieNode node, int level) {
        if (node.children.isEmpty()) {
            return;
        }

        for (TrieNode child : node.children) {
            System.out.println(indent(level) + child.word + " - " + child.translation);
            printChildrenv1(child, level + 1);
        }
    }

    /**
     * 生成缩进字符串
     * @param level 层级
     * @return 缩进字符串
     */
    private String indent(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("    ");
        }
        return indent.toString();
    }

    /**
     * 查找节点 (版本1)
     * @param word 要查找的单词
     * @param currentNode 当前节点
     * @param parents 父节点列表
     * @return 找到的节点，否则返回 null
     */
    public TrieNode findNodev1(String word, TrieNode currentNode, List<String> parents) {
        if (currentNode == null) {
            return null;
        }

        for (TrieNode child : currentNode.children) {
            if (word.equals(child.word)) {
                return child;  // 找到该节点，返回
            }

            // 递归查找子节点
            if (word.startsWith(child.word)) {
                parents.add(child.word + " - " + child.translation);
                TrieNode found = findNodev1(word, child, parents);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * 显示单词树
     */
    public void display() {
        display(root, 0);  // 从根节点开始，层级从0开始
    }

    /**
     * 递归显示节点
     * @param node 当前节点
     * @param level 层级
     */
    private void display(TrieNode node, int level) {
        StringBuilder indent = new StringBuilder();  // 用于存储缩进的字符串
        for (int i = 0; i < level; i++) {  // 根据节点的层级设置缩进
            indent.append("    ");  // 每个层级增加四个空格
        }
        if (node.word != null) {  // 如果当前节点的单词不为空
            // 打印当前节点的单词和翻译
            System.out.println(indent + node.word + " - " + node.translation);
        }
        for (TrieNode child : node.children) {  // 遍历当前节点的子节点
            display(child, level + 1);  // 递归调用 display 打印子节点，层级+1
        }
    }
    /**
     * 递归将节点转换为JSON格式的Map
     * @param includeParents 是否包含父节点信息
     * @param includeChildren 是否包含子节点信息
     * @return Map表示的JSON结构
     */


}