package mathStuff;

import static java.lang.Math.exp;

import java.util.ArrayList;
import neuron.Neuron;
import neuron.NeuronalConnection;


public class NeuronMath {

	/**
	 * 
	 * @param t the time
	 * @param tau characteristic time for the synaptic current
	 */
	public static double alphaFunction (double t)
	{
		double tauD = 10;
		double tauR = 1;
		double result = 0;
		if (t>0)
			result = 1 /(tauD - tauR) * ( exp(-t / tauD) - exp(-t/tauR));

		return result;
	}
	/*
	public static double calcIsynaptic(double currentTime , Neuron thisNeuron)
	{
		double Isyn = 0;
		double gSyn = 3; // not sure about this value 
		if (thisNeuron.inputSynapses != null)
		{
			Iterator<NeuronalConnection> iteratorSynapse = thisNeuron.inputSynapses.iterator();
			for(int i=0;i < thisNeuron.inputSynapses.size();i++)
			{
				
				NeuronalConnection currentSynapse = iteratorSynapse.next(); 
				Neuron currentNeuron = currentSynapse.neuron ;
				ArrayList<Double> currentNeuronSpikeTimes = currentNeuron.getSpikeTimes();
				for (int j = 0; j < currentNeuronSpikeTimes.size(); j++) {
					Isyn += -1  * gSyn * NeuronMath.alphaFunction(currentTime -currentNeuronSpikeTimes.get(j) - currentSynapse.synapseLatency ) * (thisNeuron.V - currentSynapse.vSyn );
				}
			}
		}
		return Isyn;
	}*/
	
	
	
	public static double calcIsynaptic (Neuron currentNeuron ,double currentTime ,double delta_t)
	{
		delta_t = 1;
		double Isyn = 0;
		//double gSyn = 3; // not sure about this value
		double tauD = 10;
		double tauR = 1;
		
		if (currentNeuron.inputSynapses != null)
		{
			ArrayList<NeuronalConnection> input = currentNeuron.inputSynapses;
			for (NeuronalConnection currentSynapse : input) {
				currentSynapse.neuronYrecursive = currentSynapse.neuronYrecursive * Math.exp (-delta_t /tauD) ;
				currentSynapse.neuronZrecursive = currentSynapse.neuronZrecursive * Math.exp (-delta_t /tauR) ;
				// check if the last spike - synapse latency is in the current bin 0< and <dt
				//	System.out.println("************************************" + (currentSynapse.neuron.lastSpikeTime - currentTime) );
				
				if(  currentSynapse.lastSpikeTime.size() > 0  && (currentTime - currentSynapse.lastSpikeTime.peek() - currentSynapse.synapseLatency) >= 0)
				{
					currentSynapse.lastSpikeTime.poll();
					currentSynapse.neuronYrecursive = currentSynapse.neuronYrecursive + 1 / (tauD - tauR);
					currentSynapse.neuronZrecursive = currentSynapse.neuronZrecursive + 1 / (tauD - tauR);
				}
				
				
				Isyn += -1 * currentSynapse.synapseStrength *(currentSynapse.neuronYrecursive - currentSynapse.neuronZrecursive ) * (currentNeuron.V - currentSynapse.vSyn );
								
			}
			
		}
		return Isyn;
	}


}
