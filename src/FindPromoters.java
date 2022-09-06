import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import ngsep.genome.GenomicRegion;
import ngsep.genome.GenomicRegionImpl;
import ngsep.genome.GenomicRegionSortedCollection;
import ngsep.genome.ReferenceGenome;
import ngsep.sequences.DNAMaskedSequence;
import ngsep.sequences.QualifiedSequenceList;
import ngsep.transcriptome.Gene;
import ngsep.transcriptome.Transcript;
import ngsep.transcriptome.TranscriptSegment;
import ngsep.transcriptome.Transcriptome;
import ngsep.transcriptome.io.GFF3TranscriptomeHandler;

public class FindPromoters {
	
	public static int promoterSize = 2000;
	
	public static void findPromoters(String refGenomeFile, String GFF3File) throws IOException {
		try(PrintWriter writer = new PrintWriter("annotatedPromoters.fa")){
			ReferenceGenome genome = new ReferenceGenome(refGenomeFile);
			QualifiedSequenceList sequenceNames = genome.getSequencesList();
			GenomicRegionSortedCollection<Gene> promoters = new GenomicRegionSortedCollection<>(sequenceNames);
			GFF3TranscriptomeHandler gff3Handler = new GFF3TranscriptomeHandler(sequenceNames);
			Transcriptome transcriptome = gff3Handler.loadMap(GFF3File);
			List<Gene> genes = transcriptome.getAllGenes();
			for(Gene gene : genes) {
				boolean isPositiveStrand = gene.isPositiveStrand();
				String seq = gene.getSequenceName();
				int first = isPositiveStrand ? gene.getFirst()-promoterSize : gene.getLast();
				int last = isPositiveStrand ? gene.getFirst() : gene.getLast() + promoterSize;
				gene.setFirst(first);
				gene.setLast(last);
				promoters.add(gene);
			}
			promoters.forceSort();
			List<Gene> promotersList = promoters.asList();
			for(Gene promoter : promotersList) {
				writePromoterAnnotation(writer, promoter, genome);
			}
				/**else {
					for(TranscriptSegment segment : segments) {
						boolean is5UTR = TranscriptSegment.STATUS_5P_UTR == segment.getStatus();
						boolean is3UTR = TranscriptSegment.STATUS_3P_UTR == segment.getStatus();
						if((isPositiveStrand && is5UTR) || (!isPositiveStrand && is3UTR)) {
							GenomicRegion promoter = segment;
							writePromoterAnnotation(writer, gene, promoter, genome, isPositiveStrand, true);
							break;
						}
					}
				}**/
		}
	}
	
	private static void writePromoterAnnotation(PrintWriter writer, Gene promoter, ReferenceGenome genome) {
		int lineLength = 60;
		String header = ">" + promoter.getId() + ";" +
				promoter.getSequenceName() + " " + promoter.getFirst() + " " + promoter.getLast();
		CharSequence sequence = genome.getReference(promoter);
		if(sequence==null) {
			if(promoter.getLast() > genome.getSequenceCharacters(promoter.getSequenceName()).length()) {
				sequence = genome.getReference(promoter.getSequenceName(), promoter.getFirst(), genome.
						getSequenceCharacters(promoter.getSequenceName()).length());
			}
			else {
				sequence = genome.getReference(promoter.getSequenceName(), 1, promoter.getLast());
			}
		}
		boolean isPositiveStrand = promoter.isPositiveStrand();
		if(!isPositiveStrand) sequence = DNAMaskedSequence.getReverseComplement(sequence);
		int seqLength = sequence.length();
		writer.println(header);
		for(int i = 0; i < seqLength; i+=lineLength) {
			writer.println(sequence.subSequence(i, Math.min(seqLength, i+=lineLength)));
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String refFile = args[0];
		String GFF3File = args[1];
		findPromoters(refFile, GFF3File);
	}

}
