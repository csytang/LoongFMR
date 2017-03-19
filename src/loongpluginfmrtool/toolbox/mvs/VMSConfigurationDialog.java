package loongpluginfmrtool.toolbox.mvs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import loongplugin.source.database.model.LElement;
import loongpluginfmrtool.module.model.hierarchicalstructure.HierarchicalBuilder;
import loongpluginfmrtool.module.model.module.Module;
import loongpluginfmrtool.module.model.module.ModuleBuilder;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Combo;

public class VMSConfigurationDialog extends Dialog {
	private Text text;
	private Shell shell;
	private ModuleBuilder builder;
	private int optionalcluster;
	private int commoncluster;
	private Combo combo;
	private Combo comboEntranceMethod;
	private Map<Integer,Module> comboIndextoModule = new HashMap<Integer,Module>();
	private Map<Integer,LElement> comboIndextoMethod = new HashMap<Integer,LElement>();
	private List<Module> mdset = new LinkedList<Module>();
	private Module entranceModule;
	private LElement entranceMethod;
	private Text commonclustertext;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public VMSConfigurationDialog(ModuleBuilder mbuilder,Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
		Set<Module> mutiplemoduleset= new HashSet<Module>(mbuilder.getIndexToModule().values());
		mutiplemoduleset = refinemainset(mutiplemoduleset);
		this.mdset = new LinkedList<Module>(mutiplemoduleset);
		this.builder = mbuilder;
	}

	
	protected Set<Module> refinemainset(Set<Module> parentmoduleset){
		Set<Module>mainmodules = new HashSet<Module>();
		for(Module md:parentmoduleset){
			if(md.hasMainMethod()){
				mainmodules.add(md);
			}
		}
		if(mainmodules.isEmpty()){
			return parentmoduleset;
		}
		return mainmodules;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 5;
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblNewLabel.setText("# Optional Feature Preferred");
		
		text = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.heightHint = 20;
		text.setLayoutData(gd_text);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblcommonfeatureLabel = new Label(container, SWT.NONE);
		lblcommonfeatureLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		lblcommonfeatureLabel.setText("#Common Features(exclude abstracts)");
		
		commonclustertext = new Text(container, SWT.BORDER);
		commonclustertext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblEntranceModule = new Label(container, SWT.NONE);
		lblEntranceModule.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblEntranceModule.setText("Entrance Module");
		
		combo = new Combo(container, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		
		combo.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				int currentSelecitonIndex = combo.getSelectionIndex();
				if(currentSelecitonIndex==-1)
					return;
				
				entranceModule = comboIndextoModule.get(currentSelecitonIndex); 
				List<LElement> allmethodsselected = new LinkedList(entranceModule.getallMethods());
				comboEntranceMethod.removeAll();
				comboIndextoMethod.clear();
				
				// run all elements
				for(int i = 0;i < allmethodsselected.size();i++){
					MethodDeclaration methoddecl = (MethodDeclaration)allmethodsselected.get(i).getASTNode();
					String fullmethodName = "";
					fullmethodName+=methoddecl.getName().toString();
					
					Type returntype = methoddecl.getReturnType2();
					if(returntype!=null){
						fullmethodName+="::";
						fullmethodName+=returntype.toString();
					}
					//fullmethodName+=methoddecl.getReturnType().toString();
					comboEntranceMethod.add(fullmethodName, i);
					comboIndextoMethod.put(i, allmethodsselected.get(i));
				}
				
				
				comboEntranceMethod.update();
				
			}
		});
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblEntranceMethod = new Label(container, SWT.NONE);
		lblEntranceMethod.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblEntranceMethod.setText("Entrance Method");
		
		comboEntranceMethod = new Combo(container, SWT.NONE);
		comboEntranceMethod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		comboEntranceMethod.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int currentSelectionIndex = comboEntranceMethod.getSelectionIndex();
				if(currentSelectionIndex==-1)
					return;
				
				entranceMethod = comboIndextoMethod.get(currentSelectionIndex);
				
			}
		});
		
		// add content to comb modules
		for(int i = 0;i < mdset.size();i++){
			Module md = mdset.get(i);
			
			combo.add(md.getDisplayName(), i);
			comboIndextoModule.put(i, md);
		}
		
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okbutton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		
	}
	
	

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		
		String textcontent = text.getText();
		String commontextcontent = commonclustertext.getText();
		try{
			this.optionalcluster = Integer.parseInt(textcontent);
			this.commoncluster = Integer.parseInt(commontextcontent);
			
			VariabilityModuleSystem mvs = new VariabilityModuleSystem(builder,commoncluster,optionalcluster,entranceModule,entranceMethod);
		}catch(NumberFormatException e){
			Display.getCurrent().syncExec(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
					"Please input an integer for number of cluster configuration.");
				}
		    	
		    });
		}
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(561, 324);
	}

}
