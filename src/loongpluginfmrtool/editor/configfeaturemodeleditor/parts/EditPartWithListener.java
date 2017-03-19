package loongpluginfmrtool.editor.configfeaturemodeleditor.parts;

import java.beans.PropertyChangeListener;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.AbstractModel;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public abstract class EditPartWithListener extends AbstractGraphicalEditPart
		implements PropertyChangeListener {

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
