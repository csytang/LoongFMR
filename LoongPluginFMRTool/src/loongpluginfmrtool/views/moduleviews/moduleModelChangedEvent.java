package loongpluginfmrtool.views.moduleviews;

import java.util.EventObject;

public class moduleModelChangedEvent extends EventObject{
	private ModuleModel amodel;
	public moduleModelChangedEvent(Object source,ModuleModel pmodel) {
		super(source);
		this.amodel = pmodel;
		// TODO Auto-generated constructor stub
	}
	
	public ModuleModel getModuleModel(){
		return this.amodel;
	}

}
