package com.craftinginterpreters.lox;

public class Token {
    final TokenType type;
    final String lexeme;
    final String line_string;
    final Object literal;
    final int line;
    final int line_offset;

    Token(TokenType type, String lexeme, String line_string, Object literal, int line, int line_offset) {
        this.type = type;
        this.lexeme = lexeme;
        this.line_string = line_string;
        this.literal = literal;
        this.line = line;
        this.line_offset = line_offset;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
