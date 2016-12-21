package loongpluginfmrtool.module.model.constrains;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.util.TypeBindingVisitor;

public class TypeConstrains {

	private Module amodule;
	private LFlyweightElementFactory alElementfactory;
	private CompilationUnit unit;
	private Set<LElement> type_typeconstrains = new HashSet<LElement>();
	private Map<LElement,Set<LElement>>method_typeconstrains = new HashMap<LElement,Set<LElement>>();
	private Set<LElement>allmethods;
	public TypeConstrains(Module pmodule,LFlyweightElementFactory plElementfactory){
		this.amodule = pmodule;
		this.alElementfactory = plElementfactory;
		this.allmethods = this.amodule.getallMethods();
		findTypeConstrains();
	}
	
	public void findTypeConstrains(){
		unit = amodule.getCompilationUnit();
		List<AbstractTypeDeclaration> types = unit.types();
		for(AbstractTypeDeclaration abstypedecl:types){
			ITypeBinding typebinding = abstypedecl.resolveBinding();
			// 检查这个类是否有 需要继承的父类 接口
			if(typebinding.isClass()){
				// is a class
				LElement classtarget = alElementfactory.getElement(typebinding);
				ASTNode classastnode = classtarget.getASTNode();
				assert classastnode instanceof TypeDeclaration;
				TypeDeclaration typedecl = (TypeDeclaration)classastnode;
				// get super class if any
				Type superclasstype = typedecl.getSuperclassType();
				if(superclasstype!=null){
					ITypeBinding parenttypebinding = superclasstype.resolveBinding();
					LElement parentelement = alElementfactory.getElement(parenttypebinding);
					if(parentelement!=null){
						// if it is  in the system
						type_typeconstrains.add(parentelement);
					}
				}
				// get interface if any
				List<Type> superinterfaces = typedecl.superInterfaceTypes();
				if(superinterfaces!=null){
					for(Type superinterface:superinterfaces){
						// add the type element into constrains
						ITypeBinding interfacebinding = superinterface.resolveBinding();
						LElement interfaceelement = alElementfactory.getElement(interfacebinding);
						if(interfaceelement!=null){
							// if the interface is  in the system
							type_typeconstrains.add(interfaceelement);
						}
					}
				}
			}else if(typebinding.isEnum()){
				continue;
			}else if(typebinding.isInterface()){
				LElement classtarget = alElementfactory.getElement(typebinding);
				ASTNode classastnode = classtarget.getASTNode();
				assert classastnode instanceof TypeDeclaration;
				TypeDeclaration typedecl = (TypeDeclaration)classastnode;
				// get interface if any
				List<Type> superinterfaces = typedecl.superInterfaceTypes();
				if(superinterfaces!=null){
					for(Type superinterface:superinterfaces){
						// add the type element into constrains
						ITypeBinding interfacebinding = superinterface.resolveBinding();
						LElement interfaceelement = alElementfactory.getElement(interfacebinding);
						if(interfaceelement!=null){
							// if the interface is  in the system
							type_typeconstrains.add(interfaceelement);
						}
					}
				}
			}else{
				continue;
			}
		}
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
								type_typeconstrains.add(typeelement);
							}
						}
					}
				}
			}
		}
		
		
	}
}
