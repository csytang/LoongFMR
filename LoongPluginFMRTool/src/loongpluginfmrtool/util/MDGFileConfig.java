package loongpluginfmrtool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
public class MDGFileConfig extends TitleAreaDialog {
	private Text text;

	private ModuleBuilder mbuilder;
	private Map<Integer,Module> indexToModule = new HashMap<Integer,Module>();
	private ModuleDependencyTable dependency_table;
	private int[][] table;
	private IProject aProject; 
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public MDGFileConfig(Shell parentShell,IProject pProject,ModuleBuilder pbuilder,Map<Integer,Module> pindexToModule,ModuleDependencyTable pdependency_table,
			int[][] ptable) {
		super(parentShell);
		this.aProject = pProject;
		this.mbuilder = pbuilder;
		this.indexToModule = pindexToModule;
		this.dependency_table = pdependency_table;
		this.table = ptable;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Please input the directory that you want to store the MDG file");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		text = new Text(container, SWT.BORDER);
		text.setBounds(81, 111, 332, 19);
		
		Label lblPleaseInputThe = new Label(container, SWT.NONE);
		lblPleaseInputThe.setBounds(10, 10, 316, 14);
		lblPleaseInputThe.setText("Please input the directory path");
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(10, 41, 407, 50);
		lblNewLabel.setText("By default(no input), it will create the .mdg file\n under this project workspace with name <project name>.mdg");

		return area;
	}

	
	
	
	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		String path = text.getText();
		try {
			createMDGFile(dependency_table,path);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		return new Point(450, 300);
	}
	
	
	private void createMDGFile(ModuleDependencyTable dependency_table,String directory) throws FileNotFoundException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		table = dependency_table.getTable();
		int size = table.length;
		PrintWriter writer = null;
		ByteArrayOutputStream out = null;
		boolean isdefault = false;
		IFile file = null;
		directory = directory.trim();
		if(directory.equals("")){
			file = aProject.getFile(aProject.getName()+".mdg");
			out = new ByteArrayOutputStream();
			isdefault= true;
		}else{
			writer = new PrintWriter(directory+"/"+aProject.getName()+".mdg", "UTF-8");
			// write to file
		}
		for(int i = 0;i < size;i++){
				for(int j = 0;j < size;j++){
					if(i!=j){
						if(table[i][j]!=0){
							Module modulei = indexToModule.get(i);
							Module modulej = indexToModule.get(j);
							String modulePackageNamei = modulei.getNameWithPackage();
							String modulePackageNamej = modulej.getNameWithPackage();
							if(isdefault){
								String str = modulePackageNamei+"\t"+modulePackageNamej+"\n";
								try {
									out.write(str.getBytes());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else
								writer.println(modulePackageNamei+"\t"+modulePackageNamej);
						}
					}
				}
		}
		if(isdefault){
			InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
			try {
				if(file.exists()){
					file.setContents(inputsource, true, true, null);
				}else
					file.create(inputsource, EFS.NONE, null);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			writer.close();
		}
		
	}
	
}
