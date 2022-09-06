import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GraphMatrixToEdgeList {
	
	public static final String WRITE_SEP = "\t";
	public static final String READ_SEP = ",";
	
	public static void conversion(String file) throws IOException {
		try(BufferedReader reader = new BufferedReader(new FileReader(file));
				PrintWriter writer = new PrintWriter("edgeList.tsv")){
			String line = reader.readLine();
			String[] elements = line.split(READ_SEP);
			String[] genomes = new String[elements.length-1];
			for(int i = 1; i<elements.length;i++) genomes[i-1] = elements[i];
			int i = 0;
			while(true) {
				line = reader.readLine();
				if(line == null) break;
				String[] elems = line.split(READ_SEP);
				for(int j = 1; j<elems.length;j++) {
					boolean value = Boolean.parseBoolean(elems[j]);
					if(value) {
						writer.println(genomes[i] + WRITE_SEP + genomes[j-1]);
					}
				}
				i++;
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		conversion(args[0]);
	}

}
