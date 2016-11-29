package loongpluginfmrtool.module.model;

public class ConfigurationRelationLink {
	private ConfigurationOption asource;
	private ConfigurationOption atarget;
	private ConfigRelation arelation;
	public  ConfigurationRelationLink(ConfigurationOption source,ConfigurationOption target,ConfigRelation relation){
		this.asource = source;
		this.atarget = target;
		this.arelation = relation;
	}
	public ConfigurationOption getSourceConfigurationOption(){
		return asource;
	}
	public ConfigurationOption getTargetConfigurationOption(){
		return atarget;
	}
	public ConfigRelation getRelation(){
		return arelation;
	}
	public ConfigurationOption getParent(){
		return asource;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String link = asource.toString()+"\t-->\t"+atarget.toString();
		return link;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(!(obj instanceof ConfigurationRelationLink))
			return false;
		ConfigurationRelationLink conf_link = (ConfigurationRelationLink)obj;
		if(!asource.equals(conf_link.asource))
			return false;
		if(!atarget.equals(conf_link.atarget))
			return false;
		if(!arelation.equals(conf_link.arelation))
			return false;
		return true;
	}
	
	
}
