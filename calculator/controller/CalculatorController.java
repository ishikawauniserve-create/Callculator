package calculator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import calculator.model.ArithmeticOperation;
import calculator.view.CalculatorFrame;

public class CalculatorController implements ActionListener {
	
	private final JTextField result;//resultの表示への参照
	private final JButton[] buttons;//電卓の全ボタンを参照
	private final ArithmeticOperation operation;//計算処理を使うためにControllerが持つ

	//別クラスとの接続処理
    public CalculatorController(CalculatorFrame frame, ArithmeticOperation operation) {
        this.result = frame.getResultField();//Frame内のresultを参照
        this.buttons = frame.getButtons();//Frame内のbuttonsを参照
        this.operation = operation;//計算係をControllerの手元に置く

        for (int i =0; i < buttons.length; i++) { //配列を参照して変数ｂに
        	JButton b = buttons[i];
        	if(b != null) b.addActionListener(this);//nullの場合は登録しない、このコントローラーが信号受け取る
        }
    }
    //ボタン内処理
	@Override
	public void actionPerformed(ActionEvent e) {//メソッドの宣言
		JButton b = (JButton) e.getSource();//リンクを受け取る
		String buttonlabel = b.getText();//戻り値をbuttonlabelに

		handleButton(buttonlabel);//トリガーから信号を受け取る
	}
	private void handleButton(String label) {//
		if(label == null || label.isEmpty()) {
			return;
		}
		// AC処理
		if(label.equals("AC")) { // ACが押されたら
			result.setText("0");//画面に0を表示する
			return;//CalculatorFrameに返す
		}
		// C処理
		if (label.equals("C")) {//C入力
		    String text = result.getText();//resultに表示される文字列を取り出す

		    if (!text.isEmpty()) {//空じゃない場合はtrue
		        text = text.substring(0, text.length() - 1);//文字列の末尾から1文字消す
		    }

		    if (text.isEmpty()) text = "0";//空だとfalse？？
		    result.setText(text);
		    return;
		} 



		}
}