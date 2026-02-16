package calculator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import calculator.controller.CalculatorController;
import calculator.model.ArithmeticOperation;

public class CalculatorFrame extends JFrame {

	private JButton[] buttons;

	private JTextField history = new JTextField("");

	private JTextField result = new JTextField("0");

	public CalculatorFrame() {

		//ウィンドウ
		setTitle("電卓");

		setSize(420, 640);

		setLocationRelativeTo(null);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setLayout(new BorderLayout(8, 8));

		getContentPane().setBackground(new Color(30, 30, 30));

		result.setPreferredSize(new Dimension(0, 120));

		result.setFont(new Font("Consolas", Font.BOLD, 36));

		result.setHorizontalAlignment(JTextField.RIGHT);

		result.setEditable(false);

		result.setBackground(new Color(20, 20, 20)); //画面背景色

		result.setForeground(new Color(80, 220, 120));

		result.setBorder(new EmptyBorder(20, 20, 20, 20));

		// --- 表示パネルの作成 ---
		JPanel displayPanel = new JPanel(new GridLayout(2, 1)); // 2行1列のレイアウト

		displayPanel.setBackground(new Color(20, 20, 20));

		// 1. 履歴/式の表示（上段）
		history.setPreferredSize(new Dimension(0, 40));

		history.setFont(new Font("Consolas", Font.PLAIN, 18)); // 少し小さめ

		history.setHorizontalAlignment(JTextField.RIGHT);

		history.setEditable(false);

		history.setBackground(new Color(20, 20, 20));

		history.setForeground(new Color(150, 150, 150)); // 控えめなグレー

		history.setBorder(new EmptyBorder(10, 20, 0, 20)); // 下の余白を詰める

		history.setText(""); // テスト表示用

		// 2. 結果の表示（下段）
		result.setPreferredSize(new Dimension(0, 80)); // 高さを少し調整

		result.setFont(new Font("Consolas", Font.BOLD, 48));

		result.setHorizontalAlignment(JTextField.RIGHT);

		result.setEditable(false);

		result.setBackground(new Color(20, 20, 20));

		result.setForeground(new Color(80, 220, 120));

		result.setBorder(new EmptyBorder(0, 20, 10, 20)); // 上の余白を詰める

		// パネルにまとめて追加
		displayPanel.add(history);

		displayPanel.add(result);

		// メインフレームの北側に配置
		add(displayPanel, BorderLayout.NORTH);

		//ソフトキー
		JPanel softKey = new JPanel(new GridLayout(6, 4, 8, 8));

		softKey.setBorder(new EmptyBorder(10, 10, 10, 10));

		softKey.setBackground(new Color(30, 30, 30));

		add(softKey, BorderLayout.CENTER);

		buttons = new JButton[24];

		String[] labels = {
				"AC", "C", "%", "÷",
				"7", "8", "9", "x",
				"4", "5", "6", "-",
				"1", "2", "3", "+",
				"+/-", "0", ".", "=",
				"(", ")", "!", "√"
		};

		for (int i = 0; i < buttons.length; i++) {

			JButton b = new JButton(labels[i]);

			styleButton(b, labels[i]);

			buttons[i] = b;

			softKey.add(b);

		}

		setVisible(true);
	}

	// ソフトキーのスタイル
	private void styleButton(JButton b, String label) {

		b.setFont(new Font("Segoe UI", Font.BOLD, 20));

		b.setFocusPainted(false);

		b.setBorderPainted(false);

		b.setOpaque(true);

		// 種類ごとに色分け
		if (label.matches("[0-9.]+")) {

			b.setBackground(new Color(60, 60, 60));

			b.setForeground(Color.WHITE);
		}

		else if (label.equals("=")) {

			b.setBackground(new Color(80, 160, 255));

			b.setForeground(Color.WHITE);

		} else if (label.equals("AC") || label.equals("C")) {

			b.setBackground(new Color(200, 80, 80));

			b.setForeground(Color.WHITE);

		} else {

			b.setBackground(new Color(100, 100, 100));

			b.setForeground(Color.WHITE);

		}

		// ホバー効果
		b.addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseEntered(java.awt.event.MouseEvent evt) {

				b.setBackground(b.getBackground().brighter());

			}

			public void mouseExited(java.awt.event.MouseEvent evt) {

				b.setBackground(b.getBackground().darker());
			}
		});
	}

	public JTextField getResultField() {

		return result;
	}

	public JButton[] getButtons() {

		return buttons;
	}

	public JTextField getHistoryField() {

		return history;
	}

	public static void main(String[] args) {

		CalculatorFrame frame = new CalculatorFrame();

		new CalculatorController(frame, new ArithmeticOperation());

	}
}