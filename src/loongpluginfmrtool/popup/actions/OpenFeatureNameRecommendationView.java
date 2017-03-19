package loongpluginfmrtool.popup.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class OpenFeatureNameRecommendationView implements IObjectActionDelegate{

	private IProject aProject;
	private IStructuredSelection aSelection;
	private Shell shell;
	private IWorkbenchPart part;
	
	public OpenFeatureNameRecommendationView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		aProject = getSelectedProject();
		
		IFile tempfeaturemodelfile = aProject.getFile("configfeaturemodel.mconfig");
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    try {
			IDE.openEditor(page, tempfeaturemodelfile);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // 3. pop-up a menu to guide user to drag project to view
	    Display.getCurrent().syncExec(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
				"To create a recommend list for feature name, you can simply drag the project or some packages into the recommend feature list view.");
			}
	    	
	    });
	    
	    
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
