package loongpluginfmrtool.popup.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import loongplugin.source.database.ApplicationObserver;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.module.ModuleBuilder;

public class BuildModuleDependencyAction implements IObjectActionDelegate{

	private IProject aProject;
	private IStructuredSelection aSelection;
	private Shell shell;
	private IWorkbenchPart part;
	private ApplicationObserver lDB;
	private ModuleBuilder mbuilder;
	
	public BuildModuleDependencyAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		aProject = getSelectedProject();
		lDB = ApplicationObserver.getInstance();		
		if(lDB.isInitialized(aProject)){
			mbuilder = ModuleBuilder.getInstance(aProject, lDB);
			if(mbuilder!=null){
				HierarchicalBuilder hbuilder = new HierarchicalBuilder(mbuilder,lDB.getLFlyweightElementFactory());
				hbuilder.writetoxml();
			}else{
				Display.getCurrent().syncExec(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
						"Please build the # variability module first.");
					}
			    	
			    });
			}
		}else{
			Display.getCurrent().syncExec(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
					"Please create the programDB first.");
				}
		    	
		    });
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		if (selection instanceof IStructuredSelection)
			aSelection = (IStructuredSelection) selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		this.part = targetPart;
		shell = targetPart.getSite().getShell();
	}

	private IProject getSelectedProject() {
		IProject lReturn = null;
		Iterator i = aSelection.iterator();
		if (i.hasNext()) {
			Object lNext = i.next();
			if (lNext instanceof IResource) {
				lReturn = ((IResource) lNext).getProject();
			} else if (lNext instanceof IJavaElement) {
				IJavaProject lProject = ((IJavaElement) lNext).getJavaProject();
				lReturn = lProject.getProject();
			}
		}
		return lReturn;
	
	}
}
