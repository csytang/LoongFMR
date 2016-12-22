package loongpluginfmrtool.popup.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFileChooser;

import loongplugin.source.database.ApplicationObserver;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;
import loongpluginfmrtool.module.model.module.ModuleCallDependencyTable;
import loongpluginfmrtool.util.MDGFileConfig;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CreateMDGFile implements IObjectActionDelegate{
	
	/**
	 * this script will create the mdg graph for the application
	 */
	private IStructuredSelection aSelection;
	private IProject aProject;
	private Shell shell;
	private IWorkbenchPart part;
	private ApplicationObserver lDB;
	private ModuleBuilder mbuilder;
	private Map<Integer,Module> indexToModule = new HashMap<Integer,Module>();
	private ModuleCallDependencyTable dependency_table;
	private int[][] table;
	
	public CreateMDGFile() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		aProject = getSelectedProject();
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
			mbuilder = ModuleBuilder.getInstance(aProject, lDB);
			mbuilder.init(false);
			
			//mbuilder.buildModuleHelper();
			indexToModule = mbuilder.getIndexToModule();
			dependency_table = mbuilder.getDependencyTable();
			
			
		}
		
		
		// TODO Auto-generated method stub
		MDGFileConfig config = new MDGFileConfig(shell, aProject, mbuilder, indexToModule, dependency_table, table);
		config.create();
		config.open();
		
		 
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
