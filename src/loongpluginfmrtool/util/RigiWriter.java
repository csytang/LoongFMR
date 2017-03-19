package loongpluginfmrtool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Vector;

import javax.swing.JOptionPane;

public class RigiWriter {
	/**
	 * =
     * Write stuff out in Structured RSF.
     * @param prjFilename the name of the prj file or the database table to store the data
     * @param artifacts the collection of artifacts
     * @param relationships the collection of relationships
     
    public static void saveData(String prjFilename, Vector artifacts, Vector relationships, PRJFile prjFile) {

		// make sure the file has the right extension
		if(!prjFilename.endsWith(".prj"))
		    prjFilename += ".prj";

		String rsfFilename = prjFilename.substring (0, prjFilename.length() - 4) + ".rsf";

		// for now do nothing if the file exists.  Later, ask for overwriting permission
		File file = new File(prjFilename);
		File rsfFile = new File (rsfFilename);
		
		// write the prj file
		FileOutputStream fileOutputStream = null;
		PrintWriter writer = null;
		try { 
			fileOutputStream = new FileOutputStream (file);
			writer = new PrintWriter (fileOutputStream);
			writer.println ("PRJ-Version: 2.0");
			writer.println ("RSF-File: " + rsfFile.getName());
			writer.println ("RSF-Domain: ../../rsf/domain/c");
			if (prjFile.getCodeDirectory()!=null)
				writer.println ("CODE-Directory: " + getDirName (prjFile.getCodeDirectory()));
			if (prjFile.getDocDirectory()!=null)
				writer.println ("DOC-Directory: " + getDirName (prjFile.getDocDirectory()));
			if (prjFile.getUmlDirectory()!=null)
				writer.println ("LAYOUT_UML-Directory: " + getDirName (prjFile.getUmlDirectory()));
			if (prjFile.getSourceDirectory()!=null)
				writer.println ("SOURCE-Directory: " + getDirName (prjFile.getSourceDirectory()));
									
			writer.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Warning...", JOptionPane.ERROR_MESSAGE); 
			return;
		} catch (IOException e) {
			// just ignore this since it's on the close
		}

		// write the rsf file
		try { 
			fileOutputStream = new FileOutputStream (rsfFile);
			writer = new PrintWriter (fileOutputStream);

			for (int i = 0; i < artifacts.size(); i++) {
				Artifact art = (Artifact) artifacts.elementAt(i);
				String artName = art.getName();
				artName = artName.replaceAll("!", "&#33;");
				String artID = art.getDataBean().getStringFromExternalArtifactID(art.getExternalId());
				artID = artID.replaceAll("!", "&#33;");
				String artType = art.getType();
				artType = artType.replaceAll("!", "&#33;");
				//write the node id, name, and type
				writer.println ("\"n!type\"\t\"" + artID + "!" + artName + "\"\t\"" + artType + "\"");
                
                //write out node attributes
                Vector attribNames = art.getAttributeNames();
                for (int j=0; j< attribNames.size(); j++) {
                    String attName = (String) attribNames.elementAt(j);
                    Object attValue = art.getAttribute(attName);
                    if (attName.equals (SoftwareDomainConstants.NOM_ATTR_SOURCE_CODE_URI)){
                    	attName = "nodeurl"; //TODO find out if this is because of Rigi
                    }
                    if (attValue != null && attValue instanceof String) {
                        String attValueStr = attValue.toString();
                        attValueStr = attValueStr.replaceAll("!", "&#33;");
                        attValueStr = URLEncoder.encode(attValueStr, "UTF-8");
                    	if (attValueStr.length() > 0) {                     	    
	                    	writer.println("\"n!" + attName + "\"\t\"" + art.getID() + "!" + art.getName() + "\"\t\"" + attValueStr + "\"");
                    	}
                    } else {
                        System.out.println(artName + " - Not a string ... ignoring attribute " + attName + " = " + attValue);
                    }
                }                
			}

			// write the other relationships
			for (int i = 0; i < relationships.size(); i++) {
				Relationship rel = (Relationship) relationships.elementAt(i);
				
				//Only output the relationship if both ends of it still exist
				if (rel.getArtifacts().size() >= 2) {
					Artifact src = (Artifact)rel.getArtifacts().elementAt(0);
					Artifact des = (Artifact)rel.getArtifacts().elementAt(1);
					writer.println ("\"a!" + rel.getType() + "\"\t\"" + src.getID() + "!" + rel.getID() + "\"\t\"" + des.getID() + "!" + des.getName() + "\"");
				}
			}

			writer.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Warning...", JOptionPane.ERROR_MESSAGE); 
			return;
		} catch (IOException e) {
			// just ignore this since it's on the close
		}

		JOptionPane.showMessageDialog(null, "The Graph has been Saved", "Save", JOptionPane.INFORMATION_MESSAGE); 
    }
	 */
	
	
	
}
