package edu.usc.softarch.arcade.clustering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Joiner;

import edu.usc.softarch.arcade.clustering.util.ClusterUtil;
import edu.usc.softarch.arcade.config.Config;
import edu.usc.softarch.arcade.config.Config.SimMeasure;
import edu.usc.softarch.arcade.topics.DocTopics;
import edu.usc.softarch.arcade.topics.TopicModelExtractionMethod;
import edu.usc.softarch.arcade.topics.TopicUtil;
import edu.usc.softarch.arcade.util.ExtractionContext;
import edu.usc.softarch.arcade.util.StopWatch;

public class ConcernClusteringRunner extends ClusteringAlgoRunner {
	//public TopicModelExtractionMethod tmeMethod = TopicModelExtractionMethod.VAR_MALLET_FILE;
	//public String srcDir = "";
	//public int numTopics = 0;
	//private String topicModelFilename;
	
	/**
	 * @param vecs feature vectors (dependencies) of entities
	 * @param tmeMethod method of topic model extraction
	 * @param srcDir directories with java or c files
	 * @param numTopics number of topics to extract
	 */
	public ConcernClusteringRunner(FastFeatureVectors vecs,
			TopicModelExtractionMethod tmeMethod, String srcDir,String artifactsDir, int numTopics,
			String topicModelFilename, String docTopicsFilename, String topWordsFilename) {
		setFastFeatureVectors(vecs);
		initializeClusters(srcDir);	
		initializeDocTopicsForEachFastCluster(tmeMethod,srcDir,artifactsDir,numTopics,topicModelFilename,docTopicsFilename,topWordsFilename);
	}
	
	public void computeClustersWithConcernsAndFastClusters(StoppingCriterion stoppingCriterion) {
		StopWatch loopSummaryStopwatch = new StopWatch();

		// SimCalcUtil.verifySymmetricClusterOrdering(clusters);

		/*
		 * if (logger.isDebugEnabled()) {
		 * printMostSimilarClustersForEachCluster(); }
		 */

		loopSummaryStopwatch.start();
		
		StopWatch matrixCreateTimer = new StopWatch();
		matrixCreateTimer.start();
		List<List<Double>> simMatrix = createSimilarityMatrix(fastClusters);
		matrixCreateTimer.stop();
		//System.out.println("time to create similarity matrix: " + matrixCreateTimer.getElapsedTime());

		while (stoppingCriterion.notReadyToStop()) {

			if (Config.stoppingCriterion.equals(Config.StoppingCriterionConfig.clustergain)) {
				double clusterGain = ClusterUtil.computeClusterGainUsingTopics(fastClusters);
				checkAndUpdateClusterGain(clusterGain);
			}

			
			StopWatch timer = new StopWatch();
			timer.start();
			//identifyMostSimClustersForConcernsMultiThreaded(data);
			MaxSimData data  = identifyMostSimClusters(simMatrix);
			timer.stop();
			//System.out.println("time to identify two most similar clusters: "+ timer.getElapsedTime());
			
			boolean isPrintingTwoMostSimilar = false;
			if (isPrintingTwoMostSimilar) {
				//printDataForTwoMostSimilarClustersWithTopicsForConcerns(data);
				printDataForTwoMostSimilarClustersWithTopicsForConcerns(data);
			}
			

			// printDataForClustersBeingMerged(data);

			FastCluster newCluster = mergeFastClustersUsingTopics(data);

			/*
			 * if (logger.isDebugEnabled()) {
			 * printStructuralDataForClustersBeingMerged(newCluster);
			 * //logger.debug("\t\t" // + newCluster.docTopicItem //
			 * .toStringWithLeadingTabsAndLineBreaks(2)); }
			 */


			updateFastClustersAndSimMatrixToReflectMergedCluster(data, newCluster, simMatrix);
			
			
			performPostProcessingConditionally();

			boolean isShowingPostMergeClusterInfo = false;

		//	if (logger.isDebugEnabled()) {
				System.out.println("after merge, clusters size: "+ fastClusters.size());
			//}
			if (isShowingPostMergeClusterInfo) {
				//if (logger.isDebugEnabled()) {
					ClusterUtil.printFastClustersByLine(fastClusters);
					System.out.println("");
				//}
			}
		}

		loopSummaryStopwatch.stop();

		System.out.println("Time in milliseconds to compute clusters: "
				+ loopSummaryStopwatch.getElapsedTime());
		System.out.println("max cluster gain: " + maxClusterGain);
		System.out.println("num clusters at max cluster gain: "
				+ numClustersAtMaxClusterGain);
	}
	
