package neuron;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NeuronalGroup {
	LinkedList<Neuron> neuronsInGroup;
	public String groupName ;
	

	public NeuronalGroup(int numberOfMembers, String groupName) {
		this(numberOfMembers, -64.8,groupName);
	}
	

	public NeuronalGroup(int numberOfMembers, double V, String groupName) {
		neuronsInGroup = new LinkedList<Neuron>();
		this.groupName = groupName;
		for (int i = 0; i < numberOfMembers; i++) {
			neuronsInGroup.add( new Neuron(0,0,0,V) );
		}
	}
	
	
	public void getInputFromNeuron(Neuron inputNeuron ,double synapseStrength,double synapseLatency, boolean isExitatory, double dt)
	{
		for (Neuron neuronInGroup : neuronsInGroup) {
			neuronInGroup.addInputNeuron(inputNeuron, synapseStrength, synapseLatency,isExitatory,dt);
		}
	}
	
	public void getInputFromGroup(NeuronalGroup inputGroup,double synapseStrength,double synapseLatency, boolean isExitatory, double dt)
	{
		for (Neuron inputNeuron : inputGroup.neuronsInGroup) {
			this.getInputFromNeuron(inputNeuron, synapseStrength, synapseLatency,isExitatory, dt);
		}
	}
	public void getInputFromGroup(NeuronalGroup inputGroup,double synapseLatency, boolean isExitatory, double dt)
	{
		double synapseStrength = 3 / inputGroup.size();
		getInputFromGroup(inputGroup,synapseStrength,synapseLatency, isExitatory, dt);
	}
	public void getInputFromGroupWithProbability(NeuronalGroup inputGroup,double synapseStrength,double synapseLatency, boolean isExitatory, double dt, double p)
	{
		double rand = Math.random();
		
		if (rand <= p)
		{
			for (Neuron inputNeuron : inputGroup.neuronsInGroup) {
				this.getInputFromNeuron(inputNeuron, synapseStrength, synapseLatency,isExitatory, dt);
			}	
		}
		
	}
	
	public void addNeuron (Neuron newNeuron)
	{
		this.neuronsInGroup.add(newNeuron);
	}
	
	public void addAllNeuronsInOtherGroup (NeuronalGroup newGroup)
	{
		for (Neuron newNeuron : newGroup.neuronsInGroup) {
			this.addNeuron(newNeuron);
		}
	}
	public int size()
	{
		return neuronsInGroup.size();
	}
	/**
	 * 
	 * @param totalTime the total time of the tr
	 * @param possionRate the firing rate of each of the random poisson neurons
	 * @param numberOfNeuronToStimulate how many neurons to stimulate
	 * @param synapseStrength how strong is this noise synapse
	 * @param E_syn the resting potential of the synapse, 0 for exicatory, -80 for inhibitory
	 */
	public void addNoiseToEachNeuronInGroup (double totalTime, double possionRate, int numberOfNeuronToStimulate, double synapseStrength ,double E_syn)
	{
		System.out.println("adding noise synapse to group:" + this.groupName);
		for (Neuron neuron : this.neuronsInGroup) {
			ConcurrentLinkedQueue<Double> noise = Noise.getNoise(totalTime, possionRate, numberOfNeuronToStimulate);
			NoiseSynapse noiseSynapse = new NoiseSynapse(null,synapseStrength,5,E_syn,noise);
			neuron.inputSynapses.add(noiseSynapse);
		}
		System.out.println("finished creating noise to group");

	}
}
