package calculator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class CalculatorFrame extends JFrame{
	//コンポネント作成
	BorderLayout borderlayout1 =new BorderLayout();//仕切り枠
	GridLayout gridlayout = new GridLayout(6, 4);
	
	private JButton[] buttons; 
	private JTextField result = new JTextField("");//計算結果を表示する

	public CalculatorFrame() {

		//画面描画設定
		this.setLayout(borderlayout1);//contentPaneに対して命令
		this.setSize(400, 600);// 座標、そして画面サイズ
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);// ×を押したらウィンドウが閉じる
		this.setTitle("電卓");//ウィンドウの名前
		this.add(result, BorderLayout.NORTH);//ペインの北に結果を配置
		result.setPreferredSize(new Dimension(0, 120));

		//ソフトキー描画設定
		JPanel SoftKey = new JPanel();
		SoftKey.setLayout(gridlayout);//左から順に配置してくれるメソッド
		this.add(SoftKey, BorderLayout.CENTER);

		JButton[] buttons = new JButton[24];//配列によってキーを24個配置する 中身はnull
		String[] displayLabels = { //表示用
				"AC","C","%","÷",
				"7","8","9","X",
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
		public JTextField getResultField() {//これがないとControllerで参照できない
	    return result;
		}
		public JButton[] getButtons() {//geterがないと参照できない
	    return buttons;
		}
	 public static void main(String[] args) {
	        new CalculatorFrame();
	    }
	}