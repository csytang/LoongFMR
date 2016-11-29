package loongpluginfmrtool.editor.configfeaturemodeleditor.ui;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.EventObject;

import loongplugin.LoongPlugin;
import loongpluginfmrtool.LoongPluginFMRTool;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.AbstractModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeatureModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.FeatureConnectionModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.parts.PartFactory;
import loongpluginfmrtool.editor.configfeaturemodeleditor.serializer.DiagramSerializer;
import loongpluginfmrtool.views.recommendedfeatureview.RecommendedFeatureView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.dnd.TransferDropTargetListener;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;


public class ConfigurableFeatureModelEditor extends GraphicalEditorWithPalette implements IResourceChangeListener{

	/**
	 * TODO: 加入对 从 recommend feature list中抓取的 控制
	 */
	public static final String ID = LoongPlugin.PLUGIN_ID+".FMRTool.mConfigFeatureModelEditor";
	
	public ImageDescriptor FEATURE_DESCRIPTION;
	public ImageDescriptor FEATURECONNECTION_DESCRIPTION;
	private boolean needViewerRefreshFlag = true;
	public static ConfigurableFeatureModelEditor instance;
	private static ConfFeatureModel conffeatureModel;
	public static ConfigurableFeatureModelEditor getInstance(){
		if(instance==null)
			instance = new ConfigurableFeatureModelEditor();
		return instance;
	}
	public ConfigurableFeatureModelEditor() {
		Bundle bundle = Platform.getBundle(LoongPluginFMRTool.PLUGIN_ID);
		URL fullPathString = BundleUtility.find(bundle,"icons/feature.jpg");
		FEATURE_DESCRIPTION = ImageDescriptor.createFromURL(fullPathString);
		URL fullConnectionPathString = BundleUtility.find(bundle,"icons/arrow.gif");
		FEATURECONNECTION_DESCRIPTION = ImageDescriptor.createFromURL(fullConnectionPathString);
		setEditDomain(new DefaultEditDomain(this));
		// Open the recommended Feature view
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RecommendedFeatureView.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		getGraphicalViewer().setEditPartFactory(new PartFactory());
		getGraphicalViewer().setContextMenu(new ConfFeatureEditorContextMenuProvider(getGraphicalViewer(), getActionRegistry()));
	}
	
	public EditPartViewer getRecommendedFeatureViewer(){
		RecommendedFeatureView recommendview = RecommendedFeatureView.getInstance();
		return (EditPartViewer)recommendview.getTreeViewer();
	}

	@Override
	protected void initializeGraphicalViewer() {
		IEditorInput input = getEditorInput();
		if(input instanceof IFileEditorInput){
			IFile file = ((IFileEditorInput)input).getFile();
			if(file.exists()){
				try {
					conffeatureModel = (ConfFeatureModel)DiagramSerializer.deserialize(file.getContents());
					if(conffeatureModel==null){
						conffeatureModel = new ConfFeatureModel();
					}
					getGraphicalViewer().setContents(conffeatureModel);
				} catch (UnsupportedEncodingException | CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	}

	
	
	@Override
	public void commandStackChanged(EventObject event) {
		// TODO Auto-generated method stub
		firePropertyChange(PROP_DIRTY);
		super.commandStackChanged(event);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		try {
			IEditorInput input = getEditorInput();
			if(input instanceof IFileEditorInput){
				needViewerRefreshFlag = false;
				IFile file = ((IFileEditorInput)input).getFile();
				file.setContents(DiagramSerializer.serialize((AbstractModel)getGraphicalViewer().getContents().getModel()),
						true,true,monitor);
			}
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
		getCommandStack().markSaveLocation();
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();

		PaletteGroup toolGroup = new PaletteGroup("Tools");
		ToolEntry tool = new SelectionToolEntry();
		toolGroup.add(tool);
		root.setDefaultEntry(tool);
		toolGroup.add(new MarqueeToolEntry());

		PaletteDrawer drawer = new PaletteDrawer("Element");
		CreationToolEntry creationEntry = new CreationToolEntry("Feature",
				"Add a feature to diagram", new SimpleFactory(ConfFeature.class), FEATURE_DESCRIPTION,
				FEATURE_DESCRIPTION);
		drawer.add(creationEntry);
		
		PaletteDrawer relations = new PaletteDrawer("Relations");
		ConnectionCreationToolEntry connectionEntry = new ConnectionCreationToolEntry("Connection",
				"Connect featurs", new SimpleFactory(FeatureConnectionModel.class), FEATURECONNECTION_DESCRIPTION,
				FEATURECONNECTION_DESCRIPTION);
		relations.add(connectionEntry);

		root.add(toolGroup);
		root.add(drawer);
		root.add(relations);
		
		
		return root;
	}

	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			final IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						IFile file = ((IFileEditorInput) input).getFile();
						if (!file.exists()) {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							page.closeEditor(ConfigurableFeatureModelEditor.this, false);
						} else {
							if (!getPartName().equals(file.getName())) {
								setPartName(file.getName());
							}
							if(needViewerRefreshFlag){
								refreshGraphicalViewer();
							} else {
								needViewerRefreshFlag = true;
							}
						}
					}
				});
			}
		}
	}
	
	protected void refreshGraphicalViewer(){
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput) {
			try {
				IFile file = ((IFileEditorInput) input).getFile();
				GraphicalViewer viewer = getGraphicalViewer();

				// desirialize
				AbstractModel newRoot = null;
				try {
					newRoot = DiagramSerializer.deserialize(file.getContents());
				} catch(Exception ex){
					
					return;
				}

				// copy to editing model
				AbstractModel root = (AbstractModel) viewer.getContents().getModel();
				root = newRoot;

			} catch (Exception ex) {
				LoongPlugin.logException(ex);
			}
		}
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return getCommandStack().isDirty();
	}
	@Override
	protected void setInput(IEditorInput input) {
		// TODO Auto-generated method stub
		super.setInput(input);
	}
	
	public ConfFeatureModel getConfFeatureModel(){
		return conffeatureModel;
	}
	
}
