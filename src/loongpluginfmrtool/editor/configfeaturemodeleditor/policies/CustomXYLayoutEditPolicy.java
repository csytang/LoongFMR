package loongpluginfmrtool.editor.configfeaturemodeleditor.policies;

import loongpluginfmrtool.editor.configfeaturemodeleditor.commands.ChangeConfFeatureConstraintCommand;
import loongpluginfmrtool.editor.configfeaturemodeleditor.commands.CreateConfFeatureCommand;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

public class CustomXYLayoutEditPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		ChangeConfFeatureConstraintCommand command = new ChangeConfFeatureConstraintCommand();
		command.setModel(child.getModel());
		command.setConstraint((Rectangle) constraint);
		return command;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		CreateConfFeatureCommand createCommand = new CreateConfFeatureCommand();
		Rectangle constraint = (Rectangle) getConstraintFor(request);
		ConfFeature model = (ConfFeature) request.getNewObject();
		model.setConstraint(constraint);
		createCommand.setConfFeatureModel(getHost().getModel());
		createCommand.setConfFeature(model);
		return createCommand;
	}

}
