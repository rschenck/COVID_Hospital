clear all;
% url = 'https://github.com/nytimes/covid-19-data/blob/master/us-counties.csv';
% data = webread(url,'ContentType','csv');

dataIn = readtable('us-counties.csv');
temp=zeros(size(dataIn,1),1);
temp(string(dataIn.state)=='Florida')=1;
data(:,1)=temp;%string(dataIn.state)=='Florida';
data(:,2)=datenum(dataIn.date(:)-dataIn.date(1));
dataIn.fips(isnan(dataIn.fips))=99999;% set nans to 99999
data(:,3)=dataIn.fips;
data(:,4)=dataIn.cases;
data(:,5)=dataIn.deaths;

dataFL=data(data(:,1)==1,2:5);%saves: date, county, cases, deaths
dataFL(:,2)=dataFL(:,2)-12000;

% [times cases deaths] = getFLDataOverTime(dataFL,40,64);
% plot(times,cases,'k');hold on;
% plot(times,deaths,'r');

[times cases deaths] = getHillsDataOverTime(dataFL,40,64);
plot(times,cases,'k');hold on;
plot(times,deaths,'r');

function [times cases deaths] = getFLDataOverTime(dataFL,tp_i,tp_f)
    ind=1;
    for t=tp_i:tp_f
        times(ind)=t;
        cases(ind)=0;
        deaths(ind)=0;
        for i=1:size(dataFL,1)
            if(dataFL(i,1)==t)
                cases(ind)=cases(ind)+dataFL(i,3);
                deaths(ind)=deaths(ind)+dataFL(i,4);
            end
        end
        ind=ind+1;
    end
end

function [times cases deaths] = getHillsDataOverTime(dataFL,tp_i,tp_f)
    ind=1;
    for t=tp_i:tp_f
        times(ind)=t;
        cases(ind)=0;
        deaths(ind)=0;
        for i=1:size(dataFL,1)
            if(dataFL(i,1)==t & dataFL(i,2)==57)
                cases(ind)=cases(ind)+dataFL(i,3);
                deaths(ind)=deaths(ind)+dataFL(i,4);
            end
        end
        ind=ind+1;
    end
end

