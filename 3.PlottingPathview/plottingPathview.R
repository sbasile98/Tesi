library(pathview)
library(org.Hs.eg.db)
library(data.table)

directory <- "C:/Users/simon/Desktop/Tesi/3.PlottingPathview/"

#Imposto la directory nella quale salvare i plot
setwd("C:/Users/simon/Desktop/Tesi/3.PlottingPathview/plots")

#Inizializzo il database per la traduzione dei nomi dei geni in EntrezID
hs <- org.Hs.eg.db

#Leggo la lista di tutte le pathway
pathway_name_list <- scan(paste(directory, "data/PathwaysName.txt", sep = ""),
                          what = "",
                          sep = "\n")

#Leggo la lista di tutti i geni driver
gene_driver_list <- scan(paste(directory, "data/geneList.txt", sep = ""),
                         what = "",
                         sep = "\n")

for(i in 1:length(gene_driver_list)) {
  current_gene <- gene_driver_list[i]
  
  for(j in 1:length(pathway_name_list)) {
    #Inizializzo il database da manipolare per effettuare il plot finale
    data("gse16873.d")
    
    current_pathway <- pathway_name_list[j]
    
    #Leggo la tabella con i record [gene - (mutazione/non-mutazione)] relativa al gene driver e al pathway correnti
    genePath <- read.table(paste(directory, "input/", current_gene, "_", current_pathway, "_out.txt", sep = ""),
                           header = FALSE,
                           sep = "\t")
    
    #Estraggo solo la colonna relativa ai nomi dei geni per procedere alla traduzione in EntrezID
    geneLabels <- as.vector(genePath[, 1])
    
    #Effettuo la traduzione dal nome del gene al EntrezID del gene
    geneEntrezID <- select(hs,
                           keys = geneLabels,
                           columns = c("ENTREZID"),
                           keytype = "SYMBOL")
    
    listEntrezID <- as.array(geneEntrezID$ENTREZID)
    listDataBase <- as.array(rownames(gse16873.d))
    
    #Estraggo i geni comuni tra il database e il gene corrente per evitare problemi con il plot
    geneIntersection <- Reduce(intersect, list(listDataBase, listEntrezID))
    
    #Estraggo il database finale su cui effettuare le modifiche per i colori
    finalDataBase <- gse16873.d[geneIntersection, ]
    
    #Se ho problemi con la fase di plotting la salto e passo al ciclo successivo
    tryCatch({
      #Imposto i colori ciclando sul database finale
      for(k in 1:length(finalDataBase)) {
        #Seleziono il label del gene relativo all'EntrezID corrente
        entry <- rownames(finalDataBase)[k]
        
        if(is.na(entry) || is.null(entry)) next
        
        label <- select(hs,
                        keys = entry,
                        columns = c("SYMBOL"),
                        keytype = "ENTREZID")
        
        #Seleziono il valore da passare per la codifica del colore nel plot
        val <- genePath[genePath$V1 == label$SYMBOL, ]$V2
        
        #Assegno il valore trovato
        finalDataBase[k, ] <- val
      }
      
      #Plot finale
      pathview::pathview(gene.data = finalDataBase[, 1],
                         pathway.id = substring(current_pathway, 4),
                         species = "hsa",
                         kegg.dir = "C:/Users/simon/Desktop/Tesi/3.PlottingPathview/kegg/",
                         out.suffix = current_gene,
                         discrete = list(gene = TRUE, cpd = TRUE),
                         limit = list(gene = 1, cpd = 1),
                         bins = list(gene = 2, cpd = 1),
                         mid = list(gene = "#00FF00", cpd = "#00FF00"),
                         high = list(gene = "#FF0000", cpd = "#00FF00"),
                         kegg.native = TRUE)
    },
    error = function(e) {
      print(paste("ERROR:", current_pathway, "-", current_gene, sep = " "))
    })
  }
}

