package loongpluginfmrtool.bunchtorsf;


import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;

import edu.usc.softarch.arcade.facts.driver.BunchClusterToRsfClusterConverter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.viewers.ListViewer;

public class BatchBunchToRSFDialog extends TitleAreaDialog {
	private Text text;
	private Button btnLoad;
	private Button btnOpen;
	private Shell shell;
	private ListViewer listViewerBunch;
	private ListViewer listViewerRSF;
	private org.eclipse.swt.widgets.List bunchlist;
	private org.eclipse.swt.widgets.List rsflist;
	private List<File>allbunchfiles = new LinkedList<File>();
	private Map<File,String>bunchfileNames = new HashMap<File,String>();
	private List<File>allrsfresultfiles = new LinkedList<File>();
	private Map<File,String>rsffileNames = new HashMap<File,String>();
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public BatchBunchToRSFDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE);
		shell = parentShell;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Batched Bunch file to RSF file");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(7, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblDirectory = new Label(container, SWT.NONE);
		lblDirectory.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblDirectory.setText("Directory:");
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1));
		lblNewLabel.setText("Transferred RSF Files");
		
		text = new Text(container, SWT.BORDER);
		//gd_text.widthHint = 130;

		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		btnOpen = new Button(container, SWT.NONE);
		btnOpen.setText("Open");
		btnOpen.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				 DirectoryDialog dlg = new DirectoryDialog(shell);
				 dlg.setText("Please select the direct to look up");
				 String dir = dlg.open();
				 if(dir!=null){
					 text.setText(dir);
				 }
			}
			
		});
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		btnLoad = new Button(container, SWT.NONE);
		btnLoad.setText("Load");
		btnLoad.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if(!text.getText().trim().equals("")){
					// the text is not equal
					//processContainer();
					final File directory = new File(text.getText());
					if(directory.exists()){
						WorkspaceJob op = new WorkspaceJob("Create the bunch to rsf file job") {
	
							@Override
							public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
								// TODO Auto-generated method stub
								processDirectory(directory,monitor);
								return Status.OK_STATUS;
							}
							
						};
						op.setUser(true);
						op.schedule();
						
						try {
							op.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Collection<String>allpaths = bunchfileNames.values();
						String[]allpatharray = new String[allpaths.size()];
						allpaths.toArray(allpatharray);
						bunchlist.setItems(allpatharray);
					}
				}
			}
			
		});
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		listViewerBunch = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		bunchlist = listViewerBunch.getList();
		
		GridData gd_bunchlist = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_bunchlist.heightHint = 80;
		gd_bunchlist.widthHint = 203;
		bunchlist.setLayoutData(gd_bunchlist);
		
		listViewerRSF = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		rsflist = listViewerRSF.getList();
		GridData gd_list = new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1);
		gd_list.heightHint = 80;
		gd_list.widthHint = 185;
		rsflist.setLayoutData(gd_list);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnTransfer = new Button(container, SWT.NONE);
		btnTransfer.setText("Transfer");
		btnTransfer.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if(bunchlist.getItemCount()>0){
					if(bunchlist.getItemCount()==bunchfileNames.size()){
						for(File bunchfile:allbunchfiles){
							String inputPath = bunchfile.getAbsolutePath();
							int lastIndex = inputPath.lastIndexOf(File.separatorChar);
							String prePath = inputPath.substring(0, lastIndex);
							String shorFileName = inputPath.substring(lastIndex+1);
							shorFileName = shorFileName.substring(0, shorFileName.length()-".bunch".length());
							String outputPath = "bunch_"+prePath+File.separatorChar+shorFileName+".rsf";
							
							BunchClusterToRsfClusterConverter.BunchClusterToRsfClusterConverter(inputPath, outputPath);
							File outputFile = new File(outputPath);
							allrsfresultfiles.add(outputFile);
							rsffileNames.put(outputFile, outputPath);
							
						}
						Collection<String>allpaths = rsffileNames.values();
						String[]allpatharray = new String[allpaths.size()];
						allpaths.toArray(allpatharray);
						rsflist.setItems(allpatharray);
						
						
					}else{
						try {
							throw new Exception("Not sync for bunchlist and internal variable");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			}
			
		});
		
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

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
		return new Point(508, 365);
	}
	
	private void processDirectory(File file,IProgressMonitor monitor){
		if(file.isDirectory()){
			for(File subfile:file.listFiles()){
				processDirectory(subfile,monitor);
			}
		}else{
			String filepath = file.getAbsolutePath();
			if(filepath.endsWith(".bunch")){
				allbunchfiles.add(file);
				bunchfileNames.put(file,filepath);
			}
		}
		
	}
	
	

}
