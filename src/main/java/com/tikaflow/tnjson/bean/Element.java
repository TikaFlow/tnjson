package com.tikaflow.tnjson.bean;

import com.tikaflow.tnjson.json.JSONArray;
import com.tikaflow.tnjson.json.JSONObject;
import com.tikaflow.tnjson.type.ElementType;
import com.tikaflow.tnjson.util.Misc;
import lombok.Getter;
import lombok.val;

import static com.tikaflow.tnjson.type.ElementType.*;

public class Element<T> {
    private final ElementType type;
    @Getter
    private T value;

    public Class<?> getType() {
        switch (type) {
            case STRING:
                return String.class;
            case NUMBER:
                return Double.class;
            case OBJECT:
                return JSONObject.class;
            case ARRAY:
                return JSONArray.class;
            case BOOLEAN:
                return Boolean.class;
            case NULL:
            default:
                return null;
        }
    }

    public Element() {
        this.type = NULL;
    }

    public Element(T value) {
        if (value == null) {
            this.type = NULL;
            return;
        }

        if (value instanceof JSONObject) {
            this.type = OBJECT;
        } else if (value instanceof JSONArray) {
            this.type = ARRAY;
        } else if (value instanceof String) {
            this.type = STRING;
        } else if (value instanceof Double) {
            this.type = NUMBER;
        } else if (value instanceof Boolean) {
            this.type = BOOLEAN;
        } else {
            this.type = NULL;
            return;
        }

        this.value = value;
    }

    public String toString() {
        switch (type) {
            case STRING:
                return "\"" + value.toString() + "\"";
            case NUMBER:
                val str = value.toString();
                if (Misc.lastNotOf(str, '0') == '.') {
                    // for example, 1.0, we want to show it as 1
                    return str.substring(0, str.indexOf("."));
                }
                return str;
            case OBJECT:
            case ARRAY:
            case BOOLEAN:
                return value.toString();
            case NULL:
            default:
                return "null";
        }
    }

    public String format(String indent) {
        switch (type) {
            case OBJECT:
                return ((JSONObject) value).format(indent);
            case ARRAY:
                return ((JSONArray) value).format(indent);
            case STRING:
            case NUMBER:
            case BOOLEAN:
            case NULL:
            default:
                return toString();
        }
    }
}
