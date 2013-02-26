package neuron;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import configuration.ConfigurationReader;


public class runNeurons {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//double Iext = 9.7;
		
		//int numberOfNeurons = 100;
		
		//% Simulation time
		NeuronalGroup[] groups = ConfigurationReader.getGroups("config.xml");
		double t_final = 500; //                stop after msec 
		double dt = 0.05;                 //  step size of ... msec
		int totalTime = (int)(t_final/dt);
		int IextStartTime = 15;
		int IextEndTime = 20 ; // in msec
		IextEndTime = (int)(IextEndTime / dt) ; // 
		
		// Initial conditions
		double V = -64.8;
		NeuronalGroup groupA = new NeuronalGroup(100,V, "groupA");
		NeuronalGroup groupB = new NeuronalGroup(100,V, "groupB");
		NeuronalGroup groupC = new NeuronalGroup(100,V, "groupC");
		
		double synapseStrength ;
		double p = 0.8;
		
		synapseStrength = (double)5/(double)groupC.size();
		//groupA.getInputFromGroup(groupC,synapseStrength,15, true, dt);
		groupA.getInputFromGroupWithProbability(groupC, synapseStrength, 15, true,dt, p);
		
		synapseStrength = (double)5/(double)groupA.size();
		//groupB.getInputFromGroup(groupA,synapseStrength,15, true, dt);
		groupB.getInputFromGroupWithProbability(groupA, synapseStrength, 15, true,dt, p);


		synapseStrength = (double)5/(double)groupB.size();
		//groupC.getInputFromGroup(groupB,synapseStrength,15, true, dt);
		groupC.getInputFromGroupWithProbability(groupB, synapseStrength, 15, true,dt, p);


		
		NeuronalGroup allNeurons = new NeuronalGroup(0,"allNeurons");
		allNeurons.addAllNeuronsInOtherGroup(groupA);
		allNeurons.addAllNeuronsInOtherGroup(groupB);
		allNeurons.addAllNeuronsInOtherGroup(groupC);

		allNeurons.addNoiseToEachNeuronInGroup(t_final/dt, 0.005*dt, 800, 0.25 ,0); // add exicatory noise
		allNeurons.addNoiseToEachNeuronInGroup(t_final/dt, 0.005*dt, 200, 0.25 ,-80); // add ihibitory noise
		// 888 exicattory, 1-4 Hz , neuron should fire something like 6 spikes
		Neuron[] neurons = new Neuron[allNeurons.size()];
		allNeurons.neuronsInGroup.toArray(neurons);

		double[][] Iext = createExternalCurrent(neurons.length, 9.7, 0, 100 , IextStartTime, IextEndTime, totalTime);
		int Nt = (int) Math.floor(t_final/dt);
		
