package loongpluginfmrtool.toolbox.mvs;

import java.util.HashSet;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;

public class MVSFeaturePair {
	protected Set<ModuledFeature>allfeautureneeds = new HashSet<ModuledFeature>();
	public MVSFeaturePair(){
		
	}
	public void addModuledFeature(ModuledFeature feature){
		this.allfeautureneeds.add(feature);
	}
	public Set<ModuledFeature> getModuledFeature(){
		return this.allfeautureneeds;
	}
}
