package loongpluginfmrtool.toolbox.acdc;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.usc.softarch.arcade.util.StopWatch;

import org.eclipse.swt.widgets.Button;

public class ACDCConfigurationDialog extends TitleAreaDialog {
	private Text sourcetext;
	private IProject aProject;
	private Text targettext;
	private Shell shell;
	private static boolean isBenabled = false;
	private static boolean isSenabled = false;
	private static boolean isOenabled = false;
	private Text clustertext;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ACDCConfigurationDialog(Shell parentShell,IProject pProject) {
		
		super(parentShell);
		shell = parentShell;
		aProject = pProject;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		ACDCConfigurationDialogCheckboxListener listener[] = new ACDCConfigurationDialogCheckboxListener[3];
		
		setTitle("Please input the configuration argument for ACDC clustering");
		parent.setToolTipText("");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblAcdcArgument = new Label(container, SWT.NONE);
		lblAcdcArgument.setBounds(10, 10, 430, 14);
		lblAcdcArgument.setText("ACDC Source File(.ta or .rsf): *Emtpy shows the default");
		
		sourcetext = new Text(container, SWT.BORDER);
		sourcetext.setBounds(10, 30, 430, 19);
		
		Label lblAcdcTargetFilersf = new Label(container, SWT.NONE);
		lblAcdcTargetFilersf.setBounds(10, 52, 430, 14);
		lblAcdcTargetFilersf.setText("ACDC target File(.rsf): *Empty shows the default");
		
		targettext = new Text(container, SWT.BORDER);
		targettext.setBounds(10, 70, 430, 19);
		
		Button btnCheckButton_b = new Button(container, SWT.CHECK);
		btnCheckButton_b.setBounds(10, 118, 126, 18);
		btnCheckButton_b.setText("B (Body Header)");
		listener[0] = new ACDCConfigurationDialogCheckboxListener("b");
		btnCheckButton_b.addSelectionListener(listener[0]);
		
		
		Button btnCheckButton_s = new Button(container, SWT.CHECK);
		btnCheckButton_s.setBounds(142, 118, 151, 18);
		btnCheckButton_s.setText("S (SubGraph Dominator)");
		listener[1] = new ACDCConfigurationDialogCheckboxListener("s");
		btnCheckButton_s.addSelectionListener(listener[1]);
		
		
		Button btnCheckButton_o = new Button(container, SWT.CHECK);
		btnCheckButton_o.setBounds(310, 118, 130, 18);
		btnCheckButton_o.setText("O (Orphan Adoption)");
		listener[2] = new ACDCConfigurationDialogCheckboxListener("o");
		btnCheckButton_o.addSelectionListener(listener[2]);
		
		
		Label lblAcdcConfigurationOptions = new Label(container, SWT.NONE);
		lblAcdcConfigurationOptions.setBounds(10, 98, 430, 14);
		lblAcdcConfigurationOptions.setText("ACDC configuration options:");
		
		Label lblAcdcClusterSize = new Label(container, SWT.NONE);
		lblAcdcClusterSize.setBounds(10, 142, 430, 14);
		lblAcdcClusterSize.setText("ACDC cluster size:");
		
		clustertext = new Text(container, SWT.BORDER);
		clustertext.setBounds(10, 162, 111, 19);
		
		

		return area;
	}
	
	

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		String clusterstr = clustertext.getText();
		int cluster;
		try{
			cluster = Integer.parseInt(clusterstr);
		}catch(Exception e){
			Display.getCurrent().syncExec(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
					"Please intput a number in cluster count option");
				}
		    	
		    });
			super.okPressed();
			return;
		}
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		String projectPath = workspace.getRoot().getLocation().toOSString();
		String sourcefilepath = this.sourcetext.getText();
		sourcefilepath = sourcefilepath.trim();
		String targetfilepath = this.targettext.getText();
		List<String>argulist = new LinkedList<String>();
		if(sourcefilepath.equals("")){
			IFile file = aProject.getFile("data"+File.separatorChar+aProject.getName()+"_deps.rsf");
			if(!file.exists()){
				Display.getCurrent().syncExec(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
						"Cannot find the intput file for acdc under project:"+aProject.getName().toString());
					}
			    	
			    });
				super.okPressed();
				return;
			}
			IPath path = file.getFullPath();
			path = path.makeAbsolute();
			String fullsourcePath = projectPath+path.toOSString();
			argulist.add(fullsourcePath);
		}else{
			argulist.add(sourcefilepath);
		}
		
		if(targetfilepath.equals("")){
			IFile file = aProject.getFile("acdc_clusteringresult.rsf");
			IPath path = file.getFullPath();
			path = path.makeAbsolute();
			String fulltargetPath = projectPath+path.toOSString();
			argulist.add(fulltargetPath);
		}else{
			argulist.add(targetfilepath);
		}
		
		// patterns
		String patterns = "";
		if(isBenabled){
			patterns = patterns+"b";
		}
		if(isSenabled){
			patterns = patterns+"s";
		}
		if(isOenabled){
			patterns = patterns+"o";
		}
		if(patterns.trim().equals("")){
			patterns = "bso";
		}
		patterns = "+"+patterns;
		
		argulist.add(patterns);
		
		// options
		
		argulist.add("-d1");
		
		argulist.add("-l"+cluster);
		
		String[] args = new String[argulist.size()];
		
		for(int i = 0; i < argulist.size();i++){
			args[i] = argulist.get(i);
		}
		
		@SuppressWarnings("unused")
		StopWatch stopwatch = new StopWatch();

		stopwatch.start();
		
		ACDC acdc = new ACDC(args);
		
		stopwatch.stop();

		// Statistics
		String timeInSecsToComputeClusters = "Time in seconds to compute clusters: "
				+ stopwatch.getElapsedTimeSecs();
		String timeInMilliSecondsToComputeClusters = "Time in milliseconds to compute clusters: "
				+ stopwatch.getElapsedTime();
		System.out.println(timeInSecsToComputeClusters);
		System.out.println(timeInMilliSecondsToComputeClusters);
		System.out.println("Finish clustering");
		
		super.okPressed();
		
		
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(529, 346);
	}
	
	
	public static void setBodyHeaderEnabled(){
		ACDCConfigurationDialog.isBenabled = true;
	}
	
	public static void setBodyHeaderDisabled(){
		ACDCConfigurationDialog.isBenabled = false;
	}
	
	public static void setSubGraphDominatorEnabled(){
		ACDCConfigurationDialog.isSenabled = true;
	}
	
	public static void setSubGraphDominatorDisabled(){
		ACDCConfigurationDialog.isSenabled = false;
	}
	
	public static void setOrphanAdoptionEnabled(){
		ACDCConfigurationDialog.isOenabled = true;
	}
	
	public static void setOrphanAdoptionDisabled(){
		ACDCConfigurationDialog.isOenabled = false;
	}
	
}
