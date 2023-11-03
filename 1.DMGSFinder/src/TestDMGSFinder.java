import java.util.*;

public class TestDMGSFinder
{
   public static void main(String[] args)
   {
       String inputFile="Data/BRCA_germline_SNP_KNOWN_CODING.txt";
       String driverSet="BRCA1,BRCA2";
       String positiveFile="Data/BRCA_positive_set.txt";
       String infoType="genes";
       boolean maximization=true;
       int maxNumSolGenes=10;
       //String outputFile=null;
       //String outputFile="Results/BRCA_maximal_itemsets_SNP.txt";

       //Read mutation data
       System.out.println("Reading mutation matrix...");
       FileManager fm=new FileManager();
       String[] driverGeneNames=driverSet.split(",");
       HashSet<String> driverGenesSet=new HashSet<>(Arrays.asList(driverGeneNames));
       HashSet<String> setPositives=fm.getPositiveSet(inputFile,driverGenesSet,positiveFile,infoType);
       System.out.println(setPositives.size());
       Vector<HashMap<String,HashSet<String>>> listMutMatrices=fm.readMutationData(inputFile,setPositives);
       HashMap<String,HashSet<String>> posMutData=listMutMatrices.get(0);
       HashMap<String,HashSet<String>> negMutData=listMutMatrices.get(1);
       HashSet<String> setNegatives=Utility.getSampleSet(negMutData);
       System.out.println(setNegatives.size());

       //Initialize data structures
       HashSet<String> setGenes=Utility.getGeneSet(posMutData,negMutData);
       Iterator<String> it=setGenes.iterator();
       while(it.hasNext())
       {
           String geneCand=it.next();
           String[] split=geneCand.split("_");
           if(driverGenesSet.contains(split[0]))
               it.remove();
       }

       HashSet<String> currSol=new HashSet<>();
       HashMap<String,Integer> currMapPosCov=new HashMap<>();
       HashMap<String,Integer> currMapNegCov=new HashMap<>();
       for(String sample : setPositives)
           currMapPosCov.put(sample,0);
       for(String sample : setNegatives)
           currMapNegCov.put(sample,0);

       //Run greedy algorithm
       double lastBestCov=1.0;
       while(currSol.size()<maxNumSolGenes)
       {
           Vector<String> bestRes=Utility.findBestSol(setGenes,posMutData,negMutData,currMapPosCov,currMapNegCov,currSol,maximization);
           String bestGene=bestRes.get(0);
           double bestDiffCov=Double.parseDouble((bestRes).get(1));
           System.out.println(bestGene);
           System.out.println(bestDiffCov);
           Utility.updateSolution(bestGene,posMutData,negMutData,currMapPosCov,currMapNegCov,currSol);
           lastBestCov=bestDiffCov;
       }
       System.out.println(currSol+"\t"+lastBestCov);

   }
}
