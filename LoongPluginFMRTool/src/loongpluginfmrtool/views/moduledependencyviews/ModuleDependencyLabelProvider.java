package loongpluginfmrtool.views.moduledependencyviews;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;

import loongpluginfmrtool.module.model.module.Module;

public class ModuleDependencyLabelProvider extends LabelProvider {

	
	@Override
	public String getText(Object element) {
		// TODO Auto-generated method stub
		if(element instanceof Module){
			Module module = (Module)element;
			return module.getDisplayName();
		}
		return "";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
