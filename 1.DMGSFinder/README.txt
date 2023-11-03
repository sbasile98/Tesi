Software: DMGSFinder
Author: Giovanni Micale

Description: DMGSFinder is a greedy algorithm for finding Differentially Mutated Gene Sets (DMGS).
A DMGS is a set of genes that maximizes the differential coverage with respect to a set of positive samples and a set of negative samples.


USAGE:

java -cp ./out DMGSFinder -m <mutationsFile> -p <infoType> <positiveInfo>
		-ms <maxNumSolutionGenes> -o <resultsFile>
		
REQUIRED PARAMETERS:
-m	Mutation matrix file
-p		Info type: can be 'genes' or 'samples'
		Positive info:
		a) If Info type is 'genes', a list of comma separated genes (with or without '-' prefix for non-mutated or mutated genes, respectively)
		b) If Info type is 'samples', a file of samples

OPTIONAL PARAMETERS:
-ms	Maximum number of genes in the reported solutions (default=10)
-o		Results file (default=print results to standard output)



EXAMPLES:

1) Find a DMGS with 10 genes starting from a set of driver genes D={BRCA1,BRCA2}.
The positive set will be the set of samples where at least one gene among BRCA1 and BRCA2 is mutated, 
the negative set will be the one in which neither BRCA1 nor BRCA2 are mutated.

java -cp ./out DMGSFinder -m Data/BRCA_germline_matrix_ALL.txt -p genes BRCA1,BRCA2


2) Find a DMGS with 10 genes starting from a file of positive samples.
Save final results to "results.txt"

java -cp ./out DMGSFinder -m Data/BRCA_germline_matrix_ALL.txt -p samples positives.txt -o results.txt


8) Find a DMGS with 20 genes starting from a set of driver genes D={BRCA1,BRCA2}.

java -cp ./out DMGSFinder -m Data/BRCA_germline_matrix_ALL.txt -p genes BRCA1,BRCA2 -ms 50



INPUT FILES FORMAT:

- Mutation matrix file

The first line contains the names of the samples.
The following lines contain the name of the gene followed by a list of numbers.
Rows are genes and columns are samples. Each entry contains 0 if a gene is mutated in a sample. 
Any other value means that the gene is mutated in the sample.
Values and names are separated by tabs (\t) 

Example:

Sample1	Sample2	Sample3	Sample4
Gene1	0	0	2	1
Gene2	1	0	3	0


- Positive set file (if required)

The positive set file contains a list of samples that form the set of positives. Each row contains a sample. 
Samples must match the columns of the mutation matrix

Example:

Sample1
Sample3



RESULTS FILE FORMAT

The results file contains the DMGS found. 

The first line is an header with the names of all statistics reported for the DMGS:
a) DMGS: the list of genes forming the DMGS;
b) Average positive coverage;	
c) Average negative coverage;
d) Differential coverage.

The following lines contain the values of each statistic for each reported solution.
All the values of the statistics are separated by tab character (\t).

Example:

DMGS	Average positive coverage	Average negative coverage	Differential coverage
[DOCK11, LOC100506083, TMEM138, IL1R2, CASP4, IL31RA, PLEKHA6, SPATA31C1, FER1L6, TANC2]	74.39%	19.0%	55.39%
