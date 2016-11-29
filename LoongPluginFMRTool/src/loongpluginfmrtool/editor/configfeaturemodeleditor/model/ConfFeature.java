package loongpluginfmrtool.editor.configfeaturemodeleditor.model;



import java.util.ArrayList;
import java.util.List;

import loongpluginfmrtool.editor.configfeaturemodeleditor.parts.CustomDirectEditManager;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.viewers.TextCellEditor;

public class ConfFeature extends AbstractModel {

	public static final String PROP_CONSTRAINT = "CONSTRAINT";
	public static final String PROP_TEXT = "TEXT";
	public static final String PROP_SOURCE_CONNECTION = "SOURCE_CONNECTION";
	public static final String PROP_TARGET_CONNECTION = "TARGET_CONNECTION";
	
	
	private String text = "unknown";

	private Rectangle constraint;

	private CustomDirectEditManager directEditManager;
	
	private List sourceConnection = new ArrayList();

	private List targetConnection = new ArrayList();
	
	public Rectangle getConstraint() {
		return constraint;
	}

	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
		firePropertyChange(PROP_CONSTRAINT, null, constraint);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		firePropertyChange(PROP_TEXT, null, text);
	}

	public List getModelSourceConnections() {
		return sourceConnection;
	}

	public List getModelTargetConnections() {
		return targetConnection;
	}

	public void addSourceConnection(Object conn) {
		sourceConnection.add(conn);
		firePropertyChange(PROP_SOURCE_CONNECTION, null, null);
	}

	public void addTargetConnection(Object conn) {
		targetConnection.add(conn);
		firePropertyChange(PROP_TARGET_CONNECTION, null, null);
	}

	public void removeSourceConnection(Object conn) {
		sourceConnection.remove(conn);
		firePropertyChange(PROP_SOURCE_CONNECTION, null, null);
	}

	public void removeTargetConnection(Object conn) {
		targetConnection.remove(conn);
		firePropertyChange(PROP_TARGET_CONNECTION, null, null);
	}
	
	
	
}
