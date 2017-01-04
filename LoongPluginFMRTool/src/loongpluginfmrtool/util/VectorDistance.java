package loongpluginfmrtool.util;

public class VectorDistance {
	
	public static double generalizedjaccardDistance(double[] vector,double[] vectorref){
		double distance = 1.0;
		double similaritycoeffcient = 0.0;
		assert vector.length==vectorref.length;
		int length = vector.length;
		double minsum = 0.0;
		double maxsum = 0.0;
		
		for(int i = 0;i < length;i++){
			double vector_i = vector[i];
			double vector_ref_i = vectorref[i];
			if(vector_i>=vector_ref_i){
				minsum+=vector_ref_i;
				maxsum+=vector_i;
			}else{
				minsum+=vector_i;
				maxsum+=vector_ref_i;
			}
		}
		if(maxsum==0)
			similaritycoeffcient = 1.0;
		else	
			similaritycoeffcient = minsum/maxsum;
		
		assert distance>0;
		assert distance<1;
		return distance;
		
	}
}
