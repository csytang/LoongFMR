package loongpluginfmrtool.module.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;


public class ConfigurationOptionTree {
	private Set<ConfigurationOption> roots;
	private Module module;
	private Map<ConfigurationOption,HashSet<ConfigurationOption>> successors;//后继
	private Map<ConfigurationOption,HashSet<ConfigurationOption>> preprocessors;//前驱 多个 在不同的 
	private Set<ConfigurationOption>optionset;
	public ConfigurationOptionTree(Module pmodule){
		this.module = pmodule;
		this.successors =new HashMap<ConfigurationOption,HashSet<ConfigurationOption>>();
		this.preprocessors = new HashMap<ConfigurationOption,HashSet<ConfigurationOption>>();
		this.optionset = module.getAllConfigurationOptions();
		// set a template collection of root and remove unneed ones
		this.roots = new HashSet<ConfigurationOption>(optionset);
		build();
		
	}
	
	
	public Set<ConfigurationOption> getRoots(){
		return roots;
	}
	
	public void build(){
		// option --> target
		for(ConfigurationOption source:optionset){// 遍历module 中所有的 option
			Set<ConfigurationRelationLink>connectedlinks = source.getlinks();
			for(ConfigurationRelationLink link:connectedlinks){
				ConfigurationOption target = link.getTargetConfigurationOption();
				if(target.getAssociatedModule()!=module){
					continue;
				}
				ConfigRelation relation = link.getRelation();
				if(!relation.equals(ConfigRelation.CONTAINS)){
					continue;
				}
				/*
				 * if there is a link from source--> target,
				 * add source to successor and add target to preprocessors;
				 */
				if(successors.containsKey(source)){// 后继 集合
					HashSet<ConfigurationOption>targetset = successors.get(source);
					targetset.add(target);
					successors.put(source, targetset);
				}else{
					HashSet<ConfigurationOption>targetset = new HashSet<ConfigurationOption>();
					targetset.add(target);
					successors.put(source, targetset);
				}
				if(preprocessors.containsKey(target)){// 前驱 集合
					HashSet<ConfigurationOption>sourceset = preprocessors.get(target);
					sourceset.add(source);
					preprocessors.put(target, sourceset);
				}else{
					HashSet<ConfigurationOption>sourceset = new HashSet<ConfigurationOption>();
					sourceset.add(source);
					preprocessors.put(target, sourceset);
				}
				if(this.roots.contains(target)){
					this.roots.remove(target);
				}
			}
		}
	}

	public Set<ConfigurationOption> getChildren(ConfigurationOption option_top) {
		// TODO Auto-generated method stub
		return successors.get(option_top);
	}
	
	public Set<ConfigurationOption> getSameMethodDirectChildren(ConfigurationOption option) {
		Set<ConfigurationOption> children = successors.get(option);
		Set<ConfigurationOption> res = new HashSet<ConfigurationOption>();
		MethodDeclaration methoddecl = option.getMethod();
		if(children!=null){
			for(ConfigurationOption child:children){
				MethodDeclaration childmethoddecl = child.getMethod();
				if(childmethoddecl.equals(methoddecl)){
					//res.add(child);
					res = addSubChecking(res,child);
				}
			}
		}
		return res;
	}


	private Set<ConfigurationOption> addSubChecking(Set<ConfigurationOption> res,
			ConfigurationOption child) {
		// TODO Auto-generated method stub
		Set<ConfigurationOption> rescopy = new HashSet<ConfigurationOption>();
		for(ConfigurationOption config:res){
			if(child.isParentOf(config)){
				rescopy.add(child);
			}else{
				rescopy.add(config);
			}
		}
		return rescopy;
	}


	public Set<ConfigurationOption> getParent(ConfigurationOption curr) {
		// TODO Auto-generated method stub
		return preprocessors.get(curr);
	}


	public ConfigurationOption getSameMethodDirectParent(ConfigurationOption curr) {
		// TODO Auto-generated method stub
		Set<ConfigurationOption> allparents = preprocessors.get(curr);
		MethodDeclaration methoddecl = curr.getMethod();
		ConfigurationOption res = null;
		for(ConfigurationOption parent:allparents){
			MethodDeclaration parentmethoddecl = parent.getMethod();
			if(parentmethoddecl.equals(methoddecl)){
				if(res!=null){// a->b->c  c---> a?
					if(!parent.isParentOf(res))
						res = parent;
				}else
					res = parent;
			}
		}
		return res;
	}
}
