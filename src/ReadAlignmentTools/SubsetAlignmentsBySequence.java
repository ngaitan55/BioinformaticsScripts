package ReadAlignmentTools;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ngsep.alignments.ReadAlignment;
import ngsep.alignments.io.ReadAlignmentFileReader;
import ngsep.alignments.io.ReadAlignmentFileWriter;
import ngsep.genome.ReferenceGenome;
import ngsep.sequences.QualifiedSequence;
import ngsep.sequences.QualifiedSequenceList;

public class SubsetAlignmentsBySequence {
	
	public static void run(String alnFile, String refFile, String outFile, List<String> sequencesToSubset)
			throws IOException, Exception {
		ReadAlignmentFileReader reader = new ReadAlignmentFileReader(alnFile);
		PrintStream stream = new PrintStream(outFile);
		ReferenceGenome refGenome = new ReferenceGenome(refFile);
		Map<String, Integer> seqMap = new HashMap<>();
		Map<String, Integer> refSeqMap = new HashMap<>();
		QualifiedSequenceList qualifiedSeqs = new QualifiedSequenceList();
		List<String> refSeqs = refGenome.getSequenceNamesStringList();
		for (String refSeq:refSeqs) {
			refSeqMap.put(refSeq, 0);
		}
		for (String seq:sequencesToSubset) {
			if(!refSeqMap.containsKey(seq)) throw new Exception("Sequence " + seq + " is not present in reference genome");
			seqMap.put(seq, 0);
			QualifiedSequence qualifiedSeq = new QualifiedSequence(seq);
			qualifiedSeqs.add(qualifiedSeq);
		}
		ReadAlignmentFileWriter writer = new ReadAlignmentFileWriter(qualifiedSeqs, stream);
		Iterator<ReadAlignment> it = reader.iterator();
		while(it.hasNext()) {
			ReadAlignment aln = it.next();
			String currentSequenceName = aln.getSequenceName();
			if(seqMap.containsKey(currentSequenceName)) {
				seqMap.compute(currentSequenceName, (k, v) -> (v == null) ? 1 : v++);
				writer.write(aln);
			}
		}
		writer.close();
	}
	
	public static void main(String[] args) throws IOException, Exception {
		// TODO Auto-generated method stub
		List<String> sequences = new ArrayList<>();
		for(int i = 3; i < args.length; i++) {
			sequences.add(args[i]);
		}
		run(args[0], args[1], args[2], sequences);
	}

}
