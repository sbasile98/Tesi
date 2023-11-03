library(shiny)
library(imager)
library(DT)

#Get assets
directory <- "C:/Users/simon/Desktop/Tesi/5.Shiny/"

gene_driver_list <- scan(paste(directory, "input/geneList.txt", sep = ""),
                         what = "",
                         sep = "\n")

gene_driver_list[22] <- ""


pathway_name_list <- scan(paste(directory, "input/PathwaysName.txt", sep = ""),
                          what = "",
                          sep = "\n")

pathway_name_list[211] <- ""


#Definisco la UI
ui <- fluidPage(
  br(),
  
  titlePanel(
    h1("Association Rules for Genetic Mutations and their visualization on Biological Networks", align = "center")
  ),
  
  br(), br(),
  
  sidebarLayout(
    position = "right",
    
    sidebarPanel(
      selectInput(
        "gene",
        h4("Select a driver gene"),
        choices = gene_driver_list,
        selected = ""
      ),
      
      conditionalPanel(
        condition = "input.gene != ''",
        selectInput(
          "pathway",
          h4("Select a pathway"),
          choices = pathway_name_list,
          selected = ""
        )
      ),
      
      conditionalPanel(
        condition = "input.pathway != ''",
        actionButton(
          "submit",
          "Plot"
        )
      ),
      
      width = 2
    ),
    
    mainPanel(
      fluidRow(
        column(1),
        
        column(10,
               imageOutput("plot")
               ),
        
        column(1)
      ),
      
      br(), br(), br(), br(), br(), br(), br(), br(), br(), br(), br(),
      br(), br(), br(), br(), br(), br(), br(), br(), br(), br(), br(),
      br(), br(), br(), br(), br(), br(), br(), br(), br(), br(), br(),
      
      fluidRow(
        column(7,
          DT::dataTableOutput("centrality")
        ),
        
        column(1),
        
        column(4,
          DT::dataTableOutput("pagerank")
        )
      ),
      
      br(), br(),
      
      width = 10
    )
  )
)

#Definisco le logiche del server
server <- function(input, output) {
  
  observeEvent(input$submit, {
    output$plot <- renderImage({
      filename <- normalizePath(file.path(paste(directory, "output/plots/", input$pathway, ".", input$gene, ".png", sep = "")))
      
      list(src = filename,
           width = 1333,
           height = 972)
    })
  
    centrality <- read.table(paste(directory, "output/centralityValues/", input$pathway, "_centralityValues.txt", sep = ""),
                             col.names = c("gene", "betweenness", "closness", "degree"),
                             dec = ".",
                             sep = "\t")
      
    output$centrality <- DT::renderDataTable(
      {centrality},
      server = F,
      rownames = F,
      selection = "single")
    
    pagerank <- read.table(paste(directory, "output/endpointsPageRank/", input$pathway, "_endpointsPageRank.txt", sep = ""),
                           col.names = c("endpoint", "page_rank"),
                           dec = ".",
                           sep = "\t")
      
    output$pagerank <- DT::renderDataTable(
      {pagerank},
      server = F,
      rownames = F,
      selection = "single")
      
  })
}

#Run the app
shinyApp(ui = ui, server = server)