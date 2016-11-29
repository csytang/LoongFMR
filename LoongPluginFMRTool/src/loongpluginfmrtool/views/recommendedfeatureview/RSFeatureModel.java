package loongpluginfmrtool.views.recommendedfeatureview;


import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class RSFeatureModel implements Serializable{
	
	public Set<RSFeature> allRSFeatures = new HashSet<RSFeature>();
	
	
	public RSFeatureModel(){
		
	}
	
	public void addRSFeature(RSFeature feature){
		allRSFeatures.add(feature);
	}

	public Set<RSFeature> getFeatures() {
		// TODO Auto-generated method stub
		return allRSFeatures;
	}
	
	
}
