package loongpluginfmrtool.module.featuremodelbuilder;

import java.util.Map;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.model.Module;

public class KullbackLeiblerTable {
	private ModuleBuilder builder;
	private int totalsize;
	private double[][] kullback_leibler;
	private Map<Integer,Module> indexToModule;
	private double[][] normalizedtable; // p(f|m_{i})
	private ModuleDependencyTable table;
	public KullbackLeiblerTable(ModuleBuilder pbuilder){
		builder = pbuilder;
		table = pbuilder.getDependencyTable();
		normalizedtable = table.getNormalizedTable();
	}
	
	
	
	public void buildTable(){
		indexToModule = builder.getIndexToModule();
		totalsize = indexToModule.size();
		this.kullback_leibler = new double[this.totalsize][this.totalsize];
		for(int i = 0;i < this.totalsize;i++){
			for(int j = i;j < this.totalsize;j++){
				double kullbackleibler;
				if(i==j){
					kullbackleibler = 0.0;
				}else{
				    kullbackleibler = compute_Single_Kullback_Leibler(i,j);
				    kullback_leibler[i][j] = kullbackleibler;
				    kullback_leibler[j][i] = kullbackleibler;
				}
			}
		}
		
	}
	protected double compute_Single_Kullback_Leibler(int index_1,int index_2){
		double distance = 0.0;
		int module_index1 = index_1;
		int module_index2 = index_2;
		
		for(int j = 0;j < totalsize;j++){
			double value_index_1 = normalizedtable[module_index1][j];
			double value_index_2 = normalizedtable[module_index2][j];
			if(value_index_1==0||value_index_2==0){
				continue;
			}
			double temp = Math.log(value_index_1/value_index_2);
			distance+= temp*value_index_1;
		}
		
		return distance;
	}



	public double[][] getTable() {
		// TODO Auto-generated method stub
		return kullback_leibler;
	}
}
