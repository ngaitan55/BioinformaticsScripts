package VCFModifiers;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ngsep.genome.GenomicRegion;
import ngsep.genome.GenomicRegionSortedCollection;
import ngsep.genome.ReferenceGenome;
import ngsep.variants.GenomicVariant;
import ngsep.variants.GenomicVariantImpl;
import ngsep.vcf.VCFFileHeader;
import ngsep.vcf.VCFFileReader;
import ngsep.vcf.VCFFileWriter;
import ngsep.vcf.VCFRecord;

public class RemoveIntersectingVCFRecords {
		
	public static final String SEPARATOR = "\t";
	
	public static void run(String file, String refGenome) throws IOException{
		ReferenceGenome ref = new ReferenceGenome(refGenome);
		GenomicRegionSortedCollection<VCFRecord> records = new GenomicRegionSortedCollection<>(ref.getSequencesList());
		VCFFileReader reader = new VCFFileReader(file);
		VCFFileHeader header = reader.getHeader();
		Iterator<VCFRecord> it = reader.iterator();
		while(it.hasNext()) {
			VCFRecord rec = it.next();
			records.add(rec);
		}
		records.forceSort();
		int n = records.size();
		boolean[] visited = new boolean[n];
		Set<VCFRecord> recordsToKeep = new HashSet<>();
		List<VCFRecord> recordsList = records.asList();
		for (int i = 0; i < n; i++) {
			VCFRecord rec = recordsList.get(i);
			List<VCFRecord> spanningVariants = records.findSpanningRegions(rec).asList();
			if(!visited[i]) {
				if(spanningVariants.size() < 2) {
					recordsToKeep.add(rec);
					continue;
				}
				recordsToKeep.add(spanningVariants.get(spanningVariants.size() - 1));
				for(VCFRecord visitedVariant : spanningVariants) {
					int idx = recordsList.indexOf(visitedVariant);
					visited[idx] = true;
				}
			}
		}
		records.retainAll(recordsToKeep);
		List<VCFRecord> filteredRecordsList = records.asList();
		try(PrintStream pr = new PrintStream(file);){
			VCFFileWriter writer = new VCFFileWriter();
			writer.printHeader(header, pr);
			writer.printVCFRecords(filteredRecordsList, pr);
		}
		String bedFile = "indels.HACK.bed";
		try(PrintWriter writer = new PrintWriter(bedFile)){
			String sep = "\t";
			for(int i = 0; i < filteredRecordsList.size(); i++) {
				VCFRecord rec = filteredRecordsList.get(i);
				String seq = rec.getSequenceName();
				int first = rec.getFirst();
				int last = rec.getLast();
				int length = rec.length();
				String type = GenomicVariantImpl.getVariantTypeName(rec.getVariant().getType());
				String info = "";
				if(type.equals(GenomicVariant.TYPENAME_LARGEDEL)) {
						type = "deletion";
						info = "None";
				}
				else if(type.equals(GenomicVariant.TYPENAME_LARGEINS)) {
					type = "insertion";
					info = rec.getVariant().getAlleles()[1];
				}
				else if(type.equals(GenomicVariant.TYPENAME_INVERSION)) {
					type = "inversion";
					info = "None";
				}
				int random = new Random().nextInt(11);
				writer.println(seq + SEPARATOR + first + SEPARATOR + last + SEPARATOR + type + SEPARATOR + info
						+ SEPARATOR + random);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		run(args[0], args[1]);
	}

}
