clear all;
datType = 'c';%c=all counties,s=states,
daysInUS=68;% as of 3/28
logMax=8;%right now max is 1121

%references -  
fipStates=[1:2 4:6 8:10 12:13 15:42 44:51 53:56 60 66 69 72 78]';%FL=12
fipFLCounties=[1:2:23 27:2:85 86 87:2:133]';%Hillsborough=12057

if(datType=='c')
    [data]=loadCountyData();
    
    inds=find(data(:,1)==1);
    dataFL=data(inds,2:5);
    [cases deaths] = getFLCountiesDataOverTime(dataFL,fipFLCounties,daysInUS);

    [pType props fipMap s2t] = loadMapData();
    [xs ys] = getPolygons(pType,props,fipMap,s2t);
    for t=40:daysInUS
        for c=1:size(fipFLCounties,1)
            mapDataOverTimeFL(c,t,cases(c,t),xs,ys,logMax);
        end
        makeMovie(t);
    end

    % [times cases deaths] = getFLDataOverTime(dataFL,40,64);
    % plot(times,cases,'k');hold on;
%     plot(times,deaths,'r');
% 
%     [times cases deaths] = getHillsDataOverTime(dataFL,40,64);
%     plot(times,cases,'k');hold on;
%     plot(times,deaths,'r'); 
elseif(datType=='s')
    data=loadStateData();
    [times cases deaths]=getStatesDataOverTime(data,fipStates); 
end

%%%%%%%%%%%%%%functions%%%%%%%%%%%%%%%%%%%%

%%%%%%%%drawing%%%%%%%%%%%%%%

function [times cases deaths] = barsStatesOverTime(data,fipStates)
    times=zeros(78,64);%time points x states
    cases=zeros(size(fipStates,1),65);
    deaths=zeros(size(fipStates,1),65);%time points x states
    for s=1:size(fipStates,1)
        inds=find(data(:,2)==fipStates(s));
        cases(s,data(inds,1)+1)=data(inds,3);
        deaths(s,data(inds,1)+1)=data(inds,4);
    end
    
    for t=1:65
        subplot(2,1,1);
        bar(cases(:,t));
        set(gca,'yscale','log')
        subplot(2,1,2);
        bar(deaths(:,t));
        pause(0.1);
    end
end

function [] = plotStatesCasesvsDeaths(data,fipStates)
    cases=zeros(size(fipStates,1),65);
    deaths=zeros(size(fipStates,1),65);%time points x states
    for s=1:size(fipStates,1)
        inds=find(data(:,2)==fipStates(s));
        cases(s,data(inds,1)+1)=data(inds,3);
        deaths(s,data(inds,1)+1)=data(inds,4);
        if(fipStates(s)==12 || fipStates(s)==36 || fipStates(s)==53 || fipStates(s)==22) 
            plot(1:65,cases(s,:));hold on;
        end
    end
end

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

function [xs ys] = getPolygons(pType,props,fipMap,s2t)
    for c=1:67
        if(pType(fipMap(c))=="Polygon")
            m=s2t.coordinates{fipMap(c)};
            xs{c}=m(:,:,1);
            ys{c}=m(:,:,2);
        else
            m=s2t.coordinates{fipMap(c)};
            m2=m{1};
            xs{c}=m2(:,:,1);
            ys{c}=m2(:,:,2);
        end
        
    end
end

function [] = mapDataOverTimeFL(c,t,data,xs,ys,logMax)
%     if(data==0)
%         logData=0;
%     elseif(data<3)
%         logData=1;
%     else
%         logData=log(data);
%     end
    logData=log(data);
    patch(xs{c},ys{c},logData);hold on;
    colormap(flipud(hot));
    caxis([0 logMax]);
    colorbar;
end

function [] = makeMovie(t)
    F(t) = getframe;
    im2 = frame2im(F(t));
    [imind,cm] = rgb2ind(im2,256);
    tempName = ['movie/cplot',num2str(t),'.gif'];
    imwrite(imind,cm,tempName,'gif');
end

% get specific data

function [cases deaths] = getFLCountiesDataOverTime(dataFL,fipFLCounties,daysInUS)
    cases=zeros(size(fipFLCounties,1),daysInUS);
    deaths=zeros(size(fipFLCounties,1),daysInUS);%time points x states
    for c=1:size(fipFLCounties,1)
        inds=find(dataFL(:,2)==12000+fipFLCounties(c));
        cases(c,dataFL(inds,1)+1)=dataFL(inds,3);
        deaths(c,dataFL(inds,1)+1)=dataFL(inds,4);
    end
end

function [times cases deaths] = getStatesDataOverTime(data,fipStates)
    times=zeros(78,64);%time points x states
    cases=zeros(size(fipStates,1),65);
    deaths=zeros(size(fipStates,1),65);%time points x states
    for s=1:size(fipStates,1)
        inds=find(data(:,2)==fipStates(s));
        cases(s,data(inds,1)+1)=data(inds,3);
        deaths(s,data(inds,1)+1)=data(inds,4);
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

%load data

function [data] = loadCountyData()
    dataIn = readtable('us-counties.csv');
    temp=zeros(size(dataIn,1),1);
    temp(string(dataIn.state)=='Florida')=1;
    data(:,1)=temp;%string(dataIn.state)=='Florida';
    data(:,2)=datenum(dataIn.date(:)-dataIn.date(1));
    dataIn.fips(isnan(dataIn.fips))=99999;% set nans to 99999
    data(:,3)=dataIn.fips;
    data(:,4)=dataIn.cases;
    data(:,5)=dataIn.deaths;
    
    
%      dataIn = readtable('us-counties.csv');
%     temp=zeros(size(dataIn,1),1);
%     temp(string(dataIn.state)=='Florida')=1;
%     data(:,1)=temp;%string(dataIn.state)=='Florida';
%     data(:,2)=datenum(dataIn.date(:)-dataIn.date(1));
%     dataIn.fips(isnan(dataIn.fips))=99999;% set nans to 99999
%     data(:,3)=dataIn.fips;
%     data(:,4)=dataIn.cases;
%     data(:,5)=dataIn.deaths;
end

function [data] = loadStateData()
    dataIn=readtable('us-states.csv');
    data(:,1)=datenum(dataIn.date(:)-dataIn.date(1));
    dataIn.fips(isnan(dataIn.fips))=99999;% set nans to 99999
    data(:,2)=dataIn.fips;
    data(:,3)=dataIn.cases;
    data(:,4)=dataIn.deaths;
end

function [pType props fipMap s2t] = loadMapData()
    fname = 'Florida_Counties.geojson';
    fid = fopen(fname);
    raw = fread(fid,inf);
    str = char(raw');
    fclose(fid);
    val = jsondecode(str);
    props=struct2table([val.features.properties]);
    s2t=struct2table([val.features.geometry]);
    pType=string(s2t.type);
    
    %sort fips
    for c=1:67
        cc(c)=str2num(props.COUNTY{c});
    end
    cc(cc==25)=86;%Dade County gone, Miami-Dade is 86;
    [cc2 fipMap]=sort(cc); 
end

