import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileToString {

	public static String readFile(File file) throws FileNotFoundException, IOException  {
		byte[] fileInBytes = new byte[(int) file.length()];
		FileInputStream is = null;
		StringBuilder stringResult = new StringBuilder();
		is = new FileInputStream(file);

		/// read until the end of the stream.
		while (is.available() != 0) {
			is.read(fileInBytes, 0, fileInBytes.length);
		}
		
		for (byte b : fileInBytes) {
			stringResult.append((char) b);
		}

		// close file stream
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//return new String(fileInBytes);
		return stringResult.toString();
	}
}
