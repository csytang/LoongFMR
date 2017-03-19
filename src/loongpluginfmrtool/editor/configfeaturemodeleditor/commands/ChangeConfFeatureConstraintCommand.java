package loongpluginfmrtool.editor.configfeaturemodeleditor.commands;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

public class ChangeConfFeatureConstraintCommand extends Command {

	private ConfFeature confFeature;

	private Rectangle constraint;

	private Rectangle oldConstraint;

	@Override
	public void execute() {
		confFeature.setConstraint(constraint);
	}

	public void setModel(Object model) {
		this.confFeature = (ConfFeature) model;
		oldConstraint = confFeature.getConstraint();
	}

	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
	}

	@Override
	public void undo() {
		confFeature.setConstraint(oldConstraint);
	}

}
