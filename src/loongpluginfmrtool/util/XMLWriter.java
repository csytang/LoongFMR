package loongpluginfmrtool.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLWriter {
	private String targetfilepath;
	private Element root;
	private Document doc;
	
	/**
	 * write the xml file to target filepath with the root proot
	 * @param filepath
	 * @param proot
	 */
	public XMLWriter(String filepath,Element proot){
		this.targetfilepath = filepath;
		this.root = proot;
	}
	
	public void writetoFile(){
		assert this.targetfilepath!=null;
		assert this.root!=null;
		this.doc = new Document(root);
		//this.doc.setRootElement(this.root);
		XMLOutputter xmloutput = new XMLOutputter();
		xmloutput.setFormat(Format.getPrettyFormat());
		try {
			xmloutput.output(this.doc, new FileWriter(targetfilepath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addChild(Element parent,Element child){
		assert parent!=null;
		assert child!=null;
		parent.addContent(child);
	}
	
	
	public static Element createElement(String name, String attribute,String attributeValue){
		Element element = new Element(name);
		element.setAttribute(new Attribute(attribute,attributeValue));
		return element;
	}
	
	
	public static Element createElement(String name, String textContent){
		Element element = new Element(name);
		element.setText(textContent);
		return element;
	}
	
	public static Element createElement(String name){
		Element element = new Element(name);
		return element;
	}
	
}
