package loongpluginfmrtool.popup.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import loongpluginfmrtool.util.FeatureCounter;

public class FeatureCountAction implements IObjectActionDelegate {

	
	private IProject aProject;
	private IWorkbenchPart part;
	private final List<IResource> resources = new ArrayList<IResource>();
	private IResource targetResource =  null;
	
	public FeatureCountAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		assert !resources.isEmpty();
		aProject = resources.get(0).getProject();
		
		// Process unsupported file selections from multiple projects
		for (IResource r : resources) {
			if (r.getProject() != aProject) {
				MessageBox messageBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.OK);
				messageBox
						.setText("Unsupported selection. Select resources from a single project only.");
				messageBox.open();
				return;
			}
		}
		
		
		// Only one file is allowed;
		targetResource = resources.get(0);
		
		// Check whether is file is allowed in feature count context;
		// 1. should in .rsf format
		if(targetResource instanceof IFile){
			IFile file = (IFile)targetResource;
			if(file.getFileExtension().equals("rsf")){
				File localfile = file.getLocation().toFile();
				FeatureCounter counter = new FeatureCounter(localfile);
			}else{
				MessageBox messageBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.OK);
				messageBox.setText("Error");
				messageBox.setMessage("The file should be a rsf file rather than "+file.getFileExtension());
				messageBox.open();
				return;
			}
			
		}else{
			MessageBox messageBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.OK);
			messageBox.setText("Error");
			messageBox.setMessage("A single file should be selected, No a single file error.");
			messageBox.open();
			return;
		}
		
		
		
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		resources.clear();
		if (selection instanceof IStructuredSelection) {
			for (Object selected : ((IStructuredSelection) selection).toArray()) {
				if (selected instanceof IResource) {
					resources.add((IResource) selected);
				}

			}
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		this.part = targetPart;
	}

}
