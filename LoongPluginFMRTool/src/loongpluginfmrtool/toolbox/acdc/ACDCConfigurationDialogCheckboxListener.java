package loongpluginfmrtool.toolbox.acdc;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class ACDCConfigurationDialogCheckboxListener extends SelectionAdapter {
	
	private String pattern;
	public ACDCConfigurationDialogCheckboxListener(String ppattern){
		pattern = ppattern;
	}
	
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		Button button = (Button)e.widget;
		//button.getSelection()
		if(button.getSelection()){
			switch(pattern){
			case "b":{
				ACDCConfigurationDialog.setBodyHeaderEnabled();
				break;
			}
			case "s":{
				ACDCConfigurationDialog.setSubGraphDominatorEnabled();
				break;
			}
			case "o":{
				ACDCConfigurationDialog.setOrphanAdoptionEnabled();
				break;
			}
			default:{
				break;
			}
			}
		}else{
			switch(pattern){
			case "b":{
				ACDCConfigurationDialog.setBodyHeaderDisabled();
				break;
			}
			case "s":{
				ACDCConfigurationDialog.setSubGraphDominatorDisabled();
				break;
			}
			case "o":{
				ACDCConfigurationDialog.setOrphanAdoptionDisabled();
				break;
			}
			default:{
				break;
			}
			}
		}
	}

}
