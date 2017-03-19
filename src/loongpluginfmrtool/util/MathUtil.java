package loongpluginfmrtool.util;

import Jama.Matrix;

public class MathUtil {
	public static double cosineSimilarity(double [] vector,double[] reference){
		double distance= 0.0;
		int size = vector.length;
		double totalsum = 0;
		double totalvector = 0;
		double totalreference = 0;
		for(int i = 0;i < size;i++){
			totalsum += vector[i]*reference[i];
			totalvector+= Math.pow(vector[i],2);
			totalreference+= Math.pow(reference[i], 2);
		}
		double ref = Math.sqrt(totalvector)*Math.sqrt(totalreference);
		if(ref==0){
			distance = -1;
		}else{
			distance = totalsum/(Math.sqrt(totalvector)*Math.sqrt(totalreference));
		}
		
		if(distance<=0){
			distance=0.0;
		}
		return distance;
	}
}
