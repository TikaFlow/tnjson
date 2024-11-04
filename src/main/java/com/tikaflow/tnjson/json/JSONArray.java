package com.tikaflow.tnjson.json;

import java.util.ArrayList;

import lombok.NoArgsConstructor;
import com.tikaflow.tnjson.bean.Element;
import lombok.val;
import lombok.var;

@NoArgsConstructor
public class JSONArray {
    private final ArrayList<Element<?>> items = new ArrayList<>();

    public JSONArray push(JSONObject obj) {
        items.add(new Element<>(obj));
        return this;
    }

    public JSONArray push(JSONArray arr) {
        items.add(new Element<>(arr));
        return this;
    }

    public JSONArray push(String str) {
        items.add(new Element<>(str));
        return this;
    }

    public JSONArray push(double num) {
        items.add(new Element<>(num));
        return this;
    }

    public JSONArray push(boolean bool) {
        items.add(new Element<>(bool));
        return this;
    }

    public JSONArray pushNull() {
        items.add(new Element<>());
        return this;
    }

    public String toString() {
        return items.toString();
    }

    public String format(String indent) {
        var sb = new StringBuilder("[");

        if (!items.isEmpty()) {
            sb.append("\n");
        }

        val itemIndent = indent + "    ";
        for (var item : items) {
            sb.append(itemIndent);

            val value = item.getValue();
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
                .append("]");

        return sb.toString();
    }

    public String format() {
        return format("");
    }
}