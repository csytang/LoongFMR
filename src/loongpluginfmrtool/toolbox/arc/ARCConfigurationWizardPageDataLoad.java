package loongpluginfmrtool.toolbox.arc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ARCConfigurationWizardPageDataLoad extends WizardPage {
	private Text offlineupdatecontenttext;
	private Button offlineupdateButton;
	private Button onlineDownloadButton;
	private ARCConfigurationWizardDataLoadSelectionListener[]listener = new ARCConfigurationWizardDataLoadSelectionListener[2];
	private String projectPath;
	private Label lblstatusLabel;
	private Button btnUpload;
	/**
	 * Create the wizard.
	 */
	private static ARCConfigurationWizardPageDataLoad instance;
	private String baseurl = "http://www.chrisyttang.org/loong_fmr/sup/";
	private String stopword = "stopwords.txt";
	private String stopworddie = "stoplists";
	private String[] lang = {"cs.txt","de.txt","en.txt","fi.txt","fr.txt","jp.txt","misc.txt","project.txt","stopwords_total.txt"};
	private IProject aProject;
	private Shell ashell;
	private ARCConfigurationWizardPageDataLoad(IProject pProject,Shell shell) {
		super("wizardPage");
		setTitle("Data Load Wizard for Architecture Recovery With Concerns\n");
		setDescription("This configuration will help you download or direct the modules and files used in ARC");
		aProject = pProject;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+aProject.getName().toString();
		ashell = shell;
	}
	
	public static ARCConfigurationWizardPageDataLoad getDefault(IProject pProject,Shell shell){
		if(instance==null){
			instance = new ARCConfigurationWizardPageDataLoad(pProject,shell);
		}
		return instance;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblCreating = new Label(container, SWT.NONE);
		lblCreating.setBounds(10, 10, 457, 14);
		lblCreating.setText("Download supporting stopping dictionaries from webpage or local directory\n");
		
		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 30, 256, 69);
		
		Button btnRadioButton = new Button(group, SWT.RADIO);
		btnRadioButton.setBounds(10, 10, 181, 18);
		btnRadioButton.setText("Download From Webpage");
		listener[0] = new ARCConfigurationWizardDataLoadSelectionListener(this,DataLoadMode.online);
		btnRadioButton.addSelectionListener(listener[0]);
		
		Button btnRadioButton_1 = new Button(group, SWT.RADIO);
		btnRadioButton_1.setBounds(10, 36, 212, 18);
		btnRadioButton_1.setText("Use the Files Already Downloaded");
		listener[1] = new ARCConfigurationWizardDataLoadSelectionListener(this,DataLoadMode.offline);
		btnRadioButton_1.addSelectionListener(listener[1]);
		
		offlineupdatecontenttext = new Text(container, SWT.BORDER);
		offlineupdatecontenttext.setEnabled(false);
		offlineupdatecontenttext.setBounds(10, 113, 354, 19);
		
		offlineupdateButton = new Button(container, SWT.NONE);
		offlineupdateButton.setEnabled(false);
		offlineupdateButton.setBounds(391, 109, 127, 28);
		offlineupdateButton.setText("Add Files");
		offlineupdateButton.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				// 确定是正确的
				String filePath = "";
				
				FileDialog fDialog = new FileDialog(ashell,SWT.OPEN);
				fDialog.setFilterExtensions(new String [] {"*.txt"});
				filePath = fDialog.open();
				if(filePath==null||filePath.equals(""))
					return;
				String fileLastSection = filePath.substring(filePath.lastIndexOf(File.separatorChar)+1);
				if(fileLastSection==null||fileLastSection.equals("")){
					createFileSelectionError("No input");
				}else if(fileLastSection.equals(stopword)){
					String curr = offlineupdatecontenttext.getText();
					if(!curr.equals("")){
						curr+=";";
						
					}
					curr+=filePath;
					offlineupdatecontenttext.setText(curr);
					setPageComplete(true);
				}else if(inStringArray(fileLastSection,lang)){
					String curr = offlineupdatecontenttext.getText();
					if(!curr.equals("")){
						curr+=";";
					}
					curr+=filePath;
					offlineupdatecontenttext.setText(curr);
					setPageComplete(true);
				}else{
					createFileSelectionError("Not a supported file");
				}
			}

			
			
		});
		
		
		lblstatusLabel = new Label(container, SWT.NONE);
		lblstatusLabel.setEnabled(false);
		lblstatusLabel.setAlignment(SWT.CENTER);
		lblstatusLabel.setBounds(10, 205, 592, 38);
		lblstatusLabel.setText("Status");
		
		onlineDownloadButton = new Button(container, SWT.NONE);
		onlineDownloadButton.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				File stopwordfile = new File(projectPath+File.separatorChar+stopword);
				URL url;
				try {
					url = new URL(baseurl+stopword);
					lblstatusLabel.setText("Download:"+stopwordfile.getName());
					FileUtils.copyURLToFile(url, stopwordfile);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for(String sublang:lang){
					File sublangfile = new File(projectPath+File.separatorChar+stopworddie+File.separatorChar+sublang);
					try {
						url = new URL(baseurl+stopworddie+"/"+sublang);
						lblstatusLabel.setText("Download:"+sublangfile.getName());
						FileUtils.copyURLToFile(url, sublangfile);
						
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				setPageComplete(true);
			}
			
		});
		
		onlineDownloadButton.setEnabled(false);
		onlineDownloadButton.setBounds(249, 155, 127, 28);
		onlineDownloadButton.setText("Download online");
		
		btnUpload = new Button(container, SWT.NONE);
		btnUpload.setEnabled(false);
		btnUpload.setBounds(524, 109, 94, 28);
		btnUpload.setText("Upload");
		btnUpload.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				if(offlineupdatecontenttext.getText().equals("")){
					Display.getCurrent().syncExec(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							MessageDialog.openInformation(null, "Loong Plugin System-FMRTool",
							"Cannot update empty file");
						}
				    	
				    });
					return;
				}
				String filepaths = offlineupdatecontenttext.getText();
				String[] filepathlist = filepaths.split(";");
				for(String filepath:filepathlist){
					File file = new File(filepath);
					//FileUtils.copyFile(file, destFile);
					File destFile = null;
					if(file.getAbsolutePath().endsWith("stopwords.txt")){
						destFile = new File(projectPath+File.separatorChar+"sup"+File.separatorChar+stopword);	
					}else{
						// get last string
						
						int lasts = filepaths.lastIndexOf(File.separatorChar);
						String nameInShort = filepaths.substring(lasts+1);
						destFile = new File(projectPath+File.separatorChar+"sup"+File.separatorChar+stopworddie+File.separatorChar+nameInShort);
					}
					try {
						FileUtils.copyFile(file, destFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				setPageComplete(true);
			}
		});
		
		setPageComplete(false);
	}
	
	
	protected void createFileSelectionError(final String errormsg) {
		// TODO Auto-generated method stub
		Display.getCurrent().syncExec(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MessageDialog.openInformation(null, "Loong Plugin System-FMRTool",
						errormsg);
			}
	    	
	    });
	}

	public void setOnlineSetting() {
		// TODO Auto-generated method stub
		
		onlineDownloadButton.setEnabled(true);
		offlineupdatecontenttext.setEnabled(false);
		offlineupdatecontenttext.setEditable(false);
		offlineupdateButton.setEnabled(false);
		btnUpload.setEnabled(false);
	}

	public void setOfflineSetting() {
		// TODO Auto-generated method stub
		
		onlineDownloadButton.setEnabled(false);
		offlineupdatecontenttext.setEnabled(true);
		offlineupdatecontenttext.setEditable(true);
		offlineupdateButton.setEnabled(true);
		btnUpload.setEnabled(true);
	}

	
	private boolean inStringArray(String fileLastSection, String[] lang) {
		// TODO Auto-generated method stub
		for(String pl:lang){
			if(fileLastSection.equals(pl))
				return true;
		}
		return false;
	}
	
	
	
	
}
