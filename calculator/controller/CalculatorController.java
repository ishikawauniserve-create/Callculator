package calculator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import calculator.model.ArithmeticOperation;
import calculator.view.CalculatorFrame;

public class CalculatorController implements ActionListener {

	private final JTextField result;//JTextField型、1行のテキスト入力・表示を扱うGUI部品

	private final JTextField history;

	private final JButton[] buttons;//電卓の全ボタンを参照

	private final ArithmeticOperation operation;//計算処理を使うためにControllerが持つ

	// フラグ変数
	private boolean justEvaluated;//計算直後フラグ

	private String lastOperator;//直前演算子

	private String lastOperand;//直前オペラント（演算される値）

	private String lastExpr;//直前計算式

	//別クラスとの接続処理
	public CalculatorController(CalculatorFrame frame, ArithmeticOperation operation) {

		this.result = frame.getResultField();//Frame内のresultを参照

		this.buttons = frame.getButtons();//Frame内のbuttonsを参照

		this.history = frame.getHistoryField(); // ← ここ追加

		this.operation = operation;//計算係をControllerの手元に置く

		for (JButton b : buttons) {//配列を参照して変数ｂに

			if (b != null) {

				b.addActionListener(this);

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

		String text = result.getText();

		// エラー表示中の入力制御
		if (text.equals("Overflow") || text.equals("Error")) {

			// AC だけ許可
			if (label.equals("AC")) {

				result.setText("0");

			}

			// C（1文字削除）も許可するなら ↓ を残す
			else if (label.equals("C")) {
				result.setText("0");
			}

			return; // ← ここで止める
		}

		// 全削除処理
		if (label.equals("AC")) {

			result.setText("0");

			history.setText("");

			justEvaluated = false;

			lastOperator = null;

			lastOperand = null;

			lastExpr = null;

			return;
		}

		// 1文字削除処理
		if (label.equals("C")) {

			justEvaluated = false;//通常入力へ遷移

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

		//イコールの処理
		if (label.equals("=")) {

			if (text.isEmpty() || text.equals("Error"))

				return;

			//  連続＝の処理
			if (justEvaluated && lastOperator != null && lastOperand != null && isNumberLike(text.replace(",", ""))) {

				String chainedExpr = text + lastOperator + lastOperand;

				chainedExpr = chainedExpr.replace(",", "");//カンマ除去

				String chainedResult = operation.calculate(chainedExpr);

				history.setText(chainedExpr + " =");

				result.setText(chainedResult);

				lastExpr = chainedExpr;

				justEvaluated = true;//計算直後状態

				return;
			}

			// 通常計算 
			String expression = buildExpressionForEquals(text);

			expression = expression.replace(",", "");

			String calculated = operation.calculate(expression);

			if (!calculated.equals("Error")) {

				captureLastBinaryOperation(expression);

				history.setText(expression + " =");
			}

			lastExpr = expression;

			justEvaluated = true;

			result.setText(calculated);

			return;
		}

		if (isDigit(label)) {//数値ボタンへ

			result.setText(appendDigit(text, label));

			return;
		}

		if (label.equals(".")) {//．の処理へ

			result.setText(appendDecimal(text));

			justEvaluated = false;

			return;
		}

		if (label.equals("+/-")) {//トグル処理へ

			result.setText(toggleSign(text));

			justEvaluated = false;

			return;
		}

		if (label.equals("%")) {//％処理へ

			result.setText(applyPercent(text));

			justEvaluated = false;

			return;
		}

		if (label.equals("!")) {

			result.setText(appendFactorial(text));

			justEvaluated = false;

			return;
		}

		if (label.equals("√")) {

			result.setText(applySqrt(text));

			justEvaluated = false;

			return;
		}

		if (label.equals("(")) {//カッコの処理へ

			result.setText(appendLeftParen(text));

			justEvaluated = false;

			return;
		}

		if (label.equals(")")) {//括弧閉じ処理へ

			result.setText(appendRightParen(text));

			justEvaluated = false;

			return;
		}

		if (isOperator(label)) {//

			justEvaluated = false;

			result.setText(appendOperator(text, label));

			return;

		}

	}

	//桁判定処理
	private boolean isDigit(String label) {

		return label.length() == 1 && Character.isDigit(label.charAt(0));

	}

	//四則演算子判定
	private boolean isOperator(String label) {

		return label.equals("+") || label.equals("-") || label.equals("x")

				|| label.equals("÷");

	}

	//演算子判定
	private boolean isTrailingOperator(char c) {

		return c == '+' || c == '-' || c == 'x' || c == '÷';

	}

	//＝の処理前に計算式を構築するメソッド
	private String buildExpressionForEquals(String text) {

		if (text.isEmpty()) {

			return "0";
		}

		char last = text.charAt(text.length() - 1);

		if (!isTrailingOperator(last)) {

			return text;
		}

		String left = text.substring(0, text.length() - 1);

		if (!isNumberLike(left)) {

			return trimTrailingOperators(text);
		}

		return left + last + left;
	}

	//数値入力時のゼロの正規化処理、置換処理
	private String appendDigit(String text, String digit) {

		if (justEvaluated) {

			justEvaluated = false;

			return digit;
		}

		if (text.equals("Error"))

			return digit;

		if (text.equals("0") && digit.equals("0"))

			return "0";

		if (text.equals("0"))

			return digit;

		String currentNumber = getCurrentNumber(text);

		if (currentNumber.equals("0") && !digit.equals("0")) {

			return text.substring(0, text.length() - 1) + digit;

		}

		if (currentNumber.equals("0") && digit.equals("0")) {

			return text;
		}

		return text + digit;
	}

	//演算子ボタンが押されたときに、安全な形で式に追加する処理
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

	//小数点ボタンが押されたときの入力補正メソッド
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

	//カッコ挿入処理
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

	//カッコ閉じの判定
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

	// 階乗記号を追加
	private String appendFactorial(String text) {

		if (text.equals("Error"))

			return text;

		if (text.isEmpty())

			return text;

		char last = text.charAt(text.length() - 1);

		// 数字か ) の直後だけ許可
		if (Character.isDigit(last) || last == ')') {

			return text + "!";
		}

		return text;
	}

	// √ボタン処理, 末尾の数値トークンを取得して sqrt を適用する
	private String applySqrt(String text) {

		if (text.equals("Error")) {

			return "0";
		}

		String num = getCurrentNumber(text);

		if (!isNumberLike(num)) {

			return text;
		}

		// sqrt(数値) の式を Model に渡す
		String value = operation.calculate("sqrt(" + num + ")");

		if (value.equals("Error")) {

			return "Error";
		}

		// 元の数値部分を置き換える
		return text.substring(0, text.length() - num.length()) + value;
	}

	//文字をカウントする処理
	private int countChar(String text, char target) {

		int count = 0;

		for (int i = 0; i < text.length(); i++) {

			if (text.charAt(i) == target) {

				count++;
			}
		}

		return count;
	}

	//数値トークンを切り出す処理
	private String getCurrentNumber(String text) {

		int index = text.length() - 1;

		while (index >= 0) {

			char c = text.charAt(index);

			if (!Character.isDigit(c) && c != '.') {//数字と処理点はループ

				break;
			}
			index--;
		}

		return text.substring(index + 1);
	}

	// ±トグル処理
	// 末尾の数値トークンを抽出し、符号を反転する（単項マイナスの付与／除去）
	private String toggleSign(String text) {

		if (text.equals("Error")) {

			return "0";
		}

		int end = text.length() - 1;

		while (end >= 0 && !Character.isDigit(text.charAt(end)) && text.charAt(end) != '.') {

			end--;
		}

		// 数値が見つからなければ変更なし
		if (end < 0) {

			return text;
		}

		int start = end;

		while (start >= 0 && (Character.isDigit(text.charAt(start)) || text.charAt(start) == '.')) {

			start--;

		}

		// 単項マイナスが既に付いている場合は除去（先頭・演算子直後・( の直後のみ負号とみなす）
		if (start >= 0 && text.charAt(start) == '-') {

			if (start == 0 || isTrailingOperator(text.charAt(start - 1)) || text.charAt(start - 1) == '(') {

				return text.substring(0, start) + text.substring(start + 1);
			}
		}

		// 単項マイナスを付与して符号反転
		return text.substring(0, start + 1) + "-" + text.substring(start + 1);
	}

	//％の演算処理
	private String applyPercent(String text) {// 末尾の数値トークンを抽出し、÷100した値に置換する

		// エラー表示は初期値に戻す
		if (text.equals("Error")) {

			return "0";
		}

		int end = text.length() - 1;

		while (end >= 0 && !Character.isDigit(text.charAt(end)) && text.charAt(end) != '.') {

			end--;

		}

		// 数値が無ければ変更しない
		if (end < 0) {
			return text;
		}

		int start = end;

		while (start >= 0 && (Character.isDigit(text.charAt(start)) || text.charAt(start) == '.')) {

			start--;
		}

		// 単項マイナス（負数）をトークンに含めるか判定
		if (start >= 0 && text.charAt(start) == '-') {

			if (start == 0 || isTrailingOperator(text.charAt(start - 1)) || text.charAt(start - 1) == '(') {

				start--;
			}
		}

		String numberText = text.substring(start + 1, end + 1);

		// 数値形式チェック
		if (!isNumberLike(numberText)) {

			return text;

		}

		// ÷100 を評価
		String percentValue = operation.calculate(numberText + "/100");

		if (percentValue.equals("Error")) {

			return "Error";

		}

		// 元の式の該当トークンを置換
		return text.substring(0, start + 1) + percentValue + text.substring(end + 1);
	}

	// 数値フォーマット（符号・小数点を含む）の妥当性チェック
	private boolean isNumberLike(String value) {

		if (value == null || value.isEmpty())

			return false;

		boolean hasDigit = false;

		int dots = 0;

		for (int i = 0; i < value.length(); i++) {

			char c = value.charAt(i);

			// 先頭マイナス許可
			if (i == 0 && c == '-')

				continue;

			if (c == '.') {

				dots++;

				if (dots > 1)

					return false;

				continue;
			}

			if (Character.isDigit(c)) {

				hasDigit = true;

				continue;
			}

			return false;
		}

		if (!hasDigit)

			return false;

		if (value.endsWith("."))

			return false;

		if (value.equals("-0"))

			return false;

		return true;
	}

	//末端の演算子を取り除くメソッド
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

	//最後の2項計算を記憶する処理
	private void captureLastBinaryOperation(String expression) {

		lastOperator = null;

		lastOperand = null;

		int depth = 0;

		for (int i = expression.length() - 1; i >= 0; i--) {

			char c = expression.charAt(i);

			if (c == ')') {

				depth++;//カッコの深さをカウント

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

				if (c == '-' && (i == 0 || isTrailingOperator(expression.charAt(i - 1))

						|| expression.charAt(i - 1) == '(')) {

					continue;
				}

				String right = expression.substring(i + 1);// 最後の演算子と右側の数字を取り出して覚えておく

				if (!isNumberLike(right)) {

					return;
				}

				lastOperator = String.valueOf(c);//最後の計算パーツを保存する

				lastOperand = right;

				return;
			}
		}
	}
}
