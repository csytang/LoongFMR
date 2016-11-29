package loongpluginfmrtool.views.moduleviews;

import loongpluginfmrtool.module.model.ConfigurationRelationLink;
import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.module.model.ModuleComponent;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ModuleContentProvider implements ITreeContentProvider{

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		if(parentElement instanceof ModuleModel){
			ModuleModel mm_parent_element = (ModuleModel)parentElement;
			return mm_parent_element.getModules().toArray();
		}else if(parentElement instanceof Module){
			Module m_parent_element = (Module)parentElement;
			return m_parent_element.getComponents().toArray();
		}else if(parentElement instanceof ModuleComponent){
			ModuleComponent mc_parent_element = (ModuleComponent)parentElement;
			return mc_parent_element.getChildren();
		}else if(parentElement instanceof ConfigurationRelationLink){
			return null;
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		if(element instanceof ConfigurationRelationLink){
			ConfigurationRelationLink config_link_elemnet = (ConfigurationRelationLink)element;
			return config_link_elemnet.getParent();
		}else if(element instanceof ModuleComponent){
			ModuleComponent module_element = (ModuleComponent)element;
			return module_element.getParent();
		}else if(element instanceof Module){
			Module m_element = (Module)element;
			return m_element.getParent();
		}else if(element instanceof ModuleModel){
			return null;
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		if(element instanceof ModuleModel){
			ModuleModel mm_parent_element = (ModuleModel)element;
			return mm_parent_element.getModules().size()>0;
		}else if(element instanceof Module){
			Module m_parent_element = (Module)element;
			return m_parent_element.getComponents().size()>0;
		}else if(element instanceof ModuleComponent){
			ModuleComponent m_conf_element = (ModuleComponent)element;
			return m_conf_element.getChildren().length>0;
		}else if(element instanceof ConfigurationRelationLink){
			return false;
		}
		return false;
	}

}
