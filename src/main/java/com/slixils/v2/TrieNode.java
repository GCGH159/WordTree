package com.slixils.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.*;

// 定义 TrieNode 类
@Data
public class TrieNode {

    String word;
    String translation;
    String ushone;
    String ukphone;
    List<TrieNode> children;
    List<TrieNode> parents;

    /**
     * TrieNode 构造函数
     * @param word 单词
     * @param translation 翻译
     * @param ushone 美式发音
     * @param ukphone 英式发音
     */
    public TrieNode(String word, String translation, String ushone, String ukphone) {
        this.word = word;
        this.translation = translation;
        this.ushone = ushone;
        this.ukphone = ukphone;
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

    /**
     * 添加子节点并排序
     * @param child 子节点
     */
    public void addChild(TrieNode child) {
        this.children.add(child);
        // 按照字母顺序对 children 排序
        Collections.sort(this.children, Comparator.comparing(node -> node.word));
    }

    /**
     * 查找子节点
     * @param word 要查找的单词
     * @return 找到的子节点，否则返回 null
     */
    public TrieNode findChild(String word) {
        for (TrieNode child : children) {
            if (child.word.equals(word)) {
                return child;
            }
        }
        return null;
    }

    /**
     * 添加父节点并排序
     * @param parent 父节点
     */
    public void addParent(TrieNode parent) {
        this.parents.add(parent);
        // 按照字母顺序对 parents 排序
        Collections.sort(this.parents, Comparator.comparing(node -> node.word));
    }

    /**
     * 递归将节点转换为JSON格式的Map
     * @param includeParents 是否包含父节点信息
     * @param includeChildren 是否包含子节点信息
     * @return Map表示的JSON结构
     */
    public Map<String, Object> toJson(boolean includeParents, boolean includeChildren) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("word", this.word);
        jsonMap.put("translation", this.translation);
        jsonMap.put("ushone", this.ushone);
        jsonMap.put("ukphone", this.ukphone);

        // 递归处理子节点
        if (includeChildren && !this.children.isEmpty()) {
            List<Map<String, Object>> childrenJson = new ArrayList<>();
            for (TrieNode child : this.children) {
                childrenJson.add(child.toJson(false, true)); // 子节点不需要再包含父节点
            }
            jsonMap.put("children", childrenJson);
        }

        // 递归处理父节点
        if (includeParents && !this.parents.isEmpty()) {
            List<Map<String, Object>> parentsJson = new ArrayList<>();
            for (TrieNode parent : this.parents) {
                parentsJson.add(parent.toJson(true, false)); // 父节点不需要再包含子节点
            }
            jsonMap.put("parents", parentsJson);
        }

        return jsonMap;
    }

    /**
     * 将节点转换为JSON字符串
     * @param includeParents 是否包含父节点
     * @param includeChildren 是否包含子节点
     * @return JSON字符串
     */
    public String toJsonString(boolean includeParents, boolean includeChildren) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this.toJson(includeParents, includeChildren));
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}