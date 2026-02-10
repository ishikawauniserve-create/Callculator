package calculator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import calculator.controller.CalculatorController;
import calculator.model.ArithmeticOperation;

public class CalculatorFrame extends JFrame{//クラスの宣言、パブリッククラスなので、どこからでも参照することができる。JFrame型を参照している　ウィンドウを作るクラス
	

	//コンポネント作成
	BorderLayout borderlayout1 =new BorderLayout();//レイアウト型、東西南北、中央の５つに分割できる。レイアウト型から変数を作っている。　newメモリ上にオブジェクト化する。

	GridLayout gridlayout = new GridLayout(6, 4);//コンテナ内の部品を格子状に均等に配置するクラス
	
	private JButton[] buttons; //このクラス内のみでしかアクセスできない。JButtonの配列型を参照できる変数buttonsを宣言、lengthはサイズが固定されている

	private JTextField result = new JTextField("0");//JTextField型を参照できる変数resultを宣言

	public CalculatorFrame() { //コンストラクタ宣言、クラスオブジェクトを生成するときに自動で実行されるメソッド

		//画面描画設定
		this.setLayout(borderlayout1);//this = 生成中のオブジェクトを自身へ参照

		this.setSize(400, 600);// 

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);// ×を押したらウィンドウが閉じる

		this.setTitle("電卓");//ウィンドウの名前

		this.add(result, BorderLayout.NORTH);//ペインの北に結果を配置

		result.setPreferredSize(new Dimension(0, 120));//計算結果画面サイズ

		result.setText("0");//最初は０表示

		result.setHorizontalAlignment(JTextField.RIGHT);  //右寄せ

		//ソフトキー描画設定
		JPanel SoftKey = new JPanel();

		SoftKey.setLayout(gridlayout);//左から順に配置してくれるメソッド

		this.add(SoftKey, BorderLayout.CENTER);

		buttons = new JButton[24];//配列によってキーを24個配置する 中身はnull

		String[] displayLabels = { //表示用
				"AC","C","%","÷",
				"7","8","9","x",
				"4","5","6","-",
				"1","2","3","+",
				"+/-","0",".","=",
				"(",")","",""
		};	

		//forでループ処理
		for (int i = 0; i < buttons.length; i++){ //for文で24個つくる

			JButton b = new JButton(displayLabels[i]);//表示文字としてボタンを作成

			buttons[i] = b;     // 配列に保存

			SoftKey.add(b);     //SoftKeyに加える		

		}

		this.setVisible(true);//ウィンドウを可視化

		}

		public JTextField getResultField() {//Controllerで参照

	    return result;

		}

		public JButton[] getButtons() {//geterがないと参照できない

	    return buttons;

		}

		public static void main(String[] args) {

		    CalculatorFrame frame = new CalculatorFrame();
		    new CalculatorController(frame, new ArithmeticOperation());
		}
	}