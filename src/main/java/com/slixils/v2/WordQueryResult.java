package com.slixils.v2;

import java.util.List;

/**
 * 单词查询返回结构体，封装单词、翻译、发音、父节点和子节点等信息
 */
public class WordQueryResult {
    /** 单词 */
    private String word;
    /** 翻译 */
    private String translation;
    /** 美式发音 */
    private String ushone;
    /** 英式发音 */
    private String ukphone;
        /** 层级 */
        private int level;
    /** 父节点（单词）列表 */
    private List<SimpleNode> parents;
    /** 子节点（单词）列表 */
    private List<SimpleNode> children;

    public WordQueryResult() {}
    public static class SimpleNode {
        /** 单词 */
        private String word;
        /** 翻译 */
        private String translation;
        /** 美式发音 */
        private String ushone;
        /** 英式发音 */
        private String ukphone;
        /** 层级 */
        private int level;

        public SimpleNode() {

        }

        /**
         * 构造方法，传入单词和翻译
         * @param word 单词
         * @param translation 翻译
         */
        public SimpleNode(String word, String translation) {
            this.word = word;
            this.translation = translation;
        }
        /** 构造函数 */
        public SimpleNode(String word, String translation, String ushone, String ukphone) {
            this.word = word;
            this.translation = translation;
            this.ushone = ushone;
            this.ukphone = ukphone;
        }
        
        /**
         * 构造方法，传入单词、翻译和层级
         * @param word 单词
         * @param translation 翻译
         * @param level 层级
         */
        public SimpleNode(String word, String translation, int level) {
            this.word = word;
            this.translation = translation;
            this.level = level;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getTranslation() {
            return translation;
        }

        public void setTranslation(String translation) {
            this.translation = translation;
        }

        public String getUshone() {
            return ushone;
        }

        public void setUshone(String ushone) {
            this.ushone = ushone;
        }

        public String getUkphone() {
            return ukphone;
        }

        public void setUkphone(String ukphone) {
            this.ukphone = ukphone;
        }
    }
    /**
     * 构造函数
     * @param word 单词
     * @param translation 翻译
     * @param ushone 美式发音
     * @param ukphone 英式发音
     * @param parents 父节点列表
     * @param children 子节点列表
     */
    public WordQueryResult(String word, String translation, String ushone, String ukphone, List<SimpleNode> parents, List<SimpleNode> children) {
        this.word = word;
        this.translation = translation;
        this.ushone = ushone;
        this.ukphone = ukphone;
        this.parents = parents;
        this.children = children;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getUshone() {
        return ushone;
    }

    public void setUshone(String ushone) {
        this.ushone = ushone;
    }

    public String getUkphone() {
        return ukphone;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }

    public void setUkphone(String ukphone) {
        this.ukphone = ukphone;
    }

    public List<SimpleNode> getParents() {
        return parents;
    }

    public void setParents(List<SimpleNode> parents) {
        this.parents = parents;
    }

    public List<SimpleNode> getChildren() {
        return children;
    }

    public void setChildren(List<SimpleNode> children) {
        this.children = children;
    }
}


/**
 * 简化节点信息结构体
 */
