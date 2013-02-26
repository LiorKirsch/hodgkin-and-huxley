package neuron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Noise {
	public int[][] voltageInTime;
	
	/*private static double[] createRandomNoiseForSingleNeuron (double totalTime, double possionRate)
	{
		// possion distribution has an exponential CDF, so this way we can get back the possion distribution 
		double lambda = 1 / possionRate;
		double rand;
		double expProbability;
		double[] randomNoiseForNeuron = new double[totalTime];
		
		// for the first bin
		rand = Math.random();
		expProbability = -Math.log(rand)/lambda;
		randomNoiseForNeuron[0] = expProbability;	

	
		for (int j = 1; j < totalTime; j++) {
			rand = Math.random();
			expProbability = -Math.log(rand)/lambda; 
			randomNoiseForNeuron[j] = expProbability + randomNoiseForNeuron[j-1];
		}
	
		return randomNoiseForNeuron;

	}*/
	private static ArrayList<Double> createRandomNoiseForSingleNeuron2 (double totalTime, double possionRate)
	{
		// possion distribution has an exponential CDF, so this way we can get back the possion distribution 
		double lambda = possionRate;
		double rand;
		double expProbability;
		ArrayList<Double>  randomNoiseForNeuron = new ArrayList<Double > ();
		
		// for the first bin
		rand = Math.random();
		expProbability = -Math.log(rand)/lambda;
		expProbability += -Math.log(rand)/lambda;
		randomNoiseForNeuron.add(expProbability)  ;	

	
		//for (int j = 1; j < totalTime & randomNoiseForNeuron.get(randomNoiseForNeuron.size()-1) <= totalTime; j++) {
		for (int j = 1;  randomNoiseForNeuron.get(randomNoiseForNeuron.size()-1) <= totalTime; j++) {
			rand = Math.random();
			expProbability = -Math.log(rand)/lambda; 
			randomNoiseForNeuron.add(expProbability + randomNoiseForNeuron.get(j-1 )  );
		}
	
		return randomNoiseForNeuron;

	}
	
	/*public static double[] sumItUponManyNeurons(double totalTime, double possionRate, int numberOfNeuronToStimulate)
	{
	
		double[][] neuronsInTime = new double[numberOfNeuronToStimulate][];
		double[] output;
	
		for (int i = 0; i < numberOfNeuronToStimulate; i++) {
			neuronsInTime[i] = createRandomNoiseForSingleNeuron(totalTime, possionRate)  ;
	
		}
		output = Noise.mergeAndSortArrays(neuronsInTime);
		return output;
	}*/
	
	public static ArrayList<Double> sumItUponManyNeurons2(double totalTime, double possionRate, int numberOfNeuronToStimulate)
		{
			
			ArrayList<Double> neuronsInTime = new ArrayList<Double>();
			
			for (int i = 0; i < numberOfNeuronToStimulate; i++) {
				neuronsInTime.addAll(  createRandomNoiseForSingleNeuron2(totalTime, possionRate)  );
			}
			
			Collections.sort(neuronsInTime);
		return neuronsInTime;
	}
	public static ConcurrentLinkedQueue<Double> getNoise(double totalTime, double possionRate, int numberOfNeuronToStimulate)
	{
		ConcurrentLinkedQueue<Double> randomNoise;
		ArrayList<Double> randomSpikesList = Noise.sumItUponManyNeurons2(totalTime, possionRate , numberOfNeuronToStimulate);
		randomNoise = Noise.changeListToQueue(randomSpikesList);
		
		return randomNoise;
	}
	
	public int[] changeFromTimeToBits(double[] neuronInTime, int[] output)
	{
		int currentBin;
		for (int i = 0; i < neuronInTime.length; i++) {
			currentBin = (int)Math.floor(neuronInTime[i]);
			if (currentBin< output.length &  0 <= currentBin)
			{
				output[currentBin] = output[currentBin] + 1;
			}
		}
		return output;
	}
	private static double[] mergeAndSortArrays(double[][] arrayOfArrays )
	{
		int lengthOfOutput = 0;
		for (double[] array : arrayOfArrays) {
			lengthOfOutput += array.length;
		}
		double[] output = new double[lengthOfOutput];
		int[] indexOfEachArray = new int[arrayOfArrays.length];
		double smallestElement = 0;
		int smallestIndex ;
		for (int i = 0; i < output.length; i++) {
			
			// init with the first value that is still in range
			boolean foundAnArray = false;
			int m = 0;
			while (!foundAnArray)
				{
					if(indexOfEachArray[m] < arrayOfArrays[m].length )
					{
						foundAnArray = true;
					}
					m++;
				}
			smallestElement = arrayOfArrays[m-1][ indexOfEachArray[m-1] ];
			smallestIndex = m-1;
			
			for (int j = m; j < arrayOfArrays.length; j++) {
				
				if ( indexOfEachArray[j]  <  arrayOfArrays[j].length )
				{
					if ( arrayOfArrays[j][ indexOfEachArray[j] ] < smallestElement)
					{
						smallestElement = arrayOfArrays[j][ indexOfEachArray[j] ];
						smallestIndex = j;
					}
				}
				
			}
			
			output[i] = smallestElement;
			indexOfEachArray[smallestIndex] ++ ;
			
		}
		
		return output;
	}
	
	public static void main(String[] args)
	{
		
		
		ArrayList<Double> output2 = Noise.sumItUponManyNeurons2(500, 0.005 , 1000);
		
		
		Neuron neuron = new Neuron();
		neuron.spikeTimes = output2 ;
		
		WriteFile.writeSpikeTimes(new Neuron[] {neuron}, "noiseFile.txt");
		
	}
	
	private static ConcurrentLinkedQueue<Double> changeListToQueue(List<Double> aList)
	{
		ConcurrentLinkedQueue<Double> result = new ConcurrentLinkedQueue<Double>();
		
		for (Double element : aList) {
			result.add(element);
		}
		return result;
	}
}
