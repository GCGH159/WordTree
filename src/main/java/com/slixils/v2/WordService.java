package com.slixils.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 单词服务类，处理单词树的业务逻辑
 */
@Service
@Slf4j
public class WordService {

    private WordTree wordTree = new WordTree();

    /**
     * 初始化方法，在服务启动时加载单词数据。
     * 您需要将 en2zh_CN-min.json 文件放到项目的 resources 目录下，
     * 或者修改下面的文件路径为绝对路径。
     */
    @PostConstruct
    public void initialize() {
        // TODO: 将 "en2zh_CN-min.json" 文件路径配置化或确保其在类路径中
        // String filePath = "src/main/resources/en2zh_CN-min.json"; // 示例路径
//        String filePath = "e:\\com\\slixils\\WordTree-main\\src\\main\\java\\com\\slixils\\v2\\en2zh_CN-min.json"; // 使用用户提供的绝对路径
        try {
//            log.info("开始加载单词数据从: {}", filePath);
//            extractAndInsert(filePath);
//            log.info("单词数据加载完成。");
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
            for (String[] wordWithTranslation : wordsWithTranslation) {
                try {
                    wordTree.insert(wordWithTranslation[0], wordWithTranslation[1],null,null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
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
            Type wordListType = new TypeToken<List<Word>>() {}.getType();
            List<Word> words = gson.fromJson(br, wordListType);
            String translation;
            for (Word word : words) {
                String name = word.getName();
                if (name == null) {
                    continue;
                }
                if (word.getTrans() == null) {
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
                    wordTree.insert(name, translation, usphone, ukphone);
                } catch (RuntimeException e) {
                    log.warn("插入单词 '{}' 时出现重复: {}", name, e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("读取单词文件失败 '{}': ", filePath, e);
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