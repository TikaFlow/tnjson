package com.tikaflow.tnjson.util;

import com.tikaflow.tnjson.json.JSONObject;
import com.tikaflow.tnjson.json.JSONArray;
import com.tikaflow.tnjson.parse.Lexer;
import com.tikaflow.tnjson.parse.Parser;
import lombok.val;
import lombok.var;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static <T> T parseObject(String json, Class<T> clazz) {
        return parseObject(json).convertTo(clazz);
    }

    public static String toJSONString(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof String
                || obj instanceof Character) {
            return "\"" + obj + "\"";
        } else if (obj instanceof Byte
                || obj instanceof Short
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Float
                || obj instanceof Double
                || obj instanceof Boolean) {
            return obj.toString();
        }

        var sb = new StringBuilder("{");

        val clazz = obj.getClass();
        val getters = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().startsWith("get")
                        && !"getClass".equals(m.getName())
                        && m.getName().length() > 3
                        && Misc.isUpper(m.getName().charAt(3))
                        && m.getReturnType() != void.class
                        && m.getParameterCount() == 0)
                .collect(Collectors.toList());

        for (val getter : getters) {
            try {
                val getterName = getter.getName();
                val key = getterName.substring(3, 4).toLowerCase() + getterName.substring(4);
                val value = getter.invoke(obj);

                if (value instanceof String
                        || value instanceof Character) {
                    sb.append("\"")
                            .append(key)
                            .append("\"")
                            .append(":")
                            .append("\"")
                            .append(value)
                            .append("\"");
                } else if (value instanceof Byte
                        || value instanceof Short
                        || value instanceof Integer
                        || value instanceof Long
                        || value instanceof Float
                        || value instanceof Double
                        || value instanceof Boolean) {
                    sb.append("\"")
                            .append(key)
                            .append("\"")
                            .append(":")
                            .append(value);
                } else if (value.getClass().isArray()) {
                    val len = Array.getLength(value);
                    sb.append("\"")
                            .append(key)
                            .append("\"")
                            .append(":")
                            .append("[");
                    for (int i = 0; i < len; i++) {
                        sb.append(toJSONString(Array.get(value, i)))
                                .append(",");
                    }
                    if (len > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    sb.append("]");
                } else {
                    sb.append(toJSONString(value));
                }

                sb.append(",");
            } catch (Exception e) {
                continue;
            }
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");

        return sb.toString();
    }
}
