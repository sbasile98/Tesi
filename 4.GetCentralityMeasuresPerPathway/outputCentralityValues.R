#Il seguente codice, presi in input i file che sono stati generati in questa directory, restituisce in output un coppia di
#file di testo per pathway così costruiti:
#1. gene, betweeness centrality, closness centrality e degree centrality
#2. gene_endpoint e relativo page rank

library(igraph)

#asset del PC
directory <- "C:/Users/simon/Desktop/Tesi/4.GetCentralityMeasuresPerPathway/"

#Costruisco il grafo relativo alla MetaPathway
nodi <- read.table(paste(directory,"input/nodesMetaPathway.txt", sep = ""),
                   header = FALSE,
                   sep = "\t",
                   dec = ".")

archi <- read.table(paste(directory,"input/edgesMetaPathway.txt", sep = ""),
                    header = FALSE,
                    sep = "\t",
                    dec = ".")

metaPathway <- graph_from_data_frame(archi,directed = TRUE, vertices = nodi)


#Leggo la lista di pathway
pathway_name_list <- scan(paste(directory,"input/PathwaysName.txt", sep = ""),
                          what = "",
                          sep = "\n")


#Ciclo per ogni pathway nella lista delle pathway
for(i in 1:length(pathway_name_list)) {
  pathway_name <- pathway_name_list[i]
  
  #Per ogni pathway procuro la lista dei geni che la compongono
  current_pathway_nodes <- scan(paste(directory, "pathwaysGenes/", pathway_name, "_genelist.txt", sep = ""),
                            what = "",
                            sep = "\n")
  
  #Per ogni pathway procuro la lista degli endpoint
  current_pathway_endpoints <- scan(paste(directory, "endpoints/", pathway_name, "_endpoints.txt", sep = ""),
                                    what = "",
                                    sep = "\n")
  
  #Calcolo le misure di centralità per i nodi che compongono le pathway
  between_centrality <- betweenness(metaPathway,
                                    v = current_pathway_nodes)
  closness_centrality <- closeness(metaPathway,
                                   v = current_pathway_nodes)
  degree_centrality <- degree(metaPathway,
                              v = current_pathway_nodes)
  page_rank <- page.rank(metaPathway,
                         vids = current_pathway_endpoints)
  
  #Creo la matrice riassuntiva di tutti i valori (between, closness, degree) e la esporto su txt
  mtrx <- matrix(c(current_pathway_nodes, between_centrality, closness_centrality, degree_centrality),
                 ncol = 4)
  
  write.table(mtrx,
              file = paste(directory, "centralityValues/", pathway_name, "_centralityValues.txt", sep = ""),
              quote = FALSE,
              row.names = FALSE,
              col.names = FALSE,
              sep = "\t")
  
  #Creo la matrice relativa ai PageRank degli endpoint e la esporto su txt
  mtrx_pr <- matrix(c(current_pathway_endpoints,page_rank[["vector"]]), ncol = 2)
  
  write.table(mtrx_pr,
              file = paste(directory, "endpointsPageRank/", pathway_name, "_endpointsPageRank.txt", sep = ""),
              quote = FALSE,
              row.names = FALSE,
              col.names = FALSE,
              sep = "\t")
}