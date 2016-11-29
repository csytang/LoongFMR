package loongpluginfmrtool.statistic.c2c;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;

import edu.usc.softarch.arcade.facts.ConcernCluster;
import edu.usc.softarch.arcade.facts.driver.ConcernClusterRsf;
import edu.usc.softarch.arcade.metrics.MWBMatchingAlgorithm;

public class Cluster2Cluster {
	private String targetRsf;
	private String sourceRsf;
	private Set<ConcernCluster> sourceClusters;
	private Set<ConcernCluster> targetClusters;
	private double c2cresult_cvghigh = 0.0;
	private double c2cresult_cvgmedium = 0.0;
	private double c2cresult_cvglow = 0.0;
	private Set<ConcernCluster> overlaphigh = new HashSet<ConcernCluster>();
	private Set<ConcernCluster> overlapmedium = new HashSet<ConcernCluster>();
	private Set<ConcernCluster> overlaplow = new HashSet<ConcernCluster>();
	
	private double thgcvghigh = 0.5;
	private double thgcvmedium = 0.33;
	private double thgcvlow = 0.1;
	private int groundsize = 0;
	
	public Cluster2Cluster(String psourceRsf,String ptargetRsf){
		
		this.sourceRsf = psourceRsf;
		this.targetRsf = ptargetRsf;
		sourceClusters = ConcernClusterRsf.extractConcernClustersFromRsfFile(sourceRsf);
		targetClusters = ConcernClusterRsf.extractConcernClustersFromRsfFile(targetRsf);
		
		System.out.println("Source clusters: ");
		System.out.println(clustersToString(sourceClusters));
		System.out.println("Target clusters: ");
		System.out.println(clustersToString(targetClusters));
		
		//groundtruthclustersize = targetClusters.size();
		
		Set<String> sourceEntities = getAllEntitiesInClusters(sourceClusters);
		Set<String> targetEntities = getAllEntitiesInClusters(targetClusters);
		
		System.out.println("\n");
		System.out.println("source entities: " + sourceEntities);
		System.out.println("target entities: " + targetEntities);
		
		Set<String> entitiesToRemove = new HashSet<String>(sourceEntities);
		entitiesToRemove.removeAll(targetEntities);
		Set<String> entitiesToAdd = new HashSet<String>(targetEntities);
		entitiesToAdd.removeAll(sourceEntities);
		
		System.out.println("\n");
		System.out.println("entities to remove: " + entitiesToRemove);
		System.out.println("entities to add: " + entitiesToAdd);

		// We need to determine the  intersection of entities between clusters in the source and target to help us minimize the number of moves
		// Pooyan!: We are maping this problem to a problem of Maximum Weighted Matching, and we use Hungurian Algorithm to solve it. 
		
		int ns = sourceClusters.size();
		int nt = targetClusters.size();
		Map<Integer,ConcernCluster> sourceNumToCluster = new HashMap<Integer,ConcernCluster>(); // Pooyan! It maps every source_cluster to a number from 0 to ns-1
		Map<ConcernCluster,Integer> sourceClusterToNum = new HashMap<ConcernCluster,Integer>(); // Pooyan! It maps every target_cluster to a number from 0 to nm-1
		int counter = 0 ;
		for (ConcernCluster source:sourceClusters){
			sourceNumToCluster.put(counter, source);
			sourceClusterToNum.put(source,  counter);
			counter++;
		}
		
		groundsize = nt>ns?nt:ns; 
		
		
		Map<Integer,ConcernCluster> targetNumToCluster = new HashMap<Integer,ConcernCluster>();
		Map<ConcernCluster,Integer> targetClusterToNum = new HashMap<ConcernCluster,Integer>();
		counter = 0;
		for (ConcernCluster target:targetClusters){
			targetNumToCluster.put(counter,  target);
			targetClusterToNum.put(target, counter);
			counter++;
		}
		
		MWBMatchingAlgorithm ma = new MWBMatchingAlgorithm(ns,nt);// Pooyan! Initiating the mathching
		for (int i=0;i<ns;i++)
			for (int j=0;j<nt;j++)
				ma.setWeight(i, j, 0);
		
		for (ConcernCluster sourceCluster : sourceClusters) {
			for (ConcernCluster targetCluster : targetClusters) {
				Set<String> entitiesIntersection = new HashSet<String>(sourceCluster.getEntities());
				entitiesIntersection.retainAll(targetCluster.getEntities());
				ma.setWeight(sourceClusterToNum.get(sourceCluster), targetClusterToNum.get(targetCluster), entitiesIntersection.size()); // Pooyan the weight of (source,target) as the interesection between them
			}	
		}
		
		Map<ConcernCluster,Set<String>> sourceClusterMatchEntities = new HashMap<ConcernCluster,Set<String>>(); //Pooyan! It keeps the source Cluster Match Entities, not necessarily the max match 
		Map<ConcernCluster,ConcernCluster> matchOfSourceInTarget = new HashMap<ConcernCluster,ConcernCluster>();//Pooyan! It keeps the matched cluster in target for every source
		Map<ConcernCluster,ConcernCluster> matchOfTargetInSource = new HashMap<ConcernCluster,ConcernCluster>();//Pooyan! It keeps the matched cluster in source for every target
		
		int[] match = ma.getMatching(); // Pooyan! calculates the max weighted match;
		
		for (int i=0;i<match.length;i++){

			ConcernCluster source = sourceNumToCluster.get(i);
			ConcernCluster target = new ConcernCluster();
			target.setName("-1"); // Pooyan! dummy, in case that the cluster is not matched to any cluster, to avoid null pointer exceptions
			if (match[i]!=-1)
				target=targetNumToCluster.get(match[i]) ;					
			matchOfSourceInTarget.put(source, target); // Pooyan! set the match of source
			matchOfTargetInSource.put(target, source); // Pooyan! set the match of target
			Set<String> entitiesIntersection = new HashSet<String>(source.getEntities());
			entitiesIntersection.retainAll(target.getEntities());
			sourceClusterMatchEntities.put(source, entitiesIntersection);	
			System.out.println("Pooyan -> "+source.getName() +" is matched to "+target.getName()+ " - the interesection size is " + entitiesIntersection.size() );
		}
		
		System.out.println("\n");
		System.out.println("Pooyan -> cluster -> intersecting entities in the matched source clusters");
		System.out.println(Joiner.on("\n").withKeyValueSeparator("->").join(sourceClusterMatchEntities));
		System.out.println("Pooyan -> cluster -> matched clusters in target cluster for every source cluster");
		System.out.println(Joiner.on("\n").withKeyValueSeparator("->").useForNull("null").join(matchOfSourceInTarget));
		
		int sourceClusterRemovalCount=0 ;
		Set<ConcernCluster> removedSourceClusters = new HashSet<ConcernCluster> () ;
		
		//Pooyan! unmatched clusters must be removed
		for (ConcernCluster source:sourceClusters){
			ConcernCluster matched = matchOfSourceInTarget.get(source);
			if (matched.getName().equals("-1")){
				sourceClusterRemovalCount++;
				removedSourceClusters.add(source);
			}
		}
		System.out.println("Pooyan -> Removed source clusters:");
		System.out.println(Joiner.on(",").join(removedSourceClusters));
		
		Set<String> entitiesToMoveInRemovedSourceClusters = new HashSet<String>(); // Pooyan! These are the entities in the removed source clusters which exists in the target clusters and have to be moved 
		System.out.println("Entities of removed clusters:");
		for (ConcernCluster source : removedSourceClusters) {
			Set<String> entities = source.getEntities();
			entities.removeAll(entitiesToRemove); // Make sure we are not trying to move entities that no longer exist in the target cluster
			System.out.println("Pooyan -> these in enitities in: "+ source.getName() + " will be moved: " + entities);
			entitiesToMoveInRemovedSourceClusters.addAll(entities);
		}
		
		// The clusters that remain after removal of clusters 
		Set<ConcernCluster> remainingSourceClusters = new HashSet<ConcernCluster>(sourceClusters);
		remainingSourceClusters.removeAll(removedSourceClusters);
		
		// for each cluster, the map gives the set of entities that may be moved (not including added or
		// removed entities)
		Map<ConcernCluster,Set<String>> entitiesToMoveInCluster = new HashMap<ConcernCluster,Set<String>>(); 
		for (ConcernCluster remainingCluster : remainingSourceClusters) {
			Set<String> matchedIntersectionEntities = sourceClusterMatchEntities.get(remainingCluster);
			Set<String> currEntitiesToMove = new HashSet<String>( remainingCluster.getEntities() );
			if (matchOfSourceInTarget.get(remainingCluster) != null && matchOfTargetInSource.get(matchOfSourceInTarget.get(remainingCluster)).equals(remainingCluster))// Pooyan! if the ramaining cluster is  the base cluster, it is entity should not be removed, otherwise they should be in the current entity to move
				currEntitiesToMove.removeAll(matchedIntersectionEntities); // the problem is here!!! It should move the maxIntersecting Entities since the cluster in the other arc is assigned to another cluster
			else{
				//logger.debug("Pooyan -> /*");
				//logger.debug("Pooyan -> remainingCluster: "+remainingCluster.getName());
				//logger.debug("Pooyan -> clusterToMaxIntersectingCluster.get(remainingCluster): " +clusterToMaxIntersectingCluster.get(remainingCluster).getName());
				//logger.debug("Pooyan -> targetClusterMatchInSource.get(clusterToMaxIntersectingCluster.get(remainingCluster)): "+targetClusterMatchInSource.get(clusterToMaxIntersectingCluster.get(remainingCluster)));
			}
			
			currEntitiesToMove.removeAll(entitiesToAdd);
			currEntitiesToMove.removeAll(entitiesToRemove);
			entitiesToMoveInCluster.put(remainingCluster,currEntitiesToMove);
			for (String e:currEntitiesToMove)
				System.out.println("Pooyan -> remaining cluster " + remainingCluster.getName() + ", adn current entity to move :" +e);
		}
		
		Set<String> allEntitiesToMove = new HashSet<String>();
		for (Set<String> currEntitiesToMove : entitiesToMoveInCluster.values()) {
			allEntitiesToMove.addAll(currEntitiesToMove); // entities to move in clusters not removed
		}
		allEntitiesToMove.addAll(entitiesToMoveInRemovedSourceClusters); // entities to move in removed clusters (i.e., all the entities in those clusters)
		allEntitiesToMove.addAll(entitiesToAdd);
		allEntitiesToMove.addAll(entitiesToRemove);
		
		for (String e:allEntitiesToMove)
			System.out.println("Pooyan -> enitity to be moved: " +e); 
		System.out.println("entities to move in each cluster: ");
		System.out.println(Joiner.on("\n").withKeyValueSeparator("->").join(entitiesToMoveInCluster));
		
		int movesForAddedEntities = entitiesToAdd.size();
		int movesForRemovedEntities = entitiesToRemove.size();
		
		System.out.println("\n");
		System.out.println("moves for added entities: " + movesForAddedEntities);
		System.out.println("moves for removed entities: " + movesForRemovedEntities);
		
		
		
		// Don't think I need this block for actual sysevo computation
		Map<String,ConcernCluster> entityToTargetCluster = new HashMap<String,ConcernCluster>(); 		
		for (ConcernCluster sourceCluster : sourceClusters) {
			Set<String> sourceEntitiesToMove = new HashSet<String>( sourceCluster.getEntities() ); // entities that exist already and might be moved
			sourceEntitiesToMove.removeAll(entitiesToAdd); // so you need to ignore added entities
			sourceEntitiesToMove.removeAll(entitiesToRemove); // and removed entities.
			for (ConcernCluster targetCluster : targetClusters) {
				Set<String> currTargetEntitites = targetCluster.getEntities();
				Set<String> intersectingEntities = new HashSet<String>(sourceEntitiesToMove); // entities in both the current source and target cluster
				intersectingEntities.retainAll(currTargetEntitites);
				System.out.println("intersecting entities: ");
				
				System.out.println(intersectingEntities);
				for (String entity : intersectingEntities) { // mark that these source entities belong to this target cluster
					entityToTargetCluster.put(entity,targetCluster);
				}
				
				//overlap rate
				int sizesourccluster = sourceCluster.getEntities().size();
				int targetsourcecluster = targetCluster.getEntities().size();
				int maxsize = sizesourccluster>targetsourcecluster?sizesourccluster:targetsourcecluster;
				double overlaperate = ((double)intersectingEntities.size())/maxsize;
				if(overlaphigh.contains(sourceCluster)){
					break;
				}else{
					if(overlaperate>=thgcvghigh){
						overlaphigh.add(sourceCluster);
						overlapmedium.add(sourceCluster);
						overlaplow.add(sourceCluster);
						break;
					}
				}
				
				if(overlapmedium.contains(sourceCluster)){
					break;
				}else{
					if(overlaperate>=thgcvmedium){
						overlapmedium.add(sourceCluster);
						overlaplow.add(sourceCluster);
						break;
					}
				}
				
				if(overlaplow.contains(sourceCluster)){
					break;
				}else{
					if(overlaperate>=thgcvlow){
						overlaplow.add(sourceCluster);
						break;
					}
				}
				
			}
		}
		
		c2cresult_cvghigh = ((double)overlaphigh.size()/groundsize)*100;
		c2cresult_cvgmedium = ((double)overlapmedium.size()/groundsize)*100;
		c2cresult_cvglow = ((double)overlaplow.size()/groundsize)*100;
		
		
	}
	
	
	private static String clustersToString(Set<ConcernCluster> sourceClusters) {
		String output = "";
		for (ConcernCluster cluster : sourceClusters) {
			output += cluster.getName() +  ": ";
			for (String entity : cluster.getEntities()) {
				output += entity + " ";
			}
			output += "\n";
		}
		return output;
	}

	private static Set<String> getAllEntitiesInClusters(
			Set<ConcernCluster> clusters) {
		Set<String> entities = new HashSet<String>();
		for (ConcernCluster cluster : clusters) {
			entities.addAll( cluster.getEntities() );
		}
		return entities;
	}
	
	
	public double getCluster2ClusterResultHigh() {
		// TODO Auto-generated method stub
		return c2cresult_cvghigh;
	}
	
	public double getCluster2ClusterResultMedium() {
		// TODO Auto-generated method stub
		return c2cresult_cvgmedium;
	}
	
	public double getCluster2ClusterResultLow() {
		// TODO Auto-generated method stub
		return c2cresult_cvglow;
	}


	public String getResultStr() {
		// TODO Auto-generated method stub
		DecimalFormat   fnum  =   new  DecimalFormat("##0.00");
		String c2cresult_cvghighstr = fnum.format(c2cresult_cvghigh);
		String c2cresult_cvgmediumstr = fnum.format(c2cresult_cvgmedium);
		String c2cresult_cvglowstr = fnum.format(c2cresult_cvglow);

		
		String result = "[max->min:"+c2cresult_cvghighstr+","+c2cresult_cvgmediumstr+","+c2cresult_cvglowstr+"]";
		return result;
	}
}
