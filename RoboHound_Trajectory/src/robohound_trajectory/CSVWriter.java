package robohound_trajectory;

import java.awt.List;
import java.io.FileWriter;
import java.io.IOException;


public class CSVWriter {
	
	private String path;
	private FileWriter writer;
	private char seperator = ','; // this should stay as a comma unless there is a specific reason to change it
	
	public CSVWriter(String path) throws IOException {
		this.path = path;
		writer = new FileWriter(path);
	}
	
	private static String followCVSformat(String value) {
        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;
    }
	
	public void writeLine(String[] data) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		boolean first = true;
		for (String v : data) {
			if (!first) {
				sb.append(seperator);
			}
			sb.append(followCVSformat(v));
			first = false;
		}
		
		sb.append("\n");
		writer.append(sb.toString());
	}
	
	public void done() throws IOException {
		writer.flush();
		writer.close();
	}
	

}
