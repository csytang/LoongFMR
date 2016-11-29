package loongpluginfmrtool.editor.configfeaturemodeleditor.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.AbstractModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeatureModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.policies.CustomXYLayoutEditPolicy;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;

public class ConfFeatureModelEditPart extends EditPartWithListener {

	@Override
	protected IFigure createFigure() {
		Layer figure = new Layer();
		figure.setLayoutManager(new XYLayout());
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,new CustomXYLayoutEditPolicy());
	}

	@Override
	protected List getModelChildren() {
		return ((ConfFeatureModel) getModel()).getChildren();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ConfFeatureModel.PROP_CHILDREN))
			refreshChildren();
	}
	
	@Override
	public void activate() {
		super.activate();
		((AbstractModel) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		((AbstractModel) getModel()).removePropertyChangeListener(this);
	}
	

}
