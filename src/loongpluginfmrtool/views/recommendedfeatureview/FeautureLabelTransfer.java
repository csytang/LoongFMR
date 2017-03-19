package loongpluginfmrtool.views.recommendedfeatureview;


import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class FeautureLabelTransfer extends ByteArrayTransfer {
   
	private static FeautureLabelTransfer instance = new FeautureLabelTransfer();
	private static final String TYPE_NAME = "feature-label-transfer-format";
	private static final int TYPEID = registerType(TYPE_NAME);
	
	public static FeautureLabelTransfer getInstance(){
		return instance;
	}
	
	@Override
	protected int[] getTypeIds() {
		// TODO Auto-generated method stub
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		// TODO Auto-generated method stub
		return new String[] { TYPE_NAME };
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		// TODO Auto-generated method stub
		if(object instanceof RSFeature){
			super.javaToNative((RSFeature)object, transferData);
		}else if(object instanceof ASTNodeWrapper){
			ASTNodeWrapper astnodewarpper = (ASTNodeWrapper)object;
			IJavaElementWrapper elementwrapper = astnodewarpper.getParent();
			RSFeature rsfeature = elementwrapper.getParent();
			super.javaToNative(rsfeature, transferData);
		}else if(object instanceof IJavaElementWrapper){
			IJavaElementWrapper elementwrapper = (IJavaElementWrapper)object;
			RSFeature rsfeature = elementwrapper.getParent();
			super.javaToNative(rsfeature, transferData);
		}
	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		// TODO Auto-generated method stub
		return super.nativeToJava(transferData);
	}
	
	

}
