package loongpluginfmrtool.views.moduleviews;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import loongplugin.LoongPlugin;
import loongplugin.editor.CLREditor;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.views.astview.EditorUtility;
import loongpluginfmrtool.module.model.configuration.ConfigurationOption;
import loongpluginfmrtool.module.model.configuration.ConfigurationRelationLink;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.module.model.module.ModuleComponent;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class ModuleViewPart extends ViewPart {
/**
 * Module - id
 * 	-Import
 *  -Configuration Operation
 *  -Variability
 *  -Function and Variables
 *  ----Assess Matter----
 *  -In deep
 *  -Number of Valid Configurations
 *  -Number of Configuration Options
 *  -Scope of Affected.
 */
	
	private ModuleModel moduleModel = new ModuleModel();
	private TreeViewer fViewer;	
	private Tree tree;
	private String[]columnNames={"name","attribute_1","attribute_2","attribute_3","attribute_4","attribute_5"};
	private IProject selectedProject=null;
	public static ModuleViewPart instance;
	private ModuleModelChangeListener listener;
	public static final String ID = LoongPlugin.PLUGIN_ID+".FMRTool.ModuleView";
	public static ModuleViewPart getInstance(){
		if(instance==null)
			instance = new ModuleViewPart();
		return instance;
	}
	
	public ModuleViewPart() {
		// TODO Auto-generated constructor stub
		instance = this;
		selectedProject = getSelectedProject();
		if(selectedProject==null){
			// Obtain the selectedProject from active editor
			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(activeEditor==null)
				return;
			IEditorInput input = activeEditor.getEditorInput();
			if (input instanceof IFileEditorInput){
				IFileEditorInput editorinput = (IFileEditorInput)input;
				selectedProject = editorinput.getFile().getProject();
			}
		}
		listener = new ModuleModelChangeListener();
	}
	
	public ModuleModelChangeListener getModuleListener(){
		return listener;
	}
	public void resetModuleListener(){
		this.listener  = new ModuleModelChangeListener();
	}
	
	private IProject getSelectedProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
	    ISelection selection = selectionService.getSelection();    
	    IProject project = null; 
	    if(selection==null)
	    	return null;
		Object element = ((IStructuredSelection)selection).getFirstElement();    

        if (element instanceof IResource) {    
            project= ((IResource)element).getProject();    
        } else if (element instanceof PackageFragmentRootContainer) {    
            IJavaProject jProject =  ((PackageFragmentRootContainer)element).getJavaProject();    
            project = jProject.getProject();    
        } else if (element instanceof IJavaElement) {    
            IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
            project = jProject.getProject();    
        }    
        return project;
	}
	
	public TreeViewer getTreeViewer(){
		return fViewer;
	}
	

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		createTree(parent);
		createTableViewer();
		addTableCellClick();
	}
	
	private void addTableCellClick() {
		// TODO Auto-generated method stub
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
		    @Override
		    public void doubleClick(DoubleClickEvent event) {
		        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		        Object selectedObject = selection.getFirstElement();
		        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		        IWorkbenchPage page = window.getActivePage();
		        if(selectedObject instanceof Module){
		        	Module module = (Module)selectedObject;
		        	IFile file = module.getIFile();
		        	try {
						IDE.openEditor(page, file, CLREditor.ID);
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }else if(selectedObject instanceof ModuleComponent){
		        	ModuleComponent moduleComponent = (ModuleComponent)selectedObject;
		        	if(moduleComponent instanceof ConfigurationOption){
		        		ConfigurationOption option = (ConfigurationOption)moduleComponent;
		        		Module module = option.getParent();
		        		Expression expression = option.getConfigurationCondition().getFirstExpression();
		        		IFile file = module.getIFile();
		        		try {
		        			IEditorPart editpart = IDE.openEditor(page, file, CLREditor.ID);
		        			EditorUtility.selectInEditor((ITextEditor)editpart, expression.getStartPosition(), expression.getLength());
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}
		        	
		        }else if(selectedObject instanceof ConfigurationRelationLink){
		        	ConfigurationRelationLink link = (ConfigurationRelationLink)selectedObject;
		        	
	        		ConfigurationOption target_option = link.getTargetConfigurationOption();
	        		Module target_module = target_option.getParent();
	        		Expression target_expression = target_option.getConfigurationCondition().getFirstExpression();
	        		IFile target_file = target_module.getIFile();
	        		try {
	        			IEditorPart editpart = IDE.openEditor(page, target_file, CLREditor.ID);
	        			EditorUtility.selectInEditor((ITextEditor)editpart, target_expression.getStartPosition(), target_expression.getLength());
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		
		        }else{
		        	return;
		        }
		    }
		});
	}

	private void createTree(Composite parent) {
		tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);

		TreeColumn column;
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("Module/Config");
		column.setWidth(120);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("General");
		column.setWidth(100);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("Links To Module");
		column.setWidth(100);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("InVarLevel(#)");
		column.setWidth(100);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("ExVarLevel(#)");
		column.setWidth(100);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("OverallVarLevel(#)");
		column.setWidth(100);
		// Pack the columns
	    for (int i = 0, n = tree.getColumnCount(); i < n; i++) {
	    	tree.getColumn(i).pack();
	    }
	    
	    tree.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				int width = tree.getClientArea().width;
				if (width > 200) {
					tree.getColumn(0).setWidth(width - 60);
					tree.getColumn(1).setWidth(80);
				} else {
					tree.getColumn(0).setWidth(width / 2);
					tree.getColumn(1).setWidth(width / 2);
				}
			}
		});
	}
	
	private void createTableViewer() {
		fViewer = new TreeViewer(tree);
		fViewer.setColumnProperties(columnNames);
		fViewer.setLabelProvider(new ModuleLabelProvider());
		fViewer.setContentProvider(new ModuleContentProvider());
		fViewer.setInput(moduleModel);
		fViewer.expandAll();
	}
	
	
	
	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		fViewer.getControl().setFocus();
	}

	private void redraw(){
		
		tree.setRedraw(false);
		fViewer.setInput(moduleModel);
		fViewer.expandAll();
		tree.setRedraw(true);
		
	}
	
	public class ModuleModelChangeListener implements IModuleModelChangeListener{

		@Override
		public void moduleModelChanged(moduleModelChangedEvent event) {
			// TODO Auto-generated method stub
			moduleModel = event.getModuleModel();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						redraw();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});	
		}
		
	}
}
