package loongpluginfmrtool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

public class FeatureCounter {
	private File afile;
	private Set<String>featureNames;
	public FeatureCounter(File file){
		this.afile = file;
		this.featureNames = new HashSet<String>();
		computeFeatureCount();
	}
	
	private void computeFeatureCount(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(afile));
			String line = br.readLine();
			while(line!=null){
				String[] substrlines = line.split("\t");
				if(substrlines.length==3){
					String featureName = substrlines[1];
					featureName = featureName.trim();
					if(!this.featureNames.contains(featureName)){
						this.featureNames.add(featureName);
					}
				}else{
					return;
				}
				line = br.readLine();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MessageBox messageBox = new MessageBox(Display.getCurrent()
				.getActiveShell(), SWT.OK);
		messageBox.setText("Result");
		messageBox.setMessage("There are: "+featureNames.size()+" features found");
		messageBox.open();
		
	}
}
