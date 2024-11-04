package com.tikaflow.tnjson.parse;

import com.tikaflow.tnjson.bean.Token;
import com.tikaflow.tnjson.json.JSONArray;
import com.tikaflow.tnjson.json.JSONObject;
import com.tikaflow.tnjson.type.TokenType;
import lombok.val;
import lombok.var;

import java.util.ArrayList;

import static com.tikaflow.tnjson.type.TokenType.*;

public class Parser {
    private final ArrayList<Token> tokens;
    private int index = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        return tokens.get(index);
    }

    private Token next() {
        val token = peek();
        if (token.getType() == EOF) {
            return null;
        }

        index++;
        return token;
    }

    private void eat() {
        index--;
    }

    private Token match(TokenType type, String what) {
        val token = next();
        if (token == null || token.getType() != type) {
            throw new RuntimeException("expect " + what + " but got: " + (token == null ? "null" : token.toString()));
        }

        return token;
    }

    private void parseItem(JSONObject obj) {
        val key = match(STRING, "key").getContent();
        match(COLON, "colon");

        val token = next();
        if (token == null) {
            throw new RuntimeException("expect value but got EOF");
        }

        switch (token.getType()) {
            case LEFT_BRACE:
                eat();
                obj.put(key, parseObject());
                break;
            case LEFT_BRACKET:
                eat();
                obj.put(key, parseArray());
                break;
            case STRING:
                obj.put(key, token.getContent());
                break;
            case NUMBER:
                obj.put(key, token.getValue());
                break;
            case TRUE:
                obj.put(key, true);
                break;
            case FALSE:
                obj.put(key, false);
                break;
            case NULL:
                obj.putNull(key);
                break;
            default:
                throw new RuntimeException("expect value but got: " + token);
        }
    }

    private void parseElement(JSONArray arr) {
        val token = next();
        if (token == null) {
            throw new RuntimeException("expect value but got EOF");
        }

        switch (token.getType()) {
            case LEFT_BRACE:
                eat();
                arr.push(parseObject());
                break;
            case LEFT_BRACKET:
                eat();
                arr.push(parseArray());
                break;
            case STRING:
                arr.push(token.getContent());
                break;
            case NUMBER:
                arr.push(token.getValue());
                break;
            case TRUE:
                arr.push(true);
                break;
            case FALSE:
                arr.push(false);
                break;
            case NULL:
                arr.pushNull();
                break;
            default:
                throw new RuntimeException("expect value but got: " + token);
        }
    }

    public JSONObject parseObject() {
        match(LEFT_BRACE, "open brace");

        var token = peek();
        if (token.getType() == EOF) {
            throw new RuntimeException("expect value but got EOF");
        }

        val obj = new JSONObject();
        if (token.getType() != RIGHT_BRACE) {
            while (true) {
                // parse item and add
                parseItem(obj);

                token = next();
                if (token == null || token.getType() != COMMA) {
                    eat();
                    break;
                }
            }
        }

        match(RIGHT_BRACE, "close brace");
        return obj;
    }

    public JSONArray parseArray() {
        match(LEFT_BRACKET, "open brace");

        var token = peek();
        if (token.getType() == EOF) {
            throw new RuntimeException("expect value but got EOF");
        }

        val arr = new JSONArray();
        if (token.getType() != RIGHT_BRACKET) {
            while (true) {
                // parse element and add
                parseElement(arr);

                token = next();
                if (token == null || token.getType() != COMMA) {
                    eat();
                    break;
                }
            }
        }

        match(RIGHT_BRACKET, "close bracket");
        return arr;
    }
}
