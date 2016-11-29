package loongpluginfmrtool.editor.configfeaturemodeleditor.parts;



import loongpluginfmrtool.editor.configfeaturemodeleditor.policies.CustomConnectionEndpointEditPolicy;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

public class CustomAbstractConnectionEditPart extends
AbstractConnectionEditPart {

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
			new CustomConnectionEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
			new CustomConnectionEndpointEditPolicy());
	}
}
