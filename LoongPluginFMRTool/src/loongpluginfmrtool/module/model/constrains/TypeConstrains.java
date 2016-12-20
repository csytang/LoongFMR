package loongpluginfmrtool.module.model.constrains;

import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.module.Module;

public class TypeConstrains {

	private Module amodule;
	private LFlyweightElementFactory alElementfactory;
	
	public TypeConstrains(Module pmodule,LFlyweightElementFactory plElementfactory){
		this.amodule = pmodule;
		this.alElementfactory = plElementfactory;
		findTypeConstrains();
	}
	
	public void findTypeConstrains(){
		
	}
}
