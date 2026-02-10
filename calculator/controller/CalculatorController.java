package calculator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import calculator.model.ArithmeticOperation;
import calculator.view.CalculatorFrame;

public class CalculatorController implements ActionListener {
	
	private final JTextField result;//JTextField型、1行のテキスト入力・表示を扱うGUI部品
	private final JButton[] buttons;//電卓の全ボタンを参照
	private final ArithmeticOperation operation;//計算処理を使うためにControllerが持つ

	// = 連打用の記憶
	private boolean justEvaluated;
	private String lastOperator;
	private String lastOperand;
	private String lastExpr;

	


	//別クラスとの接続処理
    public CalculatorController(CalculatorFrame frame, ArithmeticOperation operation) {
        this.result = frame.getResultField();//Frame内のresultを参照
        this.buttons = frame.getButtons();//Frame内のbuttonsを参照
        this.operation = operation;//計算係をControllerの手元に置く

        for (JButton b : buttons) {//配列を参照して変数ｂに
        	if (b != null) {
        		b.addActionListener(this);    //nullの場合は登録しない、このコントローラーが信号受け取る
        	}
        }

        this.result.setText("0"); //初期は0を表示

        }


    //ボタン内処理
	@Override
	public void actionPerformed(ActionEvent e) {//イベント発火時、自動で呼び出される　actionEvent型を渡す

		JButton b = (JButton) e.getSource();//リンクを受け取り、JButtonで扱えるようにｂに変換

		String buttonlabel = b.getText();//ボタンの表示文字を習得する

		handleButton(buttonlabel);//トリガーから信号を受け取り処理に投げる_変換→取り出し→渡す（イベント処理）
	}

	private void handleButton(String label) {


		//実装後に直す必要アリ_空白部分にボタン実装予定のため
		if (label == null || label.isEmpty()) {

			return;

		}


		//テキスト表示のガード
		String text = result.getText();//画面に表示された文字列を習得する

		//実装後に直す必要アリ
		if (text == null ) {
			
			text ="";
			
		}


		// 全削除処理
		if (label.equals("AC")) {
			
		    result.setText("0");
		    
		    justEvaluated = false;
		    
		    lastOperator = null;
		    
		    lastOperand = null;
		    
		    lastExpr = null;
		    
		    return;
		}


		// 1文字削除処理
		if (label.equals("C")) {
			
			justEvaluated = false;
			
			if (text.length() <= 1) {
				
				result.setText("0");
				
				return;
				
			}
			
		    text = text.substring(0, text.length() - 1);//文字列の末尾から1文字消す
		    
		    if (text.isEmpty()) {
		    	
		    	text = "0";
		    	
		    
		    }
		    result.setText(text);
		    
		    return;
		    
		}
	
		if (label.equals("=")) {
			
			if (text.isEmpty() || text.equals("Error")) return;
			
			if (justEvaluated && lastOperator != null && lastOperand != null && isNumberLike(text)) {
				
				String chainedExpr = text + lastOperator + lastOperand;
				
				String chainedResult = operation.calculate(chainedExpr);
				
				result.setText(chainedResult);
				
				lastExpr = chainedExpr;
				
				justEvaluated = true;
				
				return;
				
			}
		    
			
			String expression = trimTrailingOperators(text);
			
			String calculated = operation.calculate(expression);
			
			if (!calculated.equals("Error")) {
				
				captureLastBinaryOperation(expression);
				
			}
			
			lastExpr = expression;
			
			justEvaluated = true;
			
			result.setText(calculated);
			
			return;
			
		}
		
		if (isDigit(label)) {
			
			result.setText(appendDigit(text, label));
			
			return;
			
		}
		
		if (isOperator(label)) {
			
			justEvaluated = false;
			
			result.setText(appendOperator(text, label));
			
			return;
			
		}
		
	
		if (label.equals(".")) {
			
		result.setText(appendDecimal(text));
		
		justEvaluated = false;
		
		return;
		
		}
		
		if (label.equals("+/-")) {
			
			result.setText(toggleSign(text));
			
			justEvaluated = false;
			
			return;
			
		}
		

		if (label.equals("(")) {
			
			result.setText(appendLeftParen(text));
			
			justEvaluated = false;
			
			return;
			
		}

		if (label.equals(")")) {
			
			result.setText(appendRightParen(text));
			
			justEvaluated = false;
			
			return;
			
		}

		if (label.equals("%")) {
			
			result.setText(applyPercent(text));
			
			justEvaluated = false;
			
			return;
			
		}
	}
	

