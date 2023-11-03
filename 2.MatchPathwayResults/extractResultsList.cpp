/*Con il seguente codice estraggo la lista di geni dai file generati dal DMGSFinder*/

#include <bits/stdc++.h>
#include <regex>
#include <string>
#include <algorithm>

using namespace std;
using namespace std::regex_constants;

ifstream geneList("input/geneList.txt");

/*Funzione ad-hoc per eseguire la funzione split su una stringa in un vector*/
vector<string> split(string str, string sep) {
    char* cstr = const_cast<char*>(str.c_str());
    char* current;
    vector<string> arr;
    current = strtok(cstr,sep.c_str());
    while(current != NULL) {
        arr.push_back(current);
        current = strtok(NULL, sep.c_str());
    }
    return arr;
}


int main() {
    while(!geneList.eof()) {
        string gene;
        geneList >> gene;
        
        cout << gene << endl;
        
        ifstream geneResult(("DMGSResults/" + gene + ".txt").c_str());
        ofstream out(("input/" + gene + ".txt").c_str());
        
        string fileContent = "";
        
        while (!geneResult.eof()) {
            string tmp;
            geneResult >> tmp;
            fileContent += tmp;
        }
        
        regex e("(<?\\[)((\\w*,)*\\w*)", ECMAScript | icase );
        
        smatch m;
        
        string str = "";
        
        while (std::regex_search (fileContent,m,e)) {
            str += m[0];
            fileContent = m.suffix().str();
        }
        
        str.erase(std::remove(str.begin(), str.end(), '['), str.end());
        
        vector<string> arr = split(str, ", ");
        
        for (size_t i = 0; i < arr.size(); i++) {
            if (i < arr.size() - 1) out << arr[i] << " ";
            else out << arr[i];
        }
    }
    return 0;
}
