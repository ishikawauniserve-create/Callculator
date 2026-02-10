package calculator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ArithmeticOperation {

    public String calculate(String expr) {

        expr = normalize(expr);

        List<String> postfix = toPostfix(expr);//toPostfに式が入ってる

        double value = evalPostfix(postfix);

        if (value == (long) value) {
            return String.valueOf((long) value);
        }

        return String.valueOf(value);
    }

    // 記号をJava計算用に変換
    private String normalize(String s) {
        return s.replace("x", "*")
                .replace("÷", "/");
    }

    // 優先順位
    private int prec(String op) {

        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "%":
                return 2;
        }
        return 0;
    }

 
    private boolean isOp(String s) {
        return "+-*/%".contains(s);
    }

    // 中置 → 後置
    private List<String> toPostfix(String expr) {//Stringは

        List<String> out = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        StringBuilder num = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) { //exprは式を持って来ている。lengthは文字を数える、
        											//1+1のときは３文字 i <３なので3回forが回る。iに３回まわす

            char c = expr.charAt(i); //charAtはｎ番目をもってくる

            if (Character.isDigit(c) || c == '.') {
            	
                num.append(c);
                continue;
            }

            if (num.length() > 0) {
                out.add(num.toString());
                num.setLength(0);
            }

            String token = String.valueOf(c);

            if (isOp(token)) {

                while (!stack.isEmpty()
                        && isOp(stack.peek())
                        && prec(stack.peek()) >= prec(token)) {
                    out.add(stack.pop());
                }
                stack.push(token);
            }

            else if (c == '(') {
                stack.push("(");
            }

            else if (c == ')') {
                while (!stack.peek().equals("(")) {
                    out.add(stack.pop());
                }
                stack.pop();
            }
        }

        if (num.length() > 0) out.add(num.toString());

        while (!stack.isEmpty()) {
            out.add(stack.pop());
        }

        return out;
    }

    // 後置評価
    private double evalPostfix(List<String> list) {

        Stack<Double> st = new Stack<>();

        for (String t : list) {

            if (isOp(t)) {

                double b = st.pop();
                double a = st.pop();

                switch (t) {
                    case "+": st.push(a + b); break;
                    case "-": st.push(a - b); break;
                    case "*": st.push(a * b); break;
                    case "/": st.push(a / b); break;
                    case "%": st.push(a % b); break;
                }

            } else {
                st.push(Double.parseDouble(t));
            }
        }

        return st.pop();//popって
    }
}
