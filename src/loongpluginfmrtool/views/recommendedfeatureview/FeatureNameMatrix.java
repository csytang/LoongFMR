package loongpluginfmrtool.views.recommendedfeatureview;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import loongplugin.utils.SVD_NR;
import loongpluginfmrtool.views.recommendedfeatureview.util.LevenshteinDis;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

public class FeatureNameMatrix {
	
	Map<Integer,String>featureNameCol_Index = new HashMap<Integer,String>();
	Map<Integer,String>nonfeatureStringRow_Index = new HashMap<Integer,String>();
	private Map<String,Map<IJavaElement,Set<ASTNode>>>afeatureNameDictionary;
	private Map<String,Map<IJavaElement,Set<ASTNode>>>anonfeaturetextMapping;
	int num_columns = 0;
	int num_rows = 0;
	double[][] correlationMatrix;
	boolean debug = true;
	private RSFeatureModel model = new RSFeatureModel();
	Map<String,Double>featureNameToWeight = new HashMap<String,Double>();
	
	public FeatureNameMatrix(Map<String,Map<IJavaElement,Set<ASTNode>>>featureNameDictionary,Map<String,Map<IJavaElement,Set<ASTNode>>>nonfeaturetextMapping){
		// build the matrix for the feature name
		int index = 0;
		for(String str:featureNameDictionary.keySet()){
			featureNameCol_Index.put(index,str);
			index++;
		}
		
		index = 0;
		for(String str:nonfeaturetextMapping.keySet()){
			nonfeatureStringRow_Index.put(index,str);
			index++;
		}
		
		num_columns = featureNameDictionary.keySet().size();
		num_rows = nonfeaturetextMapping.keySet().size();
		
		correlationMatrix = new double[num_rows][num_columns];
		afeatureNameDictionary = featureNameDictionary;
		anonfeaturetextMapping = nonfeaturetextMapping;
		init();
		
		double[] w = new double[num_columns];
	    double[][] V = new double[num_columns][num_columns];

	    SVD_NR.svd(correlationMatrix, w, V);
	    
	    for(int i = 0;i < num_columns;i++){
	    	String featurestr = featureNameCol_Index.get(i);
	    	featureNameToWeight.put(featurestr, w[i]);
	    	RSFeature feature = new RSFeature(featurestr,w[i],afeatureNameDictionary.get(featurestr));
	    	model.addRSFeature(feature);
	    }
	}
	
	/**
	 * 
	 */
	private void init(){
		for(int i = 0;i < num_rows;i++){
			String nonfeaturestr = nonfeatureStringRow_Index.get(i);
			Map<IJavaElement,Set<ASTNode>> nonfeatureBinds = anonfeaturetextMapping.get(nonfeaturestr);
			for(int j = 0;j < num_columns;j++){
				String featurestr = featureNameCol_Index.get(j);
				Map<IJavaElement,Set<ASTNode>> featureBinds = afeatureNameDictionary.get(featurestr);
				// 0. levenshtein distance
				boolean isfirst = true;
				int minimallevedistance = nonfeaturestr.length();
				for(String substr:nonfeaturestr.split(" ")){
					int levedistance = LevenshteinDis.getLevenshteinDis(substr, featurestr, substr.length());
					if(isfirst){
						minimallevedistance = levedistance;
						isfirst = false;
					}else{
						if(levedistance<minimallevedistance)
							minimallevedistance = levedistance;
					}
				}
				
				// 1. occurrences in the same doc
				double countcrossocc = 0.0;
				double counttotalocc = 0.0;
				for(IJavaElement element:nonfeatureBinds.keySet()){
					counttotalocc+=nonfeatureBinds.get(element).size();
					if(featureBinds.keySet().contains(element)){
						countcrossocc+=nonfeatureBinds.get(element).size();
						countcrossocc+=featureBinds.get(element).size();
					}
				}
				for(IJavaElement element:featureBinds.keySet()){
					counttotalocc+=featureBinds.get(element).size();
				}
				
				correlationMatrix[i][j] = (double)countcrossocc/counttotalocc+(double)minimallevedistance;
			}
		}
		
	}

	public RSFeatureModel getRSFeatureModel() {
		// TODO Auto-generated method stub
		
		return model;
	}
}
