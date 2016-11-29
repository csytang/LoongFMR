package loongpluginfmrtool.views.recommendedfeatureview;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import loongplugin.LoongImages;
import loongplugin.LoongPlugin;
import loongplugin.views.astview.views.ASTAttribute;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeatureModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.serializer.DiagramSerializer;
import loongpluginfmrtool.editor.configfeaturemodeleditor.ui.ConfigurableFeatureModelEditor;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.SWT;
import org.osgi.framework.Bundle;
import org.eclipse.swt.dnd.DragSource;


public class RecommendedFeatureView extends ViewPart {

	public static final String ID = LoongPlugin.PLUGIN_ID+".FMRTool.recommendedFeatureList";
	private TreeViewer fViewer;
	
	private Tree tree;
	private RSFeatureModel rsfeatureModel = new RSFeatureModel();
	private List<IJavaElement> allJavaElements;
	public static RecommendedFeatureView instance;
	private String[]columnNames={"properties","value"};
	//private Action exportsToEditors;
	private IProject selectedProject=null;
	private RSFeatureModelChangeListener featuremodelListener;
	private Map<ConfFeature,RSFeature>confToRSFeature = new HashMap<ConfFeature,RSFeature>();
	
	public static RecommendedFeatureView getInstance(){
		if(instance==null)
			instance = new RecommendedFeatureView();
		return instance;
	}
	
	public RSFeatureModel getRSFeatureModel(){
		return rsfeatureModel;
	}
	public Map<ConfFeature,RSFeature> getConfToRSFeature(){
		return confToRSFeature;
	}
	public RecommendedFeatureView() {
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
		featuremodelListener = new RSFeatureModelChangeListener();
		
	}
	
	public TreeViewer getTreeViewer(){
		return fViewer;
	}
	
	public void setIJavaElementsToResolve(List<IJavaElement> pallJavaElements){
		this.allJavaElements = pallJavaElements;
	}

	@Override
	public void createPartControl(Composite parent) {
		createTree(parent);
		createTableViewer();
	}
	
	
	

	public RSFeatureModelChangeListener getFeatureModelListener(){
		if(featuremodelListener==null)
			featuremodelListener = new RSFeatureModelChangeListener();
		return featuremodelListener;
	}
	
	private void createTableViewer() {
		fViewer = new TreeViewer(tree);
		fViewer.setColumnProperties(columnNames);
		// add drop support to fViewer
		int ops = DND.DROP_COPY|DND.DROP_LINK;
		Transfer[] transfers = new Transfer[]{LocalSelectionTransfer.getTransfer()};
		fViewer.addDropSupport(ops, transfers, new RecommendTreeDropAdapter(fViewer));
		fViewer.setLabelProvider(new RecommendedFeatureNameLabelProvider());
		fViewer.setContentProvider(new RecommendedFeatureContentProvider());
		fViewer.setInput(rsfeatureModel);
		fViewer.expandAll();
	}
	private void createTree(Composite parent) {
		tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);

		TreeColumn column;
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("FeatureName_Properties");
		column.setWidth(120);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("Value");
		column.setWidth(80);

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
	
	class RecommendedFeatureContentProvider implements ITreeContentProvider{

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			if(parentElement instanceof RSFeatureModel){
				RSFeatureModel featuremodel = (RSFeatureModel)parentElement;
				return featuremodel.getFeatures().toArray();
			}
			else if(parentElement instanceof RSFeature){
				RSFeature rselement = (RSFeature)parentElement;
				return rselement.getChildren().toArray();
			}else if(parentElement instanceof IJavaElementWrapper){
				IJavaElementWrapper ijavaparentElement = (IJavaElementWrapper)parentElement;
				return ijavaparentElement.getChildren().toArray();
			}else if(parentElement instanceof ASTNodeWrapper){
				return null;
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			if(element instanceof RSFeature){
				return null;
			}else if(element instanceof IJavaElementWrapper){
				IJavaElementWrapper ijavaparentElement = (IJavaElementWrapper)element;
				return ijavaparentElement.getParent();
			}else if(element instanceof ASTNodeWrapper){
				ASTNodeWrapper astwrapper = (ASTNodeWrapper)element;
				return astwrapper.getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			if(element instanceof RSFeature){
				RSFeature rselement = (RSFeature)element;
				return rselement.getChildren().size()>0;
			}else if(element instanceof IJavaElementWrapper){
				IJavaElementWrapper ijavaparentElement = (IJavaElementWrapper)element;
				return ijavaparentElement.getChildren().size()>0;
			}else if(element instanceof ASTNodeWrapper){
				return false;
			}
			return false;
		}
		
	}
	
	
	class RecommendedFeatureNameLabelProvider implements ITableLabelProvider{
	
		
		public RecommendedFeatureNameLabelProvider(){
			//this.featureImage = pfeatureImage;
		}
		
		
		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			
			switch(columnIndex){
				case 0:{
					if(element instanceof RSFeature){
						return LoongImages.getImage(LoongImages.FEATURE);
					}else if(element instanceof ASTNodeWrapper){
						ASTNodeWrapper wrapper = (ASTNodeWrapper)element;
						ASTNode node = wrapper.getASTNode();
						if(node instanceof MethodDeclaration){
							return LoongImages.getImage(LoongImages.METHOD_DEF);
						}else if(node instanceof FieldDeclaration){
							return LoongImages.getImage(LoongImages.FIELD_DEFAULT_OBJ);
						}else if(node instanceof CompilationUnit){
							return LoongImages.getImage(LoongImages.JAVA_OBJ);
						}else if(node instanceof ImportDeclaration){
							return LoongImages.getImage(LoongImages.IMPORT_CO);
						}else if(node instanceof SingleVariableDeclaration||
								node instanceof VariableDeclarationStatement||
								node instanceof VariableDeclarationFragment||
								node instanceof VariableDeclarationExpression){
							return LoongImages.getImage(LoongImages.LOCAL_OBJ);
						}else if(node instanceof PackageDeclaration){
							return  LoongImages.getImage(LoongImages.PACKAGE_OBJ);
						}else if(node instanceof TypeDeclaration||
								node instanceof TypeDeclarationStatement){
							return  LoongImages.getImage(LoongImages.TYPES);
						}else
						
							return null;
					}else if(element instanceof IJavaElementWrapper){
						return  LoongImages.getImage(LoongImages.JAVA_OBJ);
					}
				}
			}
			return null;
		}
		@Override
		public String getColumnText(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			switch(columnIndex){
				case 0:{ 
					if(element instanceof RSFeature){
						RSFeature rsfeature = (RSFeature)element;
						return rsfeature.getFeatureName();
					}else if(element instanceof ASTNodeWrapper){
						return "ASTNode";
					}else if(element instanceof IJavaElementWrapper){
						return "IJavaElement";
					}
				}
				case 1:{
					if(element instanceof RSFeature){
						return ((RSFeature)element).getWeight()+"";
					}else if(element instanceof ASTNodeWrapper){
						return getText(((ASTNodeWrapper)element).getASTNode());
					}else if(element instanceof IJavaElementWrapper){
						return ((IJavaElementWrapper)element).getIJavaElement().getElementName();
					}
				}
			}
			return null;
		}
		
	}
	
