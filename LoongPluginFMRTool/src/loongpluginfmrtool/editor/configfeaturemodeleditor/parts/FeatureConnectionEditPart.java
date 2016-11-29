package loongpluginfmrtool.editor.configfeaturemodeleditor.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;


public class FeatureConnectionEditPart extends CustomAbstractConnectionEditPart{

	@Override
	protected IFigure createFigure() {
		PolylineConnection conn = new PolylineConnection();
		conn.setTargetDecoration(new PolygonDecoration());
		return conn;
	}
	
}
