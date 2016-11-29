package loongpluginfmrtool.editor.configfeaturemodeleditor.model;

import java.util.ArrayList;
import java.util.List;

public class ConfFeatureModel extends AbstractModel {

	public static final String PROP_CHILDREN = "CHILDREN";

	private List children = new ArrayList();

	public void addChild(Object child) {
		children.add(child);
		firePropertyChange(PROP_CHILDREN, null, null);
	}

	public void removeChild(Object child) {
		children.remove(child);
		firePropertyChange(PROP_CHILDREN, null, null);
	}
	
	public List getChildren() {
		return children;
	}
	
	
}
