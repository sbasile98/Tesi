
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class FileManager
{

    public Vector<HashMap<String,HashSet<String>>> readMutationData(String inputFile, HashSet<String> positiveSet)
    {
        Vector<HashMap<String,HashSet<String>>> mutMatrices=new Vector<>();
        HashMap<String,HashSet<String>> posMutMatrix=new HashMap<>();
        HashMap<String,HashSet<String>> negMutMatrix=new HashMap<>();
        try
        {
            BufferedReader br=new BufferedReader(new FileReader(inputFile));
            String str=br.readLine();
            String[] sampleNames=str.split("\t");
            while((str=br.readLine())!=null)
            {
                String[] split=str.split("\t");
                String gene=split[0];
                for(int i=1;i<split.length;i++)
                {
                    int freq=Integer.parseInt(split[i]);
                    String sample=sampleNames[i-1];
                    if(freq>0)
                    {
                        if(positiveSet.contains(sample))
                        {
                            if(posMutMatrix.containsKey(gene))
                                posMutMatrix.get(gene).add(sample);
                            else
                            {
                                HashSet<String> setSamples=new HashSet<>();
                                setSamples.add(sample);
                                posMutMatrix.put(gene,setSamples);
                            }
                        }
                        else
                        {
                            if(negMutMatrix.containsKey(gene))
                                negMutMatrix.get(gene).add(sample);
                            else
                            {
                                HashSet<String> setSamples=new HashSet<>();
                                setSamples.add(sample);
                                negMutMatrix.put(gene,setSamples);
                            }
                        }
                    }
                }
            }
            br.close();
        }
        catch (Exception e){
            System.out.println(e.toString());
        }

        mutMatrices.add(posMutMatrix);
        mutMatrices.add(negMutMatrix);
        return mutMatrices;
    }

    private HashSet<String> readPositiveSetFile(String samplesFile)
    {
        HashSet<String> positiveSet=new HashSet<>();
        try
        {
            String str;
            BufferedReader br=new BufferedReader(new FileReader(samplesFile));
            while((str=br.readLine())!=null)
                positiveSet.add(str);
            br.close();
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        return positiveSet;
    }

    public HashSet<String> getPositiveSet(String inputFile, HashSet<String> driverGenesSet, String positiveFile, String infoType)
    {
        HashSet<String> positiveSet;
        if(infoType.equals("genes"))
        {
            positiveSet=new HashSet<>();
            try
            {
                BufferedReader br=new BufferedReader(new FileReader(inputFile));
                String str=br.readLine();
                String[] setSamples=str.split("\t");
                while((str=br.readLine())!=null)
                {
                    String[] split=str.split("\t");
                    String gene=split[0];
                    String[] split2=gene.split("_");
                    if(driverGenesSet.contains(split2[0]))
                    {
                        for(int i=1;i<split.length;i++)
                        {
                            int freq=Integer.parseInt(split[i]);
                            if(freq>0)
                                positiveSet.add(setSamples[i-1]);
                        }
                    }
                }
                br.close();
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }
        else
            positiveSet=readPositiveSetFile(positiveFile);
        return positiveSet;
    }

    public void writeResults(String outputFile, HashSet<String> geneSet, double posCov, double negCov, double diffCov)
    {
        if(outputFile==null)
        {
            System.out.println("\nDMGS: "+geneSet);
            System.out.println("Average positive coverage: "+posCov+"%");
            System.out.println("Average negative coverage: "+negCov+"%");
            System.out.println("Differential coverage: "+diffCov+"%");
        }
        else
        {
            try{
                BufferedWriter bw=new BufferedWriter(new FileWriter(outputFile));
                bw.write("DMGS\tAverage positive coverage\tAverage negative coverage\tDifferential coverage\n");
                bw.write(geneSet+"\t"+posCov+"%\t"+negCov+"%\t"+diffCov+"%\n");
                bw.close();
            }
            catch (Exception e){
                System.out.println(e);
            }
            System.out.println("Results written to "+outputFile);
        }
    }
}
