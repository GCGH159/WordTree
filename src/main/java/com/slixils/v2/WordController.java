package com.slixils.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 单词控制器，提供 REST API 用于单词操作
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/words")
public class WordController {

    private final WordService wordService;

    /**
     * 构造函数，注入 WordService
     * @param wordService 单词服务
     */
    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    /**
     * 添加一个新单词
     * @param payload 包含单词信息的请求体，应包含 word, translation, ushone, ukphone
     * @return ResponseEntity 包含操作结果
     */
    @PostMapping("/addWord")
    public ResponseEntity<String> addWord(@RequestBody Map<String, String> payload) {
        try {
            String word = payload.get("word");
            String translation = payload.get("translation");
            String ushone = payload.get("ushone");
            String ukphone = payload.get("ukphone");

            if (word == null || word.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("单词不能为空");
            }
            // translation, ushone, ukphone 可以是可选的，根据业务逻辑调整
            wordService.insertWord(word, translation, ushone, ukphone);
            return ResponseEntity.status(HttpStatus.CREATED).body("单词 '" + word + "' 添加成功");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("添加单词时发生错误: " + e.getMessage());
        }
    }

    /**
     * 查询单词信息 (父节点和子节点)
     * @param word 要查询的单词
     * @return 单词信息或未找到提示
     */
    @GetMapping("/queryWord/{word}")
    public ResponseEntity<String> queryWord(@PathVariable String word) {
        try {
            String result = wordService.queryWord(word);
            if (result.startsWith("未找到单词")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {

            System.out.println("aaaa");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("查询单词时发生错误: " + e.getMessage());
        }
    }

    /**
     * 查询单词信息 (queryv1 逻辑)
     * @param word 要查询的单词
     * @return 单词信息或未找到提示
     */
    @GetMapping("/v1/{word}")
    public ResponseEntity<String> queryWordV1(@PathVariable String word) {
        try {
            String result = wordService.queryWordV1(word);
            if (result.startsWith("未找到单词")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("查询单词 (v1) 时发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取整个单词树的 JSON 表示
     * @return 单词树的 JSON 字符串
     */
    @GetMapping("/tree")
    public ResponseEntity<String> getWordTree() {
        try {
            String jsonTree = wordService.getWordTreeJson();
            return ResponseEntity.ok(jsonTree);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取单词树时发生错误: " + e.getMessage());
        }
    }

    // TODO: 根据需要实现删除和修改单词的端点
    // @DeleteMapping("/{word}")
    // public ResponseEntity<String> deleteWord(@PathVariable String word) { ... }

    // @PutMapping("/{oldWord}")
    // public ResponseEntity<String> updateWord(@PathVariable String oldWord, @RequestBody Map<String, String> payload) { ... }
}