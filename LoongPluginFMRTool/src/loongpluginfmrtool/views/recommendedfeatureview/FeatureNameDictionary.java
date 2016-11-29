package loongpluginfmrtool.views.recommendedfeatureview;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongplugin.LoongPlugin;
import loongplugin.featuremodeleditor.event.FeatureModelChangedEvent;
import loongplugin.utils.Stemmer;
import loongplugin.utils.StringListToFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

public class FeatureNameDictionary {
	
	private final Map<String,Map<IJavaElement,Set<ASTNode>>>featureNameDictionary = new HashMap<String,Map<IJavaElement,Set<ASTNode>>>();
	private final Map<String,Map<IJavaElement,Set<ASTNode>>>nonfeaturetextMapping = new HashMap<String,Map<IJavaElement,Set<ASTNode>>>();
	private IProject project = null;
	private static FeatureNameDictionary instance;
	private IProgressMonitor monitor;
	private List<String>allStringList = new LinkedList<String>();
	private List<IRSFeatureModelChangeListener>listeners = new LinkedList<IRSFeatureModelChangeListener>();
	private RSFeatureModel rsfeaturemodel = new RSFeatureModel();
	
	public static FeatureNameDictionary getInstance(IProgressMonitor pmonitor){
		if(instance==null)
			instance = new FeatureNameDictionary(pmonitor);
		return instance;
	}
	public FeatureNameDictionary(IProgressMonitor pmonitor){
		this.monitor = pmonitor;
	}
	
	public void addRSFeatureModelChangeListener(IRSFeatureModelChangeListener listener){
		listeners.add(listener);
	}
	/**
	 * 
	 * @param associatedString potential feature name
	 * @param element   IJavaElement belongs
	 * @param astnode   a specific astnode contains this string
	 */
	public void addDictBuiltElement(String associatedString,IJavaElement element,ASTNode astnode){
		String asssociatedStringLowwercase = associatedString.toLowerCase();
		asssociatedStringLowwercase = replaceSymbolToSpace(asssociatedStringLowwercase);
		asssociatedStringLowwercase = asssociatedStringLowwercase.trim();
		Stemmer stemmer = new Stemmer();
		asssociatedStringLowwercase = stemmer.stem(asssociatedStringLowwercase);
		if(asssociatedStringLowwercase.length()<=2)
			return;
		
		if(featureNameDictionary.containsKey(asssociatedStringLowwercase)){
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = featureNameDictionary.get(asssociatedStringLowwercase);
			if(iJavaElementBindings.containsKey(element)){
				Set<ASTNode> bindingastnodes = iJavaElementBindings.get(element);
				bindingastnodes.add(astnode);
				iJavaElementBindings.put(element, bindingastnodes);
				featureNameDictionary.put(asssociatedStringLowwercase, iJavaElementBindings);
			}else{
				Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
				bindingastnodes.add(astnode);
				iJavaElementBindings.put(element, bindingastnodes);
				featureNameDictionary.put(asssociatedStringLowwercase, iJavaElementBindings);
			}
		}else{
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = new HashMap<IJavaElement,Set<ASTNode>>();
			Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
			bindingastnodes.add(astnode);
			iJavaElementBindings.put(element, bindingastnodes);
			featureNameDictionary.put(asssociatedStringLowwercase, iJavaElementBindings);
		}
	}
	