		double[] voltageInTime = new double[neurons.length];
	
	 
		File f=new File("textfile3.txt");
     	try {
			FileOutputStream fop=new FileOutputStream(f);
				
			for (int t = 0; t < Nt; t++) {
	
				for (int neuronIndex=0; neuronIndex<neurons.length; neuronIndex++)
				{
					voltageInTime[neuronIndex] = neurons[neuronIndex].calcStates(Iext[t][neuronIndex], t, dt);
				}
				
					
				writeTrainToFile(voltageInTime,fop);
				if ((t%1000) ==0)
				{
					System.out.println(100*t/Nt );
					fop.flush();
				}
					
			}
			System.out.println("finished phase 1" );
			
			fop.close();
	        System.out.println("The data has been written");
	        
	     	} 
	     	catch (FileNotFoundException e) {
				System.out.println("This file does not exist");
			} catch (IOException e) {
				System.out.println("An IO error has accord");
			}
			WriteFile.writeSpikeTimes(neurons, "spikeTimes.txt");
	     	 
	}
	/**
	 * writes the spike train to a file with space separating the different neuron
	 * and a line separating the different times
	 * @param twoDimDoubleArray first dim - time , second dim - neuron Index
	 * @throws IOException 
	 */
	public static void writeTrainToFile(double[] twoDimDoubleArray,FileOutputStream fop) throws IOException 
	{
      	String eol = System.getProperty( "line.separator" );
      	String tempRow ="";
      	
  		
		for (int i=0; i< twoDimDoubleArray.length; i++)
		{
			tempRow += twoDimDoubleArray[i] + " ";
		}
		fop.write(tempRow.getBytes());
		fop.write(eol.getBytes());
    }
	public static void writeTrainToFile(ArrayList<Double> array,FileOutputStream fop) throws IOException 
	{
      	String eol = System.getProperty( "line.separator" );
      	String tempRow ="";
      	
  		Iterator<Double> iter = array.iterator();
		while(iter.hasNext())
		{
			tempRow += iter.next() + " ";
		}
		fop.write(tempRow.getBytes());
		fop.write(eol.getBytes());
    }
	public static void writeTrainToFilewithPad(ArrayList<Double> array,FileOutputStream fop,int desiredLength) throws IOException 
	{
		if (array.size() < desiredLength)
		{
			for(int j=array.size(); j<desiredLength; j++)
				array.add(Double.NaN);
		}
		writeTrainToFile(array, fop);
    }
	
	public static void writeSpikeTimes(Neuron[] neurons, String fileName)
	{
		int longestSpike = neurons[0].getSpikeTimes().size();
		for (int neuronIndex=0; neuronIndex<neurons.length; neuronIndex++)
		{
			if (longestSpike < neurons[neuronIndex].getSpikeTimes().size())
				longestSpike = neurons[neuronIndex].getSpikeTimes().size();
		}
		
		File f=new File(fileName);
     	try {
			FileOutputStream fop=new FileOutputStream(f);
			ArrayList<Double> spikeTimes = new ArrayList<Double>();
			
			writeTrainToFilewithPad(neurons[0].getSpikeTimes(),fop,longestSpike);
			for (int neuronIndex=1; neuronIndex<neurons.length; neuronIndex++)
			{
				spikeTimes = neurons[neuronIndex].getSpikeTimes();
				if (spikeTimes.size()==0)
					spikeTimes.add(Double.NaN);
				writeTrainToFile(spikeTimes,fop);
			}

			fop.flush();
			fop.close();			
			System.out.println("finished phase writing spike times" );
			} 
	     	catch (FileNotFoundException e) {
				System.out.println("This file does not exist");
			} catch (IOException e) {
				System.out.println("An IO error has accord");
			}
	 
	}
	/**
	 * createExternalCurrent creates an array of doubles where the first dimension is the time when the external current
	 * should be entered and the second is the index of the neuron that receives the external current
	 * the external input will be creating starting with startTime until endTime
	 * the external input will be creating starting with startNeuronIndex until endNeuronIndex
	 * @param numOfNeurons 
	 * @param currentAmplitude the same amplitude to all the neurons, in micro Amper
	 * @param startNeuronIndex  
	 * @param endNeuronIndex 
	 * @param startTime
	 * @param endTime
	 * @param totalTime
	 * @return a matrix of doubles where the first dimension is time and the second is neuron index
	 */
	public static double[][] createExternalCurrent(int numOfNeurons,double currentAmplitude,int startNeuronIndex, int endNeuronIndex ,int startTime, int endTime,int totalTime)
	{
		double[][] externalCurrentInTime = new double[totalTime][numOfNeurons];
		if (startTime >= 0 & startNeuronIndex >= 0)
		{
			if (endTime <= totalTime & endNeuronIndex <=numOfNeurons)
			{
				for (int j=startTime;j<endTime;j++) 
				{
					for (int i=startNeuronIndex;i<endNeuronIndex;i++)
					{
						externalCurrentInTime[j][i] = currentAmplitude;
					}
				}
			}	
			else
				System.out.println("end time of external (or endNeuronIndex) current is larger then total time (or numberOfNeurons) , no external input was entered");		
				
		}
		else
			System.out.println("start time is not possitive (or startNeuronIndex), no external input was entered");	
		
		return externalCurrentInTime;
	}
}
