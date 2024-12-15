package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Lox {

    private static final Interpreter interpreter = new Interpreter();
    static boolean promptMode = false;
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void  runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        promptMode = true;
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for (;;) {
            System.out.println("> ");
            String line = reader.readLine();
            if (line.equals("exit")) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        if (promptMode && !source.endsWith(";")) {
            Expr expr = parser.parseExpr();
            if (hadError) return;
            System.out.println(interpreter.interpret(expr));
            return;
        }
        List<Stmt> statements = parser.parse();
        if (hadError) return;
        interpreter.interpret(statements);
    }

    static void error(Token token, String message) {
        int line = token.line;
        int line_offset = token.line_offset;
        String line_string = token.line_string;
        error(line, line_offset, line_string, message);
    }

    static void error(int line, int line_offset, String line_string, String message) {
        hadError = true;
        System.err.println("Error:" + message);
        System.err.println(line + " |" + line_string);
        int times = line_offset + String.valueOf(line).length() + 1;
        String indent = String.join("", Collections.nCopies(times, " "));
        System.err.println(indent + "^");
    }

    static void runtimeError(RuntimeError run_time_error) {
        Token token = run_time_error.token;
        String message = run_time_error.getMessage();
        error(token, message);
        hadRuntimeError = true;
    }
}