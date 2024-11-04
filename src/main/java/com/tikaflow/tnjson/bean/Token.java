package com.tikaflow.tnjson.bean;

import com.tikaflow.tnjson.type.TokenType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    private TokenType type;
    private String content;
    private Double value;

    public String toString() {
        return "\"" + (type == TokenType.NUMBER ? value.toString() : content) + "\"";
    }
}
