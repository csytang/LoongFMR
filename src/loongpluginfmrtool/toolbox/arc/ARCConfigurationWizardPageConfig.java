package loongpluginfmrtool.toolbox.arc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

public class ARCConfigurationWizardPageConfig extends WizardPage {
	private static ARCConfigurationWizardPageConfig instance;
	private Text projectNameTextContent;
	private static Text odemtextContent;
	private Text grandtruthtextContent;
	private Text deptrsftextContent;
	private Text txtArc;
	private Combo granulecombo;
	private static Text preselectedRangeStart;
	private Text numtopicrangeContentStart;
	private Text topictextContent;
	private Text selectePkgTextContent;
	private Text docTopicFileTextContent;
	private Text txtJava_1;
	private Combo simmeasurecombo;
	private Text smellclstextContent;
	private Combo stoppingcriteriacombo;
	private IProject aProject;
	private Shell shell;
	private String projectPath = "";
	private Text numtopicrangeContentEnd;
	private Text numtopicStep;
	private Text preselectedRangeEnd;
	private static Text preselectedRangeStep;
	private ApplicationObserver aAO;
	private ProgramDatabase aPD;
	private String atopicModelFilePath ="";
	private String adocTopicsFilePath ="";
	private int aminaltopics =0;
	private int atotaltopics =0;
	private String workspacePath;
	private String arcClustersFilename;
	private String projectodemfile = "";
	private String relativeprojectcfgpath = "";
	private ArchRConcernAlg alg;
	private Label lblNumclusters;
	private Text numClusterTextContent;
	private ARCConfigurationWizardPageConfig(ArchRConcernAlg palg,IProject pProject,ApplicationObserver pAO,Shell pShell,String topicModelFilePath,String docTopicsFilePath,String parcClustersFilename,int minaltopics,int totaltopics) {
		super("wizardPage");
		this.alg = palg;
		this.aProject = pProject;
		this.shell = pShell;
		this.aAO = pAO;
		this.aPD = this.aAO.getProgramDatabase();
		this.atopicModelFilePath = topicModelFilePath;
		this.adocTopicsFilePath = docTopicsFilePath;
		this.aminaltopics = minaltopics;
		this.atotaltopics = totaltopics;
		this.arcClustersFilename = parcClustersFilename;
		
		workspacePath =ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		projectPath = workspacePath+File.separatorChar+aProject.getName().toString();
		this.projectodemfile = projectPath+File.separatorChar+this.aProject.getName()+".odem";
		this.relativeprojectcfgpath = this.aProject.getName()+".cfg";
				
		setTitle("Data Load Wizard for Architecture Recovery With Concerns");
		setDescription("This configuration will help you create configuration file (.cfg) for ARC");
	}
	
	public static void setODEMLocation(String location){
		if(instance!=null){
			odemtextContent.setText(location);
		}
	}
	
	public static void setPreSelectionValue(int start,int step){
		if(instance!=null){
			preselectedRangeStart.setText(start+"");
			preselectedRangeStep.setText(step+"");
		}
	}
	
