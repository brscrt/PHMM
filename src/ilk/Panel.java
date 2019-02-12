package ilk;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Panel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Markov markov = new Markov();
	FileOperations fileOperations = new FileOperations();

	JButton phmm1 = new JButton("phmm 1");
	JButton phmm2 = new JButton("phmm 2");
	boolean delete = false;
	DecimalFormat decimalFormat = new DecimalFormat("#.###");

	protected void initFrame() {
		JFrame frame = new JFrame();

		frame.add(this);

		frame.setSize(1024, 720);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Panel() {
		super();
		this.setLayout(null);

		phmm1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				markov.reset();
				fileOperations.fileRead("phmm1.txt");
				markov.generate();
				delete = true;
				repaint();
			}
		});
		phmm2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				markov.reset();
				fileOperations.fileRead("phmm2.txt");
				markov.generate();
				delete = true;

				repaint();
			}
		});

		phmm1.setBounds(800, 10, phmm1.getPreferredSize().width,
				phmm1.getPreferredSize().height);
		phmm2.setBounds(900, 10, phmm2.getPreferredSize().width,
				phmm2.getPreferredSize().height);

		this.add(phmm1);
		this.add(phmm2);

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (delete) {
			g.setColor(getBackground());
			g.fillRect(0, 0, 2000, 1400);
			g.setColor(getForeground());
			delete = false;
		}
		signature(g);
		showSequences(g);
		drawTable(g);

	}

	private void signature(Graphics g) {
		g.drawString("Mehmet Baris Cirit", 850, 60);
	}

	private void showSequences(Graphics g) {
		String sequence;
		int buf = 0;
		char[] buffer = new char[Markov.sequences.get(0).length()];
		for (int i = 0; i < Markov.sequences.size(); i++) {
			sequence = "S" + i + "= ";
			g.setColor(Color.blue);
			g.drawString(sequence, 10, 10 + i * 20);
			g.setColor(getForeground());
			for (int j = 0; j < buffer.length; j++) {
				buffer[j] = Markov.sequences.get(i).charAt(j);
			}
			for (int j = 0; j < buffer.length; j++) {

				g.drawChars(buffer, j, 1, 40 + j * 10, 10 + i * 20);
				buf = 10 + i * 20;
			}

		}
		for (int i = 0; i < Markov.protectedFieldNumber.size(); i++) {
			g.setColor(Color.red);
			g.drawString("*", 42 + 10 * Markov.protectedFieldNumber.get(i),
					buf + 12);
		}

	}

	private void drawTable(Graphics g) {
		int[] xPoints = new int[4];
		int[] yPoints = { 220, 200, 220, 240 };
		int buffer = 0;
		for (int i = 0; i < Markov.end + 1; i++) {

			xPoints[0] = 10 + i * 80;
			xPoints[1] = 30 + i * 80;
			xPoints[2] = 50 + i * 80;
			xPoints[3] = 30 + i * 80;
			g.setColor(Color.red);
			g.drawRect(10 + i * 80, 300, 35, 20);
			g.setColor(getForeground());

			buffer = 0;
			if (i == 0) {

				g.setColor(Color.red);
				if (i < Markov.end - 1)
					for (int j = 0; j < Markov.sequences.size(); j++) {

						if (Markov.matchEmissions[i][j] == "") {
							buffer--;
						}
						g.drawString(Markov.matchEmissions[i][j], 90 + 80 * i,
								410 + (j + buffer) * 15);
					}
				g.setColor(Color.blue);
				if (Markov.insertEmissions[i] != "") {
					//
					String[] buff = Markov.insertEmissions[i].split("//");
					for (int j = 0; j < buff.length; j++) {
						g.drawString(buff[j], 10 + i * 80, 500 + j * 10);
					}
				}
				g.setColor(Color.blue);
				g.drawString(String.valueOf(i), 27 + i * 80, 225);
				g.drawPolygon(xPoints, yPoints, 4);
				g.setColor(getForeground());
				g.drawString(
						decimalFormat.format(Markov.matchProbabilities[0][i]),
						55 + i * 80, 323);

				// between matches
				g.drawString("Begin", 12, 315);
				g.drawLine(45, 310, 90, 310);

				for (int t = 0; t < Markov.matchTransitions[0][i].length(); t++)
					g.drawString("S" + Markov.matchTransitions[0][i].charAt(t),
							55 + i * 80, 335 + 10 * t);

				// between match and insert
				if (Markov.insertProbabilities[0][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.insertProbabilities[0][i]),
							30 + i * 80, 290);
					g.drawLine(30 + i * 80, 300, 30 + i * 80, 240);
					for (int t = 0; t < Markov.matchTransitions[1][i].length(); t++)
						g.drawString(
								"S" + Markov.matchTransitions[1][i].charAt(t),
								30 + i * 80, 250 + t * 10);
				}
				// between match and delete
				if (Markov.deleteProbabilities[0][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.deleteProbabilities[0][i]),
							55 + i * 80, 280);
					g.drawLine(45 + i * 80, 300, 23 + (i + 1) * 80, 145);
					for (int t = 0; t < Markov.matchTransitions[2][i].length(); t++)
						g.drawString(
								"S" + Markov.matchTransitions[2][i].charAt(t),
								81 + i * 80 - t * 3, 160 + t * 10);
				}
				// between inserts
				if (Markov.insertProbabilities[1][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.insertProbabilities[1][i]),
							13 + i * 80, 198);
					g.drawArc(13 + i * 80, 198, 15, 20, 330, 300);
				}
				// between insert and match
				if (Markov.matchProbabilities[1][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.matchProbabilities[1][i]),
							55 + i * 80, 250);
					g.drawLine(50 + i * 80, 220, 10 + (i + 1) * 80, 300);
					for (int t = 0; t < Markov.insertTransitions[0][i].length(); t++)
						g.drawString(
								"S" + Markov.insertTransitions[0][i].charAt(t),
								75 + i * 80 + t * 3, 270 + 10 * t);
				}
				// between insert and delete
				if (Markov.deleteProbabilities[1][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.deleteProbabilities[1][i]),
							75 + i * 80, 155);
					g.drawLine(30 + i * 80, 200, 15 + (i + 1) * 80, 135);
					for (int t = 0; t < Markov.insertTransitions[2][i].length(); t++)
						g.drawString(
								"S" + Markov.insertTransitions[2][i].charAt(t),
								55 + i * 80, 160 + 10 * t);

				}
			} else if (i == Markov.end) {
				g.drawString("End", 18 + i * 80, 315);
				g.setColor(Color.blue);
				g.drawString("Insert Emissions", 30 + i * 80, 500);
				g.setColor(Color.red);
				g.drawString("Match Emissions", 30 + 80 * i, 410);
				g.setColor(getForeground());
			} else {

				g.setColor(Color.red);
				if (i < Markov.end - 1)
					for (int j = 0; j < Markov.sequences.size(); j++) {

						if (Markov.matchEmissions[i][j] == "") {
							buffer--;
						}
						g.drawString(Markov.matchEmissions[i][j], 90 + 80 * i,
								410 + (j + buffer) * 15);
					}
				g.setColor(Color.blue);
				if (Markov.insertEmissions[i] != "") {
					//
					String[] buff = Markov.insertEmissions[i].split("//");
					for (int j = 0; j < buff.length; j++) {
						g.drawString(buff[j], 10 + i * 80, 500 + j * 15);
					}
				}
				g.setColor(Color.magenta);
				g.drawString(String.valueOf(i), 25 + i * 80, 138);
				g.drawOval(15 + i * 80, 120, 25, 25);
				g.setColor(Color.blue);
				g.drawString(String.valueOf(i), 27 + i * 80, 225);
				g.drawPolygon(xPoints, yPoints, 4);
				g.setColor(getForeground());
				g.drawString(
						decimalFormat.format(Markov.matchProbabilities[0][i]),
						55 + i * 80, 323);

				// between matches
				if (Markov.matchProbabilities[0][i] != 0) {
					g.setColor(Color.red);
					g.drawString(String.valueOf(i), 25 + i * 80, 315);
					g.setColor(getForeground());
					g.drawLine(45 + i * 80, 310, 90 + i * 80, 310);
					for (int t = 0; t < Markov.matchTransitions[0][i].length(); t++)
						g.drawString(
								"S" + Markov.matchTransitions[0][i].charAt(t),
								55 + i * 80, 335 + 10 * t);
				}
				// between match and insert
				if (Markov.insertProbabilities[0][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.insertProbabilities[0][i]),
							30 + i * 80, 290);
					g.drawLine(30 + i * 80, 300, 30 + i * 80, 240);
					for (int t = 0; t < Markov.matchTransitions[1][i].length(); t++)
						g.drawString(
								"S" + Markov.matchTransitions[1][i].charAt(t),
								30 + i * 80, 250 + t * 10);

				}
				// between match and delete
				if (Markov.deleteProbabilities[0][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.deleteProbabilities[0][i]),
							55 + i * 80, 280);
					g.drawLine(45 + i * 80, 300, 23 + (i + 1) * 80, 145);
					for (int t = 0; t < Markov.matchTransitions[2][i].length(); t++)
						g.drawString(
								"S" + Markov.matchTransitions[2][i].charAt(t),
								81 + i * 80 - t * 3, 160 + t * 10);
				}
				// between insert and match
				if (Markov.matchProbabilities[1][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.matchProbabilities[1][i]),
							55 + i * 80, 250);
					g.drawLine(50 + i * 80, 220, 10 + (i + 1) * 80, 300);
					for (int t = 0; t < Markov.insertTransitions[0][i].length(); t++)
						g.drawString(
								"S" + Markov.insertTransitions[0][i].charAt(t),
								75 + i * 80 + t * 3, 270 + 10 * t);

				}
				// between inserts
				if (Markov.insertProbabilities[1][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.insertProbabilities[1][i]),
							13 + i * 80, 198);
					g.drawArc(13 + i * 80, 198, 15, 20, 330, 300);

				}
				// between insert and delete
				if (Markov.deleteProbabilities[1][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.deleteProbabilities[1][i]),
							75 + i * 80, 155);
					g.drawLine(30 + i * 80, 200, 15 + (i + 1) * 80, 135);
					for (int t = 0; t < Markov.insertTransitions[2][i].length(); t++)
						g.drawString(
								"S" + Markov.insertTransitions[2][i].charAt(t),
								55 + i * 80, 160 + 10 * t);

				}
				// between delete and match
				if (Markov.matchProbabilities[2][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.matchProbabilities[2][i]),
							50 + i * 80, 155);
					g.drawLine(40 + i * 80, 135, 23 + (i + 1) * 80, 300);
					for (int t = 0; t < Markov.deleteTransitions[0][i].length(); t++)
						g.drawString(
								"S" + Markov.deleteTransitions[0][i].charAt(t),
								31 + i * 80 + t * 4, 160 + t * 10);
				}
				// between delete and insert
				if (Markov.insertProbabilities[2][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.insertProbabilities[2][i]),
							30 + i * 80, 190);
					g.drawLine(30 + i * 80, 145, 30 + i * 80, 200);
					for (int t = 0; t < Markov.deleteTransitions[1][i].length(); t++)
						g.drawString(
								"S" + Markov.deleteTransitions[1][i].charAt(t),
								30 + i * 80, 160 + t * 10);
				}
				// between deletes
				if (Markov.deleteProbabilities[2][i] != 0) {
					g.drawString(decimalFormat
							.format(Markov.deleteProbabilities[2][i]),
							45 + i * 80, 130);
					g.drawLine(40 + i * 80, 133, 15 + (i + 1) * 80, 133);
					for (int t = 0; t < Markov.deleteTransitions[2][i].length(); t++)
						g.drawString(
								"S" + Markov.deleteTransitions[2][i].charAt(t),
								50 + i * 80, 133 + t * 10);
				}
			}

		}
	}

}
