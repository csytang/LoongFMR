package loongpluginfmrtool.editor.configfeaturemodeleditor.policies;


import loongpluginfmrtool.editor.configfeaturemodeleditor.commands.CreateConfFeatureConnectionCommand;
import loongpluginfmrtool.editor.configfeaturemodeleditor.commands.ReconnectConfFeatureConnectionCommand;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public class CustomGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		CreateConfFeatureConnectionCommand command = (CreateConfFeatureConnectionCommand) request
				.getStartCommand();
		command.setTarget(getHost().getModel());
		return command;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		CreateConfFeatureConnectionCommand command = new CreateConfFeatureConnectionCommand();
		command.setConnection(request.getNewObject());
		command.setSource(getHost().getModel());
		request.setStartCommand(command);
		return command;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		ReconnectConfFeatureConnectionCommand command = new ReconnectConfFeatureConnectionCommand();
		command.setConnectionModel(request.getConnectionEditPart().getModel());
		command.setNewSource(getHost().getModel());
		return command;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		ReconnectConfFeatureConnectionCommand command = new ReconnectConfFeatureConnectionCommand();
		command.setConnectionModel(request.getConnectionEditPart().getModel());
		command.setNewTarget(getHost().getModel());
		return command;
	}

}

