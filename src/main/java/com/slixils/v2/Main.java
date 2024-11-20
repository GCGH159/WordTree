package org.jeecg.common.util.v2;
import java.io.*;
import java.lang.reflect.Type;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


// 定义 TrieNode 类
@Data
class TrieNode {

//    HttpClient httpClient;

    String word;
    String translation;

    String ushone;
    String ukphone;
    List<TrieNode> children;
    List<TrieNode> parents;

    public TrieNode(String word, String translation,String ushone,String ukphone) {
        this.word = word;
        this.translation = translation;
        this.ushone=ushone;
        this.ukphone=ukphone;
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

    // 检查是否已经包含该单词
    public void addChild(TrieNode child) {
        this.children.add(child);
        // 按照字母顺序对 children 排序
        Collections.sort(this.children, Comparator.comparing(node -> node.word));
    }
    public TrieNode findChild(String word) {
        for (TrieNode child : children) {
            if (child.word.equals(word)) {
                return child;
            }
        }
        return null;
    }


    // 添加父节点并排序
    public void addParent(TrieNode parent) {
        this.parents.add(parent);
        // 按照字母顺序对 parents 排序
        Collections.sort(this.parents, Comparator.comparing(node -> node.word));
    }
}

// 定义 WordTree 类
@Slf4j
class WordTree {
    private TrieNode root;

    public WordTree() {
        this.root = new TrieNode(null, null, null,null);
    }

    // 插入新单词及其翻译
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
                throw new RuntimeException("单词已经存在！"+newWord+"-{"+translation+"}");
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

    private boolean isSubword(String w1, String w2) {
        if (w1 == null || w2 == null) {
            return false;
        }
        return w2.contains(w1);
    }


    private List<TrieNode> getAllNodes() {
        List<TrieNode> result = new ArrayList<>();
        dfs(root, result);
        return result;
    }

    private void dfs(TrieNode node, List<TrieNode> result) {
        if (node != null) {
            result.add(node);
            for (TrieNode child : node.children) {
                dfs(child, result);
            }
        }
    }

    // 根据单词查找节点
    private TrieNode findNode(String word) {
        for (TrieNode node : getAllNodes()) {
            if (word.equals(node.word)) {
                return node;
            }
        }
        return null;  // 如果找不到该单词，返回 null
    }

    // 查询单词的父节点和子节点
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

    public String toPrettyJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(buildJson(root));
    }

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
    private void printChildrenv1(TrieNode node, int level) {
        if (node.children.isEmpty()) {
            return;
        }

        for (TrieNode child : node.children) {
            System.out.println(indent(level) + child.word + " - " + child.translation);
            printChildrenv1(child, level + 1);
        }
    }
    private String indent(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("    ");
        }
        return indent.toString();
    }


    private TrieNode findNodev1(String word, TrieNode currentNode, List<String> parents) {
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
    public void display() {
        display(root, 0);  // 从根节点开始，层级从0开始
    }
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
}
@Data
class Word {
    String name;
    JsonElement trans;
    String usphone;
    String ukphone;
}
@Slf4j
// 主类
public class Main {

    public static void saveJsonToFile(String directoryPath, String fileName ,String json) {
        String jsonContent = json;

        // 创建目录
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(directory, fileName)))) {
            writer.write(jsonContent);
            System.out.println("JSON 文件已保存到: " + directoryPath + "/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  static void extractAndInsert(String filePath) {
        WordTree treeaa = new WordTree();
        Gson gson = new Gson();

        // 从文件读取 JSON 数据
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            Type wordListType = new TypeToken<List<Word>>() {}.getType();
            List<Word> words = gson.fromJson(br, wordListType);
            String translation;
            for (Word word : words) {
                String name = word.name;
                System.out.println("word = " + word);
                if (name == null){
                    continue;
                }
                if (word.trans == null){
                    continue;
                }
                try {
                    if (word.trans.isJsonArray()) {
                        if (word.trans.getAsJsonArray().size() > 0) {
                            translation = word.trans.getAsJsonArray().get(0).getAsString(); // 取第一个翻译
                        } else {
                            translation = "无翻译"; // 如果数组为空，可以设定默认值
                        } // 取第一个翻译
                    }else if (!word.trans.isJsonNull()) {
                        translation = word.trans.getAsString(); // 直接作为翻译
                    } else {
                        translation = "无翻译"; // 如果是 JsonNull，可以设定默认值
                    }
                } catch (Exception e) {
                    translation = "无翻译";
                    log.error(e.getMessage());
                }
//                String translation = word.trans.get(0); // 取第一个翻译
                String usphone = word.usphone;
                String ukphone = word.ukphone;

                treeaa.insert(name, translation, usphone, ukphone);
            }
            treeaa.display();

            saveJsonToFile("json","english.json",treeaa.toPrettyJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateSign(String appid, String q, String salt, String secretKey) {
        String stringToSign = appid + q + salt + secretKey;
        return SecureUtil.md5(stringToSign).toLowerCase();
    }
    public static void translate(String appid, String secretKey, String query, String fromLang, String toLang) {
        // 生成随机数
        String salt = String.valueOf(new Random().nextInt(10000));

        // 生成签名
        String sign = generateSign(appid, query, salt, secretKey);

        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("q", query);
        params.put("from", fromLang);
        params.put("to", toLang);
        params.put("appid", appid);
        params.put("salt", salt);
        params.put("sign", sign);

        // 发送 POST 请求
        String url = "https://fanyi-api.baidu.com/api/trans/vip/translate";
        String response = HttpUtil.post(url, params);

        new Gson().fromJson(response, Map.class).forEach((key, value) -> System.out.println(key + ": " + value));
        // 打印响应内容
//        System.out.println(response+"aa");
    }

    public static void main(String[] args) {
        WordTree tree = new WordTree();
        Scanner scanner = new Scanner(System.in);
        String input;

        String[][] wordsWithTranslation = {

//                {"generation", "一代"},
        };
        for (String[] wordWithTranslation : wordsWithTranslation) {
            try {
                tree.insert(wordWithTranslation[0], wordWithTranslation[1],null,null);
            } catch (Exception e) {
                log.error("单词出现重复" ,e);
//                throw new RuntimeException(e);
            }
        }
//        extractAndInsert("D:\\com\\翰衡\\JeecgBoot\\后台\\hanhenghoutai\\jeecg-boot\\jeecg-boot-base-core\\src\\main\\java\\org\\jeecg\\common\\util\\v2\\en2zh_CN-min.json");
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));

//
        // 提供查询单词的功能
//        while (true) {
//            System.out.println("请输入要插入的单词，输入 'exit' 结束查询：");
//            input = scanner.nextLine();
//
//            // 判断是否输入了 "exit"
//            if (input.equalsIgnoreCase("exit")) {
//                break;
//            }
//            String[] parts = input.split("-", 2);
//            if (parts.length != 2) {
//                System.out.println("输入格式错误，请重新输入（格式：word translation）：");
//                continue;
//            }
//
//            String word = parts[0];
//            String translation = parts[1];
//
//            // 插入单词及其翻译
//            try {
//                tree.insert(word, translation,null,null);
////                HttpUtils.getHttpClient();
////                HttpUtil.post(word, translation);
//                translate("","",word,"auto","zh");
//            } catch (Exception e) {
//                log.info("失败原因",e);
//            }
//            tree.display();
//            // 查询单词
////            tree.queryv1(word);
//
//        }

        // 打印生成的 JSON 树
//        System.out.println("生成的单词树 JSON：");
//        System.out.println(tree.toPrettyJson());
    }
}
