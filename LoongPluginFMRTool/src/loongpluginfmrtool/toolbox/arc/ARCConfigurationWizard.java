package loongpluginfmrtool.toolbox.arc;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;

public class ARCConfigurationWizard extends Wizard {

	private ARCConfigurationWizardPageDataLoad dataload;
	private ARCConfigurationWizardPageKeywordsDownload keywordload;
	private ARCConfigurationWizardPageConfig cfg;
	private ARCConfigurationWizardPageODEMDownloader odemload;
	private ApplicationObserver aAO;
	private IProject aProject;
	private ArchRConcernAlg alg;
	public ARCConfigurationWizard(ArchRConcernAlg palg,IProject pProject,ApplicationObserver pAO,Shell shell,String topicModelFilePath,String docTopicsFilePath,String arcClustersFilename,int minaltopics,int totaltopics) {
		super();
		this.aProject = pProject;
		this.aAO = pAO;
		this.alg = palg;
		keywordload = ARCConfigurationWizardPageKeywordsDownload.getDefault(aProject,shell);
		odemload = ARCConfigurationWizardPageODEMDownloader.getDefault(aProject,shell);
		dataload =  ARCConfigurationWizardPageDataLoad.getDefault(aProject,shell);
		cfg = ARCConfigurationWizardPageConfig.getDefault(alg,aProject,aAO,shell,topicModelFilePath,docTopicsFilePath,arcClustersFilename,minaltopics,totaltopics);
		setWindowTitle("Configuration Page");
	}

	@Override
	public void addPages() {
		
		addPage(keywordload);
		addPage(odemload);
		addPage(dataload);
		addPage(cfg);
	}


	@Override
	public IWizardPage getNextPage(IWizardPage page) {// keyword  --> dataload --> odemload --> config
		// TODO Auto-generated method stub
		if(page==keywordload){
	    	return dataload;
	    }else if(page==dataload){
			return odemload;
		}else if(page==odemload){
			return cfg;
		}
		return null;
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}
	
	

}
