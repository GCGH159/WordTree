package com.slixils.v2;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



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


}
