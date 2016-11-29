package loongpluginfmrtool.module.model;

import java.util.Set;


public abstract class ModuleComponent {
	private Module amodule;
	public ModuleComponent(Module module){
		amodule = module;
	}
	public Module getParent() {
		// TODO Auto-generated method stub
		return amodule;
	}
	public abstract Object[] getChildren();
}
