package loongpluginfmrtool.toolbox.arc;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class ARCConfigurationWizardKeywordsListener extends SelectionAdapter {

	private ARCConfigurationWizardPageKeywordsDownload ainstance;
	private DataLoadMode amode;
	public ARCConfigurationWizardKeywordsListener(ARCConfigurationWizardPageKeywordsDownload pinstance,DataLoadMode pmode){
		this.ainstance = pinstance;
		this.amode = pmode;
	}
	
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		Button button = (Button)e.widget;
		if(button.getSelection()){
			if(amode.equals(DataLoadMode.online)){
				ainstance.setOnlineSetting();
			}else{
				ainstance.setOfflineSetting();
			}
		}
	}
}
