
# 单词树 (Word Tree)

一个简单高效的单词树（Trie Tree）实现，用于快速存储和检索字符串集合。适合用于词频统计、自动补全和关键词匹配等场景。

---

## 功能介绍

- **单词插入**：快速插入单词到树中。
- **单词搜索**：支持精确搜索和前缀搜索。
- **删除单词**：从树中删除指定单词。
- **统计词频**：统计某单词出现的次数。
- **自动补全**：基于前缀提供候选单词列表。

---

## 项目结构

```
word-tree/
├── src/
│   ├── WordTree.java          # 核心实现（Java 示例）
│   ├── TrieNode.java          # 单词树节点类
│   └── Main.java              # 示例和测试用例
├── README.md                  # 项目说明
└── LICENSE                    # 许可信息
```

---

## 环境要求

- JDK 8 或以上版本
- Maven（可选，用于依赖管理）

---

## 安装与运行

### 1. 克隆仓库

```bash
git clone https://github.com/yourusername/word-tree.git
cd word-tree
```

### 2. 编译项目

使用 Maven 编译：

```bash
mvn clean package
```

或直接用 `javac` 编译源文件：

```bash
javac src/*.java
```

### 3. 运行示例

```bash
java -cp src Main
```

---

## 示例用法

```java
WordTree trie = new WordTree();

// 插入单词
trie.insert("apple");
trie.insert("app");
trie.insert("application");

// 搜索单词
System.out.println(trie.search("apple"));       // true
System.out.println(trie.search("app"));         // true
System.out.println(trie.search("apples"));      // false

// 前缀匹配
System.out.println(trie.startsWith("app"));     // true

// 自动补全
System.out.println(trie.getWordsByPrefix("app")); // ["app", "apple", "application"]

// 删除单词
trie.delete("app");
System.out.println(trie.search("app"));         // false
```

---

## 贡献指南

1. Fork 本项目并克隆到本地。
2. 创建功能分支：
   ```bash
   git checkout -b feature/new-feature
   ```
3. 提交更改：
   ```bash
   git commit -m "Add new feature"
   ```
4. 推送分支：
   ```bash
   git push origin feature/new-feature
   ```
5. 创建 Pull Request。

---

## 许可证

本项目基于 [MIT License](LICENSE) 开源。

---

## 联系方式

如果您有任何疑问或建议，请通过 [gcgh159@qq.com](slixils159@gmail.com) 联系我们。

由于整个单词树比较大，200M无法上传，请联系我！

---

## TODO

- 增加多语言支持。
- 支持序列化和反序列化。
- 优化自动补全性能。
