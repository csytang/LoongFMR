package loongpluginfmrtool.editor.configfeaturemodeleditor.ui;

import java.util.List;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginfmrtool.editor.configfeaturemodeleditor.parts.ConfFeaturePart;
import loongpluginfmrtool.views.recommendedfeatureview.RSFeature;
import loongpluginfmrtool.views.recommendedfeatureview.RSFeatureModel;
import loongpluginfmrtool.views.recommendedfeatureview.RecommendedFeatureView;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory;

public class ConfFeatureEditorContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;
	private final EditPartViewer viewer;
	public ConfFeatureEditorContextMenuProvider(EditPartViewer fviewer, final ActionRegistry actionRegistry) {
		
		super(fviewer);
        setActionRegistry(actionRegistry);
        this.viewer = fviewer;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		// TODO Auto-generated method stub
		GEFActionConstants.addStandardActionGroups(menu);
		final IStructuredSelection selection = (IStructuredSelection) this.viewer
                .getSelection();
        IAction action;

        action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
        action = getActionRegistry().getAction(ActionFactory.REDO.getId());
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
        action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO,action);
        		
        IMenuManager renamemenus = new MenuManager("Rename To:");
        RSFeatureModel rsfeaturemodel = RecommendedFeatureView.getInstance().getRSFeatureModel();
        for(final RSFeature feature:rsfeaturemodel.getFeatures()){
        	if(validNameSet(feature.getFeatureName())){
	        	renamemenus.add(new Action(feature.getFeatureName()){
					@Override
					public void run() {
						if(selection.getFirstElement() instanceof ConfFeaturePart){
							ConfFeaturePart confFeaturepart = (ConfFeaturePart)selection.getFirstElement();
							ConfFeature conffeature = (ConfFeature) confFeaturepart.getModel();
							if(validNameSet(feature.getFeatureName()))
								conffeature.setText(feature.getFeatureName());
							else{
								 Display.getCurrent().syncExec(new Runnable(){
	
										@Override
										public void run() {
											// TODO Auto-generated method stub
											MessageDialog.openInformation(null, "Loong Plugin System-SARTool",
											"There is an exist feature with this name");
										}
								    	
								    });
							}
								
						}
					}
	        	});
        	}
        }
        
        menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, renamemenus);
	}
	
	private void setActionRegistry(final ActionRegistry actionRegistry) {
        this.actionRegistry = actionRegistry;
    }
	
	private boolean validNameSet(String featureName){
		ConfigurableFeatureModelEditor instance = ConfigurableFeatureModelEditor.getInstance();
		List<ConfFeature> features = instance.getConfFeatureModel().getChildren();
		for(ConfFeature fe:features){
			if(fe.getText().equalsIgnoreCase(featureName))
				return false;
		}
		return true;
	}
	
	private ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

}
