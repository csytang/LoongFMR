package loongpluginfmrtool.toolbox.arc;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class ARCConfigurationWizardDataLoadSelectionListener extends SelectionAdapter {
	
	private ARCConfigurationWizardPageDataLoad ainstance;
	private DataLoadMode amode;
	public ARCConfigurationWizardDataLoadSelectionListener(ARCConfigurationWizardPageDataLoad pinstance,DataLoadMode pmode){
		this.ainstance = pinstance;
		this.amode = pmode;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		Button button = (Button)e.widget;
		if(button.getSelection()){
			if(amode.equals(DataLoadMode.online)){
				ainstance.setOnlineSetting();
			}else if(amode.equals(DataLoadMode.offline)){
				ainstance.setOfflineSetting();
			}
		}
	}
}
