package loongpluginfmrtool.editor.configfeaturemodeleditor.commands;



import loongpluginfmrtool.editor.configfeaturemodeleditor.model.AbstractConnectionModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.gef.commands.Command;

public class CreateConfFeatureConnectionCommand extends Command {
	private ConfFeature source, target;

	private AbstractConnectionModel connection;

	@Override
	public boolean canExecute() {
		if (source == null | target == null)
			return false;
		if (source == target)
			return false;
		return true;
	}

	@Override
	public void execute() {
		connection.attachSource();
		connection.attachTarget();
	}

	public void setConnection(Object model) {
		connection = (AbstractConnectionModel) model;
	}

	public void setSource(Object model) {
		source = (ConfFeature) model;
		connection.setSource(source);
	}

	public void setTarget(Object model) {
		target = (ConfFeature) model;
		connection.setTarget(target);
	}

	@Override
	public void undo() {
		connection.detachSource();
		connection.detachTarget();
	}
}
