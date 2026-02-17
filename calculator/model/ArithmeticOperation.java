package calculator.model;

import java.math.BigDecimal; //正確な数値計算行うクラス
import java.math.MathContext;//端数の切り捨て四捨五入を行う 割り算の精度ルールをおこなう
import java.text.DecimalFormat;
import java.util.ArrayList;//可変サイズ配列（伸びる配列）
import java.util.List;//リストのインターフェース型
import java.util.Stack;

public class ArithmeticOperation {

	
	// =========================
	// ■ 外部公開メソッド
	// =========================

	public String calculate(String expr) {

		try {

			// 記号正規化
			String normalized = normalize(expr);

			List<String> postfix = toPostfix(normalized);//toPostfix計算する順番を数字を並べ替える。

			BigDecimal value = evalPostfix(postfix);//evalPostfix並べ替えたあとの式を、実際に計算して答えを出す担当者

			return formatResult(value);

		} catch (ArithmeticException | IllegalStateException | IllegalArgumentException e) {

			return "Error";

		}

	}

	//前処理

	// 文字変換
	private String normalize(String s) {

		return s.replace("x", "*")

				.replace("÷", "/")

				.replace("√", "sqrt")

				.replace("π", "3.1415926535897932384626433832795028841971");
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

		case "mod":

			return 2;

		case "^":

			return 3;

		case "!":

		case "sqrt":

			return 4;

		}

