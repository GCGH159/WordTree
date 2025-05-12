package com.slixils.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

// 定义 TrieNode 类，表示字典树中的一个节点
@Slf4j
public class TrieNode {
    String word;  // 保存单词



    String translation;  // 保存单词的翻译
    List<TrieNode> children;  // 保存子节点（单词的子单词）
    List<TrieNode> parents;  // 保存父节点（单词的父单词，支持多继承）

    // 构造函数，初始化 word 和 translation，并创建 children 和 parents 的空列表
    public TrieNode(String word, String translation) {
        this.word = word;
        this.translation = translation;
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
    }

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
    @Override
    public String toString() {
        return "TrieNode{" +
                "word='" + word + '\'' +
                ", translation='" + translation + '\'' +
                ", children=" + children +
                ", parents=" + parents +
                '}';
    }
}
@Slf4j
// 定义 WordTree 类，表示一个包含单词和翻译的字典树
class WordTree {
    //TODO 关于持久化设计
//    CREATE TABLE Words (单词表
//            id INT AUTO_INCREMENT PRIMARY KEY,
//            word VARCHAR(255) NOT NULL UNIQUE,
//    translation TEXT NOT NULL
//);对应表
//    CREATE TABLE WordRelationships (
//            parent_id INT,
//            child_id INT,
//            PRIMARY KEY (parent_id, child_id),
//    FOREIGN KEY (parent_id) REFERENCES Words(id),
//    FOREIGN KEY (child_id) REFERENCES Words(id)
//            );

//    SELECT w.word, w.translation
//    FROM Words w
//    JOIN WordRelationships wr ON w.id = wr.parent_id
//    JOIN Words wc ON wr.child_id = wc.id
//    WHERE wc.word = '某个单词';查询父单词
//    SELECT w.word, w.translation
//    FROM Words w
//    JOIN WordRelationships wr ON w.id = wr.child_id
//    JOIN Words wp ON wr.parent_id = wp.id
//    WHERE wp.word = '某个单词';查询子单词

    private TrieNode root;  // 树的根节点，初始为空

    // 构造函数，创建一个空的根节点（word 和 translation 为 null）
    public WordTree() {
        this.root = new TrieNode(null, null);
    }

