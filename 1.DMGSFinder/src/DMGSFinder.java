import java.util.*;

public class DMGSFinder
{
   public static void main(String[] args)
   {
       String mutationsFile=null;
       String positiveInfo=null;
       String positiveType=null;
       int maxNumSolGenes=10;
       String outputFile=null;

       int i;
       for (i=0;i<args.length;i++)
       {
           switch (args[i])
           {
               case "-m" -> mutationsFile = args[++i];
               case "-p" -> {
                   positiveType = args[++i];
                   positiveInfo = args[++i];
               }
               case "-ms" -> maxNumSolGenes = Integer.parseInt(args[++i]);
               case "-o" -> outputFile = args[++i];
               default -> {
                   System.out.println("Error! Unrecognizable command '" + args[i] + "'");
                   printHelp();
                   System.exit(1);
               }
           }
       }

       //Error in case mutation matrix file and/or info about positive samples or gene types are missing or wrong
       if(mutationsFile==null)
       {
           System.out.println("Error! No file for mutation matrix has been specified!");
           printHelp();
           System.exit(1);
       }
       if(positiveInfo==null)
       {
           System.out.println("Error! No info about the set of positive samples have been specified!");
           printHelp();
           System.exit(1);
       }
       if(!positiveType.equals("samples") && !positiveType.equals("genes"))
       {
           System.out.println("Error! '"+positiveType+"' is not a valid value for '-p' parameter!");
           printHelp();
           System.exit(1);
       }

       //Read mutation data
       System.out.println("\nReading mutation matrix...");
       FileManager fm=new FileManager();
       HashSet<String> driverGenesSet=null;
       if(positiveType.equals("genes"))
       {
           String[] driverGeneNames=positiveInfo.split(",");
           driverGenesSet=new HashSet<>(Arrays.asList(driverGeneNames));
       }
       HashSet<String> setPositives=fm.getPositiveSet(mutationsFile,driverGenesSet,positiveInfo,positiveType);
       //System.out.println(setPositives.size());
       Vector<HashMap<String,HashSet<String>>> listMutMatrices=fm.readMutationData(mutationsFile,setPositives);
       HashMap<String,HashSet<String>> posMutData=listMutMatrices.get(0);
       HashMap<String,HashSet<String>> negMutData=listMutMatrices.get(1);
       HashSet<String> setNegatives=Utility.getSampleSet(negMutData);
       //System.out.println(setNegatives.size());

       //Build the set of candidate genes
       HashSet<String> setGenes=Utility.getGeneSet(posMutData,negMutData);

       //Remove driver set genes from the set of candidates, if necessary
       if(driverGenesSet!=null)
       {
           Iterator<String> it=setGenes.iterator();
           while(it.hasNext())
           {
               String geneCand=it.next();
               String[] split=geneCand.split("_");
               if(driverGenesSet.contains(split[0]))
                   it.remove();
           }
       }

       //Initialize data structures
       HashSet<String> currSol=new HashSet<>();
       HashMap<String,Integer> currMapPosCov=new HashMap<>();
       HashMap<String,Integer> currMapNegCov=new HashMap<>();
       for(String sample : setPositives)
           currMapPosCov.put(sample,0);
       for(String sample : setNegatives)
           currMapNegCov.put(sample,0);

       //Run greedy algorithm
       System.out.println("Start algorithm...");
       double lastPosCov=1.0;
       double lastNegCov=1.0;
       double lastDiffCov=1.0;
       int iter=1;
       while(currSol.size()<maxNumSolGenes)
       {
           System.out.print("Step "+iter+"/"+maxNumSolGenes+"\t");
           Vector<String> bestRes=Utility.findBestSol(setGenes,posMutData,negMutData,currMapPosCov,currMapNegCov,currSol,true);
           String bestGene=bestRes.get(0);
           double bestAvgPosCov=Double.parseDouble(bestRes.get(1));
           double bestAvgNegCov=Double.parseDouble(bestRes.get(2));
           double bestDiffCov=bestAvgPosCov-bestAvgNegCov;
           //System.out.println(bestGene);
           //System.out.println(bestDiffCov);
           double posCovPerc=((double)Math.round(bestAvgPosCov*10000))/100;
           double negCovPerc=((double)Math.round(bestAvgNegCov*10000))/100;
           double diffCovPerc=((double)Math.round(bestDiffCov*10000))/100;
           System.out.println("Best gene: "+bestGene+"\t"+"PosCov: "+posCovPerc+"%\tNegCov: "+negCovPerc+"%\tDiffCov: "+diffCovPerc+"%");
           Utility.updateSolution(bestGene,posMutData,negMutData,currMapPosCov,currMapNegCov,currSol);
           lastPosCov=posCovPerc;
           lastNegCov=negCovPerc;
           lastDiffCov=diffCovPerc;
           iter++;
       }

       //Print or save results
       fm.writeResults(outputFile,currSol,lastPosCov,lastNegCov,lastDiffCov);

   }

    private static void printHelp()
    {
        String help = "Usage: java -cp ./out DMGSFinder -m <mutationsFile> -p <infoType> <positiveInfo> " +
                "[-ms <maxNumSolutionGenes> -o <resultsFile>]\n\n";
        help+="REQUIRED PARAMETERS:\n";
        help+="-m\tMutation matrix file\n";
        help+="-p\tInfo type: can be 'genes' or 'samples'\n";
        help+="\tPositive info:\n";
        help+="\ta) If Info type is 'genes', a list of comma separated genes (with or without '-' prefix for wild type or mutated genes, respectively)\n";
        help+="\tb) If Info type is 'samples', a file of samples\n\n";
        help+="OPTIONAL PARAMETERS:\n";
        help+="-ms\tMaximum number of genes in the reported solution (default=10)\n";
        help+="-o\tResults file (default=print results to standard output)\n\n";
        System.out.println(help);
    }
}
