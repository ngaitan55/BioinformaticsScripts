import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ProteinFastaComparator {
	
	public Map<String, Integer> sequenceMap = new HashMap<>();
	public Map<String, String> sequenceIdMap = new HashMap<>();
	
	public void run(String first_path, String second_path, String outputFile) throws IOException {
		try(PrintWriter writer = new PrintWriter(outputFile)){
			readFasta(first_path);
			readFasta(second_path);
			for(Map.Entry<String, Integer> pair : sequenceMap.entrySet()) {
				if(pair.getValue() == 1) {
					String seq = pair.getKey();
					String id = sequenceIdMap.get(seq);
					writer.println(id);
					writer.println(seq);
				}
			}
		}
	}
	
	public void readFasta(String path) throws IOException {
		try(BufferedReader reader = new BufferedReader(new FileReader(path))){
			String line = reader.readLine();
			String sequence = "first";
			String id = line;
			while(line!=null) {
				if(line.charAt(0)=='>') {
					if(sequence.equals("first")) {
						line = reader.readLine();
						continue;
					}
					sequenceMap.compute(sequence, (k,v) -> v==null ? 1 : v+1);
					sequenceIdMap.put(sequence, id);
					id = line;
					sequence = "";
				}
				else {
					sequence+=line;
				}
				line = reader.readLine();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ProteinFastaComparator inst = new ProteinFastaComparator();
		inst.run(args[0], args[1], args[2]);
	}

}
