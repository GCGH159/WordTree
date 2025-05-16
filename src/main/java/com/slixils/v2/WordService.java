package com.slixils.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单词服务类，处理单词树的业务逻辑
 */
@Service
@Slf4j
public class WordService {

    private WordTree wordTree = new WordTree();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    /**
     * 初始化方法，在服务启动时加载单词数据。
     * 您需要将 en2zh_CN-min.json 文件放到项目的 resources 目录下，
     * 或者修改下面的文件路径为绝对路径。
     */
    @PostConstruct
    public void initialize() {
        // TODO: 将 "en2zh_CN-min.json" 文件路径配置化或确保其在类路径中
        // String filePath = "src/main/resources/en2zh_CN-min.json"; // 示例路径
        String filePath = "e:\\com\\slixils\\WordTree-main\\src\\main\\java\\com\\slixils\\v2\\en2zh_CN-min.json"; // 使用用户提供的绝对路径
        try {
//            log.info("开始加载单词数据从: {}", filePath);
            extractAndInsert(filePath);
//            log.info("单词数据加载完成。");


//            for (String[] wordWithTranslation : wordsWithTranslation) {
//                try {
//                    wordTree.insert(wordWithTranslation[0], wordWithTranslation[1],null,null);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
        } catch (Exception e) {
            log.error("初始化加载单词数据失败: ", e);
        }
    }

    /**
     * 从 JSON 文件中提取单词并插入到单词树中
     * @param filePath JSON 文件路径
     */
    public void extractAndInsert(String filePath) {
        Gson gson = new Gson();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            Type wordListType = new TypeToken<List<Word>>() {
            }.getType();
            List<Word> words = gson.fromJson(br, wordListType);
            int totalSize = words.size();
            int quarterSize = totalSize / 4;
            List<Word> part1 = words.subList(0, quarterSize);
            List<Word> part2 = words.subList(quarterSize, totalSize);
            log.info("开始同步加载前 1/4 单词，共 {} 个", part1.size());
            extractAndInsertPartial(part1);
            // 异步加载剩余3/4
            log.info("开始异步加载剩余 3/4 单词，共 {} 个", part2.size());
            executor.submit(() -> {
                try {
                    extractAndInsertPartial(part2);
                    log.info("异步加载完成");
                } catch (Exception e) {
                    log.error("异步加载单词失败", e);
                }
            });

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void extractAndInsertPartial(List<Word> words) {
        String translation;
        for (Word word : words) {
            String name = word.getName();
            if (name == null || word.getTrans() == null) {
                continue;
            }

            try {
                if (word.getTrans().isJsonArray()) {
                    if (word.getTrans().getAsJsonArray().size() > 0) {
                        translation = word.getTrans().getAsJsonArray().get(0).getAsString();
                    } else {
                        translation = "无翻译";
                    }
                } else if (!word.getTrans().isJsonNull()) {
                    translation = word.getTrans().getAsString();
                } else {
                    translation = "无翻译";
                }
            } catch (Exception e) {
                translation = "无翻译";
                log.error("解析翻译时出错 for word '{}': {}", name, e.getMessage());
            }

            String usphone = word.getUsphone();
            String ukphone = word.getUkphone();

            try {
                log.info("插入单词：{},翻译：{}",name,translation);
                wordTree.insert(name, translation, usphone, ukphone);
            } catch (RuntimeException e) {
                log.warn("插入单词 '{}' 时出现重复: {}", name, e.getMessage());
            }
        }
    }

    /**
     * 插入一个新单词
     * @param word 单词
     * @param translation 翻译
     * @param ushone 美式发音
     * @param ukphone 英式发音
     */
    public void insertWord(String word, String translation, String ushone, String ukphone) {
        try {
            wordTree.insert(word, translation, ushone, ukphone);
            log.info("单词 '{}' 已插入.", word);
        } catch (RuntimeException e) {
            log.error("插入单词 '{}' 失败: {}", word, e.getMessage());
            throw e; // 可以选择向上抛出异常，让 Controller 处理
        }
    }

    /**
     * 查询单词信息 (父节点和子节点)
     * @param word 要查询的单词
     * @return 查询结果的 JSON 字符串，如果未找到则返回提示信息
     */
    public String queryWord(String word) {
        List<String> parents = new ArrayList<>();
        TrieNode node = wordTree.findNodev1(word, wordTree.getRoot(), parents);
        if (node == null) {
            return "{\"message\":\"未找到单词: " + word + "\"}";
        }

        String jsonString = node.toJsonString(true, true);
        return jsonString;
    }
    
    /**
     * 递归收集所有子节点信息
     * @param node 当前节点
     * @param result 结果列表
     * @param level 当前层级
     */
    private void collectChildren(TrieNode node, List<WordQueryResult.SimpleNode> result, int level) {
        if (node.getChildren().isEmpty()) {
            return;
        }
        for (TrieNode child : node.getChildren()) {
            result.add(new WordQueryResult.SimpleNode(child.getWord(), child.getTranslation(), level));
            collectChildren(child, result, level + 1);
        }
    }

    /**
     * 查询单词信息 (queryv1 逻辑)
     * @param word 要查询的单词
     * @return 查询结果的字符串描述，如果未找到则返回提示信息
     */
    public String queryWordV1(String word) {
        // WordTree.queryv1 方法是打印到控制台，需要修改为返回数据结构或字符串
        // 这里暂时模拟返回，实际应修改 WordTree.queryv1
        TrieNode node = wordTree.findNode(word); // 使用内部的 findNode 方法
        if (node == null) {
            return "未找到单词: " + word;
        }

        System.out.println("aaa");
        StringBuilder sb = new StringBuilder();
        sb.append("查询单词: ").append(word).append("\n");
        sb.append("翻译: ").append(node.getTranslation()).append("\n");
        sb.append("美式发音: ").append(node.getUshone()).append("\n");
        sb.append("英式发音: ").append(node.getUkphone()).append("\n");

        sb.append("父节点: \n");
        if (node.getParents().isEmpty()) {
            sb.append("    无\n");
        } else {
            for (TrieNode parent : node.getParents()) {
                sb.append("    ").append(parent.getWord()).append(" - ").append(parent.getTranslation()).append("\n");
            }
        }

        sb.append("子节点: \n");
        if (node.getChildren().isEmpty()) {
            sb.append("    无\n");
        } else {
            for (TrieNode child : node.getChildren()) {
                sb.append("    ").append(child.getWord()).append(" - ").append(child.getTranslation()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 辅助方法，递归构建子节点信息 (queryv1 逻辑)
     * @param node 当前节点
     * @param level 层级
     * @param sb StringBuilder 用于构建输出
     */
    private void appendChildrenInfoV1(TrieNode node, int level, StringBuilder sb) {
        if (node.getChildren().isEmpty()) {
            if (level == 1) { // 如果根查询的节点没有子节点
                sb.append(indent(level)).append("无子节点\n");
            }
            return;
        }

        for (TrieNode child : node.getChildren()) {
            sb.append(indent(level)).append(child.getWord()).append(" - ").append(child.getTranslation()).append("\n");
            appendChildrenInfoV1(child, level + 1, sb);
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

    // TODO: 实现删除单词和修改单词的方法
    // public void deleteWord(String word) { ... }
    // public void updateWord(String oldWord, String newWord, String newTranslation, String newUshone, String newUkphone) { ... }

    /**
     * 获取整个单词树的 JSON 表示
     * @return JSON 字符串
     */
    public String getWordTreeJson() {
        return wordTree.toPrettyJson();
    }
}