	public static ARCConfigurationWizardPageConfig getDefault(ArchRConcernAlg alg,IProject pProject,ApplicationObserver pAO,Shell pShell,String topicModelFilePath,String docTopicsFilePath,String arcClustersFilename,int minaltopics,int totaltopics) {
		// TODO Auto-generated method stub
		if(instance==null)
			instance = new ARCConfigurationWizardPageConfig(alg,pProject,pAO,pShell,topicModelFilePath,docTopicsFilePath,arcClustersFilename,minaltopics,totaltopics);
		return instance;
	}
	

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER);

		setControl(container);
		container.setLayout(new GridLayout(4, false));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("project_name");
		
		projectNameTextContent = new Text(container, SWT.BORDER);
		projectNameTextContent.setEditable(false);
		projectNameTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projectNameTextContent.setText(this.aProject.getName());
		
		Label lblNewLabel_3 = new Label(container, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("lang");
		
		txtJava_1 = new Text(container, SWT.BORDER);
		txtJava_1.setEditable(false);
		txtJava_1.setText("java");
		txtJava_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
	
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("odem_file");
		
		odemtextContent = new Text(container, SWT.BORDER);
		odemtextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		new Label(container, SWT.NONE);
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.setToolTipText("Direct upload file from local disk");
		btnNewButton.setText("Load Local File");
		btnNewButton.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub

				String filePath = "";
				
				FileDialog fDialog = new FileDialog(shell,SWT.OPEN);
				fDialog.setFilterExtensions(new String [] {"*.odem"});
				filePath = fDialog.open();
				if(filePath==null||filePath.equals(""))
					return;
				File odemfile = new File(filePath);
				
				String targetpath = aProject.getFile(projectodemfile).getLocation().toOSString();
				File targetfile = new File(targetpath);
				if(!aProject.getFile(projectodemfile).exists()){
					try {
						FileUtils.copyFile(odemfile, targetfile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					odemtextContent.setText(targetpath);
				}else{
					odemtextContent.setText(targetpath);
				}
				
			}
			
		});
		
		Label lblGroundtruthfile = new Label(container, SWT.NONE);
		lblGroundtruthfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGroundtruthfile.setText("ground_truth_file");
		
		grandtruthtextContent = new Text(container, SWT.BORDER);
		grandtruthtextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		// attempt to set the grandtruthtextContent
		if(aProject.getFile("data"+File.separatorChar+aProject.getName()+"_ground_truth_recovery.rsf").exists()){
			IFile grandtruthfile = aProject.getFile("data"+File.separatorChar+aProject.getName()+"_ground_truth_recovery.rsf");
			String fullpath = grandtruthfile.getLocation().toOSString();
			grandtruthtextContent.setText(fullpath);
			grandtruthtextContent.setEditable(false);
		}
		
		Label lblDepsrsffile = new Label(container, SWT.NONE);
		lblDepsrsffile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDepsrsffile.setText("deps_rsf_file");
		
		deptrsftextContent = new Text(container, SWT.BORDER);
		deptrsftextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(aProject.getFile("data"+File.separatorChar+aProject.getName()+"_deps.rsf").exists()){
			IFile deptrsf = aProject.getFile("data"+File.separatorChar+aProject.getName()+"_deps.rsf");
			String fullpath = deptrsf.getLocation().toOSString();
			deptrsftextContent.setText(fullpath);
			deptrsftextContent.setEditable(false);
		}
		
		Label lblClusteringalgorithm = new Label(container, SWT.NONE);
		lblClusteringalgorithm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClusteringalgorithm.setText("clustering_algorithm");
		
		txtArc = new Text(container, SWT.BORDER);
		txtArc.setText("arc");
		txtArc.setEditable(false);
		txtArc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSimmeasure_1 = new Label(container, SWT.NONE);
		lblSimmeasure_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSimmeasure_1.setText("sim_measure");
		
		simmeasurecombo = new Combo(container, SWT.BORDER);
		simmeasurecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		simmeasurecombo.setItems(new String[]{"js","uem","uemnm","ilm","scm"});
		simmeasurecombo.select(0);
		
		Label lblGranule = new Label(container, SWT.NONE);
		lblGranule.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGranule.setText("granule");
		
		granulecombo = new Combo(container, SWT.NONE);
		granulecombo.setItems(new String[]{"file","class","func"});
		granulecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		granulecombo.select(0);
		
		lblNumclusters = new Label(container, SWT.NONE);
		lblNumclusters.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNumclusters.setAlignment(SWT.RIGHT);
		lblNumclusters.setText("num_clusters");
		
		numClusterTextContent = new Text(container, SWT.BORDER);
		numClusterTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPreselectedrange = new Label(container, SWT.NONE);
		lblPreselectedrange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPreselectedrange.setText("preselected_range");
		
		preselectedRangeStart = new Text(container, SWT.BORDER);
		GridData gd_text_7 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_7.widthHint = 174;
		preselectedRangeStart.setLayoutData(gd_text_7);
		
		preselectedRangeEnd = new Text(container, SWT.BORDER);
		preselectedRangeEnd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		preselectedRangeStep = new Text(container, SWT.BORDER);
		preselectedRangeStep.setText("5");
		preselectedRangeStep.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblStopcriterion = new Label(container, SWT.NONE);
		lblStopcriterion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStopcriterion.setText("stop_criterion");
		
		
		
		stoppingcriteriacombo = new Combo(container, SWT.NONE);
		stoppingcriteriacombo.setItems(new String[] {"preselected","clustergain"});
		stoppingcriteriacombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		stoppingcriteriacombo.select(0);
		
		Label lblTopicsdir = new Label(container, SWT.NONE);
		lblTopicsdir.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTopicsdir.setText("topics_dir");
		
		topictextContent = new Text(container, SWT.BORDER);
		topictextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		topictextContent.setText(projectPath);
		
		Label lblNumtopicsrange = new Label(container, SWT.NONE);
		lblNumtopicsrange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNumtopicsrange.setText("numtopics_range");
		
		numtopicrangeContentStart = new Text(container, SWT.BORDER);
		numtopicrangeContentStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		numtopicrangeContentStart.setText(aminaltopics+"");
		
		numtopicrangeContentEnd = new Text(container, SWT.BORDER);
		numtopicrangeContentEnd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		numtopicrangeContentEnd.setText(atotaltopics+"");
		
		numtopicStep = new Text(container, SWT.BORDER);
		numtopicStep.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		numtopicStep.setText("5");

		
		
		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("selected_pkgs");
		
		selectePkgTextContent = new Text(container, SWT.BORDER);
		selectePkgTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(aProject!=null){
			Set<String> apackages = this.aAO.getPackages();
			String fullpackage = "";
			Set<String> shortedpackges = new HashSet<String>();
			for(String subpackage:apackages){
				shortedpackges = replacesubPackages(subpackage,shortedpackges);
			}
			
			for(String subpackage:shortedpackges){
				fullpackage+=subpackage;
				fullpackage+=",";
			}
			if(shortedpackges.size()>=1){
				fullpackage = fullpackage.substring(0,fullpackage.length()-1);
			}
			selectePkgTextContent.setText(fullpackage);
		}

		Label lblDoctopicsfile = new Label(container, SWT.NONE);
		lblDoctopicsfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDoctopicsfile.setText("doc_topics_file");
		
		docTopicFileTextContent = new Text(container, SWT.BORDER);
		docTopicFileTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		docTopicFileTextContent.setText(adocTopicsFilePath);
		
		Label lblNewLabel_4 = new Label(container, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setText("smell_clusters_file");
		
		smellclstextContent = new Text(container, SWT.BORDER);
		smellclstextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		new Label(container, SWT.NONE);
		if(aProject!=null){
			smellclstextContent.setText(this.arcClustersFilename);
		}
		
		Button btnGenerateScript = new Button(container, SWT.NONE);
		btnGenerateScript.setText("Generate Script");
		btnGenerateScript.addListener(SWT.Selection, new Listener(){
			// add create the local configuration script
			@Override
			public void handleEvent(Event event) {
				IFile cfgfile = aProject.getFile(relativeprojectcfgpath);
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				
				// project name
				String projectname = "project_name="+projectNameTextContent.getText().trim()+"\n";
				try {
					out.write(projectname.getBytes());
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				// lang
				String lang = "lang=java\n";
				try {
					out.write(lang.getBytes());
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				
				// num_clusters;
				
				if(!numClusterTextContent.getText().trim().equals("")){
					try{
						int numcluster = Integer.parseInt(numClusterTextContent.getText().trim());
						String num_cluster = "num_clusters="+numcluster+"\n";
						out.write(num_cluster.getBytes());
					}catch(NumberFormatException e){
						Display.getCurrent().syncExec(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
										"Parameter num_clusters is not a valid integer input.");
							}
								
						});
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				// odem_file;
				if(!odemtextContent.getText().trim().equals("")){
					String odem = "odem_file="+odemtextContent.getText().trim()+"\n";
					try {
						out.write(odem.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				// deps_rsf_file
				if(!deptrsftextContent.getText().trim().equals("")){
					String depsrsf = "deps_rsf_file="+deptrsftextContent.getText().trim()+"\n";
					try {
						out.write(depsrsf.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				// groundtruth
				if(!grandtruthtextContent.getText().trim().equals("")){
					String groundtruth = "ground_truth_file="+grandtruthtextContent.getText().trim()+"\n";
					try {
						out.write(groundtruth.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				// clustering algorithm
				String clusteringalg = "clustering_algorithm=arc\n";
				try {
					out.write(clusteringalg.getBytes());
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				
				// sim measure
				if(simmeasurecombo.getSelectionIndex()!=-1){//"js","uem","uemnm","ilm","scm"
					String simapp = "js";
					switch(simmeasurecombo.getSelectionIndex()){
					case 0:{//"js"
						simapp = "js";
						break;
					}
					case 1:{//"uem"
						simapp = "uem";
						break;
					}
					case 2:{//"uemnm"
						simapp = "uemnm";
						break;
					}
					case 3:{//"ilm"
						simapp = "ilm";
						break;
					}
					case 4:{//"scm"
						simapp = "scm";
						break;
					}
					}
					String simmeasure = "sim_measure="+simapp+"\n";
					try {
						out.write(simmeasure.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				// granule
				if(granulecombo.getSelectionIndex()!=-1){
					String granfromcomb = "file";
					switch(granulecombo.getSelectionIndex()){//"file","class","func"
					case 0:{
						granfromcomb = "file";
						break;
					}
					case 1:{
						granfromcomb = "class";
						break;
					}
					case 2:{
						granfromcomb = "func";
						break;
					}
					}
					String granule = "granule="+granfromcomb+"\n";
					try {
						out.write(granule.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				// preselected_rang
				if((!preselectedRangeStart.getText().trim().equals(""))&&
						(!preselectedRangeEnd.getText().trim().equals(""))&&
						(!preselectedRangeStep.getText().trim().equals(""))){
					String preselectedrangetext = "preselected_range= "+preselectedRangeStart.getText().trim()+","+
							preselectedRangeEnd.getText().trim()+","+
							preselectedRangeStep.getText().trim()+"\n";
					try {
						out.write(preselectedrangetext.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				// stop_criterion
				if(stoppingcriteriacombo.getSelectionIndex()!=-1){
					String stoppingcrition = "preselected";//"preselected","clustergain"
					switch(stoppingcriteriacombo.getSelectionIndex()){
					case 0:{
						stoppingcrition = "preselected";
						break;
					}
					case 1:{
						stoppingcrition = "clustergain";
						break;
					}
					}
					
					String stopping = "stop_criterion="+stoppingcrition+"\n";
					try {
						out.write(stopping.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				
				// topics_dir
				if(!topictextContent.getText().trim().equals("")){
					String topics = "topics_dir="+topictextContent.getText().trim()+"\n";
					try {
						out.write(topics.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				
				// numtopics_range
				if((!numtopicrangeContentStart.getText().trim().equals(""))
						&&(!numtopicrangeContentEnd.getText().trim().equals(""))
						&&(!numtopicStep.getText().trim().equals(""))){
					String topicrange = "numtopics_range= "+numtopicrangeContentStart.getText().trim()+","+
							numtopicrangeContentEnd.getText().trim()+","+
							numtopicStep.getText().trim()+"\n";
					try {
						out.write(topicrange.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				
				//selected_pkgs
				if(!selectePkgTextContent.getText().trim().equals("")){
					String selectedPKg = "selected_pkgs="+selectePkgTextContent.getText().trim()+"\n";
					try {
						out.write(selectedPKg.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
				
				//doc_topics_file
				if(!docTopicFileTextContent.getText().trim().equals("")){
					String doctopic = "doc_topics_file="+docTopicFileTextContent.getText().trim()+"\n";
					try {
						out.write(doctopic.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				//malletTopicKeysFilename  topic_keys_file
				//String topic_keyfile = "topic_keys_file="++"\n";
						
				//word_topic_counts_file 
				
				
				//smell_clusters_file
				if(!smellclstextContent.getText().trim().equals("")){
					String smellclstext = "smell_clusters_file="+smellclstextContent.getText().trim()+"\n";
					try {
						out.write(smellclstext.getBytes());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				
				}
				
				
				
				InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
				try {
					if(cfgfile.exists()){
						cfgfile.delete(true, null);
					}
					cfgfile.create(inputsource, EFS.NONE, null);
					out.close();
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
				
			}
			 
		});
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		
	}

	/**
	 * this function will replace all sub packages of parentpackage if any
	 * @param subpackage
	 * @param shortedpackges
	 * @return
	 */
	
	private Set<String> replacesubPackages(String subject,Set<String> shortedpackges) {
		// TODO Auto-generated method stub
		Set<String>res = new HashSet<String>();
		if(shortedpackges.isEmpty()){
			res.add(subject);
			return res;
		}
		boolean shoudadd_subject = true;
		for(String str:shortedpackges){
			if(isaparent(subject,str)){
				// if parentpackage is parent of str, then add parentpackage instead of str;
				res.add(subject);
			}else if(isaparent(str,subject)){
				res.add(str);
				shoudadd_subject = false;
			}else{
				res.add(str);
			}
		}
		if(shoudadd_subject){
			res.add(subject);
		}
		return res;
		
	}
	/**
	 * whether str1 is the parent of str2
	 * @param str1
	 * @param str2
	 * @return
	 */
	private boolean isaparent(String str1,String str2){
		if(str2.indexOf(str1)==0){
			if(str2.charAt(str1.length())=='.'){
				return true;
			}
			return false;
		}
		return false;
	}
	
}
