import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class Utility
{
    public static HashSet<String> getSampleSet(HashMap<String,HashSet<String>> mapMutations)
    {
        HashSet<String> sampleSet=new HashSet<>();
        for(String gene : mapMutations.keySet())
            sampleSet.addAll(mapMutations.get(gene));
        return sampleSet;
    }

    public static HashSet<String> getGeneSet(HashMap<String,HashSet<String>> mapPosMutations, HashMap<String,HashSet<String>> mapNegMutations)
    {
        HashSet<String> setGenes=new HashSet<>();
        setGenes.addAll(mapPosMutations.keySet());
        setGenes.addAll(mapNegMutations.keySet());
        return setGenes;
    }

    public static Vector<String> findBestSol(HashSet<String> setGenes, HashMap<String,HashSet<String>> mapPosMutations, HashMap<String,HashSet<String>> mapNegMutations, HashMap<String,Integer> currMapPosCov, HashMap<String,Integer> currMapNegCov, HashSet<String> currSol, boolean maximization)
    {
        double bestAvgPosCov=0.0;
        double bestAvgNegCov=0.0;
        double bestCovDiff=0.0;
        String bestGene="";
        for(String gene : setGenes)
        {
            if(!currSol.contains(gene))
            {
                HashMap<String,Integer> mapSamplePosCov=new HashMap<>(currMapPosCov);
                if(mapPosMutations.containsKey(gene))
                {
                    HashSet<String> setSamples=mapPosMutations.get(gene);
                    for(String sample : setSamples)
                        mapSamplePosCov.put(sample,mapSamplePosCov.get(sample)+1);
                }
                HashMap<String,Integer> mapSampleNegCov=new HashMap<>(currMapNegCov);
                if(mapNegMutations.containsKey(gene))
                {
                    HashSet<String> setSamples=mapNegMutations.get(gene);
                    for(String sample : setSamples)
                        mapSampleNegCov.put(sample,mapSampleNegCov.get(sample)+1);
                }
                double avgPosCov=0.0;
                for(String sample : mapSamplePosCov.keySet())
                    avgPosCov+=((double)mapSamplePosCov.get(sample))/(currSol.size()+1);
                avgPosCov/=mapSamplePosCov.size();
                double avgNegCov=0.0;
                for(String sample : mapSampleNegCov.keySet())
                    avgNegCov+=((double)mapSampleNegCov.get(sample))/(currSol.size()+1);
                avgNegCov/=mapSampleNegCov.size();
                double covDiff=avgPosCov-avgNegCov;
                if(maximization)
                {
                    if(covDiff>bestCovDiff)
                    {
                        bestCovDiff=covDiff;
                        bestAvgPosCov=avgPosCov;
                        bestAvgNegCov=avgNegCov;
                        bestGene=gene;
                    }
                }
                else
                {
                    if(covDiff<bestCovDiff)
                    {
                        bestCovDiff=covDiff;
                        bestAvgPosCov=avgPosCov;
                        bestAvgNegCov=avgNegCov;
                        bestGene=gene;
                    }
                }
            }
        }
        Vector<String> bestRes=new Vector<>();
        bestRes.add(bestGene);
        bestRes.add(String.valueOf(bestAvgPosCov));
        bestRes.add(String.valueOf(bestAvgNegCov));
        return bestRes;
    }

    public static void updateSolution(String bestGene, HashMap<String,HashSet<String>> mapPosMutations, HashMap<String,HashSet<String>> mapNegMutations, HashMap<String,Integer> currMapPosCov, HashMap<String,Integer> currMapNegCov, HashSet<String> currSol)
    {
        currSol.add(bestGene);
        if(mapPosMutations.containsKey(bestGene))
        {
            HashSet<String> setSamples=mapPosMutations.get(bestGene);
            for(String sample : setSamples)
                currMapPosCov.put(sample,currMapPosCov.get(sample)+1);
        }
        if(mapNegMutations.containsKey(bestGene))
        {
            HashSet<String> setSamples=mapNegMutations.get(bestGene);
            for(String sample : setSamples)
                currMapNegCov.put(sample,currMapNegCov.get(sample)+1);
        }
    }

    public static double getSolAvgCovDiff(HashSet<String> currSol, HashMap<String,Integer> currMapPosCov, HashMap<String,Integer> currMapNegCov)
    {
        double avgPosCov=0.0;
        for(String sample : currMapPosCov.keySet())
            avgPosCov+=((double)currMapPosCov.get(sample))/currSol.size();
        avgPosCov/=currMapPosCov.size();
        double avgNegCov=0.0;
        for(String sample : currMapNegCov.keySet())
            avgNegCov+=((double)currMapNegCov.get(sample))/currSol.size();
        avgNegCov/=currMapNegCov.size();
        return avgPosCov-avgNegCov;
    }

    public static Vector<HashMap<String,HashSet<String>>> extractSubMutData(HashMap<String,HashSet<String>> posMutData, HashMap<String,HashSet<String>> negMutData, HashSet<String> listPositives, HashSet<String> listNegatives)
    {
        HashMap<String,HashSet<String>> subPosMutData=new HashMap<>();
        HashMap<String,HashSet<String>> subNegMutData=new HashMap<>();
        for(String gene : posMutData.keySet())
        {
            HashSet<String> setSamples=posMutData.get(gene);
            for(String sample : setSamples)
            {
                if(listPositives.contains(sample))
                {
                    if(subPosMutData.containsKey(gene))
                        subPosMutData.get(gene).add(sample);
                    else
                    {
                        HashSet<String> subSetSamples=new HashSet<>();
                        subSetSamples.add(sample);
                        subPosMutData.put(gene,subSetSamples);
                    }
                }
                else if(listNegatives.contains(sample))
                {
                    if(subNegMutData.containsKey(gene))
                        subNegMutData.get(gene).add(sample);
                    else
                    {
                        HashSet<String> subSetSamples=new HashSet<>();
                        subSetSamples.add(sample);
                        subNegMutData.put(gene,subSetSamples);
                    }
                }
            }
        }
        for(String gene : negMutData.keySet())
        {
            HashSet<String> setSamples=negMutData.get(gene);
            for(String sample : setSamples)
            {
                if(listPositives.contains(sample))
                {
                    if(subPosMutData.containsKey(gene))
                        subPosMutData.get(gene).add(sample);
                    else
                    {
                        HashSet<String> subSetSamples=new HashSet<>();
                        subSetSamples.add(sample);
                        subPosMutData.put(gene,subSetSamples);
                    }
                }
                else if(listNegatives.contains(sample))
                {
                    if(subNegMutData.containsKey(gene))
                        subNegMutData.get(gene).add(sample);
                    else
                    {
                        HashSet<String> subSetSamples=new HashSet<>();
                        subSetSamples.add(sample);
                        subNegMutData.put(gene,subSetSamples);
                    }
                }
            }
        }
        Vector<HashMap<String,HashSet<String>>> subMutMatrices=new Vector<>();
        subMutMatrices.add(subPosMutData);
        subMutMatrices.add(subNegMutData);
        return subMutMatrices;
    }
}
