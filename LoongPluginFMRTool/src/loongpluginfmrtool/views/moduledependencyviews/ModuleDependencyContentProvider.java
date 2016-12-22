package loongpluginfmrtool.views.moduledependencyviews;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.module.Module;

public class ModuleDependencyContentProvider extends ArrayContentProvider  implements IGraphEntityContentProvider {
	private HierarchicalBuilder ahbuilder;
	public ModuleDependencyContentProvider(HierarchicalBuilder hbuilder){
		this.ahbuilder = hbuilder;
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getConnectedTo(Object entity) {
		if(entity instanceof Module){
			Module node = (Module)entity;
			return ahbuilder.getConnectedTo(node);
		}else if(entity instanceof HierarchicalBuilder){
			return null;
		}
		throw new RuntimeException("Type not supported");
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		assert inputElement instanceof HierarchicalBuilder;
		HierarchicalBuilder hbuilder = (HierarchicalBuilder)inputElement;
		if(hbuilder==null)
			return null;
		else
			return hbuilder.getNodes();
	}
	
	

}
