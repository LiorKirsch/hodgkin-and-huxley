package tmpPackage;
import java.util.LinkedList;

import neuron.Neuron;


public class NeuronalGroup2 {
	public LinkedList<Neuron> groupNeurons = new LinkedList<Neuron>();
	
	public void addNeuronToGroup(Neuron neuron)
	{
		this.groupNeurons.add(neuron);
	}
	
	public void addNeuronToGroup(Neuron[] neurons, int startingNeuronIndex , int lastNeuronIndex)
	{
		
		for (int i = startingNeuronIndex; i < lastNeuronIndex; i++) {
			this.groupNeurons.add(neurons[i]);
		}
		
	}
	
	public void getInputFromNeuron (Neuron neuron,double synapseStrength,double synapseLatency, boolean excitatory ,double dt)
	{
		
		for (Neuron neuronInGroup : this.groupNeurons) {
			neuronInGroup.addInputNeuron(neuron, synapseStrength, synapseLatency, excitatory, dt);
		}
	}
	
	public void getInputFromGroup (NeuronalGroup2 otherNeuronalGroup,double synapseStrength,double synapseLatency, boolean excitatory ,double dt)
	{
		for (Neuron neuronInOtherGroup : otherNeuronalGroup.groupNeurons) {
			this.getInputFromNeuron(neuronInOtherGroup, synapseStrength, synapseLatency, excitatory, dt);
		}
	}

}
