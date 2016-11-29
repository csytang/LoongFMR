package loongpluginfmrtool.statistic.ui;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import loongpluginfmrtool.statistic.mojo.MoJoCalculator;
import org.eclipse.swt.widgets.Button;

public class MoJoSettingUI extends TitleAreaDialog {
	private Text recoveryResultTextContent;
	private Text groundTruthTextContent;
	private Combo combo;
	private IProject aProject;
	private String projectPath;
	private Label lblValue;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public MoJoSettingUI(IProject pProject,Shell parentShell) {
		super(parentShell);
		aProject = pProject;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+aProject.getName().toString();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("MoJoMeasure Setting Panel\n");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		new Label(container, SWT.NONE);
		
		Label lblArchitectureRecoveryApproach = new Label(container, SWT.NONE);
		lblArchitectureRecoveryApproach.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblArchitectureRecoveryApproach.setText("Architecture Recovery Approach:");
		
		combo = new Combo(container, SWT.NONE);
		combo.setItems(new String[] {"ACDC(Alg for Comprehension-Driven Clustering)", "LIMBO(scaLable InforMation BOttleneck)", "ARC(Arch. Recovery using Concern)", "Bunch", "WCA-UE(Weighted Combined Alg.)","WCA-UENM(Weighted Combined Alg.)", "VMS(Variability Model System)"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// String fileName = method+"_clusteringresult"+".rsf";
				
				int currentSelecitonIndex = combo.getSelectionIndex();
				IFile groundtruhfile = aProject.getFile("data"+File.separatorChar+aProject.getName()+"_ground_truth_recovery.rsf");
				if(groundtruhfile.exists()){
					String groundtruthfullpath = projectPath+File.separatorChar+"data"+File.separatorChar+aProject.getName()+"_ground_truth_recovery.rsf";
					groundTruthTextContent.setText(groundtruthfullpath);
				}
				
				
				switch(currentSelecitonIndex){
				case -1:{
					break;
				}
				case 0:{//ACDC(Alg for Comprehension-Driven Clustering)
					// file name:_clusteringresult.rsf;
					recoveryResultTextContent.setText("");
					String resultName = "acdc_clusteringresult.rsf";
					if(aProject.getFile(resultName).exists()){
						String fullresultPath = projectPath+File.separatorChar+resultName;
						recoveryResultTextContent.setText(fullresultPath);
					}
					break;
				}
				case 1:{//LIMBO(scaLable InforMation BOttleneck)
					//
					recoveryResultTextContent.setText("");
					String resultName = "limbo_clusteringresult.rsf";
					if(aProject.getFile(resultName).exists()){
						String fullresultPath = projectPath+File.separatorChar+resultName;
						recoveryResultTextContent.setText(fullresultPath);
					}
					break;
				}
				case 2:{//ARC(Arch. Recovery using Concern)
					recoveryResultTextContent.setText("");
					String resultName = "arc_clusteringresult.rsf";
					if(aProject.getFile(resultName).exists()){
						String fullresultPath = projectPath+File.separatorChar+resultName;
						recoveryResultTextContent.setText(fullresultPath);
					}
					
					break;
				}
				case 3:{//Bunch
					recoveryResultTextContent.setText("");
					String resultName = "bunch_clusteringresult.rsf";
					if(aProject.getFile(resultName).exists()){
						String fullresultPath = projectPath+File.separatorChar+resultName;
						recoveryResultTextContent.setText(fullresultPath);
					}
					break;
				}
				case 4:{//WCA(Weighted Combined Alg. UE
					recoveryResultTextContent.setText("");
					String resultName = "wca_uem_clusteringresult.rsf";
					if(aProject.getFile(resultName).exists()){
						String fullresultPath = projectPath+File.separatorChar+resultName;
						recoveryResultTextContent.setText(fullresultPath);
					}
					break;
				}
				case 5:{//WCA(Weighted Combined Alg. UENM
					recoveryResultTextContent.setText("");
					String resultName = "wca_uemnm_clusteringresult.rsf";
					if(aProject.getFile(resultName).exists()){
						String fullresultPath = projectPath+File.separatorChar+resultName;
						recoveryResultTextContent.setText(fullresultPath);
					}
					break;
				}
				case 6:{//VMS(Variability Model System
					recoveryResultTextContent.setText("");
					String resultName = "vms_clusteringresult.rsf";
					if(aProject.getFile(resultName).exists()){
						String fullresultPath = projectPath+File.separatorChar+resultName;
						recoveryResultTextContent.setText(fullresultPath);
					}
					break;
				}
				}
			}
			
		});
		new Label(container, SWT.NONE);
		
		Label lblRecoveryResult = new Label(container, SWT.NONE);
		lblRecoveryResult.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRecoveryResult.setText("Recovery Result:");
		
		recoveryResultTextContent = new Text(container, SWT.BORDER);
		recoveryResultTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		Label lblGroundTruthFile = new Label(container, SWT.NONE);
		lblGroundTruthFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGroundTruthFile.setText("Ground Truth File Path:");
		
		groundTruthTextContent = new Text(container, SWT.BORDER);
		groundTruthTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblResult = new Label(container, SWT.NONE);
		lblResult.setText("Result =");
		
		lblValue = new Label(container, SWT.NONE);
		lblValue.setText("VALUE");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnCompute = new Button(container, SWT.NONE);
		btnCompute.setText("Compute");

		btnCompute.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if(combo.getSelectionIndex()!=-1){
					lblValue.setText("Computing...");
					int selectedIndex = combo.getSelectionIndex();
					// 1. get the method
					if(recoveryResultTextContent.getText().trim().equals("")){
						
						return;
					}
					if(groundTruthTextContent.getText().trim().equals("")){
						
						return;
					}
					// Collect Statistics
					String sourcefile = recoveryResultTextContent.getText().trim();
					String targetfile = groundTruthTextContent.getText().trim();
					MoJoCalculator cal = new MoJoCalculator(sourcefile,targetfile,null);
					double resultvalue = cal.mojofm();
					lblValue.setText(resultvalue+"");
				}
			}
			
		});
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(472, 321);
	}

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		// Finsh the setting
		// Check all paths are valid
		
	
		super.okPressed();
	}
	
	
	/*
	 * if(combo.getSelectionIndex()!=-1){
			int selectedIndex = combo.getSelectionIndex();
			// 1. get the method
			if(recoveryResultTextContent.getText().trim().equals("")){
				super.okPressed();
				return;
			}
			if(groundTruthTextContent.getText().trim().equals("")){
				super.okPressed();
				return;
			}
			// Collect Statistics
			String sourcefile = recoveryResultTextContent.getText().trim();
			String targetfile = groundTruthTextContent.getText().trim();
			MoJoCalculator cal = new MoJoCalculator(sourcefile,targetfile,null);
			double resultvalue = cal.mojofm();
			lblValue.setText(resultvalue+"");
		}
	 */

}
