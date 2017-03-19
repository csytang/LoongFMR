package loongpluginfmrtool.module.model.module;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;


public class ModuleCallDependencyBuilder {
	
	private Module module;
	private LFlyweightElementFactory LElementFactory;
	private List<LElement>allmethods = new LinkedList<LElement>();
	private Map<Module,Integer> moduleDependency = new HashMap<Module,Integer>();
	
	public Map<Module,Integer> getmoduleCallDependencyResult(){
		return moduleDependency;
	}
	public ModuleCallDependencyBuilder(Module module,LFlyweightElementFactory pLElementFactory){
		this.module = module;
		this.LElementFactory = pLElementFactory;
		this.allmethods = new LinkedList<LElement>(this.module.getallMethods()); 
		
	}
	
	public void parse(){
		for(LElement method:allmethods){
			ASTNode method_astnode = method.getASTNode();
			method_astnode.accept(new MethodInvocationVisitor());
		}
	}
	
	class MethodInvocationVisitor extends ASTVisitor{
		public MethodInvocationVisitor(){
			
		}
		
		private void addDepenency(Module remote_module){
			if(moduleDependency.containsKey(remote_module)){
				int currcount = moduleDependency.get(remote_module);
				currcount+=1;
				moduleDependency.put(remote_module, currcount);
			}else{
				moduleDependency.put(remote_module, 1);
			}
			
		}
		


		@Override
		public boolean visit(MethodInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveMethodBinding();
			if(method!=null){
				LElement declelement = LElementFactory.getElement(method);
				if(declelement!=null){
					CompilationUnit compilation_unit = declelement.getCompilationUnit();
					LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
					if(compilation_unit_element!=null){
						Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
						addDepenency(remote_module);
	
					}
					
				}
			}
			return super.visit(node);
		}

		@Override
		public boolean visit(SuperConstructorInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveConstructorBinding();
			if(method!=null){
				LElement declelement = LElementFactory.getElement(method);
				if(declelement!=null && method!=null){
					CompilationUnit compilation_unit = declelement.getCompilationUnit();
					LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
				
					Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
					addDepenency(remote_module);
	
					
				}
			}
			return super.visit(node);
		}

		@Override
		public boolean visit(SuperMethodInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveMethodBinding();
			if(method!=null){
				LElement declelement = LElementFactory.getElement(method);
				if(declelement!=null){
					CompilationUnit compilation_unit = declelement.getCompilationUnit();
					LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
					Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
					addDepenency(remote_module);
	
					
				}
			}
			return super.visit(node);
		}
		
	}
}