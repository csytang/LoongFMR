package loongpluginfmrtool.newWizard;

import loongplugin.LoongPlugin;
import loongpluginfmrtool.newWizard.NewMConfigFileWizardPage;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class MConfigFileWizard extends Wizard implements INewWizard {

	private ISelection selection;
	private NewMConfigFileWizardPage mconfigPage;
	
	public MConfigFileWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		IFile file = mconfigPage.createNewFile();
		if(file==null){
			return false;
		}
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, file, true);
		} catch(PartInitException ex){
			LoongPlugin.logException(ex);
			return false;
		}
		return true;
	}

	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		mconfigPage = new NewMConfigFileWizardPage(this.selection);
		addPage(mconfigPage);
	}
	
	

	
}
