package ilk;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileOperations {

	protected void fileRead(String fileName) {
		String separator = " ";

		try {
			InputStream in=getClass().getResourceAsStream("/txts/"+fileName);
			InputStreamReader read=new InputStreamReader(in);
			BufferedReader reader=new BufferedReader(read);

			String buffer = reader.readLine();
			while (buffer != null) {
				Markov.sequences.add(buffer.split(separator)[1]);
				buffer = reader.readLine();
			}

			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

}
