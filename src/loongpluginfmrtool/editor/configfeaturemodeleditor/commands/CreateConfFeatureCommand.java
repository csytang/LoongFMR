package loongpluginfmrtool.editor.configfeaturemodeleditor.commands;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeatureModel;

import org.eclipse.gef.commands.Command;

public class CreateConfFeatureCommand extends Command {

	private ConfFeatureModel contentsModel;

	private ConfFeature helloModel;

	@Override
	public void execute() {
		contentsModel.addChild(helloModel);
	}

	public void setConfFeatureModel(Object model) {
		contentsModel = (ConfFeatureModel) model;
	}

	public void setConfFeature(Object model) {
		helloModel = (ConfFeature) model;
	}

	@Override
	public void undo() {
		contentsModel.removeChild(helloModel);
	}

}
