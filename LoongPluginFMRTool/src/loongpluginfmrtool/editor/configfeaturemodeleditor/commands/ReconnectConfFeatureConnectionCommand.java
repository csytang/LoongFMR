package loongpluginfmrtool.editor.configfeaturemodeleditor.commands;



import loongpluginfmrtool.editor.configfeaturemodeleditor.model.AbstractConnectionModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.gef.commands.Command;

public class ReconnectConfFeatureConnectionCommand extends Command {

	private AbstractConnectionModel connection;
	private ConfFeature newSource;
	private ConfFeature newTarget;
	private ConfFeature oldSource;
	private ConfFeature oldTarget;

	@Override
	public void execute() {
		if (newSource != null) {
			oldSource = connection.getSource();
			reconnectSource(newSource);
		}

		if (newTarget != null) {
			oldTarget = connection.getTarget();
			reconnectTarget(newTarget);
		}
	}

	private void reconnectSource(ConfFeature source) {
		connection.detachSource();
		connection.setSource(source);
		connection.attachSource();
	}

	private void reconnectTarget(ConfFeature target) {
		connection.detachTarget();
		connection.setTarget(target);
		connection.attachTarget();
	}

	public void setConnectionModel(Object model) {
		connection = (AbstractConnectionModel) model;
	}

	public void setNewSource(Object model) {
		newSource = (ConfFeature) model;
	}

	public void setNewTarget(Object model) {
		newTarget = (ConfFeature) model;
	}

	@Override
	public void undo() {
		if (oldSource != null)
			reconnectSource(oldSource);
		if (oldTarget != null)
			reconnectTarget(oldTarget);

		oldSource = null;
		oldTarget = null;
	}

}
