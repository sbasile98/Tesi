/*Il seguente codice genera, a partire da una singola pathway (salvata in input/Pathways/nome_pathway.txt),
  un file di testo (salvato in endpoints/nome_pathway_endpoints.txt) nel quale è riportata la lista di tutti
  gli endpoints della pathway presa in esame (ovvero l'insieme di tutti i nodi i quali hanno solo archi entranti.*/

#include <bits/stdc++.h>

using namespace std;

int main() {
	ifstream pathways("input/PathwaysName.txt");
	
	while(!pathways.eof()) {
		string current_pathway;
		pathways >> current_pathway;
		
		cout << "Processing pathway " << current_pathway << "..." << endl;
		
		ifstream hsa(("input/Pathways/" + current_pathway + ".txt").c_str());
		ofstream out(("endpoints/" + current_pathway + "_endpoints.txt").c_str());
		
		set<string> starts, ends, endpoints;
		
		while(!hsa.eof()) {
			string tmp;
		
			hsa >> tmp;
			starts.insert(tmp);
			
			hsa >> tmp;
			ends.insert(tmp);
			
			hsa >> tmp;
		}
		
		for(set<string>::iterator i = ends.begin(); i != ends.end(); ++i) {
			set<string>::iterator j;
	    	for(j = starts.begin(); j != starts.end(); ++j) {
	    		if (*i == *j) break;
			}
			if(j == starts.end()) endpoints.insert(*i);
		}
		
		for(set<string>::iterator i = endpoints.begin(); i != endpoints.end(); ++i) {
			out << *i << endl;
		}
			
	}
	
	cout << "Operation complete... Output generated in endpoints directory." << endl;
	return 0;
}
