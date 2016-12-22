package loongpluginfmrtool.module.model.module;

import java.util.Map;

public class ModuleCallDependencyTable {
	private ModuleBuilder builder;
	private int totalsize;
	private int[][] table;
	private boolean debug = true;
	private double[][] normalizedtable;
	private Map<Integer,Module> indexToModule;
	private boolean isNormalizedtableCompuated = false;
	public ModuleCallDependencyTable(ModuleBuilder pbuilder){
		builder = pbuilder;
	}
	
	public void buildTable(){
		indexToModule = builder.getIndexToModule();
		totalsize = indexToModule.size();
		table = new int[totalsize][totalsize];
		safechecker();
		for(int i = 0;i < totalsize;i++){
			for(int j = 0;j < totalsize;j++){
				if(i==j){
					table[i][j] = 0;
				}else{
					table[i][j] = computeTotalReference(indexToModule.get(i),indexToModule.get(j));
				}
			}
		}
		if(debug){
			printtable();
		}
	}
	private int computeTotalReference(Module a,Module b){
		int reference = a.getAllCallDependency().get(b);
		return reference;
	}
	
	public double[][] getNormalizedTable(){
		
		if(!isNormalizedtableCompuated){
			normalizedtable = new double[totalsize][totalsize];
			for(int i = 0;i < totalsize;i++){
				int rowtotal = 0;
				for(int j = 0;j < totalsize;j++){
					rowtotal+=table[i][j];
				}
				for(int j = 0;j < totalsize;j++){
					if(rowtotal==0||table[i][j]==0)
						normalizedtable[i][j] = 0.0;
					else{
						double double_table = ((double)table[i][j])/rowtotal;
						normalizedtable[i][j]  = double_table;
					}
				}
			}
			isNormalizedtableCompuated = true;
		}
		return normalizedtable;
	}
	
	
	private void printtable() {
		// TODO Auto-generated method stub
		for(int i = 0;i < totalsize;i++){
			for(int j = 0;j < totalsize;j++){
				System.out.print(table[i][j]);
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	
	
	private void safechecker(){
		for(int i = 0;i < totalsize;i++){
			if(!indexToModule.containsKey(i)){
				try{
					throw new Exception("cannot find module for index"+i);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public int [][] getTable() {
		// TODO Auto-generated method stub
		return table;
	}

	
	
}
