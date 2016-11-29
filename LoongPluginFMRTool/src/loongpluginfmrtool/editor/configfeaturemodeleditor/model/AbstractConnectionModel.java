package loongpluginfmrtool.editor.configfeaturemodeleditor.model;


public abstract class AbstractConnectionModel {

	private ConfFeature source, target;

	public void attachSource() {
		if (!source.getModelSourceConnections().contains(this))
			source.addSourceConnection(this);
	}

	public void detachSource() {
		source.removeSourceConnection(this);
	}

	public void attachTarget() {
		if (!target.getModelTargetConnections().contains(this))
			target.addTargetConnection(this);
	}

	public void detachTarget() {
		target.removeTargetConnection(this);
	}

	public ConfFeature getSource() {
		return source;
	}

	public void setSource(ConfFeature source) {
		this.source = source;
	}

	public ConfFeature getTarget() {
		return target;
	}

	public void setTarget(ConfFeature target) {
		this.target = target;
	}
}
