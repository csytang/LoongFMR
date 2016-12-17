package loongpluginfmrtool.module.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class Variability {
	/**
	 * 算一个varability
	 * 然后再算每一个configuration 的比重
	 */
	public Module module;
	public int totalValidConfig = 0;
	private Set<ConfigurationOption> options;
	
	public Variability(Module pmodule){
		this.module = pmodule;
		this.options = this.module.getAllConfigurationOptions();
	}
	
	
	

	public int getValidConfigurationCount() {
		// TODO Auto-generated method stub
		return totalValidConfig;
	}

	public Module getModule() {
		// TODO Auto-generated method stub
		return module;
	}
	
}
