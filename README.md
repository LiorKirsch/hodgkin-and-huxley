This program simulate a few clustered of neurons connected in a specific topolgy.
Each neuron in the group is connected to another cluster with a probablty p.

The first clustered gets an external input and then the system starts to run.
with different configuration you can notice several behaviors in the system:
1. The system oscillates.
2. The oscilations weakens until they disapear.
3. The oscilations get stronger and recuriting all the members of the cluster.

More information in the presentation file.


config.xml - configuration file 
>> how many files in each cluster of neurons.
>> how the clustered of neurons are connected to each other.


runNeurons.java - the main run file
>> runs the simulation with the current topology
>> outputs to textfile3.txt
>> you can change the connection probabilty and the external input strength to see different results.


matlabPlot.m - plots the spiketrain
>> reads 'textfile3.txt' and shows the spike trains.
