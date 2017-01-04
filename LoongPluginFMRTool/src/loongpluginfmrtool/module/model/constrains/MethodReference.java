package loongpluginfmrtool.module.model.constrains;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.util.TypeBindingVisitor;

public class MethodReference {
	private Module amodule;
	private LFlyweightElementFactory alElementfactory;
	private CompilationUnit unit;
	private Set<LElement> methods_elements = new HashSet<LElement>();
	
	private Set<LElement>allmethods;
	public MethodReference(Module pmodule,LFlyweightElementFactory plElementfactory){
		this.amodule = pmodule;
		this.alElementfactory = plElementfactory;
		this.allmethods = this.amodule.getallMethods();
		findMethodRefConstrains();
	}
	
	public Set<LElement> getMethodReferenceConstrainsLElement(){
		return methods_elements;
	}
	public void findMethodRefConstrains(){
		unit = amodule.getCompilationUnit();
		// 对于每一个函数 检查里面使用的 type 信息 有没有需要引用
		
		if(allmethods!=null){
			for(LElement method:allmethods){
				ASTNode methodastnode = method.getASTNode();
				TypeBindingVisitor typebindingvisitor = new TypeBindingVisitor();
				methodastnode.accept(typebindingvisitor);
				Set<ITypeBinding> typebindings = typebindingvisitor.getVariableBindings();
				if(typebindings!=null){
					for(ITypeBinding binding:typebindings){
						if(binding!=null){
							LElement typeelement = alElementfactory.getElement(binding);
							if(typeelement!=null){
								// if the interface is  in the system
								methods_elements.add(typeelement);
							}
						}
					}
				}
			}
		}
		
		
	}
}
