package loongpluginfmrtool.views.moduleviews;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import loongpluginfmrtool.module.model.*;

public class ModuleModel implements Serializable{

	public Set<Module> modules = new HashSet<Module>();
	
	
	public ModuleModel(){
		
	}
	
	public void addModule(Module module){
		modules.add(module);
	}

	public Set<Module> getModules() {
		// TODO Auto-generated method stub
		return modules;
	}
	
}
