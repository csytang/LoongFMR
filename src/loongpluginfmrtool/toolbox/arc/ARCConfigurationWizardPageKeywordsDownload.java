package loongpluginfmrtool.toolbox.arc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Listener;

public class ARCConfigurationWizardPageKeywordsDownload extends WizardPage {
	private Text localFileTextContent;
	private ARCConfigurationWizardKeywordsListener[] listeners = new ARCConfigurationWizardKeywordsListener[2];
	private Button btnAddLocalButton;
	private Button btnLocalUpload;
	private Button btnDownloadOnline;
	private IProject aProject;
	private Shell shell;
	private String baseurl = "http://www.chrisyttang.org/loong_fmr/res/javakeywords";
	private String projectPath = "";
	private String targetFilePath = "";
	private Label lblStatus;
	private static ARCConfigurationWizardPageKeywordsDownload instance;
	/**
	 * Create the wizard.
	 */
	private ARCConfigurationWizardPageKeywordsDownload(IProject pProject,Shell parentShell) {
		super("wizardPage");
		setTitle("Download Keywords for Project");
		setDescription("");
		aProject = pProject;
		shell = parentShell;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+aProject.getName().toString();
		targetFilePath = projectPath+File.separatorChar+"res"+File.separatorChar+"javakeywords";
				
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(6, false));
		
		Label lblDownloadKeywordsFor = new Label(container, SWT.NONE);
		lblDownloadKeywordsFor.setText("Download Keywords for setting this project:");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Group group = new Group(container, SWT.NONE);
		
		Button btnOnlineDownload = new Button(group, SWT.RADIO);
		btnOnlineDownload.setBounds(10, 10, 279, 18);
		btnOnlineDownload.setText("Download from the project webpage");
		listeners[0] = new ARCConfigurationWizardKeywordsListener(this,DataLoadMode.online);
		btnOnlineDownload.addSelectionListener(listeners[0]);
		btnOnlineDownload.setSelection(true);
		
		Button btnOfflineUpload = new Button(group, SWT.RADIO);
		btnOfflineUpload.setBounds(10, 34, 215, 18);
		btnOfflineUpload.setText("Upload from local disk");
		listeners[1] = new ARCConfigurationWizardKeywordsListener(this,DataLoadMode.offline);
		btnOfflineUpload.addSelectionListener(listeners[1]);
		
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		localFileTextContent = new Text(container, SWT.BORDER);
		localFileTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		btnAddLocalButton = new Button(container, SWT.NONE);
		btnAddLocalButton.setEnabled(false);
		btnAddLocalButton.setText("Add Local File");
		btnAddLocalButton.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				FileDialog fDialog = new FileDialog(shell,SWT.OPEN);
				
				String filePath = fDialog.open();
				if(filePath==null||filePath.equals("")||!filePath.endsWith("javakeywords"))
					return;
				localFileTextContent.setText(filePath);
			}
		});
		new Label(container, SWT.NONE);
		
		btnLocalUpload = new Button(container, SWT.NONE);
		btnLocalUpload.setEnabled(false);
		btnLocalUpload.setText("Upload");
		btnLocalUpload.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				File localFile = new File(localFileTextContent.getText().trim());
				File targetFile = new File(targetFilePath);
				try {
					FileUtils.copyFile(localFile, targetFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		new Label(container, SWT.NONE);
		
		btnDownloadOnline = new Button(container, SWT.NONE);
		btnDownloadOnline.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnDownloadOnline.setText("Download Online");
		btnDownloadOnline.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				File targetFile = new File(targetFilePath);
				URL targetURL;
				try {
					targetURL = new URL(baseurl);
					FileUtils.copyURLToFile(targetURL, targetFile);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lblStatus.setText("Finish Download");
			}
		});
		
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		lblStatus = new Label(container, SWT.NONE);
		lblStatus.setEnabled(false);
		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		lblStatus.setText("Status");
	}

	public void setOnlineSetting() {
		// TODO Auto-generated method stub
		btnAddLocalButton.setEnabled(false);
		btnLocalUpload.setEnabled(false);
		btnDownloadOnline.setEnabled(true);
	}

	public void setOfflineSetting() {
		// TODO Auto-generated method stub
		btnAddLocalButton.setEnabled(true);
		btnLocalUpload.setEnabled(true);
		btnDownloadOnline.setEnabled(false);
	}

	public static ARCConfigurationWizardPageKeywordsDownload getDefault(IProject aProject, Shell shell) {
		// TODO Auto-generated method stub
		if(instance==null)
			instance = new ARCConfigurationWizardPageKeywordsDownload(aProject,shell);
		return instance;
	}
}
