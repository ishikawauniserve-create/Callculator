package calculator.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ArithmeticOperation {

    public String calculate(String expr) {

        expr = normalize(expr);

        List<String> postfix = toPostfix(expr);//toPostfに式が入ってる

        try {
        	
            BigDecimal value = evalPostfix(postfix);
            
            return formatResult(value);
            
        } catch (ArithmeticException | NumberFormatException | IllegalStateException e) {
        	
            return "Error";
            
        }
        
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


 // 中置 → 後置 (Shunting-yard アルゴリズム)
    private List<String> toPostfix(String expr) {//Stringは

        List<String> out = new ArrayList<>();

        Stack<String> stack = new Stack<>();

        StringBuilder num = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) { //exprは式を持って来ている。lengthは文字を数える、
        											//1+1のときは３文字 i <３なので3回forが回る。iに３回まわす

            char c = expr.charAt(i); //charAtはｎ番目をもってくる

            if (Character.isWhitespace(c)) {

                continue;

            }

            if (isUnarySign(expr, i) && nextCharIsParen(expr, i)) {
            	
                out.add("0");
                
                String token = String.valueOf(c);
                
                while (!stack.isEmpty()
                		
                        && isOp(stack.peek())
                        
                        && prec(stack.peek()) >= prec(token)) {
                	
                    out.add(stack.pop());
                    
                }
                
                stack.push(token);
                
                continue;
                
            }

            if (Character.isDigit(c) || c == '.' || isUnarySign(expr, i)) {
            	
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
            	
            	 if (stack.isEmpty()) {
            		 
                     throw new IllegalStateException("Mismatched parentheses");
                     
                 }
 
                while (!stack.peek().equals("(")) {
                	
                    out.add(stack.pop());
                    
                }
                
                stack.pop();
                
            }
            
        }
 

        if (num.length() > 0) out.add(num.toString());

        while (!stack.isEmpty()) {
        	
        	  if (stack.peek().equals("(")) {
        		  
        		  
                  throw new IllegalStateException("Mismatched parentheses");
                  
              }
        	  
            out.add(stack.pop());
            
        }

        return out;
        
    }

    // 後置評価
    private boolean nextCharIsParen(String expr, int index) {
    	
        int nextIndex = index + 1;
        
        if (nextIndex >= expr.length()) {
        	
            return false;
            
        }
        
        char next = expr.charAt(nextIndex);
        
        return next == '(';
        
    }

    private boolean isUnarySign(String expr, int index) {
    	
        char c = expr.charAt(index);
        
        if (c != '+' && c != '-') {
        	
            return false;
            
        }
        
        if (index == 0) {
        	
            return true;
            
        }
        
        char prev = expr.charAt(index - 1);
        
        return isOp(String.valueOf(prev)) || prev == '(';
        
    }
    
    
    private BigDecimal evalPostfix(List<String> list) {
    	
    	Stack<BigDecimal> st = new Stack<>();
    	
    	for (String t : list) {
    		
    		if (isOp(t)) {
    			
    			if (st.size() < 2) {
    				
                    throw new IllegalArgumentException("Invalid expression");
                    
                }
    			
    			  BigDecimal b = st.pop();
    			  
                  BigDecimal a = st.pop();
                  
                  switch (t) {
                  	case "+": st.push(a.add(b)); break;
                  	
                  	case "-": st.push(a.subtract(b)); break;
                  	
                  	case "*": st.push(a.multiply(b)); break;
                  	
                  	case "/":
                  		
                      if (b.compareTo(BigDecimal.ZERO) == 0) {
                    	  
                          throw new ArithmeticException("Division by zero");
                          
                      }
                      
                      st.push(a.divide(b, MathContext.DECIMAL128));
                      
                      break;
                   
                  case "%":
                      if (b.compareTo(BigDecimal.ZERO) == 0) {
                    	  
                          throw new ArithmeticException("Division by zero");
                          
                      }
                      
                      st.push(a.remainder(b));
                      
                      break;
                      
                  }
                 
    		} else {

    			st.push(new BigDecimal(t));

    		}

    		if (st.size() != 1) {

    	         throw new IllegalArgumentException("Invalid expression");

    	    }

    	}

    	return st.pop();

    }

    private String formatResult(BigDecimal value) {

        if (value.compareTo(BigDecimal.ZERO) == 0) {

            return "0";
            
        }
        
        BigDecimal normalized = value.stripTrailingZeros();
        
        String text = normalized.toPlainString();
        
        if (text.endsWith(".")) {
        	
            text = text.substring(0, text.length() - 1);
            
        }
        
        return text;
        
    }
    
}