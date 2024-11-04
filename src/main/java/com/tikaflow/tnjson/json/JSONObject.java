package com.tikaflow.tnjson.json;

import java.util.HashMap;

import lombok.NoArgsConstructor;
import lombok.val;
import lombok.var;
import com.tikaflow.tnjson.bean.Element;

@NoArgsConstructor
public class JSONObject {
    private final HashMap<String, Element<?>> items = new HashMap<>();

    public JSONObject put(String key, JSONObject obj) {
        items.put(key, new Element<>(obj));
        return this;
    }

    public JSONObject put(String key, JSONArray arr) {
        items.put(key, new Element<>(arr));
        return this;
    }

    public JSONObject put(String key, String str) {
        items.put(key, new Element<>(str));
        return this;
    }

    public JSONObject put(String key, double num) {
        items.put(key, new Element<>(num));
        return this;
    }

    public JSONObject put(String key, boolean bool) {
        items.put(key, new Element<>(bool));
        return this;
    }

    public JSONObject putNull(String key) {
        items.put(key, new Element<>());
        return this;
    }

    public String toString() {
        var sb = new StringBuilder("{");

        for (var item : items.entrySet()) {
            sb.append("\"").append(item.getKey()).append("\": ").append(item.getValue().toString()).append(", ");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("}");

        return sb.toString();
    }

    public String format(String indent) {
        var sb = new StringBuilder("{");

        if (!items.isEmpty()) {
            sb.append("\n");
        }

        val itemIndent = indent + "    ";
        for (var item : items.entrySet()) {
            sb.append(itemIndent)
                    .append("\"")
                    .append(item.getKey())
                    .append("\": ");

            val value = item.getValue().getValue();
            if (value instanceof JSONObject) {
                sb.append(((JSONObject) value).format(itemIndent));
            } else if (value instanceof JSONArray) {
                sb.append(((JSONArray) value).format(itemIndent));
            } else if (value instanceof String) {
                sb.append("\"")
                        .append(((String) value))
                        .append("\"");
            } else {
                sb.append(item.getValue());
            }

            sb.append(",\n");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("\n")
                .append(indent)
                .append("}");

        return sb.toString();
    }

    public String format() {
        return format("");
    }
}
