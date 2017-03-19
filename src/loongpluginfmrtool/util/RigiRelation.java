package loongpluginfmrtool.util;

public enum RigiRelation {
	// 12 types in total from the shrimp tool
	accesses("accesses"),
	calls("calls"),
	caststotype("casts to type"),
	contains("contains"),
	creates("creates"),
	extendedby("extended by"),
	extendedbyinterface("extended by (interface)"),
	hasparametertype("has parameter type"),
	hasreturntype("has return type"),
	implementedby("implemented by"),
	isoftype("is of type"),
	overrids("overrids");
	
	
	
	
	
	
	
	private String code;
	RigiRelation(String pcode){
		this.code = pcode;
	}
	
	
	public String getCode(){
		return this.code;
	}
	
	
}
