package loongpluginfmrtool.editor.configfeaturemodeleditor.actions;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

public abstract class ConfFeatureEditorAction extends Action {
	private GraphicalViewer viewer;
	public ConfFeatureEditorAction(String name, GraphicalViewer viewer){
		super(name);
		this.viewer = viewer;
	}
	public ConfFeatureEditorAction(String name, int style, GraphicalViewer viewer){
		super(name, style);
		this.viewer = viewer;
	}
	protected GraphicalViewer getViewer(){
		return viewer;
	}
	
	public abstract void update(IStructuredSelection sel);
	
}
