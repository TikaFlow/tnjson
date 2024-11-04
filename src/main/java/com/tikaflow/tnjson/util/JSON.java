package com.tikaflow.tnjson.util;

import com.tikaflow.tnjson.json.JSONObject;
import com.tikaflow.tnjson.json.JSONArray;
import com.tikaflow.tnjson.parse.Lexer;
import com.tikaflow.tnjson.parse.Parser;
import lombok.val;

public class JSON {
    public static JSONObject parseObject(String json) {
        val lexer = new Lexer(json);
        val parser = new Parser(lexer.parse());

        return parser.parseObject();
    }

    public static JSONArray parseArray(String json) {
        val lexer = new Lexer(json);
        val parser = new Parser(lexer.parse());

        return parser.parseArray();
    }
}
