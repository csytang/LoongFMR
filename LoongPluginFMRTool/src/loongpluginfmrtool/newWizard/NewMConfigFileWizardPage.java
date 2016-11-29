package loongpluginfmrtool.newWizard;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginfmrtool.editor.configfeaturemodeleditor.model.ConfFeatureModel;
import loongpluginfmrtool.editor.configfeaturemodeleditor.serializer.DiagramSerializer;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewMConfigFileWizardPage extends WizardNewFileCreationPage {

	public NewMConfigFileWizardPage(
			ISelection selection) {
		super("wizardPage",(IStructuredSelection) selection);
		setTitle("Create a configurable feature model file");
		setDescription("Create a configurable feature model");
	}

	@Override
	protected InputStream getInitialContents() {
		ConfFeatureModel cnfModel = new ConfFeatureModel();
		ConfFeature cnfFeature = new ConfFeature();
		cnfFeature.setText("SPL");
		cnfFeature.setConstraint(new Rectangle(80,80,100,80));
		cnfModel.addChild(cnfFeature);
		try {
			return DiagramSerializer.serialize(cnfModel);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void createControl(Composite parent) {
		/*
		 * 
		 */
		// TODO Auto-generated method stub
		super.createControl(parent);
		this.setFileName("configfeaturemodel.mconfig");
	}
	
	
}
