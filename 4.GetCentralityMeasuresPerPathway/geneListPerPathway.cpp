/*Il seguente codice prende in input una pathway (ogni pathway è salvata in input/Pathways/pathway_name.txt) e ritorna in output il corrispettivo
  file (salvato in pathwaysGenes/pathway_name_genelist.txt) in cui è presente la lista di tutti i geni che compongono la pathway.*/

#include <bits/stdc++.h>

using namespace std;

int main() {
	ifstream pathways("input/pathwaysName.txt");
	
	while(!pathways.eof()) {
		string current_pathway;
		pathways >> current_pathway;
		
		cout << "Processing pathway " << current_pathway << "..." << endl;
		
		ifstream hsa(("input/Pathways/" + current_pathway + ".txt").c_str());
		ofstream out(("pathwaysGenes/" + current_pathway + "_genelist.txt").c_str());
		
		set<string> nodes;
		
		while(!hsa.eof()) {
			string tmp;
		
			hsa >> tmp;
			nodes.insert(tmp);
			
			hsa >> tmp;
			nodes.insert(tmp);
			
			hsa >> tmp;
		}
		
		for(set<string>::iterator i = nodes.begin(); i != nodes.end(); ++i) {
			if(i != nodes.begin()) out << *i << endl;
		}
			
	}
	
	cout << "Operation complete... Output generated in pathwaysGenes directory." << endl;
	return 0;
}
