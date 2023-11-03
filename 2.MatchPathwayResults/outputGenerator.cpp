/*Con il seguente codice verranno presi in input il file delle soluzioni generate da DMGSFinder e i files contenenti ognuno dei pathways
	della cartella Pathway, e per ogni gene driver preso in considerazione dal file geneList.txt verr‡ generato un output per ogni pathway
	preso in considerazione che sar‡ cosÏ strutturato:
	1. Il gene del pathway preso in considerazione
	2. Un valore intero nell'intervallo [-1,1] che indicher‡:
		2a. se 1: la mutazione del gene
		2b. se 0: il gene non è né mutato né non mutato
	Il file di output avrà questa nome genedriver_genedelpathway_out.txt*/

#include<bits/stdc++.h>

using namespace std;


struct nodes {
	string node;
	int weight;
	
	nodes() {
		weight = 0;
	}
};

set<string> geneList(string current_pathway) {
	ifstream pathways(("Pathways/Pathways/" + current_pathway + ".txt").c_str());
	
	set<string> genes;

	while(!pathways.eof()) {
		string a;
		pathways >> a;
		if(a != "activation" &&  a != "inhibition") {
			genes.insert(a);
		}
	}
	
	return genes;
}

nodes* createOutputNodes(string current_pathway) {
	set<string> genes = geneList(current_pathway);
	int n = genes.size();
	
	nodes *g = new nodes[n];
	
	int i = 0;
	for(set<string>::iterator it = genes.begin(); it != genes.end(); ++it) {
    	g[i++].node = *it;
	}
	
	return g;
}

nodes* createOutputWeights(nodes* g, string driver, int n) {
	ifstream driver_genes_results(("input/" + driver + ".txt").c_str());
	
	set<string> pat;
	
	while(!driver_genes_results.eof()) {
		string a;
		driver_genes_results >> a;
		pat.insert(a);
	}
	
	for(int i = 1; i < n; i++){
		for(set<string>::iterator it = pat.begin(); it != pat.end(); ++it) {
	    	string a = *it;
	    	if(a == g[i].node){
				g[i].weight = 1;
			}
		}
	}
	
	return g;
}


int main() {
    ifstream driver_genes("input/geneList.txt");
	int sum = 0;
	while(!driver_genes.eof()) {
		string driver;
		driver_genes >> driver;
		cout << driver;
        
        bool mutated = false;
		
		ifstream filenames("Pathways/PathwaysName.txt");
		while(!filenames.eof()) {
			string current_pathway;
			filenames >> current_pathway;
			
			int n = geneList(current_pathway).size();
			nodes* output = createOutputNodes(current_pathway);
			output = createOutputWeights(output,driver,n);
			
            ofstream out(("output/" + driver + "_" + current_pathway + "_out.txt").c_str());
            
			for(int i = 1; i < n; i++) {
				out << output[i].node << "\t" << output[i].weight <<"\n";
				sum += output[i].weight;
                if(output[i].weight == 1) {
                    mutated = true;
                }
			}
            
            if (mutated) cout << "+ ";
            else cout << "* ";
            mutated = false;
		}
        cout << sum << endl << endl;
        sum = 0;
	}
	return 0;
}
