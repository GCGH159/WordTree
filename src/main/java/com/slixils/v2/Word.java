package com.slixils.v2;

import com.google.gson.JsonElement;
import lombok.Data;

/**
 * 表示一个单词及其相关信息
 */
@Data
public class Word {
    String name;
    JsonElement trans;
    String usphone;
    String ukphone;


}