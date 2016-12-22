package loongpluginfmrtool.views.moduledependencyviews;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;


public class ModuleDependencyViewPart extends ViewPart implements IZoomableWorkbenchPart {

	
	private static final String ID = "LoongPlugin.FMRTool.moduledependency";
	private GraphViewer graphViewer;
	public static ModuleDependencyViewPart instance;
	private Composite aparent;
	private HierarchicalBuilder builder=null;
	private HierarchicalBuilderChangeListener listener = new HierarchicalBuilderChangeListener();
	
	public ModuleDependencyViewPart(){
		super();
	}
	public static ModuleDependencyViewPart getInstance(){
		if(instance==null)
			instance = new ModuleDependencyViewPart();
		return instance;
	}

	@Override
	public void createPartControl(Composite parent) {
		this.aparent = parent;
		
		graphViewer = new GraphViewer(parent, SWT.BORDER);
		
		// 查看Editor 如果 editor 没有 则查看选择按钮就
		LayoutAlgorithm layout = setLayout();
		graphViewer.setLayoutAlgorithm(layout, true);
		graphViewer.applyLayout();
		graphViewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		if(builder!=null){
			graphViewer.setInput(builder);
			graphViewer.setLabelProvider(new ModuleDependencyLabelProvider());
			graphViewer.setContentProvider(new ModuleDependencyContentProvider(builder));
		}
		
		fillToolBar();
		
	}

	private LayoutAlgorithm setLayout() {
	    LayoutAlgorithm layout;
	    layout = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	    return layout;

	  }
	
	@Override
	public void setFocus() {
		graphViewer.getControl().setFocus();
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		// TODO Auto-generated method stub
		return graphViewer;
	}

	
	public void redraw(HierarchicalBuilder builder,Shell shell){
		if(graphViewer==null){
			if(aparent==null)
				aparent = shell;
			graphViewer = new GraphViewer(aparent, SWT.BORDER);
			graphViewer.setLabelProvider(new ModuleDependencyLabelProvider());
			graphViewer.setContentProvider(new ModuleDependencyContentProvider(builder));
			// 查看Editor 如果 editor 没有 则查看选择按钮就
			LayoutAlgorithm layout = setLayout();
			graphViewer.setLayoutAlgorithm(layout, true);
			graphViewer.applyLayout();
			graphViewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
			graphViewer.setInput(builder);
		    
		   
		}else{
			graphViewer.setInput(builder);
		}
		graphViewer.refresh();
	}
	
	private void fillToolBar(){
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);
	    IActionBars bars = getViewSite().getActionBars();
	    bars.getMenuManager().add(toolbarZoomContributionViewItem);
	}
	
	
	class HierarchicalBuilderChangeListener implements IHierarchicalBuilderChangeListener{

		@Override
		public void hierarchicalBuilderChanged(HierarchicalBuilderChangedEvent event,Shell shell) {
			// TODO Auto-generated method stub
			builder = event.getUpdatedBuilder();
			
			ModuleDependencyViewPart.getInstance().redraw(builder,shell);
		}
		
	}

	public IHierarchicalBuilderChangeListener gethierarchicalBuildereListener() {
		// TODO Auto-generated method stub
		return listener;
	}

	public void setModuleBuilder(HierarchicalBuilder hbuilder,Shell shell) {
		// TODO Auto-generated method stub
		this.builder = hbuilder;
		
		
		try {
			
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ModuleDependencyViewPart.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.builder.notifyFeatureModelListener(shell);
	}

	
}
