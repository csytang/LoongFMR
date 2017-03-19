package loongpluginfmrtool.toolbox.arc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ARCConfigurationWizardPageODEMDownloader extends WizardPage {

	/**
	 * Create the wizard.
	 */
	private Text remoteurltext;
	private IProject aProject;
	private Map<String,URL>projectToURLs = new HashMap<String,URL>();
	private String baseurl = "http://www.chrisyttang.org/loong_fmr/odem/";
	private String targetFileName = "";
	private Combo predurlcombo;
	private Shell shell;
	private String projectPath;
	private Label lblStatusLabel;
	private static ARCConfigurationWizardPageODEMDownloader instance;
	private ARCConfigurationWizardPageODEMDownloader(IProject pproject,Shell parentShell) {
		super("wizardPage");
		setTitle("Download the odem file for this project, if exists");
		setDescription("We have create some odem files for several subject projects, you can easily download from here.");
		shell = parentShell;
		aProject = pproject;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+aProject.getName().toString();
		try {
			projectToURLs.put("Prevayler", new URL(baseurl+"prevayler.odem"));
			projectToURLs.put("MobileMedia v8", new URL(baseurl+"mobilemediav8.odem"));
			projectToURLs.put("ArgoUML", new URL(baseurl+"argouml.odem"));
			projectToURLs.put("Berkeley DB(java)", new URL(baseurl+"berkerleydbjava.odem"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		targetFileName = projectPath+File.separatorChar+aProject.getName()+".odem";
	}
	public static ARCConfigurationWizardPageODEMDownloader getDefault(IProject pProject,Shell parentShell){
		if(instance==null)
			instance = new ARCConfigurationWizardPageODEMDownloader(pProject,parentShell);
		return instance;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblPredefinedOdemFile = new Label(container, SWT.NONE);
		lblPredefinedOdemFile.setText("Predefined odem file from project page:");
		new Label(container, SWT.NONE);
		
		predurlcombo = new Combo(container, SWT.NONE);
		predurlcombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		predurlcombo.setItems(new String[]{"Not in the scope","Prevayler","MobileMedia v8","ArgoUML","Berkeley DB(java)"});
		predurlcombo.select(0);
		
		new Label(container, SWT.NONE);
		
		Label lblChrisyttangorgloongfmr = new Label(container, SWT.NONE);
		lblChrisyttangorgloongfmr.setText("www.chrisyttang.org/loong_fmr/");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblCurrentProject = new Label(container, SWT.NONE);
		lblCurrentProject.setText("Current project:");
		new Label(container, SWT.NONE);
		
		Label label = new Label(container, SWT.NONE);
		label.setText(aProject.getName());
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblOr = new Label(container, SWT.NONE);
		lblOr.setText("Or");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblDownloadFromOther = new Label(container, SWT.NONE);
		lblDownloadFromOther.setText("Download from other url:");
		new Label(container, SWT.NONE);
		
		remoteurltext = new Text(container, SWT.BORDER);
		remoteurltext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnDownload = new Button(container, SWT.NONE);
		btnDownload.setText("Download");
		btnDownload.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				File odemfile = new File(targetFileName);
				// nothing selected
				if((predurlcombo.getSelectionIndex()==0||predurlcombo.getSelectionIndex()==-1)&&
						remoteurltext.getText().trim().equals("")){
					return;// do nothing
				}
				
				// if predefined url is selected
				if(predurlcombo.getSelectionIndex()>0){
					if(remoteurltext.getText().trim().equals("")){
						URL targeturl = null;
						switch(predurlcombo.getSelectionIndex()){
						case 1:{
							targeturl = projectToURLs.get("Prevayler");
							break;
						}
						case 2:{
							targeturl = projectToURLs.get("MobileMedia v8");
							break;
						}
						case 3:{
							targeturl = projectToURLs.get("ArgoUML");
							break;
						}
						case 4:{
							targeturl = projectToURLs.get("Berkeley DB(java)");
							break;
						}
						}
						try {
							FileUtils.copyURLToFile(targeturl, odemfile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ARCConfigurationWizardPageConfig.setODEMLocation(odemfile.getAbsolutePath());
						lblStatusLabel.setText("Finish Download");
					}else{
						// return an error
						Display.getCurrent().syncExec(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
								"Both predefined url and remote url are set");
							}
					    	
					    });
					}
				}else if(!remoteurltext.getText().trim().equals("")){
					if(!remoteurltext.getText().trim().endsWith(".odem")){
						Display.getCurrent().syncExec(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
								"The remote url is not a valid url");
							}
					    	
					    });
					}
					try {
						URL remoteurl = new URL(remoteurltext.getText().trim());
						FileUtils.copyURLToFile(remoteurl, odemfile);
						ARCConfigurationWizardPageConfig.setODEMLocation(odemfile.getAbsolutePath());
						lblStatusLabel.setText("Finish Download");
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						Display.getCurrent().syncExec(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
								"The remote url is not a valid url");
							}
					    	
					    });
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		});
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		lblStatusLabel = new Label(container, SWT.NONE);
		lblStatusLabel.setText("Status");
	}

}
