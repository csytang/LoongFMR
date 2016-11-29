package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.model.Module;

public class ModuleQualityMetrics {
	private Set<ModuleWrapper> mwrapperset1;
	private Set<ModuleWrapper> mwrapperset2;
	private double intra_connectivity_feature1 = 0.0;
	private double intra_connectivity_feature2 = 0.0;

	private double inter_connectivity = 0.0;
	
	public ModuleQualityMetrics(Set<ModuleWrapper> pmset1, Set<ModuleWrapper> pmset2){
		this.mwrapperset1 = pmset1;
		this.mwrapperset2 = pmset2;
		computebasicMetrics();
	}
	
	public ModuleQualityMetrics(Set<ModuleWrapper> pmset1){
		this.mwrapperset1 = pmset1;
		intra_connectivity_feature1 = compute_intro_connectivity(mwrapperset1);
	}
	
	protected void computebasicMetrics(){
		intra_connectivity_feature1 = compute_intro_connectivity(mwrapperset1);
		intra_connectivity_feature2 = compute_intro_connectivity(mwrapperset2);
		inter_connectivity = compute_inter_connectivity(mwrapperset1,mwrapperset2);
	}
	
	public double getIntraConnectMSet1(){
		return intra_connectivity_feature1;
	}
	public double getIntraConnectMSet2(){
		return intra_connectivity_feature2;
	}
	public double getInterConnect(){
		return inter_connectivity;
	}
	
	/**
	 * the inter-connectivity measurement is a fraction of the maximum number of interedge 
	 * dependencies between clusters i and j (2NiNj) . This measuremenet is bound between the
	 * values of 0 and 1. Eij is 0 when there are no module-level relations between subsystem i
	 * sybsystem j; Eij is 1 when each module in subsystem i depends on all of the modules in subsystem j 
	 * and vice -versa
	 * @param feature1
	 * @param feature2
	 * @return
	 */
	private double compute_inter_connectivity(Set<ModuleWrapper> mset1,
			Set<ModuleWrapper> mset2) {
		// TODO Auto-generated method stub
		int num_md_f1 = 0;
		int num_md_f2 = 0;
		double result = 0.0;
		double ef1f2 = 0.0;
		//f1
		Set<Module> intramodules_f1 = new HashSet<Module>();
		for(ModuleWrapper wrapper:mset1){
			intramodules_f1.addAll(wrapper.getModuleSet());
			num_md_f1+=wrapper.getModuleSet().size();
		}
		
		//f2
		Set<Module> intramodules_f2 = new HashSet<Module>();
		for(ModuleWrapper wrapper:mset2){
			intramodules_f2.addAll(wrapper.getModuleSet());
			num_md_f2+=wrapper.getModuleSet().size();
		}
		
		for(Module module:intramodules_f1){
			Map<Module,Integer> module_count = module.getAllDependency();
			for(Map.Entry<Module, Integer>entry:module_count.entrySet()){
				Module depdent_module = entry.getKey();
				if(intramodules_f2.contains(depdent_module)){
					ef1f2 += entry.getValue();
				}
			}
		}
		
		for(Module module:intramodules_f2){
			Map<Module,Integer> module_count = module.getAllDependency();
			for(Map.Entry<Module, Integer>entry:module_count.entrySet()){
				Module depdent_module = entry.getKey();
				if(intramodules_f1.contains(depdent_module)){
					ef1f2 += entry.getValue();
				}
			}
		}
		
		result = ef1f2/(2*num_md_f1*num_md_f2);
		return result;
	}

	/**
	 * Intra-connectivity (A) measures the degree of connectivity betwen the components that are grouped in the same cluster.
	 * A high degree of intra-connectivity indicates good subsystem partitioning.
	 * A low degree of intra-connectivity indicates poor subsystem partitioning.
	 * A = u/pow(N,2)
	 * u = 
	 */
	protected double compute_intro_connectivity(Set<ModuleWrapper> mset){
		int num_module = 0;
		int intra_connection = 0;
		Set<Module> intramodules = new HashSet<Module>();
		for(ModuleWrapper wrapper:mset){
			intramodules.addAll(wrapper.getModuleSet());
			num_module+=wrapper.getModuleSet().size();
		}
		List<Module> list_intramodules = new ArrayList<Module>(intramodules);
		for(int i = 0;i < list_intramodules.size();i++){
			Module modulei = list_intramodules.get(i);
			for(int j = i+1;j < list_intramodules.size();j++){
				Module modulej = list_intramodules.get(i);
				intra_connection += modulei.getTotalDependency(modulej);
			}
		}
		
		return ((double)intra_connection)/Math.pow(num_module,2);
	}
}
