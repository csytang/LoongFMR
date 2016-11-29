package loongpluginfmrtool.toolbox.mvs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import loongpluginfmrtool.module.builder.ModuleBuilder;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

public class VMSConfigurationDialog extends Dialog {
	private Text text;
	private Shell shell;
	private ModuleBuilder builder;
	private int cluster;
	private int populationcount;
	private Text populationcounttext;
	private Text evolution;
	private int evolutioncount;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public VMSConfigurationDialog(ModuleBuilder pbuilder,Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
		this.builder = pbuilder;
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
		lblNewLabel.setText("# Clusters Prefered");
		
		text = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.heightHint = 20;
		text.setLayoutData(gd_text);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblIndividualsIn = new Label(container, SWT.NONE);
		lblIndividualsIn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblIndividualsIn.setText("# Individuals In Population");
		
		populationcounttext = new Text(container, SWT.BORDER);
		populationcounttext.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		populationcounttext.setText("50");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblEvoluation = new Label(container, SWT.NONE);
		lblEvoluation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblEvoluation.setText("# Evolution");
		
		evolution = new Text(container, SWT.BORDER);
		evolution.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		evolution.setText("200");
		
		
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
		String populationtextcontent = populationcounttext.getText();
		String evoluationtextcontent = evolution.getText();
		try{
			this.cluster = Integer.parseInt(textcontent);
			//super.okPressed();
			this.populationcount = Integer.parseInt(populationtextcontent);
			this.evolutioncount = Integer.parseInt(evoluationtextcontent);
			VariabilityModuleSystem mvs = new VariabilityModuleSystem(builder,cluster,populationcount,evolutioncount);
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
		return new Point(405, 207);
	}

}
