package ilk;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Markov {

	protected static ArrayList<String> sequences = new ArrayList<>();
	protected static ArrayList<Integer> protectedFieldNumber = new ArrayList<>();
	private String[] columns;
	private int columnCount;

	private MatchState[] matchStates;
	private InsertState[] insertStates;
	private DeleteState[] deleteStates;

	protected static int end;
	protected static float matchProbabilities[][];
	protected static float insertProbabilities[][];
	protected static float deleteProbabilities[][];
	protected static String matchTransitions[][];
	protected static String insertTransitions[][];
	protected static String deleteTransitions[][];
	protected static String matchEmissions[][];
	protected static String insertEmissions[];
	private ArrayList<Integer> insertPossibilities = new ArrayList<>();
	protected static float insertEmissionPossibilities[];
	private int ItoI[], ItoM[], ItoD[];
	private DecimalFormat decimalFormat = new DecimalFormat("#.###");

	protected void generate() {
		findColumns();
		findProtectedFields();
		generateEmptyStates();
		generateTable();
		findTransitions();
		findTransitionProbability();
		findEmissionProbability();
		for (int i = 0; i < matchStates.length; i++) {
			System.out.println("matchStates[" + i + "].to= "
					+ matchStates[i].to + "\t\t" + matchStates[i].sequence);
		}
		for (int i = 0; i < matchStates.length; i++) {
			System.out.println("deleteStates[" + i + "].to= "
					+ deleteStates[i].to + "\t\t" + deleteStates[i].sequence);
		}
		for (int i = 0; i < matchStates.length; i++) {
			System.out.println("insertStates[" + i + "].to= "
					+ insertStates[i].to + "\t\t" + insertStates[i].sequence);
		}

	}

	private void findColumns() {
		columnCount = sequences.get(0).length();
		columns = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			columns[i] = "";
			for (int j = 0; j < sequences.size(); j++) {
				columns[i] += sequences.get(j).charAt(i);
			}
		}
	}

	private void findProtectedFields() {
		int columnLength = columns[0].length();
		int gapCount;
		for (int i = 0; i < columnCount; i++) {
			gapCount = 0;
			for (int j = 0; j < columnLength; j++) {
				if (columns[i].charAt(j) == '-') {
					gapCount++;
				}
			}
			if (gapCount * 2 < columnLength) {
				protectedFieldNumber.add(i);
			}
		}

	}

	private void generateEmptyStates() {
		int matchStateCount = protectedFieldNumber.size();
		matchStates = new MatchState[matchStateCount + 2];
		insertStates = new InsertState[matchStateCount + 2];
		deleteStates = new DeleteState[matchStateCount + 2];

		for (int i = 0; i < matchStateCount + 2; i++) {
			matchStates[i] = new MatchState();

			insertStates[i] = new InsertState();

			deleteStates[i] = new DeleteState();

		}

	}

	private void generateTable() {

		int protectedNo;
		boolean first, control, added, contr = true;

		ItoI = new int[protectedFieldNumber.size() + 1];
		ItoD = new int[protectedFieldNumber.size() + 1];
		ItoM = new int[protectedFieldNumber.size() + 1];
		for (int i = 0; i < ItoI.length; i++) {
			ItoI[i] = 0;
			ItoD[i] = 0;
			ItoM[i] = 0;
		}

		for (int j = 0; j < sequences.size(); j++) {
			if (protectedFieldNumber.get(0) == 0) {

				if (sequences.get(j).charAt(0) == '-') {
					matchStates[0].to += "D" + (1);
					matchStates[0].sequence += "S" + j;
				} else {
					matchStates[0].to += "M" + (1);
					matchStates[0].sequence += "S" + j;
				}
			}
			protectedNo = 0;

			first = false;
			added = false;
			contr = true;
			for (int index = 0; index < columnCount - 1; index++) {

				control = false;
				if ((index) == protectedFieldNumber.get(protectedNo)) {
					control = true;
					if ((!gapControl(protectedNo, index + 1, j)) && contr) {
						ItoM[protectedNo + 1]++;
						contr = false;
					}
					// protected-->protected
					if (protectedNo + 1 < protectedFieldNumber.size()) {

						if ((index + 1) == protectedFieldNumber
								.get(protectedNo + 1)) {
							control = false;
							if (sequences.get(j).charAt(index) == '-') {
								// --
								if (sequences.get(j).charAt(index + 1) == '-') {
									deleteStates[protectedNo + 1].to += "D"
											+ (protectedNo + 2);
									deleteStates[protectedNo + 1].sequence += "S"
											+ j;

								}
								// -A
								else {
									deleteStates[protectedNo + 1].to += "M"
											+ (protectedNo + 2);
									deleteStates[protectedNo + 1].sequence += "S"
											+ j;

								}
							}

							else {
								// A-
								if (sequences.get(j).charAt(index + 1) == '-') {
									matchStates[protectedNo + 1].to += "D"
											+ (protectedNo + 2);
									matchStates[protectedNo + 1].sequence += "S"
											+ j;

								}
								// AA
								else {
									if (index == 0) {
										matchStates[protectedNo + 1].to += "M"
												+ (protectedNo + 2);
										matchStates[protectedNo + 1].sequence += "S"
												+ j;
									} else {
										matchStates[protectedNo + 1].to += "M"
												+ (protectedNo + 2);
										matchStates[protectedNo + 1].sequence += "S"
												+ j;
									}

								}
							}
						}
					}
					// protected-->normal
					if (control) {

						if (sequences.get(j).charAt(index) == '-') {
							// --
							if (sequences.get(j).charAt(index + 1) == '-') {
								// / deleteStates[index].to+="D"+j;

								if (gapControl(protectedNo, index, j)) {

									boolean cont = true;
									if (protectedNo + 1 < protectedFieldNumber
											.size()) {

										if (sequences.get(j).charAt(
												protectedFieldNumber
														.get(protectedNo + 1)) == '-') {
											deleteStates[protectedNo + 1].to += "D"
													+ (protectedNo + 2);
											deleteStates[protectedNo + 1].sequence += "S"
													+ j;

											cont = false;
										}
									}

									if (cont) {
										deleteStates[protectedNo + 1].to += "M"
												+ (protectedNo + 2);
										deleteStates[protectedNo + 1].sequence += "S"
												+ j;

									}

									// break;
								}
							}
							// -A
							else {
								deleteStates[protectedNo + 1].to += "I"
										+ (protectedNo + 1);
								deleteStates[protectedNo + 1].sequence += "S"
										+ j;

							}
						} else {

							// A-
							if (sequences.get(j).charAt(index + 1) == '-') {
								// matchStates[index].to+="D"+j;

								if (gapControl(protectedNo, index, j)) {

									matchStates[protectedNo + 1].to += "M"
											+ (protectedNo + 2);
									matchStates[protectedNo + 1].sequence += "S"
											+ j;
									added = true;

								} else {
									matchStates[protectedNo + 1].to += "I"
											+ (protectedNo + 2);
									matchStates[protectedNo + 1].sequence += "S"
											+ j;
								}
							}
							// AA
							else {
								matchStates[protectedNo + 1].to += "I"
										+ (protectedNo + 1);
								matchStates[protectedNo + 1].sequence += "S"
										+ j;

							}
						}
					}
					if (protectedNo + 1 < protectedFieldNumber.size())
						protectedNo++;
				}

				else {
					// normal-->protected
					if ((index + 1) == protectedFieldNumber.get(protectedNo)) {

						if (sequences.get(j).charAt(index) == '-') {

							// --
							if (sequences.get(j).charAt(index + 1) == '-') {
								// deleteStates[index].to+="D"+j;
								if (!gapControl(protectedNo, protectedNo, j)) {
									insertStates[protectedNo].to += "D"
											+ (protectedNo + 1);
									insertStates[protectedNo].sequence += "S"
											+ j;
								} else {
									matchStates[protectedNo + 1].to += "D"
											+ (protectedNo + 2);
									matchStates[protectedNo + 1].sequence += "S"
											+ j;
								}

							}
							// -A
							else {

								if (!added) {

									if (protectedNo == 0) {
										if (gapControl(protectedNo, 0, j)) {
											matchStates[protectedNo].to += "M"
													+ (protectedNo + 1);
											matchStates[protectedNo].sequence += "S"
													+ j;
										} else {
											insertStates[protectedNo].to += "M"
													+ (protectedNo + 1);
											insertStates[protectedNo].sequence += "S"
													+ j;
										}

									} else {
										matchStates[protectedNo + 1].to += "M"
												+ (protectedNo + 2);
										matchStates[protectedNo + 1].sequence += "S"
												+ j;
									}
									added = false;
								}
							}

						} else {
							// A-
							if (sequences.get(j).charAt(index + 1) == '-') {

								insertStates[protectedNo].to += "D"
										+ (protectedNo + 1);
								insertStates[protectedNo].sequence += "S" + j;

								ItoD[protectedNo]++;
							}
							// AA
							else {
								insertStates[protectedNo].to += "M"
										+ (protectedNo + 1);
								insertStates[protectedNo].sequence += "S" + j;

								ItoM[protectedNo]++;
							}
						}
					}
					// normal-->normal
					else {
						if (sequences.get(j).charAt(index) == '-') {
							// --
							if (sequences.get(j).charAt(index + 1) == '-') {

							}
							// -A
							else {

								if (!first) {
									matchStates[protectedNo].to += "I"
											+ (protectedNo);
									matchStates[protectedNo].sequence += "S"
											+ j;

								}

							}
						} else {
							// A-

							if (sequences.get(j).charAt(index + 1) == '-') {
								// matchStates[index].to+="D"+j;
								if (!gapControl(protectedNo, index + 1, j)) {
									if (index > protectedFieldNumber
											.get(protectedNo))
										ItoI[protectedNo + 1]++;
									else {
										ItoI[protectedNo]++;
									}

								}
								if (gapControl(protectedNo, index + 1, j)) {
									if (sequences.get(j).charAt(
											protectedFieldNumber
													.get(protectedNo)) == '-') {
										if (index > protectedFieldNumber
												.get(protectedNo))
											ItoM[protectedNo + 1]++;
										else {
											ItoD[protectedNo]++;
										}
									}

									else {
										if (index > protectedFieldNumber
												.get(protectedNo))
											ItoM[protectedNo + 1]++;
										else {
											ItoM[protectedNo]++;
										}
									}
								}
								if (index == 0) {
									matchStates[protectedNo].to += "I"
											+ (protectedNo);
									matchStates[protectedNo].sequence += "S"
											+ j;

									first = true;
								} else if (gapControl(protectedNo, index, j)) {

									if (sequences.get(j).charAt(
											protectedFieldNumber
													.get(protectedNo + 1)) == '-') {
										insertStates[protectedNo].to += "D"
												+ (protectedNo + 1);
										insertStates[protectedNo].sequence += "S"
												+ j;

									}

									else {
										insertStates[protectedNo].to += "M"
												+ (protectedNo + 1);
										insertStates[protectedNo].sequence += "S"
												+ j;
									}
								}
							}
							// AA
							else {

								if (index > protectedFieldNumber
										.get(protectedNo))
									ItoI[protectedNo + 1]++;
								else {
									ItoI[protectedNo]++;
								}
								if (index == 0) {
									matchStates[protectedNo].to += "I"
											+ (protectedNo);
									matchStates[protectedNo].sequence += "S"
											+ j;

									first = true;
								}
								if (protectedNo == protectedFieldNumber.size() - 1) {
									insertStates[protectedNo + 1].to += "M"
											+ (protectedNo + 2);
									insertStates[protectedNo + 1].sequence += "S"
											+ j;
								}

							}
						}
					}
				}
			}

			if (protectedFieldNumber.get(protectedNo) == columnCount - 1) {
				if (sequences.get(j).charAt(columnCount - 1) == '-') {
					deleteStates[protectedNo + 1].to += "M" + (protectedNo + 2);
					deleteStates[protectedNo + 1].sequence += "S" + j;
				} else {
					matchStates[protectedNo + 1].to += "M" + (protectedNo + 2);
					matchStates[protectedNo + 1].sequence += "S" + j;
				}

			}
		}
	}

	private boolean gapControl(int protectedNo, int index, int j) {
		boolean allGap = true;
		if (protectedNo == protectedFieldNumber.size() - 1)
			for (int k = index + 1; k < columnCount; k++) {
				if (sequences.get(j).charAt(k) != '-') {
					allGap = false;
					break;
				}
			}
		else {
			for (int k = index; k < protectedFieldNumber.get(protectedNo); k++) {
				if (sequences.get(j).charAt(k) != '-') {
					allGap = false;
					break;
				}
			}
		}

		return allGap;
	}

	private void findTransitions() {
		matchTransitions = new String[3][matchStates.length];
		insertTransitions = new String[3][insertStates.length];
		deleteTransitions = new String[3][deleteStates.length];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < matchStates.length; j++) {
				matchTransitions[i][j] = "";
				insertTransitions[i][j] = "";
				deleteTransitions[i][j] = "";
			}
		}
		int count = 0;
		int buff = 0;
		for (int k = 0; k < 3; k++) {
			count = 0;
			while (count < matchStates.length) {
				buff = 0;
				if (k == 0)
					for (int i = 1; i < matchStates[count].sequence.length(); i += 2) {

						if (matchStates[count].to.charAt(i + buff - 1) == 'M') {
							matchTransitions[0][count] += matchStates[count].sequence
									.charAt(i);
						}

						if (matchStates[count].to.charAt(i + buff - 1) == 'I') {
							matchTransitions[1][count] += matchStates[count].sequence
									.charAt(i);
						}
						if (matchStates[count].to.charAt(i + buff - 1) == 'D') {
							matchTransitions[2][count] += matchStates[count].sequence
									.charAt(i);
						}

						if (count >= 9) {
							buff++;
						}
					}
				if (k == 1)
					for (int i = 1; i < insertStates[count].sequence.length(); i += 2) {
						if (insertStates[count].to.charAt(i + buff - 1) == 'M') {
							insertTransitions[0][count] += insertStates[count].sequence
									.charAt(i);
						}

						if (insertStates[count].to.charAt(i + buff - 1) == 'D') {
							insertTransitions[2][count] += insertStates[count].sequence
									.charAt(i);
						}
						if (count >= 9) {
							buff++;
						}
					}
				if (k == 2)
					for (int i = 1; i < deleteStates[count].sequence.length(); i += 2) {
						if (deleteStates[count].to.charAt(i + buff - 1) == 'M') {
							deleteTransitions[0][count] += deleteStates[count].sequence
									.charAt(i);
						}

						if (deleteStates[count].to.charAt(i + buff - 1) == 'I') {
							deleteTransitions[1][count] += deleteStates[count].sequence
									.charAt(i);
						}
						if (deleteStates[count].to.charAt(i + buff - 1) == 'D') {
							deleteTransitions[2][count] += deleteStates[count].sequence
									.charAt(i);
						}

						if (count >= 9) {
							buff++;
						}
					}

				count++;
			}

		}
	}

	private void findTransitionProbability() {
		int matchCount, insertCount, deleteCount, totalCount;
		float matchProbability, insertProbability, deleteProbability;
		int count = 0;

		matchProbabilities = new float[3][matchStates.length];
		insertProbabilities = new float[3][insertStates.length];
		deleteProbabilities = new float[3][deleteStates.length];

		for (int k = 0; k < 3; k++) {
			count = 0;
			while (count < matchStates.length - 1) {
				matchCount = 0;
				insertCount = 0;
				deleteCount = 0;
				totalCount = 0;
				matchProbability = 0;
				insertProbability = 0;
				deleteProbability = 0;
				if (k == 0)
					for (int i = 0; i < matchStates[count].to.length(); i += 2) {
						if (matchStates[count].to.charAt(i) == 'M')
							matchCount++;
						else if (matchStates[count].to.charAt(i) == 'I')
							insertCount++;
						else if (matchStates[count].to.charAt(i) == 'D')
							deleteCount++;
					}
				else if (k == 1) {

					matchCount = ItoM[count];
					insertCount = ItoI[count];
					deleteCount = ItoD[count];
				} else if (k == 2)
					for (int i = 0; i < deleteStates[count].to.length(); i += 2) {
						if (deleteStates[count].to.charAt(i) == 'M')
							matchCount++;
						else if (deleteStates[count].to.charAt(i) == 'I')
							insertCount++;
						else if (deleteStates[count].to.charAt(i) == 'D')
							deleteCount++;
					}
				totalCount = matchCount + insertCount + deleteCount;

				if (totalCount != 0) {
					matchProbability = (float) matchCount / totalCount;
					insertProbability = (float) insertCount / totalCount;
					deleteProbability = (float) deleteCount / totalCount;
				}

				matchProbabilities[k][count] = matchProbability;
				insertProbabilities[k][count] = insertProbability;
				deleteProbabilities[k][count] = deleteProbability;

				count++;
			}
		}
		end = count;
	}

	protected void findEmissionProbability() {
		matchEmissions = new String[protectedFieldNumber.size()][sequences
				.size()];
		insertEmissions = new String[(protectedFieldNumber.size() + 1)];

		char[] chars = new char[sequences.size()];
		char[] charsAdded = new char[sequences.size()];
		int[] counts = new int[sequences.size()];
		int totalCount = 0;
		boolean added = false;

		// matches
		for (int k = 0; k < protectedFieldNumber.size(); k++) {
			for (int t = 0; t < sequences.size(); t++) {
				chars[t] = sequences.get(t).charAt(protectedFieldNumber.get(k));
				charsAdded[t] = '0';
				// probabilities[t]=0;
				counts[t] = 0;
				matchEmissions[k][t] = "";

			}
			totalCount = 0;

			for (int i = 0; i < sequences.size(); i++) {
				added = false;
				for (int j = 0; j < charsAdded.length; j++) {
					if (charsAdded[j] == chars[i]) {
						added = true;
						break;
					}
				}
				if (!added) {
					for (int j = i + 1; j < sequences.size(); j++) {
						if (chars[j] == chars[i]) {

							counts[i]++;
						}
					}
					charsAdded[i] = chars[i];

					counts[i]++;
				}
			}

			for (int i = 0; i < chars.length; i++) {
				if (counts[i] != 0) {
					if (charsAdded[i] != '-') {
						totalCount += counts[i];
					}

				}
			}
			for (int i = 0; i < chars.length; i++) {
				if (counts[i] != 0) {
					if (charsAdded[i] != '-') {
						matchEmissions[k][i] += charsAdded[i]
								+ "= "
								+ decimalFormat
										.format(((float) counts[i] / totalCount));
					}

				}
			}

		}

		int protectedNo = 0;
		String insertChars = "";
		int totalCoun = 0;
		boolean control = true;
		// inserts
		for (int d = 0; d < protectedFieldNumber.size() + 1; d++) {
			insertEmissions[d] = "";
		}
		for (int k = 0; k < columnCount; k++) {

			control = true;
			if (protectedNo < protectedFieldNumber.size()) {
				if (k != protectedFieldNumber.get(protectedNo)) {
					control = false;
					for (int t = 0; t < sequences.size(); t++) {
						if (sequences.get(t).charAt(k) != '-') {
							if (!contains(insertChars,
									sequences.get(t).charAt(k))) {
								insertChars += sequences.get(t).charAt(k);
								insertPossibilities.add(1);
							}

						}

					}

				}
			}
			if (control) {

				if (k - 1 > protectedFieldNumber.get(protectedFieldNumber
						.size() - 1)) {
					for (int t = 0; t < sequences.size(); t++) {
						if (sequences.get(t).charAt(k) != '-') {
							if (!contains(insertChars,
									sequences.get(t).charAt(k))) {
								insertChars += sequences.get(t).charAt(k);
								insertPossibilities.add(1);
							}

						}

					}

					for (int i = 0; i < insertPossibilities.size(); i++) {
						totalCoun += insertPossibilities.get(i);
					}
					for (int i = 0; i < insertChars.length(); i++) {
						insertEmissions[protectedNo - 1] += insertChars
								.charAt(i)
								+ " ="
								+ decimalFormat
										.format(((float) insertPossibilities
												.get(i) / totalCoun)) + "//";
					}

				}

				else {
					for (int i = 0; i < insertPossibilities.size(); i++) {
						totalCoun += insertPossibilities.get(i);
					}
					for (int i = 0; i < insertChars.length(); i++) {
						insertEmissions[protectedNo] += insertChars.charAt(i)
								+ " ="
								+ decimalFormat
										.format(((float) insertPossibilities
												.get(i) / totalCoun)) + "//";
					}

				}

				protectedNo++;
				totalCoun = 0;
				insertChars = "";
				insertPossibilities.clear();
			}

		}

	}

	private boolean contains(String string, char a) {
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == a) {
				insertPossibilities.set(i, insertPossibilities.get(i) + 1);
				return true;
			}

		}

		return false;
	}

	protected void reset() {
		sequences.clear();
		protectedFieldNumber.clear();
		end = 0;

	}
}

class MatchState {
	String sequence = "", to = "";
}

class InsertState {
	String sequence = "", to = "";
}

class DeleteState {
	String sequence = "", to = "";
}
