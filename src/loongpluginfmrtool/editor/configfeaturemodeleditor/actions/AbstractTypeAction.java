package loongpluginfmrtool.editor.configfeaturemodeleditor.actions;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginfmrtool.editor.configfeaturemodeleditor.parts.ConfFeaturePart;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

public class AbstractTypeAction extends ConfFeatureEditorAction{

	protected CommandStack stack;
	protected ConfFeature target;
	
	public AbstractTypeAction(String name, CommandStack stack, GraphicalViewer viewer) {
		super(name, viewer);
		this.stack = stack;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(IStructuredSelection sel) {
		// TODO Auto-generated method stub
		Object obj = sel.getFirstElement();
		if(obj!=null && obj instanceof ConfFeaturePart){
			setEnabled(true);
			target = (ConfFeature)((ConfFeaturePart)obj).getModel();
		} else {
			setEnabled(false);
			target = null;
		}
	}

}
