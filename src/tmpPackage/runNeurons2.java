package tmpPackage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import neuron.Neuron;
import neuron.NeuronalGroup;


public class runNeurons2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//double Iext = 9.7;
		
		int numberOfNeurons = 30;
		
		//% Simulation time
		double t_final = 500; //                stop after msec 
		double dt = 0.05;                 //  step size of ... msec
		int totalTime = (int)(t_final/dt);
		int IextStartTime = 15;
		int IextEndTime = 20 ; // in msec
		IextEndTime = (int)(IextEndTime / dt) ; // 
		double[][] Iext = createExternalCurrent(numberOfNeurons, 9.7, 0, 10 , IextStartTime, IextEndTime, totalTime);
		// Initial conditions
		double V = -64.8;
		NeuronalGroup groupA = new NeuronalGroup(10,V);
		NeuronalGroup groupB = new NeuronalGroup(10,V);
		NeuronalGroup groupC = new NeuronalGroup(10,V);
		
		double synapseStrength ;

		synapseStrength = (double)3/(double)groupC.size();
		groupA.getInputFromGroup(groupC,synapseStrength,15, true, dt);

		synapseStrength = (double)3/(double)groupA.size();
		groupB.getInputFromGroup(groupA,synapseStrength,15, true, dt);

		synapseStrength = (double)3/(double)groupB.size();
		groupC.getInputFromGroup(groupB,synapseStrength,15, true, dt);

		
		NeuronalGroup allNeurons = new NeuronalGroup(0);
		allNeurons.addAllNeuronsInOtherGroup(groupA);
		allNeurons.addAllNeuronsInOtherGroup(groupB);
		allNeurons.addAllNeuronsInOtherGroup(groupC);

		
		Neuron[] neurons = new Neuron[allNeurons.size()];
		allNeurons.neuronsInGroup.toArray(neurons);
		/*neurons[0].addInputNeuron(neurons[2 ],1,10,true,dt);
		neurons[1].addInputNeuron(neurons[2 ],1,10,true,dt);
		neurons[0].addInputNeuron(neurons[3 ],1,10,true,dt);
		neurons[1].addInputNeuron(neurons[3 ],1,10,true,dt);
		*/
		
//		t_vec = [0:dt:t_final];
		int Nt = (int) Math.floor(t_final/dt);
		
		double[] voltageInTime = new double[numberOfNeurons];
	
	 
		File f=new File("textfile3.txt");
     	try {
			FileOutputStream fop=new FileOutputStream(f);
				
			for (int t = 0; t < Nt; t++) {
	
				for (int neuronIndex=0; neuronIndex<numberOfNeurons; neuronIndex++)
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
			writeSpikeTimes(neurons, "spikeTimes.txt");
	     	 
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
	 * createExternalCurrent creates an array of doubles were the first dimension is the time when the external current
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
