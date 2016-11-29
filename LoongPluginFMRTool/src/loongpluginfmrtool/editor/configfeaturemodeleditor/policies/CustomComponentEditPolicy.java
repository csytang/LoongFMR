package loongpluginfmrtool.editor.configfeaturemodeleditor.policies;

import loongpluginfmrtool.editor.configfeaturemodeleditor.commands.DeleteConfFeatureCommand;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class CustomComponentEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		DeleteConfFeatureCommand deleteCommand = new DeleteConfFeatureCommand();
		deleteCommand.setConfFeatureModel(getHost().getParent().getModel());
		deleteCommand.setConfFeature(getHost().getModel());
		return deleteCommand;
	}

}