	private static MaxSimData identifyMostSimClusters(List<List<Double>> simMatrix) {
		if ( simMatrix.size() != fastClusters.size() ) {
			throw new IllegalArgumentException("expected simMatrix.size():" + simMatrix.size() + " to be fastClusters.size(): " + fastClusters.size());
		}
		for (List<Double> col : simMatrix) {
			if (col.size() != fastClusters.size()) {
				throw new IllegalArgumentException("expected col.size():" + col.size() + " to be fastClusters.size(): " + fastClusters.size());
			}
		}
		
		int length = simMatrix.size();
		MaxSimData msData = new MaxSimData();
		msData.rowIndex = 0;
		msData.colIndex = 0;
		double smallestJsDiv = Double.MAX_VALUE;
		for (int i=0;i<length;i++) {
			for (int j=0;j<length;j++) {
				double currJsDiv = simMatrix.get(i).get(j);
				if (currJsDiv < smallestJsDiv &&
					i != j) {
					smallestJsDiv = currJsDiv;
					msData.rowIndex = i;
					msData.colIndex = j;
				}
			}
		}
		msData.currentMaxSim = smallestJsDiv;
		return msData;
		
	}

	private void initializeDocTopicsForEachFastCluster(
			TopicModelExtractionMethod tmeMethod, String srcDir, String artifactsDir, int numTopics, 
			String topicModelFilename, String docTopicsFilename, String topWordsFilename) {
		//if (logger.isDebugEnabled()) {
			System.out.println("Initializing doc-topics for each cluster...");
		//}

		if (tmeMethod == TopicModelExtractionMethod.VAR_MALLET_FILE) {
			System.out.println("Using VAR_MALLET_FILE");
			TopicUtil.docTopics = TopicUtil.getDocTopicsFromVariableMalletDocTopicsFile();
			for (FastCluster c : fastClusters) {
				TopicUtil.setDocTopicForFastClusterForMalletFile(TopicUtil.docTopics, c);
			}
		}
		else if (tmeMethod == TopicModelExtractionMethod.MALLET_API) {
			System.out.println("Using MALLET_API");
			try {
				TopicUtil.docTopics = new DocTopics(srcDir,artifactsDir,numTopics,topicModelFilename,docTopicsFilename,topWordsFilename);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (FastCluster c : fastClusters) {
				TopicUtil.setDocTopicForFastClusterForMalletApi(TopicUtil.docTopics, c);
			}
		}
		
		
		List<FastCluster> jspRemoveList = new ArrayList<FastCluster>();
		for (FastCluster c : fastClusters) {
			if (c.getName().endsWith("_jsp")) {
				System.out.println("Adding " + c.getName() + " to jspRemoveList...");
				jspRemoveList.add(c);
			}
		}
		

		System.out.println("Removing jspRemoveList from fastCluters");
		for (FastCluster c : jspRemoveList) {
			fastClusters.remove(c);
		}
		
		
		Map<String,String> parentClassMap = new HashMap<String,String>();
		for (FastCluster c : fastClusters) {
			if (c.getName().contains("$")) {
				System.out.println("Nested class singleton cluster with missing doc topic: " + c.getName());
				String[] tokens = c.getName().split("\\$");
				String parentClassName = tokens[0];
				parentClassMap.put(c.getName(), parentClassName);
			}
		}
		
		System.out.println("Removing singleton clusters with no doc-topic and are non-inner classes...");
		List<FastCluster> excessClusters = new ArrayList<FastCluster>();
		for (FastCluster c : fastClusters) {
			if (c.docTopicItem == null) {
				if (!c.getName().contains("$")) {
					System.err.println("Could not find doc-topic for non-inner class: " + c.getName());
					excessClusters.add(c);
				}
			}
		}
		
		List<FastCluster> excessInners = new ArrayList<FastCluster>();
		for (FastCluster excessCluster : excessClusters) {
			for (FastCluster cluster : fastClusters) {
				if (parentClassMap.containsKey(cluster)) {
					String parentClass = parentClassMap.get(cluster);
					if (parentClass.equals(excessCluster.getName())) {
						excessInners.add(cluster);
					}
				}
			}
		}
		
		fastClusters.removeAll(excessClusters);
		fastClusters.removeAll(excessInners);

		ArrayList<FastCluster> updatedFastClusters = new ArrayList<FastCluster>(fastClusters);
		for (String key : parentClassMap.keySet()) {
			for (FastCluster nestedCluster : fastClusters) {
				if (nestedCluster.getName().equals(key)) {
					for (FastCluster parentCluster : fastClusters) {
						if (parentClassMap.get(key).equals(parentCluster.getName())) {
							FastCluster mergedCluster = mergeFastClustersUsingTopics(nestedCluster,parentCluster);
							updatedFastClusters.remove(parentCluster);
							updatedFastClusters.remove(nestedCluster);
							updatedFastClusters.add(mergedCluster);
						}
					}
				}
			}

		}		
		fastClusters = updatedFastClusters;
		
		List<FastCluster> clustersWithMissingDocTopics = new ArrayList<FastCluster>();
		for (FastCluster c : fastClusters) {
			if (c.docTopicItem == null) {
				System.out.println("Could not find doc-topic for: " + c.getName());
				clustersWithMissingDocTopics.add(c);
			}
		}
		
		System.out.println("Removing clusters with missing doc topics...");
		fastClusters.removeAll(clustersWithMissingDocTopics);

		boolean ignoreMissingDocTopics = true;
		if (ignoreMissingDocTopics) {
			System.out.println("Removing clusters with missing doc topics...");
			for (FastCluster c : clustersWithMissingDocTopics) {
				System.out.println("Removing cluster: " + c.getName());
				fastClusters.remove(c);
			}
			System.out.println("New initial clusters size: " + fastClusters.size());
		}
		
		System.out.println("New initial fast clusters:");
		System.out.println(Joiner.on("\n").join(fastClusters));
		
	}
	
	private static void printDataForTwoMostSimilarClustersWithTopicsForConcerns(
			MaxSimData data) {
	//	if (logger.isDebugEnabled()) {
			System.out.println("In, " + Thread.currentThread().getStackTrace()[1].getMethodName()
					+ ", \nMax Similar Clusters: ");
			System.out.println("sim value(" + data.rowIndex + "," + data.colIndex + "): " + data.currentMaxSim);
			System.out.println("\n");
			System.out.println("most sim clusters: " + fastClusters.get(data.rowIndex).getName() + ", " + fastClusters.get(data.colIndex).getName());
			TopicUtil.printTwoDocTopics(fastClusters.get(data.rowIndex).docTopicItem,
					fastClusters.get(data.colIndex).docTopicItem);

			System.out.println("before merge, fast clusters size: " + fastClusters.size());
		//}
	}
	
	private static FastCluster mergeFastClustersUsingTopics(MaxSimData data) {
		FastCluster cluster = fastClusters.get(data.rowIndex);
		FastCluster otherCluster = fastClusters.get(data.colIndex);
		return mergeFastClustersUsingTopics(cluster, otherCluster);
	}

	private static FastCluster mergeFastClustersUsingTopics(
			FastCluster cluster, FastCluster otherCluster) {
		FastCluster newCluster = new FastCluster(ClusteringAlgorithmType.LIMBO,cluster, otherCluster);
		
		newCluster.docTopicItem = TopicUtil.mergeDocTopicItems(
				cluster.docTopicItem, otherCluster.docTopicItem);
		return newCluster;
	}
	
	private static void updateFastClustersAndSimMatrixToReflectMergedCluster(MaxSimData data,
			FastCluster newCluster, List<List<Double>> simMatrix) {
		
		FastCluster cluster = fastClusters.get(data.rowIndex);
		FastCluster otherCluster = fastClusters.get(data.colIndex);
		
		int greaterIndex = -1, lesserIndex = -1;
		if (data.rowIndex == data.colIndex) {
			throw new IllegalArgumentException("data.rowIndex: " + data.rowIndex + " should not be the same as data.colIndex: " + data.colIndex);
		}
		if (data.rowIndex > data.colIndex) {
			greaterIndex = data.rowIndex;
			lesserIndex = data.colIndex;
		}
		else if (data.rowIndex < data.colIndex) {
			greaterIndex = data.colIndex;
			lesserIndex = data.rowIndex;
		}
		
		simMatrix.remove(greaterIndex);
		for (List<Double> col : simMatrix) {
			col.remove(greaterIndex);
		}
		
		simMatrix.remove(lesserIndex);
		for (List<Double> col : simMatrix) {
			col.remove(lesserIndex);
		}
		
		fastClusters.remove(cluster);
		fastClusters.remove(otherCluster);
		
		fastClusters.add(newCluster);
		
		List<Double> newRow = new ArrayList<Double>(fastClusters.size());
		
		for (int i=0;i<fastClusters.size();i++) {
			newRow.add(Double.MAX_VALUE);
		}
		
		simMatrix.add(newRow);
		
		for (int i=0;i<fastClusters.size()-1;i++) { // adding a new value to create new column for all but the last row, which already has the column for the new cluster
			simMatrix.get(i).add(Double.MAX_VALUE);
		}
		
		if (simMatrix.size()!=fastClusters.size()) {
			throw new RuntimeException("simMatrix.size(): " + simMatrix.size() + " is not equal to fastClusters.size(): " + fastClusters.size());
		}
		
		for (int i=0;i<fastClusters.size();i++) {
			if ( simMatrix.get(i).size() != fastClusters.size() ) {
				throw new RuntimeException("simMatrix.get(" + i + ").size(): " + simMatrix.get(i).size() + " is not equal to fastClusters.size(): " + fastClusters.size());
			}
		}
	
		
		for (int i=0;i<fastClusters.size();i++) {
			FastCluster currCluster = fastClusters.get(i);
			double currJSDivergence = Double.MAX_VALUE;
			if (Config.getCurrSimMeasure().equals(SimMeasure.js)) {
				currJSDivergence = SimCalcUtil.getJSDivergence(newCluster,currCluster);
			}
			else if (Config.getCurrSimMeasure().equals(SimMeasure.scm)) {
				currJSDivergence = FastSimCalcUtil.getStructAndConcernMeasure(numberOfEntitiesToBeClustered, newCluster, currCluster);
			}
			else {
				throw new IllegalArgumentException("Invalid similarity measure: " + Config.getCurrSimMeasure());
			}
			simMatrix.get(fastClusters.size()-1).set(i, currJSDivergence);
			simMatrix.get(i).set(fastClusters.size()-1, currJSDivergence);
		}
		
		
		// SimCalcUtil.verifySymmetricClusterOrdering(clusters);
		// newCluster.addClustersToPriorityQueue(clusters);
	}
	
	// DEPRECATED Method do not use
	public static void identifyMostSimilarClusterForConcerns(List<FastCluster> clusters,
			MaxSimData data, FastCluster cluster) {

		//HashMap<HashSet<String>, Double> map = new HashMap<HashSet<String>, Double>();

		for (FastCluster otherCluster : clusters) {
			boolean isShowingEachSimilarityComparison = false;
			if (isShowingEachSimilarityComparison) {
				//if (logger.isDebugEnabled()) {
					System.out.println("Comparing " + cluster.getName() + " to "
							+ otherCluster.getName());
					TopicUtil.printTwoDocTopics(cluster.docTopicItem,
							otherCluster.docTopicItem);
				//}
			}
			if (cluster.getName().equals(otherCluster.getName())) {
				continue;
			}

			/*HashSet<String> clusterPair = new HashSet<String>();
			clusterPair.add(cluster.getName());
			clusterPair.add(otherCluster.getName());*/

			double currJSDivergence = 0;
			//if (map.containsKey(clusterPair)) {
			//	currJSDivergence = map.get(clusterPair);
			//} else {
				currJSDivergence = SimCalcUtil.getJSDivergence(cluster,
						otherCluster);
			//	map.put(clusterPair, currJSDivergence);
			//}

			if (currJSDivergence <= data.currentMaxSim) {
				data.currentMaxSim = currJSDivergence;
				data.c1 = cluster;
				data.c2 = otherCluster;
				boolean showCurrentMostSimilar = false;
				if (showCurrentMostSimilar) {
					//if (logger.isDebugEnabled()) {
						System.out.println("Updated most similar values: ");
						System.out.println("currentMostSim: " + data.currentMaxSim);
						System.out.println("c1: " + data.c1.getName());
						System.out.println("c2: " + data.c2.getName());
						TopicUtil.printTwoDocTopics(data.c1.docTopicItem,
								data.c2.docTopicItem);
					//}
				}
			}
		}

		boolean isShowingMaxSimClusters = false;
		if (isShowingMaxSimClusters) {
			//if (logger.isDebugEnabled()) {
			System.out.println("In, "
						+ ExtractionContext.getCurrentClassAndMethodName()
						+ " Max Similar Clusters: ");
				System.out.println(data.c1.getName());
				System.out.println(data.c2.getName());
				System.out.println(data.currentMaxSim);
				System.out.println("\n");
			//}
		}
	}
	
	public static List<List<Double>> createSimilarityMatrix(List<FastCluster> clusters) {
		
		List<List<Double>> simMatrixObj = new ArrayList<List<Double>>(clusters.size());
		
		for (int i=0;i<clusters.size();i++) {
			simMatrixObj.add(new ArrayList<Double>(clusters.size()));
		}

		for (int i=0;i<clusters.size();i++) {
			FastCluster cluster = clusters.get(i);
			for (int j=0;j<clusters.size();j++) {
				FastCluster otherCluster = clusters.get(j);
				boolean isShowingEachSimilarityComparison = false;
				if (isShowingEachSimilarityComparison) {
					//if (logger.isDebugEnabled()) {
						System.out.println("Comparing " + cluster.getName() + " to "
								+ otherCluster.getName());
						TopicUtil.printTwoDocTopics(cluster.docTopicItem,
								otherCluster.docTopicItem);
					//}
				}
				
				/*if (cluster.getName().equals(otherCluster.getName())) {
					continue;
				}*/

				/*
				 * HashSet<String> clusterPair = new HashSet<String>();
				 * clusterPair.add(cluster.getName());
				 * clusterPair.add(otherCluster.getName());
				 */

				double currJSDivergence = 0;
				// if (map.containsKey(clusterPair)) {
				// currJSDivergence = map.get(clusterPair);
				// } else {
				if (Config.getCurrSimMeasure().equals(SimMeasure.js)) {
					currJSDivergence = SimCalcUtil.getJSDivergence(cluster,
						otherCluster);
				}
				else if (Config.getCurrSimMeasure().equals(SimMeasure.scm)) {
					currJSDivergence = FastSimCalcUtil.getStructAndConcernMeasure(numberOfEntitiesToBeClustered, cluster, otherCluster);
				}
				else {
					throw new IllegalArgumentException("Invalid similarity measure: " + Config.getCurrSimMeasure());
				}
				// map.put(clusterPair, currJSDivergence);
				// }
				
				simMatrixObj.get(i).add(currJSDivergence);
			}

		}
		
		return simMatrixObj;
	}
	
	
}