    // 插入新单词及其翻译
    public void insert(String newWord, String translation) {
        // 创建一个包含新单词和翻译的节点
        TrieNode newNode = new TrieNode(newWord, translation);

        // 获取树中的所有节点，并按单词长度从短到长排序
        List<TrieNode> nodes = getAllNodes();
        nodes.sort((n1, n2) -> {
            int len1 = n1.word != null ? n1.word.length() : 0;  // 计算第一个节点的单词长度
            int len2 = n2.word != null ? n2.word.length() : 0;  // 计算第二个节点的单词长度
            return -Integer.compare(len1, len2);  // 按长度比较并排序
        });

        // 遍历所有已存在的节点，匹配单词的继承关系
        for (TrieNode node : nodes) {
            if (newWord.equals(node.word)){
                throw new RuntimeException("单词已经存在！"+newWord+" 翻译 {"+translation+"}");
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

    // 判断 w1 是否是 w2 的子集
    private boolean isSubword(String w1, String w2) {
        if (w1 == null || w2 == null) {  // 如果任意单词为空，则返回 false
            return false;
        }
        return w2.contains(w1);  // 返回 w2 是否包含 w1
    }

    // 获取树中的所有节点
    private List<TrieNode> getAllNodes() {
        List<TrieNode> result = new ArrayList<>();  // 存储所有节点的列表
        dfs(root, result);  // 使用深度优先搜索（DFS）遍历树
        return result;  // 返回节点列表
    }

    // 深度优先搜索，遍历每个节点并将其添加到结果列表中
    private void dfs(TrieNode node, List<TrieNode> result) {
        if (node != null) {  // 如果当前节点不为空
            result.add(node);  // 将当前节点添加到结果列表中
            for (TrieNode child : node.children) {  // 遍历当前节点的所有子节点
                dfs(child, result);  // 递归调用 dfs 遍历子节点
            }
        }
    }

    // 打印整个单词树
    public void display() {
        display(root, 0);  // 从根节点开始，层级从0开始
    }

    // 递归打印每个节点及其子节点
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
    private JsonObject buildJsonV1(TrieNode node) {
        JsonObject jsonObject = new JsonObject();

        if (node.word != null) {
            jsonObject.addProperty("单词", node.word+"-"+ node.translation);
//            jsonObject.addProperty("translation",node.translation);
        }

        // 创建一个子节点数组
        JsonArray childrenArray = new JsonArray();
        for (TrieNode child : node.children) {
            // 递归构建子节点的 JSON 并添加到数组中
            childrenArray.add(buildJsonV1(child));
        }

        // 只有在有子节点时，才添加 children 数组
        if (childrenArray.size() > 0) {
            jsonObject.add("children", childrenArray);
        }

        return jsonObject;
    }
    public String toJson(){

        return buildJsonV1(root).toString();
    }

    // 程序入口
    public static void main(String[] args) {
        // 创建一个新的 WordTree 对象
        WordTree tree = new WordTree();

        // 定义单词和翻译的数组
        String[][] wordsWithTranslation = {
                {"act", "行动"},
                {"action", "行为"},
                {"activate", "激活"},
                {"actor", "演员"},
                {"active", "积极的"},
                {"agri", "田地"},
                {"agriculture", "农业"},
                {"agricultural", "农业的"},
                {"agronomist", "农学家"},
                {"bio", "生命"},
                {"biology", "生物学"},
                {"biographical", "传记的"},
                {"biologist", "生物学家"},
                {"biosphere", "生物圈"},
                {"cap", "抓住"},
                {"capable", "有能力的"},
                {"capacity", "容量，能力"},
                {"capture", "捕获"},
                {"captain", "队长"},
                {"cede", "割让"},
                {"ceded", "割让的"},
//                {"ceded", "割让的"},
                {"cession", "割让"},
                {"cedent", "割让者"},
                {"chron", "时间"},
                {"chronicle", "编年史"},
                {"chronological", "按时间顺序的"},
                {"chronometer", "精密计时器"},
                {"circ", "圆，环"},
                {"circle", "圆圈"},
                {"circular", "圆形的，循环的"},
                {"circumference", "周长"},
                {"circumvent", "绕开"},
                {"claim", "声称"},
                {"claimant", "索赔人"},
                {"claimed", "被声称的"},
                {"claimable", "可要求的"},
                {"cogn", "知道"},
                {"cognition", "认知"},
                {"cognitive", "认知的"},
                {"cognizant", "认识的"},
                {"cognizance", "认识，注意"},
                {"com", "共同"},
                {"combine", "结合"},
                {"combined", "结合的"},
                {"community", "社区"},
                {"communicate", "交流"},
                {"communication", "交流，通讯"},
                {"cond", "条件"},
                {"condition", "条件"},
                {"conditional", "有条件的"},
                {"conditioned", "有条件的"},
                {"conductor", "导体，指挥"},
                {"conduct", "引导，行为"},
                {"conductivity", "导电性"},
                {"crit", "判断"},
                {"critic", "批评家"},
                {"critical", "批评的，重要的"},
                {"criticism", "批评"},
                {"criticize", "批评"},
                {"dec", "十"},
                {"decade", "十年"},
                {"decimal", "十进制的"},
                {"decimate", "大量杀死"},
                {"decisive", "决定性的"},
                {"decision", "决定"},
                {"dem", "人民"},
                {"democracy", "民主"},
                {"democrat", "民主主义者"},
                {"democratic", "民主的"},
                {"demonstrate", "证明，示威"},
                {"demonstration", "证明，示威游行"},
                {"dict", "说"},
                {"dictionary", "字典"},
                {"dictate", "命令"},
                {"dictator", "独裁者"},
                {"dictatorial", "独裁的"},
                {"educ", "教育"},
                {"educate", "教育"},
                {"education", "教育"},
                {"educational", "教育的"},
                {"educator", "教育者"},
                {"pos", "放置，摆放"},
                {"pose", "姿势；提出问题；摆好姿势"},
                {"poser", "摆姿势的人，装腔作势"},
                {"expose", "暴露"},
                {"exposed", "暴露了的"},
                {"exposition", "展销会"},
                {"compose", "组成，构成"},
                {"composer", "创作者"},
                {"composed", "由什么组成，镇静的"},
                {"composure", "镇静"},
                {"composite", "合成的"},
                {"composition", "作品"},
                {"component", "部件"},
                {"decompose", "分解"},
                {"depose", "罢免"},
                {"transpose", "排序"},
                {"deposition", "罢免"},
                {"deposed", "被罢免的"},
                {"post", "岗位"},
                {"posture", "姿势"},
                {"position", "位置，立场"},
                {"positive", "积极的，积极的态度"},
                {"postman", "邮递员"},
                {"deposit", "存钱"},
                {"applied", "应用的"},
                {"observe", "注意到"},
                {"observation ", "观察"},
                {"use", "利用"},
                {"fin", "结束，边界"},
                {"final", "最终的"},
                {"finalize", "使完成，定稿"},
                {"finance", "财政，金融"},
                {"financial", "财政的，金融的"},
                {"finite", "有限的"},
                {"finity", "有限性"},
                {"flu", "流"},
                {"fluent", "流利的"},
                {"fluency", "流利"},
                {"fluid", "流体"},
                {"flux", "流动，变迁"},
                {"fract", "破碎"},
                {"fraction", "碎片，部分"},
                {"fractional", "部分的，碎片的"},
                {"fracture", "破裂，骨折"},
                {"fractious", "易怒的，易分裂的"},
                {"gen", "出生，产生"},
                {"gene", "基因"},
                {"general", "一般的，总的"},
                {"generate", "产生，生成"},
                {"generation", "一代，产生"},
                {"generator", "发电机，发生器"},
                {"generous", "慷慨的"},
                {"geo", "地球"},
                {"geography", "地理"},
                {"geological", "地质学的"},
                {"geologist", "地质学家"},
                {"geometry", "几何学"},
                {"grad", "步，级"},
                {"gradate", "分级"},
                {"gradation", "层次，分级"},
                {"grade", "等级，成绩"},
                {"gradual", "逐渐的"},
                {"gradually", "逐渐地"},
                {"graph", "写，画"},
                {"graphic", "图形的，生动的"},
                {"graphics", "制图学，图形"},
                {"graphite", "石墨"},
                {"graphology", "笔迹学"},
                {"grat", "高兴，感激"},
                {"grateful", "感激的"},
                {"gratify", "使高兴，满足"},
                {"gratitude", "感激，感恩"},
                {"gratuitous", "无端的，无报酬的"},
                {"grav", "重"},
                {"gravity", "重力，严肃性"},
                {"grave", "严肃的，坟墓"},
                {"gravitate", "受重力作用，倾向于"},
                {"gravitational", "重力的"},
                {"greg", "群"},
                {"gregarious", "群居的，爱社交的"},
                {"gregation", "群集"},
                {"gress", "行走"},
                {"progress", "进步"},
                {"progressive", "进步的"},
                {"egress", "出口，出去"},
                {"ingress", "入口，进入"},
                {"heur", "发现"},
                {"heuristic", "启发式的"},
                {"heuristics", "启发式方法"},
                {"homo", "同"},
                {"homogeneous", "同质的"},
                {"homogeneity", "同质性"},
                {"homograph", "同形异义词"},
                {"homology", "相似性，同源"},
                {"homosexual", "同性恋的"},
                {"hospit", "客人"},
                {"hospital", "医院"},
                {"hospitality", "好客"},
                {"hospitalize", "使住院"},
                {"host", "主人，节目主持人"},
                {"hostile", "敌对的"},
                {"hostility", "敌意"},
                {"typical", "典型的"},
                {"help", "帮助"},
                {"memory", "回忆"},
                {"sample", "样本，样本数据"},
                {"record", "记录，记录数据"},
                {"text", "文本，文本数据"},
                {"effective","有效的"},
                {"database", "数据库"},
                {"move","移动"},
                {"calling","职业"},
                {"eventually","终于"},
                {"release","释放"},
                {"topic","话题"},
                {"to","去"},
                {"copy","复制"},
                {"filter","过滤器"},
                {"enhance","提高"},
                {"normal","典型的"},
                {"examine","审问"},
                {"bypass","绕开"},
                {"importance","重要性"},
                {"negative","坏的"},
                {"tag","标记"},
                {"following","接着"},
                {"drive","驾驶"},
                {"enter","进入"},
                {"major","严重"},
                {"exhaust","用尽"},
//                {"graphic","绘画"},
//                {"graph","图表"},
                {"decrease","降低"},
                {"increase","增加"},
                {"crease","折痕"},
                {"failure","失败"},
                {"trap","陷阱"},
                {"extra","额外"},
                {"beyond","超出"},
                {"talent","人才"},
                {"variable","可变"},
                {"turning","转弯"},
                {"register","登记"},
                {"fully","充分的"},
                {"template","模板"},
                {"emphasize","强调"},
                {"information","信息"},
                {"appropriate","合适的"},
                {"recall","召回"},
                {"delete","删除"},
                {"edit","编辑"},
                {"structural","结构"},
                {"marked","显而易见"},
                {"area","面积"},
                {"parameter","参数"},
                {"then","然后"},
//                {"active","积极"},
                {"each","每"},
                {"start","开始"},
                {"mode","方式"},
                {"make","制造"},
                {"right","马上"},
                {"value","值"},
                {"index","索引"},
                {"without","没有"},
                {"convenient","便利"},
                {"first","第一个"},
                {"throughout","遍及"},
                {"unlike","不喜欢"},
                {"initially","最初"},
                {"return","返回"},
                {"stream","流"},
                {"qualified","符合资格"},
                {"evaluate","评估"},
                {"rather","相当"},
                {"consume","消耗"},
                {"easel","花架"},
                {"implement","实施"},
                {"forced","被迫的"},
                {"data","资料"},
                {"replacement","替换"},
                {"pattern","图案"},
                {"bar","条"},
                {"field","领域"},
                {"death","死亡"},
                {"recent","近来的"},
                {"group","分组"},
                {"criterion","标准"},
                {"immediately","立即"},
                {"cursor","游标"},
                {"charge","价钱"},
                {"code","代码"},
                {"via","通过"},
                {"contrast","对比"},
                {"respond","响应"},
                {"properly","正确的"},
                {"number","数字"},
                {"glance","扫视"},
                {"greatly","非常"},
                {"message","消息"},
                {"character","性格"},
                {"positive","积极的"},
                {"particular","专指的"},
                {"associate","联系"},
                {"want","要"},
                {"restrict","限制"},
                {"detail","细节"},
                {"intense","强烈"},
                {"zoom","快速移动"},
                {"frequently","频繁的"},
                {"presentation","报告"},
                {"dynamic","有活力的"},
                {"virtually","几乎"},
                {"solution","解答"},
                {"external","外部的"},
                {"virtual","虚拟的"},
                {"accuracy","准确"},
                {"visual","视力的"},
                {"frequently","频繁的"},
                {"physically","身体上地"},
                {"digital","数字格式"},
                {"next","下一个"},
                {"off","关着的"},
                {"control","控制"}
//                {"generation", "一代"},
        };

        // 遍历数组，将每个单词及其翻译插入到树中
        for (String[] wordWithTranslation : wordsWithTranslation) {
            try {
                tree.insert(wordWithTranslation[0], wordWithTranslation[1]);
            } catch (Exception e) {
//                throw new RuntimeException(e);
            }
        }
//        System.out.println(tree.toJson());
        // 打印整个单词树
        String input;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入单词及其翻译（格式：word translation），输入 'exit' 结束：");
        while (true) {
            input = scanner.nextLine();

            // 判断是否输入了 "exit"
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            // 按空格分隔单词和翻译
            String[] parts = input.split(" ", 2);
            if (parts.length != 2) {
                System.out.println("输入格式错误，请重新输入（格式：word translation）：");
                continue;
            }

            String word = parts[0];
            String translation = parts[1];

            // 插入单词及其翻译
            try {
                tree.insert(word, translation);
            } catch (Exception e) {
            log.info("失败原因",e);
            }
            tree.display();
        }



    }
}
