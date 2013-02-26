package neuron;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
public class WriteFile{

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
	
	public static void writeTrainToFilewithPad(ArrayList<Double> array,FileOutputStream fop,int desiredLength) throws IOException 
	{
		if (array.size() < desiredLength)
		{
			for(int j=array.size(); j<desiredLength; j++)
				array.add(Double.NaN);
		}
		writeTrainToFile(array, fop);
    }
	
	/**
	 * writes the spike train to a file with space separating the different neuron
	 * and a line separating the different times
	 * @param twoDimDoubleArray first dim - time , second dim - neuron Index
	 * @throws IOException 
	 */
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
  }
