package loongpluginfmrtool.views.recommendedfeatureview;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

public class RSFeature implements Serializable{
	private String afeatureName;
	private double aweight = 0.0;
	private Set<IJavaElementWrapper>wrappers;
	
	public RSFeature(String pfeatureName,double pweight,Map<IJavaElement,Set<ASTNode>>pbindings){
		afeatureName = pfeatureName;
		aweight = pweight;
		wrappers = new HashSet<IJavaElementWrapper>();
		for(Map.Entry<IJavaElement, Set<ASTNode>>entry:pbindings.entrySet()){
			IJavaElementWrapper wrapper = new IJavaElementWrapper(entry.getKey(),entry.getValue(),this);
			wrappers.add(wrapper);
		}
	}
	
	public String getFeatureName(){
		return afeatureName;
	}
	
	public double getWeight(){
		return aweight;
	}
	
	public Set<IJavaElementWrapper> getChildren(){
		return wrappers;
	}
	
	public void updateFeatureName(String newFeatureName){
		this.afeatureName = newFeatureName;
	}
}
