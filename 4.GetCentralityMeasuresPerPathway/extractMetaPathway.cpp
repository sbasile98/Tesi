/*Questo semplice codice prende in input un file semplificato della MetaPathway (in cui è presente la sola lista di archi di cui la MetaPathway è composta)
  e restituisce in output la lista di tutti i nodi che la compongono (senza ripetizione di nodi).*/

#include <bits/stdc++.h>

using namespace std;

ifstream in("input/edgesMetaPathway.txt");
ofstream out("input/nodesMetaPathway.txt");

int main() {
	set<string> nodes;
	string tmp;
	
	while(!in.eof()) {
		in >> tmp;
		nodes.insert(tmp);
	}
	
	for(set<string>::iterator i = nodes.begin(); i != nodes.end(); ++i) {
		out << *i << endl;
	}
}