	private boolean isDigit(String label) {
		
		return label.length() == 1 && Character.isDigit(label.charAt(0));

	}

	private boolean isOperator(String label) {
		
		return label.equals("+") || label.equals("-") || label.equals("x")
				
				|| label.equals("÷") ;
	}
	
	private String appendDigit(String text, String digit) {

	    if (justEvaluated) {
	        justEvaluated = false;
	        return digit;
	    }

	    if (text.equals("Error")) return digit;

	    if (text.equals("0") && digit.equals("0")) return "0";

	    if (text.equals("0")) return digit;
	    
	    String currentNumber = getCurrentNumber(text);
	    
	    if (currentNumber.equals("0") && !digit.equals("0")) {
	    	
	    	return text.substring(0, text.length() - 1) + digit;
	    	
	    }
	    
	    if (currentNumber.equals("0") && digit.equals("0")) {
	    	
	    	return text;
	    	
	    }
	    

	    return text + digit;
	    
	}


	private String appendOperator(String text, String op) {
		
		if (text.isEmpty() || text.equals("Error")) {
			
			return "0" + op;
			
		}
		
		char last = text.charAt(text.length() - 1);
		
		if (isTrailingOperator(last)) {
			
			return text.substring(0, text.length() - 1) + op;
			
		}
		
		return text + op;
		
	}
	

	private boolean isTrailingOperator(char c) {
		
		return c == '+' || c == '-' || c == 'x' || c == '÷' ;
		
	}
	

	private String appendDecimal(String text) {
		
		if (text.equals("Error")) {
			
			return "0.";
			
		}
		
		if (text.isEmpty() || isTrailingOperator(text.charAt(text.length() - 1)) || text.endsWith("(")) {
			
			return text + "0.";
			
		}
		
		String currentNumber = getCurrentNumber(text);
		
		if (currentNumber.contains(".")) {
			
			return text;
			
		}
		
		return text + ".";
		
	}

	
	private String appendLeftParen(String text) {
		
		if (text.equals("Error")) {
			
			return "(";
			
		}
		
		if (text.equals("0")) {
			
			return "(";
			
		}
		
		char last = text.charAt(text.length() - 1);
		
		if (Character.isDigit(last) || last == ')') {
			
			return text + "x(";
			
		}
		
		return text + "(";
		
	}

	
	private String appendRightParen(String text) {
		
		if (text.equals("Error")) {
			
			return "0";
			
		}
		
		int openCount = countChar(text, '(');
		
		int closeCount = countChar(text, ')');
		
		if (openCount <= closeCount) {
			
			return text;
			
		}
		char last = text.charAt(text.length() - 1);
		
		if (isTrailingOperator(last) || last == '(') {
			
			return text;
			
		}
		
		return text + ")";
		
	}

	
	private int countChar(String text, char target) {
		
		int count = 0;
		
		for (int i = 0; i < text.length(); i++) {
			
			if (text.charAt(i) == target) {
				
				count++;
				
			}
			
		}
		
		return count;
		
	}

	private String getCurrentNumber(String text) {
		
		int index = text.length() - 1;
		
		while (index >= 0) {
			
			char c = text.charAt(index);
			
			if (!Character.isDigit(c) && c != '.') {
				
				break;
				
			}
			
			index--;
			
		}
		
		return text.substring(index + 1);
		
	}
	

