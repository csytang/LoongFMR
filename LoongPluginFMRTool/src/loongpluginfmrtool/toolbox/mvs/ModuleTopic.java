package loongpluginfmrtool.toolbox.mvs;
import loongpluginfmrtool.module.model.module.Module;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.eclipse.core.resources.ResourcesPlugin;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.CharSequenceReplace;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;
import edu.usc.softarch.arcade.config.Config;
import edu.usc.softarch.arcade.topics.CamelCaseSeparatorPipe;
import edu.usc.softarch.arcade.topics.StemmerPipe;
import org.eclipse.core.resources.IProject;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class ModuleTopic {
	private Module module;
	private String modulestr = "";
	private String workspacePath = "";
	private String projectPath = "";
	private String artifactsDir = "";
	private int numTopics;
	public ModuleTopic(Module pmodule,int pnumTopics,IProject aProject){
		this.module = pmodule;
		this.modulestr = module.getCompilationUnit().toString();
		this.workspacePath =ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		this.projectPath = workspacePath+File.separatorChar+aProject.getName().toString();
		this.artifactsDir =  this.projectPath;
		this.numTopics = pnumTopics;
		try {
			build();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void build() throws FileNotFoundException{
		modellingTopic();
	}

	/**
	 * create the bag of word for this module
	 */
	private void modellingTopic() {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
		// Pipes: alphanumeric only, camel case separation, lowercase, tokenize,
		// remove stopwords english, remove stopwords java, stem, map to
		// features
		pipeList.add(new CharSequenceReplace(Pattern.compile("[^A-Za-z]"), " "));
		pipeList.add(new CamelCaseSeparatorPipe());
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern
				.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequenceRemoveStopwords(new File(projectPath+File.separatorChar+"stoplists/en.txt"), "UTF-8", false, false, false));
		pipeList.add(new TokenSequenceRemoveStopwords(new File(projectPath+File.separatorChar+"res/javakeywords"), "UTF-8", false, false, false));
		pipeList.add(new StemmerPipe());
		pipeList.add(new TokenSequence2FeatureSequence());
		InstanceList instances = new InstanceList(new SerialPipes(pipeList));
		Instance instance = new Instance(modulestr, "X", module.getModuleName(),null);
		instances.addThruPipe(instance);
		//instances.save(new File(artifactsDir+"/output.pipe"));
		double alpha = (double) 50 / (double) numTopics;
		double beta = .01;
		ParallelTopicModel model = null ;
		model = new ParallelTopicModel(numTopics, alpha, beta);
		model.addInstances(instances);
//		
//		// Use two parallel samplers, which each look at one half the corpus and
//		// combine
//		// statistics after every iteration.
		model.setNumThreads(2);
//
//		// Run the model for 50 iterations and stop (this is for testing only,
//		// for real applications, use 1000 to 2000 iterations)
		int numIterations = 1000;
		model.setNumIterations(numIterations);
		model.setRandomSeed(10);
		
		
		// Show the words and topics in the first instance

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();
        
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        
        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        
        // Create a new instance with high probability of topic 0
        StringBuilder topicZeroText = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();
		
        
        
		
	}
	
	
}
