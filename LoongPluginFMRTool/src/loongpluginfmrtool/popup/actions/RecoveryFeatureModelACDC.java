package loongpluginfmrtool.popup.actions;

import java.util.Iterator;

import loongplugin.source.database.ApplicationObserver;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.toolbox.acdc.ACDCConfigurationDialog;
import loongpluginfmrtool.views.moduleviews.ModuleViewPart.ModuleModelChangeListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
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

public class RecoveryFeatureModelACDC implements IObjectActionDelegate {

	private IStructuredSelection aSelection;
	private IProject aProject;
	private Shell shell;
	private IWorkbenchPart part;
	private ApplicationObserver lDB;
	
	public RecoveryFeatureModelACDC() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		
		aProject = getSelectedProject();
		WorkspaceJob op = null;
		// ProgramDB 没有被初始化
		lDB = ApplicationObserver.getInstance();
		if(!this.lDB.isInitialized(aProject)){
			
			Display.getCurrent().syncExec(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
					"Please create the programDB first.");
				}
		    	
		    });
			
		}else{
			
	        //.out.println("Finish Module Helper");
			ACDCConfigurationDialog dialog = new ACDCConfigurationDialog(shell,aProject);
			dialog.create();
			dialog.open();
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
