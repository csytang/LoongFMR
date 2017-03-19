package loongpluginfmrtool.views.recommendedfeatureview;

import java.util.EventObject;


public class RSFeatureModelChangedEvent extends EventObject{
	
	private final RSFeatureModel afeaturemodel;
	private final long atimeHash;
	public RSFeatureModelChangedEvent(Object source,RSFeatureModel pfeaturemodel,long ptimeHash) {
		super(source);
		// TODO Auto-generated constructor stub
		afeaturemodel = pfeaturemodel;
		atimeHash = ptimeHash;
	}
	
	public RSFeatureModel getFeatureModel(){
		return afeaturemodel;
	}
	
	public long getTimeHash(){
		return atimeHash;
	}
	
}