		return 0;

	}

	//トークン判定

	//演算しかどうか判定
	private boolean isOp(String s) {

		return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") || s.equals("%")

				|| s.equals("^") || s.equals("mod");

	}

	//関数かどうか
	private boolean isFunc(String s) {

		return s.equals("sqrt") || s.equals("log");

	}

	//単行符号かどうかを判断するメソッド
	private boolean isUnarySign(String expr, int index) {

		char c = expr.charAt(index);

		if (c != '+' && c != '-') {

			return false;

		}

		if (index == 0) {

			return true;

		}

		char prev = expr.charAt(index - 1);

		return isOp(String.valueOf(prev)) || prev == '(';//インデックスの前が演算子または（

	}

	private boolean isRightAssociative(String op) {

		return op.equals("^");

	}

	//Shunting-yard

	// 中置 → 後置 (Shunting-yard アルゴリズム)
	private List<String> toPostfix(String expr) {

		List<String> out = new ArrayList<>();

		Stack<String> stack = new Stack<>();

		StringBuilder num = new StringBuilder();//これから数字を1文字ずつ集めますよ。今読んでいる数字を完成させるための一時保管バッファ

		for (int i = 0; i < expr.length(); i++) {

			char c = expr.charAt(i);

			if (Character.isWhitespace(c)) {//空白スキップ

				continue;

			}

			if (expr.startsWith("sqrt", i) || expr.startsWith("log", i)) {

				// 関数トークン sqrt の検出 / log の検出
				String func;

				if (expr.startsWith("sqrt", i)) {

					func = "sqrt";

				} else {

					func = "log";

				}

				// 直前に数値があれば確定させる
				if (num.length() > 0) {

					out.add(num.toString());

					num.setLength(0);

				}

				stack.push(func); // 関数はスタックへ

				i += func.length() - 1; // "sqrt" の残り3文字をスキップ

				continue;

			}

			//この文字が数値が単項符号、小数点、数値がトークンならbuilderへ
			if (Character.isDigit(c) || c == '.' || isUnarySign(expr, i)) {

				num.append(c); //後ろについか

				continue;
			}

			//演算子、()をスタックに保存し、優先順位に取り出す
			if (num.length() > 0) {

				out.add(num.toString());//stringへ追加

				num.setLength(0);

			}

			// 階乗は後置単項演算子なので即出力
			if (c == '!') {

				out.add("!");

				continue;
			}

			//mod演算子の検出

			if (expr.startsWith("mod", i)) {

				String modtoken = "mod";

				while (!stack.isEmpty()

						&& isOp(stack.peek())

						&& (prec(stack.peek()) > prec(modtoken)

								|| (prec(stack.peek()) == prec(modtoken) && !isRightAssociative(modtoken)))) {

					out.add(stack.pop());
				}

				stack.push(modtoken);

				i += 2;

				continue;

			} ////////////////////////////////////////////////////////////////////////////////昨日はここまで

			// 現在の文字をトークン化
			String token = String.valueOf(c);

			// 演算子トークンの場合、スタック上の優先度以上の演算子を先に出力へ退避
			if (isOp(token)) {

				while (!stack.isEmpty() && isOp(stack.peek()) &&

						 (prec(stack.peek()) > prec(token) ||

						(prec(stack.peek()) == prec(token) && !isRightAssociative(token))))

				{

					out.add(stack.pop());
				}

				// 現在の演算子をスタックへ積む
				stack.push(token);
			}

			// 左括弧はそのままスタックへ
			else if (c == '(') {

				stack.push("(");
			}

			// 右括弧の場合、対応する左括弧までスタックを出力へ移動
			else if (c == ')') {

				if (stack.isEmpty()) {

					throw new IllegalStateException("Mismatched parentheses");
				}

				// "(" が出るまで演算子を出力へ
				while (!stack.peek().equals("(")) {

					out.add(stack.pop());
				}

				// 対応する "(" を破棄（出力しない）
				stack.pop();

				// 直前が関数なら出力へ
				if (!stack.isEmpty() && isFunc(stack.peek())) {

					out.add(stack.pop());
				}

			}
		}

		// ループ終了後、数値バッファが残っていれば出力へ
		if (num.length() > 0)

		{

			out.add(num.toString());

		}

		// スタックに残った演算子をすべて出力へ
		while (!stack.isEmpty()) {

			if (stack.peek().equals("(")) {

				throw new IllegalStateException("Mismatched parentheses");

			}

			out.add(stack.pop());
		}

		// 後置トークン列を返す
		return out;

	}

	//数学処理

	//√計算処理
	private BigDecimal sqrt(BigDecimal x) {

		if (x.compareTo(BigDecimal.ZERO) < 0) {//Bigdecimalは＜が使えない

			throw new ArithmeticException("Negative sqrt");

		}

		double d = Math.sqrt(x.doubleValue());

		return new BigDecimal(d, MathContext.DECIMAL128);

	}

	//log10計算処理
	private BigDecimal log10(BigDecimal x) {

		if (x.compareTo(BigDecimal.ZERO) <= 0) {

			throw new ArithmeticException("Invvalid log argument");
		}

		double d = Math.log10(x.doubleValue());

		return new BigDecimal(d, MathContext.DECIMAL128);

	}

	//階乗計算処理
	private BigDecimal factorial(int n) {

		if (n > 100)

			throw new ArithmeticException("Factorial too large");

		BigDecimal r = BigDecimal.ONE;

		for (int i = 2; i <= n; i++) {

			r = r.multiply(BigDecimal.valueOf(i));

		}

		return r;
	}

	// 後置評価

	//ここで後倒置計算処理
	private BigDecimal evalPostfix(List<String> list) {

		Stack<BigDecimal> st = new Stack<>();

		for (String t : list) {

			// ===== sqrt / log（単項）=====
			if (t.equals("sqrt") || t.equals("log")) {

				if (st.isEmpty())

					throw new IllegalArgumentException("sqrt needs operand");

				BigDecimal arg = st.pop();

				if (t.equals("sqrt")) {

					st.push(sqrt(arg));

				} else {

					st.push(log10(arg));

				}

				continue;
			}

			// ===== 階乗（単項・後置）=====
			if (t.equals("!")) {

				if (st.isEmpty())

					throw new IllegalArgumentException("Invalid factorial");

				BigDecimal v = st.pop();

				if (v.scale() > 0 || v.compareTo(BigDecimal.ZERO) < 0)

					throw new IllegalArgumentException("Factorial needs non-negative integer");

				st.push(factorial(v.intValueExact()));

				continue;
			}

			// ===== 二項演算子 =====
			if (isOp(t)) {

				if (st.size() < 2)
					throw new IllegalArgumentException("Invalid expression");

				BigDecimal b = st.pop();

				BigDecimal a = st.pop();

				switch (t) {

				case "+":

					st.push(a.add(b));

					break;

				case "-":

					st.push(a.subtract(b));

					break;

				case "*":

					st.push(a.multiply(b));

					break;

				case "/":

					if (b.compareTo(BigDecimal.ZERO) == 0)

						throw new ArithmeticException("Division by zero");

					st.push(a.divide(b, MathContext.DECIMAL128));

					break;

				case "%":
					
				case "mod":

					if (b.compareTo(BigDecimal.ZERO) == 0)

						throw new ArithmeticException("Division by zero");

					st.push(a.remainder(b));
					
					break;
					
				case "^":
					
					double power = Math.pow(a.doubleValue(), b.doubleValue());
					
					if(Double.isNaN(power) || Double.isFinite(power))
						
						throw new ArithmeticException("Invalid power");
						
						st.push(new BigDecimal(power, MathContext.DECIMAL128));
						
					break;
				}

				continue;
			}

			// ===== 数値 =====
			st.push(new BigDecimal(t));
		}

		if (st.size() != 1)

			throw new IllegalArgumentException("Invalid expression");

		return st.pop();
	}

	// 表示整形

	//計算結果がこっちに
	private String formatResult(BigDecimal value) {

		if (value.compareTo(BigDecimal.ZERO) == 0) {

			return "0";
		}

		BigDecimal normalized = value.stripTrailingZeros();

		String text = normalized.toPlainString();

		if (text.endsWith(".")) {

			text = text.substring(0, text.length() - 1);
		}

		// ===== カンマ区切り追加 =====
		if (!text.contains(".")) {

			DecimalFormat df = new DecimalFormat("#,###");

			return df.format(new BigDecimal(text));

		}

		// 小数ありは分離して処理
		String[] parts = text.split("\\.");

		DecimalFormat df = new DecimalFormat("#,###");

		return df.format(new BigDecimal(parts[0])) + "." + parts[1];
	}
}
