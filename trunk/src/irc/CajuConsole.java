package irc;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.cajuscript.CajuScript;
import org.cajuscript.Value;

public class CajuConsole extends JFrame implements ActionListener {
	private JTextArea result, field;
	private JScrollPane panel, runField;
	private JButton button, clean, cleanResult;

	public CajuConsole() {
		super("CajuConsole");
		System.setOut(fieldPrintStream);
		setMinimumSize(new Dimension(800, 400));
		panel = new JScrollPane();
		runField = new JScrollPane();
		runField.setViewportView(field = new JTextArea(10, 20));
		panel.setViewportView(result = new JTextArea(20, 80));
		result.setLineWrap(true);
		result.setEditable(false);
		add(panel, "South");

		JPanel buttons = new JPanel(new GridLayout(3, 1));
		buttons.add(button = new JButton("Run"));
		buttons.add(clean = new JButton("Clean Field"));
		buttons.add(cleanResult = new JButton("Clean Console"));

		JPanel header = new JPanel();
		header.add(runField);
		header.add(buttons);
		add(header, "North");
		button.addActionListener(this);
		clean.addActionListener(this);
		cleanResult.addActionListener(this);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new CajuConsole();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == button) {
			long ini = System.currentTimeMillis();
			try {
				String script = field.getText();

				CajuScript caju = new CajuScript();

				Value v = caju.eval(script);
				if (v != null)
					if (v.getValue() != null)
						System.out.println(v.getValue().toString());

			} catch (Exception e) {
				e.printStackTrace(fieldPrintStream);
			}
			long fin = System.currentTimeMillis();
			System.out.println("Proccess time:" + (fin - ini) + " ms");
		}
		if (arg0.getSource() == clean) {
			field.setText("");
		}
		if (arg0.getSource() == cleanResult) {
			result.setText("");
		}
	}

	private PrintStream fieldPrintStream = new PrintStream(new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			result.append(String.valueOf((char) b));
		}
	});
}
