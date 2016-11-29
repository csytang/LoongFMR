package loongpluginfmrtool.editor.configfeaturemodeleditor.parts;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginfmrtool.views.recommendedfeatureview.RecommendedFeatureView;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.swt.widgets.Text;

public class CustomDirectEditManager extends DirectEditManager {

	private ConfFeature feature;
	
	public CustomDirectEditManager(GraphicalEditPart source, Class editorType,
			CellEditorLocator locator) {
		super(source, editorType, locator);
		// TODO Auto-generated constructor stub
		feature = (ConfFeature) source.getModel();
		
	}

	@Override
	protected void initCellEditor() {
		// TODO Auto-generated method stub
		
		getCellEditor().setValue(feature.getText());
		
		Text text = (Text)getCellEditor().getControl();
		
		text.selectAll();
	}

}
