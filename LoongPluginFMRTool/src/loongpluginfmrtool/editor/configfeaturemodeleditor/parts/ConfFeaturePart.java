package loongpluginfmrtool.editor.configfeaturemodeleditor.parts;




import java.beans.PropertyChangeEvent;
import java.util.List;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginfmrtool.editor.configfeaturemodeleditor.policies.CustomComponentEditPolicy;
import loongpluginfmrtool.editor.configfeaturemodeleditor.policies.CustomGraphicalNodeEditPolicy;
import loongpluginfmrtool.editor.configfeaturemodeleditor.policies.RenameEditPolicy;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.viewers.TextCellEditor;

public class ConfFeaturePart extends EditPartWithListener implements NodeEditPart{

	private CustomDirectEditManager directEditManager;
	
	@Override
	protected IFigure createFigure() {
		ConfFeature model = (ConfFeature) getModel();

		Label label = new Label();
		label.setText(model.getText());
		label.setBorder(new CompoundBorder(new LineBorder(),
				new MarginBorder(3)));
		label.setBackgroundColor(ColorConstants.lightBlue);
		label.setOpaque(true);

		return label;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new CustomComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new CustomGraphicalNodeEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new RenameEditPolicy());
		
	}

	@Override
	protected void refreshVisuals() {
		Rectangle constraint = ((ConfFeature) getModel()).getConstraint();
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), constraint);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ConfFeature.PROP_CONSTRAINT))
			refreshVisuals();
		else if (evt.getPropertyName().equals(
				ConfFeature.PROP_SOURCE_CONNECTION))
			refreshSourceConnections();
		else if (evt.getPropertyName().equals(ConfFeature.PROP_TEXT)) {
			Label label = (Label) getFigure();
			label.setText((String) evt.getNewValue());
		}else if (evt.getPropertyName()
				.equals(ConfFeature.PROP_TARGET_CONNECTION))
			refreshTargetConnections();
	}

	@Override
	public void performRequest(Request req) {
		// TODO Auto-generated method stub
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			performDirectEdit();
			return; 
		}
		super.performRequest(req);
	}
	
	
	private void performDirectEdit(){
		if (directEditManager == null) {
			directEditManager = new CustomDirectEditManager(this,
					TextCellEditor.class, new CustomCellEditorLocator(
							getFigure()));
		}
		directEditManager.show();
	}
	
	@Override
	protected List getModelSourceConnections() {
		return ((ConfFeature) getModel()).getModelSourceConnections();
	}

	@Override
	protected List getModelTargetConnections() {
		return ((ConfFeature) getModel()).getModelTargetConnections();
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

}
