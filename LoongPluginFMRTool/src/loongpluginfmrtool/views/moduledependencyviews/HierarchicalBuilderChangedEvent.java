package loongpluginfmrtool.views.moduledependencyviews;

import java.util.EventObject;

import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;


public class HierarchicalBuilderChangedEvent extends EventObject {
	
	private HierarchicalBuilder builder;
	public HierarchicalBuilderChangedEvent(Object source, HierarchicalBuilder pbuilder) {
		super(source);
		// TODO Auto-generated constructor stub
		this.builder = pbuilder;
	}
	public HierarchicalBuilder getUpdatedBuilder(){
		return builder;
	}
}
