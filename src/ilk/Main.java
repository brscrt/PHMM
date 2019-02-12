package ilk;

//100201107 Mehmet Baris Cirit

public class Main {

	public static void main(String[] args) {
		//(new FileOperations()).fileRead("phmm2.txt");
		(new FileOperations()).fileRead("phmm1.txt");
		
		Markov markov = new Markov();
		markov.generate();

		new Panel().initFrame();
		
		
	}

}
