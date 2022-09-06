import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import ngsep.genome.GenomicRegionSortedCollection;
import ngsep.genome.ReferenceGenome;
import ngsep.sequences.DNAMaskedSequence;
import ngsep.sequences.QualifiedSequenceList;
import ngsep.transcriptome.ProteinTranslator;
import ngsep.transcriptome.Transcript;
import ngsep.transcriptome.TranscriptSegment;
import ngsep.transcriptome.Transcriptome;
import ngsep.transcriptome.io.GFF3TranscriptomeHandler;

public class AnnotateGFF3ProteinSequence {
	
	public static void processGFF(String refGenomeFile, String gffFile) throws IOException {
		try(PrintWriter writer = new PrintWriter("proteinCDSSequences.fa")){
			ReferenceGenome genome = new ReferenceGenome(refGenomeFile);
			QualifiedSequenceList sequenceNames = genome.getSequencesList();
			GenomicRegionSortedCollection<TranscriptSegment> cdsRegions = new GenomicRegionSortedCollection<>(sequenceNames);
			GFF3TranscriptomeHandler gff3Handler = new GFF3TranscriptomeHandler(sequenceNames);
			Transcriptome transcriptome = gff3Handler.loadMap(gffFile);
			List<Transcript> transcripts = transcriptome.getAllTranscripts();
			for(Transcript transcript : transcripts) {
				List<TranscriptSegment> segments = transcript.getTranscriptSegments();
				for(TranscriptSegment segment : segments) {
					if(segment.getStatus() == TranscriptSegment.STATUS_CODING) {
						cdsRegions.add(segment);
					}
				}
			}
			cdsRegions.forceSort();
			for(TranscriptSegment cds : cdsRegions) writePromoterAnnotation(writer, cds, genome);
		}
	}
	
	private static void writePromoterAnnotation(PrintWriter writer, TranscriptSegment cds, ReferenceGenome genome) {
		int lineLength = 60;
		String header = ">" + cds.getTranscript().getGeneId() + ";" +
				cds.getSequenceName() + " " + cds.getFirst() + " " + cds.getLast();
		CharSequence sequence = genome.getReference(cds);
		System.out.println(genome.getSequenceCharacters(cds.getSequenceName()).length());
		System.out.println(header);
		boolean isPositiveStrand = cds.isPositiveStrand();
		if(!isPositiveStrand) sequence = DNAMaskedSequence.getReverseComplement(sequence);
		ProteinTranslator instance = ProteinTranslator.getInstance();
		String proteinSequence = instance.getProteinSequence(sequence);
		int pSeqLength = proteinSequence.length();
		writer.println(header);
		for(int i = 0; i < pSeqLength; i+=lineLength) {
			writer.println(proteinSequence.subSequence(i, Math.min(pSeqLength, i+=lineLength)));
		}
	}
	
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		processGFF(args[0], args[1]);
	}

}
