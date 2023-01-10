package ReadAlignmentTools;

import ngsep.alignments.ReadAlignment;
import ngsep.alignments.io.ReadAlignmentFileReader;
import ngsep.genome.ReferenceGenome;

import java.io.IOException;
import java.util.Iterator;

public class ReadAlignmentQualDistribution {

    public static final String SEPARATOR = "\t";

    public static void main(String[] args) throws IOException {
        String alignmentFile = args[0];
        ReferenceGenome refGenome = new ReferenceGenome(args[1]);
        try(ReadAlignmentFileReader alignmentReader = new ReadAlignmentFileReader(alignmentFile, refGenome)){
            int filterFlags = ReadAlignment.FLAG_READ_UNMAPPED;
            alignmentReader.setFilterFlags(filterFlags);
            //alignmentReader.setMinMQ(0);
            Iterator<ReadAlignment> it = alignmentReader.iterator();
            int[] qualityDistribution = new int[257];
            while(it.hasNext()){
                ReadAlignment aln = it.next();
                int quality = aln.getAlignmentQuality();
                qualityDistribution[quality] += 1;
            }
            for(int i = 0; i < 257; i++){
                System.out.println(i + SEPARATOR + qualityDistribution[i]);
            }
        }
    }
}