	private String toggleSign(String text) {
		
		if (text.equals("Error")) {
			
			return "0";
			
		}
		
		int end = text.length() - 1;
		
		while (end >= 0 && !Character.isDigit(text.charAt(end)) && text.charAt(end) != '.') {
			
			end--;
			
		}
		
		if (end < 0) {
			
			return text;
			
		}
		
		int start = end;
		
		while (start >= 0 && (Character.isDigit(text.charAt(start)) || text.charAt(start) == '.')) {
			
			start--;
			
		}
		
		if (start >= 0 && text.charAt(start) == '-') {
			
			if (start == 0 || isTrailingOperator(text.charAt(start - 1)) || text.charAt(start - 1) == '(') {
				
				return text.substring(0, start) + text.substring(start + 1);
				
			}
			
		}
		
		return text.substring(0, start + 1) + "-" + text.substring(start + 1);
		
	}
	
	
	
	private String applyPercent(String text) {
		
		if (text.equals("Error")) {
			
			return "0";
			
		}
		int end = text.length() - 1;
		
		while (end >= 0 && !Character.isDigit(text.charAt(end)) && text.charAt(end) != '.') {
			
			end--;
			
		}
		
		if (end < 0) {
			
			return text;
			
		}
		int start = end;
		
		while (start >= 0 && (Character.isDigit(text.charAt(start)) || text.charAt(start) == '.')) {
			
			start--;
			
		}
		
		if (start >= 0 && text.charAt(start) == '-') {
			
			if (start == 0 || isTrailingOperator(text.charAt(start - 1)) || text.charAt(start - 1) == '(') {
				
				start--;
				
			}
			
		}

		
		String numberText = text.substring(start + 1, end + 1);
		
		if (!isNumberLike(numberText)) {
			return text;
			
		}
		
		String percentValue = operation.calculate(numberText + "/100");
		
		if (percentValue.equals("Error")) {
			
			return "Error";
			
		}
		
		return text.substring(0, start + 1) + percentValue + text.substring(end + 1);
		
	}

	private boolean isNumberLike(String value) {
		
		if (value == null || value.isEmpty()) {
			
			return false;
			
		}
		
		int dots = 0;
		
		for (int i = 0; i < value.length(); i++) {
			
			char c = value.charAt(i);
			
			if (i == 0 && c == '-') {
				
				continue;
				
			}
			if (c == '.') {
				
				dots++;
				
				if (dots > 1) {
					
					return false;
					
				}
				
				continue;
				
			}
			
			if (!Character.isDigit(c)) {
				
				return false;
				
			}
			
		}
		
		return true;
		
	}

	
	private String trimTrailingOperators(String text) {
		
		String trimmed = text;
		
		while (!trimmed.isEmpty()) {
			
			char last = trimmed.charAt(trimmed.length() - 1);
			
			if (isTrailingOperator(last)) {
				
				trimmed = trimmed.substring(0, trimmed.length() - 1);
				
				continue;
				
			}
			
			break;
			
		}
		
		if (trimmed.isEmpty()) {
			
			return "0";
			
		}
	
		return trimmed;
		
	}

	private void captureLastBinaryOperation(String expression) {
		
		lastOperator = null;
		
		lastOperand = null;
		

		int depth = 0;
		
		for (int i = expression.length() - 1; i >= 0; i--) {
			
			char c = expression.charAt(i);
			
			if (c == ')') {
				
				depth++;
				
				continue;
			}
			
			if (c == '(') {
				
				depth--;
				
				continue;
				
			}
			
			if (depth != 0) {
				
				continue;
				
			}
			if (c == '+' || c == '-' || c == 'x' || c == '÷') {
				
				if (c == '-' && (i == 0 || isTrailingOperator(expression.charAt(i - 1)) || expression.charAt(i - 1) == '(')) {
					
					continue;
					
				}
				String right = expression.substring(i + 1);
				
				if (!isNumberLike(right)) {
					
					return;
					
				}
				lastOperator = String.valueOf(c);
				
				lastOperand = right;
				
				return;
				
			}
		}
	}
}
		
	