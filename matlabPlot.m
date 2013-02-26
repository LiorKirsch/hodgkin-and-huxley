clear all; 
close all;
%origData=open('textfile1.txt');
dt = 0.05;
%fid = fopen('textfile3.txt','r');             % Open text file
%InputText=textscan(fid,'%s',[2,inf],'delimiter','\n'); % Read strings delimited by  a carriage return
%InputText=textscan(fid, '%f');
%fclose(fid);
InputText = importdata('textfile3.txt');
xvalues = 0:dt:((length(InputText) -1)*dt);

subplot(4,1,1);
Intro=InputText(:,1);
plot(xvalues,Intro);
xlabel('time [msec]');
ylabel('V');
subplot(4,1,2);
Intro=InputText(:,2);
plot(xvalues,Intro);
xlabel('time [msec]');
ylabel('V');
subplot(4,1,3);
Intro=InputText(:,3);
plot(xvalues,Intro);
xlabel('time [msec]');
ylabel('V');
subplot(4,1,4);
Intro=InputText(:,4);
plot(xvalues,Intro);
xlabel('time [msec]');
ylabel('V');


figure;
hold on;
inputSpikes = importdata('spikeTimes.txt');
inputSpikes = inputSpikes * 0.05;

%inputSpikes = importdata('noiseFile.txt');


for neuronIndex=1:size(inputSpikes,1)
    for i=1:size(inputSpikes,2)
        line([inputSpikes(neuronIndex,i) inputSpikes(neuronIndex,i)], [neuronIndex neuronIndex+1]);
    end
end
soundArray = zeros(1,501);

for i = 1:size(inputSpikes,2)
    if (inputSpikes(1,i) < 500 )
        soundArray( round(inputSpikes(1,i))  ) = 200;
    end
end
sound(soundArray);

xlim([0 500]);
xlabel('time [msec]');
ylabel('neuron #');


