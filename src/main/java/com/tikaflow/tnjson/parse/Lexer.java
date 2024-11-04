package com.tikaflow.tnjson.parse;

import com.tikaflow.tnjson.bean.Token;
import com.tikaflow.tnjson.util.Misc;
import lombok.val;
import lombok.var;

import java.util.ArrayList;

import static com.tikaflow.tnjson.type.TokenType.*;

public class Lexer {
    private final String json;
    private int pos = 0;

    public Lexer(String json) {
        this.json = json;
    }

    private char peek() {
        if (pos >= json.length()) {
            return '\0';
        }
        return json.charAt(pos);
    }

    private char next() {
        val c = peek();
        pos++;
        return c;
    }

    private void eat() {
        pos--;
    }

    private char skip() {
        var c = next();
        while (c == '\t' || c == '\n' || c == '\r' || c == ' ') {
            c = next();
        }
        return c;
    }

    private char parseInteger(char c) {
        if (c == '-') {
            c = next();
        }

        if (c == '0') {
            c = next();
        } else if (c >= '1' && c <= '9') {
            while (c >= '0' && c <= '9') {
                c = next();
            }
        } else {
            throw new RuntimeException("Invalid number:" + c);
        }

        return c;
    }

    private char parseFraction(char c) {
        if (c != '.') {
            return c;
        }

        c = next();
        if (c >= '0' && c <= '9') {
            while (c >= '0' && c <= '9') {
                c = next();
            }
        } else {
            throw new RuntimeException("Invalid number:" + c);
        }

        return c;
    }

    private char parseExponent(char c) {
        if (c != 'e' && c != 'E') {
            return c;
        }

        c = next();
        if (c == '+' || c == '-') {
            c = next();
        }

        if (c >= '0' && c <= '9') {
            while (c >= '0' && c <= '9') {
                c = next();
            }
        }

        return c;
    }

    private void parseNumber(Token.TokenBuilder builder, char c) {
        val start = pos - 1;

        c = parseInteger(c);
        c = parseFraction(c);
        c = parseExponent(c);
        eat();
        val content = json.substring(start, pos);

        builder.type(NUMBER);
        builder.content(content);
        builder.value(Double.parseDouble(content));
    }

    private char parseHex() {
        val c1 = next();
        if (Misc.isNotHex(c1)) {
            throw new RuntimeException("Invalid hex character: " + c1);
        }
        val c2 = next();
        if (Misc.isNotHex(c2)) {
            throw new RuntimeException("Invalid hex character: " + c2);
        }
        val c3 = next();
        if (Misc.isNotHex(c3)) {
            throw new RuntimeException("Invalid hex character: " + c3);
        }
        val c4 = next();
        if (Misc.isNotHex(c4)) {
            throw new RuntimeException("Invalid hex character: " + c4);
        }

        val str = "" + c1 + c2 + c3 + c4;
        return (char) Integer.parseInt(str, 16);
    }

    private void parseString(Token.TokenBuilder builder) {
        val sb = new StringBuilder();
        while (true) {
            val c = next();
            if (c == '"') {
                break;
            }

            if (c == '\\') {
                val c2 = next();
                switch (c2) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        sb.append(parseHex());
                        break;
                    default:
                        throw new RuntimeException("Invalid escape character: " + c2);
                }
            } else {
                sb.append(c);
            }
        }

        builder.type(STRING);
        builder.content(sb.toString());
    }

    private String parseIdentifier() {
        val start = pos - 1;

        while (true) {
            val c = next();
            if (!Misc.isAlphaNum(c)) {
                eat();
                break;
            }
        }

        return json.substring(start, pos);
    }

    private Token scan() {
        val builder = Token.builder();
        val c = skip();

        switch (c) {
            case ':':
                builder.type(COLON);
                builder.content("" + c);
                break;
            case ',':
                builder.type(COMMA);
                builder.content("" + c);
                break;
            case '{':
                builder.type(LEFT_BRACE);
                builder.content("" + c);
                break;
            case '}':
                builder.type(RIGHT_BRACE);
                builder.content("" + c);
                break;
            case '[':
                builder.type(LEFT_BRACKET);
                builder.content("" + c);
                break;
            case ']':
                builder.type(RIGHT_BRACKET);
                builder.content("" + c);
                break;
            case '\0':
                builder.type(EOF);
                builder.content("EOF");
                break;
            default:
                if (c == '-' || Misc.isDigit(c)) {
                    parseNumber(builder, c);
                } else if (c == '"') {
                    parseString(builder);
                } else if (Misc.isAlpha(c)) {
                    val id = parseIdentifier();

                    switch (id) {
                        case "true":
                            builder.type(TRUE);
                            break;
                        case "false":
                            builder.type(FALSE);
                            break;
                        case "null":
                            builder.type(NULL);
                            break;
                        default:
                            throw new RuntimeException("Unexpected identifier: " + id);
                    }
                    builder.content(id);
                } else {
                    throw new RuntimeException("Unexpected character: " + c);
                }
        }

        return builder.build();
    }

    public ArrayList<Token> parse() {
        ArrayList<Token> list = new ArrayList<>();

        while (true) {
            val cur = scan();
            list.add(cur);

            if (cur.getType() == EOF) {
                break;
            }
        }

        return list;
    }
}
