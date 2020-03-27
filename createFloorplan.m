clear all;
filename='~/Desktop/fp2.png';
sizeX=80;
sizeY=40;

file0=imread(filename);
aa = imresize(file0,[sizeY sizeX]);
bb=aa(:,:,1);
bb2=flipud(bb);
bb2(bb2<=100)=1;bb2(bb2>100)=0;
dlmwrite('floorplan.txt',bb2',' ');