	private ImageDescriptor createImageDescriptor() {
		Bundle bundle = Platform.getBundle(LoongPlugin.PLUGIN_ID);
		URL fullPathString = BundleUtility.find(bundle,"icons/feature.jpg");
		return ImageDescriptor.createFromURL(fullPathString);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		fViewer.getControl().setFocus();
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
	
	class RecommendTreeDropAdapter extends ViewerDropAdapter {
		
		private List<IJavaElement> allJavaElements;
		protected RecommendTreeDropAdapter(TreeViewer fViewer) {
			super(fViewer);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean performDrop(Object data) {
			// TODO Auto-generated method stub
			IStructuredSelection selection = (IStructuredSelection) data;
			allJavaElements = new ArrayList<IJavaElement>();
			for(Iterator<?> ite = selection.iterator(); ite.hasNext();){
				Object element = ite.next();
				if(element instanceof IJavaElement){
					allJavaElements.add((IJavaElement) element);
				}
			}
			//Create a job for this update
			RecommendFeatureNameJob job;
			
			if(selectedProject!=null)
				job = new RecommendFeatureNameJob(allJavaElements,selectedProject,RecommendedFeatureView.getInstance().getFeatureModelListener());
			else{
				selectedProject = allJavaElements.get(0).getJavaProject().getProject();
				job = new RecommendFeatureNameJob(allJavaElements,allJavaElements.get(0).getJavaProject().getProject(),RecommendedFeatureView.getInstance().getFeatureModelListener());
			}
			job.setUser(true);
			job.setPriority(Job.LONG);
			job.schedule();
			try {
				job.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return true;
		}
		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			// TODO Auto-generated method stub
			if(LocalSelectionTransfer.getTransfer().isSupportedType(transferType)){
				return true;
			}else
				return false;
		}
		@Override
		public void dropAccept(DropTargetEvent event) {
			// TODO Auto-generated method stub
			event.detail = DND.DROP_COPY;
		}
		@Override
		public void dragOver(DropTargetEvent event) {
			// TODO Auto-generated method stub
			event.detail = DND.DROP_COPY;
		}
		
		
		
	}
	
	private void redraw(){
		
		tree.setRedraw(false);
		fViewer.setInput(rsfeatureModel);
		fViewer.expandAll();
		tree.setRedraw(true);
		
	}

	class RSFeatureModelChangeListener implements IRSFeatureModelChangeListener{

		@Override
		public void featureModelChanged(RSFeatureModelChangedEvent event) {
			// TODO Auto-generated method stub
			rsfeatureModel = event.getFeatureModel();
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
	
	public String getText(Object obj) {
		StringBuffer buf= new StringBuffer();
		if (obj instanceof ASTNode) {
			getNodeType((ASTNode) obj, buf);
		} else if (obj instanceof ASTAttribute) {
			buf.append(((ASTAttribute) obj).getLabel());
		}
		return buf.toString(); 
	}
	
	private void getNodeType(ASTNode node, StringBuffer buf) {
		buf.append(Signature.getSimpleName(node.getClass().getName()));
		buf.append(" ["); //$NON-NLS-1$
		buf.append(node.getStartPosition());
		buf.append(", "); //$NON-NLS-1$
		buf.append(node.getLength());
		buf.append(']');
		if ((node.getFlags() & ASTNode.MALFORMED) != 0) {
			buf.append(" (malformed)"); //$NON-NLS-1$
		}
		if ((node.getFlags() & ASTNode.RECOVERED) != 0) {
			buf.append(" (recovered)"); //$NON-NLS-1$
		}
	}
}