	/**
	 * 
	 * @param associatedString potential feature name
	 * @param element IJavaElement belongs
	 * @param astnodes  a specific astnode contains this string
	 */
	public void addDictBuiltElement(String associatedString,IJavaElement element,Set<ASTNode> astnodes){
		String asssociatedStringLowwercase = associatedString.toLowerCase();
		asssociatedStringLowwercase = replaceSymbolToSpace(asssociatedStringLowwercase);
		asssociatedStringLowwercase = asssociatedStringLowwercase.trim();
		Stemmer stemmer = new Stemmer();
		asssociatedStringLowwercase = stemmer.stem(asssociatedStringLowwercase);
		if(asssociatedStringLowwercase.length()<=2)
			return;
		
		if(featureNameDictionary.containsKey(asssociatedStringLowwercase)){
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = featureNameDictionary.get(asssociatedStringLowwercase);
			if(iJavaElementBindings.containsKey(element)){
				Set<ASTNode> bindingastnodes = iJavaElementBindings.get(element);
				bindingastnodes.addAll(astnodes);
				iJavaElementBindings.put(element, bindingastnodes);
				featureNameDictionary.put(asssociatedStringLowwercase, iJavaElementBindings);
			}else{
				Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
				bindingastnodes.addAll(astnodes);
				iJavaElementBindings.put(element, bindingastnodes);
				featureNameDictionary.put(asssociatedStringLowwercase, iJavaElementBindings);
			}
		}else{
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = new HashMap<IJavaElement,Set<ASTNode>>();
			Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
			bindingastnodes.addAll(astnodes);
			iJavaElementBindings.put(element, bindingastnodes);
			featureNameDictionary.put(asssociatedStringLowwercase, iJavaElementBindings);
		}
	}
	
	
	public void addAnyElement(String associatedString,IJavaElement element,ASTNode astnode){
		String asssociatedStringLowwercase = associatedString.toLowerCase();
		asssociatedStringLowwercase = replaceSymbolToSpace(asssociatedStringLowwercase);
		asssociatedStringLowwercase = asssociatedStringLowwercase.trim();
		Stemmer stemmer = new Stemmer();
		asssociatedStringLowwercase = stemmer.stem(asssociatedStringLowwercase);
		if(asssociatedStringLowwercase.length()<=2)
			return;
		
		
		if(nonfeaturetextMapping.containsKey(asssociatedStringLowwercase)){
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = nonfeaturetextMapping.get(asssociatedStringLowwercase);
			if(iJavaElementBindings.containsKey(element)){
				Set<ASTNode> bindingastnodes = iJavaElementBindings.get(element);
				bindingastnodes.add(astnode);
				iJavaElementBindings.put(element, bindingastnodes);
				nonfeaturetextMapping.put(asssociatedStringLowwercase, iJavaElementBindings);
			}else{
				Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
				bindingastnodes.add(astnode);
				iJavaElementBindings.put(element, bindingastnodes);
				nonfeaturetextMapping.put(asssociatedStringLowwercase, iJavaElementBindings);
			}
		}else{
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = new HashMap<IJavaElement,Set<ASTNode>>();
			Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
			bindingastnodes.add(astnode);
			iJavaElementBindings.put(element, bindingastnodes);
			nonfeaturetextMapping.put(asssociatedStringLowwercase, iJavaElementBindings);
		}
	}
	
	
	public void addAnyElement(String associatedString,IJavaElement element,Set<ASTNode> astnodes){
		String asssociatedStringLowwercase = associatedString.toLowerCase();
		
		asssociatedStringLowwercase = replaceSymbolToSpace(asssociatedStringLowwercase);
		asssociatedStringLowwercase = asssociatedStringLowwercase.trim();
		Stemmer stemmer = new Stemmer();
		asssociatedStringLowwercase = stemmer.stem(asssociatedStringLowwercase);
		if(asssociatedStringLowwercase.length()<=2)
			return;
		
		if(nonfeaturetextMapping.containsKey(asssociatedStringLowwercase)){
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = nonfeaturetextMapping.get(asssociatedStringLowwercase);
			if(iJavaElementBindings.containsKey(element)){
				Set<ASTNode> bindingastnodes = iJavaElementBindings.get(element);
				bindingastnodes.addAll(astnodes);
				iJavaElementBindings.put(element, bindingastnodes);
				nonfeaturetextMapping.put(asssociatedStringLowwercase, iJavaElementBindings);
			}else{
				Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
				bindingastnodes.addAll(astnodes);
				iJavaElementBindings.put(element, bindingastnodes);
				nonfeaturetextMapping.put(asssociatedStringLowwercase, iJavaElementBindings);
			}
		}else{
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = new HashMap<IJavaElement,Set<ASTNode>>();
			Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
			bindingastnodes.addAll(astnodes);
			iJavaElementBindings.put(element, bindingastnodes);
			nonfeaturetextMapping.put(asssociatedStringLowwercase, iJavaElementBindings);
		}
	}
	
	/*
	 * Insert a dict to dictionary
	 */
	public void mergeAndOptimizeDict(){
		
		assert(project!=null);
		
		FeatureNameMatrix fMatrix = new FeatureNameMatrix(featureNameDictionary,nonfeaturetextMapping);
		rsfeaturemodel = fMatrix.getRSFeatureModel();
		notifyFeatureModelListener();
	}
	
	public void notifyFeatureModelListener(){
		long timesequence = System.currentTimeMillis();
		RSFeatureModelChangedEvent event = new RSFeatureModelChangedEvent(this,rsfeaturemodel,timesequence);
		for(IRSFeatureModelChangeListener listener:listeners){
			listener.featureModelChanged(event);
		}
	}


	public void setProject(IProject selectProject) {
		// TODO Auto-generated method stub
		this.project = selectProject;
	}
	
	public void convertToList() {
		// TODO Auto-generated method stub
		if(!featureNameDictionary.isEmpty())
			allStringList.addAll(featureNameDictionary.keySet());
		if(!nonfeaturetextMapping.isEmpty())
			allStringList.addAll(nonfeaturetextMapping.keySet());
	}
	
	public String replaceSymbolToSpace(String str){
		String resultstr = str;
		for(int i = 0;i < str.length();i++){
			char c = str.charAt(i);
			if(c<='z'&&c>='a'){
				continue;
			}else if(c==' '||c=='\t'){
				continue;
			}else if(resultstr.indexOf(c)!=-1){
				resultstr = resultstr.replace(c,' ');
			}
		}
		
		return resultstr;
	}
}
