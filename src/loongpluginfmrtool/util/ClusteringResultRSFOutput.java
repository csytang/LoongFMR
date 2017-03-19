package loongpluginfmrtool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import loongpluginfmrtool.module.model.module.Module;

public class ClusteringResultRSFOutput {
	private static IFile file;
	private static IProject project;
	private static String projectPath;
	public static void ModuledRSFOutput(Map<Integer,Set<Module>>pclusterres,String method,IProject sourceProject){
		project = sourceProject;
		String fileName = method+"_clusteringresult"+".rsf";
		file = project.getFile(fileName); 
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		for(Map.Entry<Integer, Set<Module>>entry:pclusterres.entrySet()){
			int clusterid = entry.getKey();
			Set<Module>set = entry.getValue();
			String fullString = "";
			
			for(Module module:set){
				
					fullString = "";
					fullString = "contain\t";
					fullString += clusterid+"\t";
					fullString += module.getShortName()+".java";
					fullString += "\n";
					try {
						out.write(fullString.getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
		}
		InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
		if(file.exists()){
			try {
				file.delete(true, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    try {
			file.create(inputsource, EFS.NONE, null);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void ModuledRSFOutput(Map<Integer,Set<Module>>commonclusterres,Map<Integer,Set<Module>>optionalclusterres,String method,IProject sourceProject){
		project = sourceProject;
		String fileName = method+"_clusteringresult"+".rsf";
		file = project.getFile(fileName); 
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		for(Map.Entry<Integer, Set<Module>>entry:commonclusterres.entrySet()){
			int clusterid = entry.getKey();
			Set<Module>set = entry.getValue();
			String fullString = "";
			
			for(Module module:set){
				
					fullString = "";
					fullString = "contain\t";
					fullString += clusterid+"\t";
					fullString += module.getShortName()+".java";
					fullString += "\n";
					try {
						out.write(fullString.getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
		}
		
		for(Map.Entry<Integer, Set<Module>>entry:optionalclusterres.entrySet()){
			int clusterid = entry.getKey();
			Set<Module>set = entry.getValue();
			String fullString = "";
			
			for(Module module:set){
				
					fullString = "";
					fullString = "contain\t";
					fullString += clusterid+"\t";
					fullString += module.getShortName()+".java";
					fullString += "\n";
					try {
						out.write(fullString.getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
		}
		
		InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
		if(file.exists()){
			try {
				file.delete(true, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    try {
			file.create(inputsource, EFS.NONE, null);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void ModuledStrOutput(Map<Integer,Set<String>>pclusterres,String method,IProject sourceProject){
		project = sourceProject;
		String fileName = method+"_clusteringresult"+".rsf";
		file = project.getFile(fileName); 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		
		projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+sourceProject.getName().toString();
		
		for(Map.Entry<Integer, Set<String>>entry:pclusterres.entrySet()){
			int clusterid = entry.getKey();
			Set<String>set = entry.getValue();
			String fullString = "";
			
			for(String str:set){
				
					fullString = "";
					fullString = "contain\t";
					fullString += clusterid+"\t";
					if(str.startsWith(projectPath)){
						String relativePath = str.substring(projectPath.length()); 
						
						if(relativePath.startsWith(File.separatorChar+"")){
							relativePath = relativePath.substring(1);
						}
						relativePath = relativePath.replace(File.separatorChar, '.');
						if(!relativePath.endsWith(".java")){
							relativePath = relativePath+".java";
						}
						fullString +=relativePath;
					}else{
						if(!str.endsWith(".java")){
							str = str+".java";
						}
						fullString += str;
					}
					fullString += "\n";
					try {
						out.write(fullString.getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
		}
		InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
	    try {
	    	if(file.exists()){
	    		file.delete(true, null);
	    	}
			file.create(inputsource, EFS.NONE, null);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
