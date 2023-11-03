/*Il seguente codice esegue le chiamate da console per tutti i geni su cui bisogna applicare il tool DMGSFinder
 e salva i risultati nella cartella results*/

#include <iostream>
#include <fstream>
#include <string>

using namespace std;

ifstream geneList("data/geneList.txt");

int main() {
    while(!geneList.eof()) {
        string gene;
        geneList >> gene;
        
        system(("java -cp ./out DMGSFinder -m data/BRCA_germline_matrix_EXON.txt -p genes " + gene + " -o results/" + gene + ".txt").c_str());
    }
}
