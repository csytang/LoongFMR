package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;

public class VariabilityLoss {
	public VariabilityLoss(){
		
	}
	
	public static double computeVLossPos(Set<ModuleWrapper>msetgroup){
		double loss = 0.0;
		int totalavariabilitycount = 0;
		Set<Module>allmodules = new HashSet<Module>();
		for(ModuleWrapper set:msetgroup){
			int configurationcount = set.getConfigurationCount();
			totalavariabilitycount += configurationcount;
			allmodules.addAll(set.getModuleSet());
		}
		
		ArrayList<ModuleWrapper>setlist = new ArrayList<ModuleWrapper>(msetgroup);
		ModuleWrapper top = setlist.get(0);
		int clusterid = top.getClusterId();
		ModuleDependencyTable table = top.getDependencyTable();
		
		ModuleWrapper mergeset = new ModuleWrapper(allmodules,table,clusterid);
		int mergedcount = mergeset.getConfigurationCount();
		if(totalavariabilitycount==0){
			return 0.0;
		}
		loss = ((double)mergedcount-totalavariabilitycount)/(mergedcount);
		return loss;
	}
}
