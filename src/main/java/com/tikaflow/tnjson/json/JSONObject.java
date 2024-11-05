package com.tikaflow.tnjson.json;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public <T> T convertTo(Class<T> clazz) {
        if (clazz.isInstance(this)) {
            return clazz.cast(this);
        }

        T bean;
        try {
            bean = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (var item : items.entrySet()) {
            val key = item.getKey();
            val element = item.getValue();
            val value = element.getValue();

            try {
                val type = element.getType();
                if (type == null) {
                    continue;
                }

                val field = clazz.getDeclaredField(key);
                field.setAccessible(true);
                val fieldType = field.getType();
                val setterName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
                val setter = clazz.getMethod(setterName, fieldType);

                if (type == JSONObject.class) {
                    val fieldValue = ((JSONObject) value).convertTo(fieldType);
                    setter.invoke(bean, fieldValue);
                } else if (type == JSONArray.class) {
                    if (!fieldType.isArray()) {
                        continue; // ignore
                    }

                    assignArray(fieldType, (JSONArray) value, setter, bean);
                } else if (type == Double.class) {
                    if (fieldType == byte.class || fieldType == Byte.class) {
                        setter.invoke(bean, ((Double) value).byteValue());
                    } else if (fieldType == short.class || fieldType == Short.class) {
                        setter.invoke(bean, ((Double) value).shortValue());
                    } else if (fieldType == char.class || fieldType == Character.class) {
                        setter.invoke(bean, (char) ((Double) value).shortValue());
                    } else if (fieldType == int.class || fieldType == Integer.class) {
                        setter.invoke(bean, ((Double) value).intValue());
                    } else if (fieldType == long.class || fieldType == Long.class) {
                        setter.invoke(bean, ((Double) value).longValue());
                    } else if (fieldType == float.class || fieldType == Float.class) {
                        setter.invoke(bean, ((Double) value).floatValue());
                    } else if (fieldType == double.class || fieldType == Double.class) {
                        setter.invoke(bean, value);
                    } else if (fieldType == String.class) {
                        setter.invoke(bean, value.toString());
                    } else {
                        throw new RuntimeException("Unsupported type: " + fieldType);
                    }
                } else {
                    setter.invoke(bean, value);
                }
            } catch (NoSuchFieldException | NoSuchMethodException e) {
                continue; // ignore
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return bean;
    }

    private static <T> void assignArray(Class<?> fieldType, JSONArray value, Method setter, T bean)
            throws IllegalAccessException, InvocationTargetException {
        // field is also array
        val arrType = fieldType.getComponentType();
        val arrLen = value.length();
        Object arr = Array.newInstance(arrType, arrLen);

        for (int i = 0; i < arrLen; i++) {
            val arrItem = value.at(i);
            val itemType = value.typeAt(i);
            // check compatible
            if (arrItem == null) {
                return;
            }
            if (itemType == JSONObject.class) {
                Array.set(arr, i, ((JSONObject) arrItem).convertTo(arrType));
                continue;
            } else if ((Number.class.isAssignableFrom(arrType) && Number.class.isAssignableFrom(itemType))
                    || arrType.isAssignableFrom(itemType)) {
                Array.set(arr, i, arrItem);
                continue;
            }

            return;
        }

        setter.invoke(bean, arr);
    }

    public String toString() {
        var sb = new StringBuilder("{");

        for (var item : items.entrySet()) {
            sb.append("\"")
                    .append(item.getKey())
                    .append("\": ")
                    .append(item.getValue().toString())
                    .append(", ");
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
                    .append("\": ")
                    .append(item.getValue().format(itemIndent))
                    .append(",\n");
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
