import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;
import Framework.Tools.FileIO;

import java.util.ArrayList;

public class Room extends AgentGrid2D<Person> {
    int xDim;
    int yDim;
    int id=0;
    int posIdx;
    Rand rng=new Rand();
    int[]mooreHood= Util.MooreHood(false);
    int nOptions;
    int[][] floorplan = new int[xDim][yDim];

    public Room(int x, int y) {
        super(x, y, Person.class);
        xDim = x;
        yDim = y;
        initialize();
    }

    public void TimeStep(){
        /*
        Movement of the peoples
         */
        for(Person p: this) {
            int[]hood;
            if(Constants.FLOORPLAN){hood= GetFloorplan(floorplan,p.Xsq(),p.Ysq());}
            else{hood=this.mooreHood;}
            if (p.status == 1) {
                nOptions = MapEmptyHood(hood, p.Xsq(), p.Ysq());
                if (nOptions > 0) {
                    posIdx = GetPatientPosition(p, nOptions);
                    p.MoveSQ(hood[posIdx]);
                }
            } else {
                nOptions = MapEmptyHood(hood, p.Xsq(), p.Ysq());
                if (nOptions > 0) {
                    posIdx = GetPosition(p, nOptions);
                    p.MoveSQ(hood[posIdx]);
                }
            }
        }

        /*
        Bring in new patients and their visitors
         */
        if(Pop()<Constants.CAPACITY && rng.Double()<Constants.NEWPATRATE){
            Person p=NewAgentSQ(xDim/2,0).Init(1, rng.Int(Constants.VISITORS), id, GetTick(), 0);
            id++;
            for (int i = 0; i < p.numVisitors; i++) {
                NewAgentSQ(xDim/2+i+1,0).Init(2,0,p.patid, GetTick(), -1);
            }
        }

        /*
        Patients will leave once they've completed Objectives
         */
        ArrayList<Integer> DeadIDs = new ArrayList<>();
        for(Person p: this){
            if(p.direction==-2){
                DeadIDs.add(p.patid);
            }
        }
        for (int i = 0; i < DeadIDs.size(); i++) {
            for(Person p: this){
                if(p.patid==DeadIDs.get(i)){
                    p.Dispose();
                }
            }
        }

        IncTick();
        CleanAgents();
        ShuffleAgents(rng);
    }

    /*
    This is used for the objectives. These are the leaders in movement
     */
    public int GetPatientPosition(Person p, int nOptions) {
        double closest = 10000000;
        int closestIdx = -10;
        double distance;
        for (int i = 0; i < nOptions; i++) {
            distance = Distance(Constants.LOCATIONS[2*p.direction], ItoX(mooreHood[i]), Constants.LOCATIONS[2*p.direction+1], ItoY(mooreHood[i]));
            if (closest > distance) {
                closest = distance;
                closestIdx = i;
            }
        }

        if(closest<3 & p.direction==0){ // Handles changing in direction
            p.direction=rng.Int(2)+1; // 0 is center
        } else if(closest<4 & (p.direction==1 | p.direction==2)) {
            int diff=GetTick()-p.LeaveTrigger;
            if (p.LeaveTrigger!=0 & GetTick()-p.LeaveTrigger>5){
                p.direction=3; // This is the entrance/exit
            } else {
                p.LeaveTrigger=GetTick();
            }
        } else if(closest<4 & p.direction==3){
            p.direction=-2; // signal to leave, meaning people are now within the distance to the exit
        }

        if (rng.Double() < 0.9) { // 80% chance you go to the empty position closest to your patient
            return (closestIdx);
        } else {
            return (rng.Int(nOptions));
        }
    }

    /*
    Used for the visitors. These are the followers as they simply move towards the leader/patient
     */
    public int GetPosition(Person thisPerson, int nOptions) {
        double closest = 10000000;
        int closestIdx = -10;
        double distance;
        for (Person p : this) {
            if (p.patid == thisPerson.patid && p.status == 1) {
                for (int i = 0; i < nOptions; i++) {
                    distance = Distance(p.Xsq(), ItoX(mooreHood[i]), p.Ysq(), ItoY(mooreHood[i]));
                    if (closest > distance) {
                        closest = distance;
                        closestIdx = i;
                    }
                }

                if (rng.Double() < 0.9) { // 80% chance you go to the empty position closest to your patient
                    return (closestIdx);
                } else {
                    return (rng.Int(nOptions));
                }
            }
        }
        return (rng.Int(nOptions));
    }

    public double Distance(int x1, int x2, int y1, int y2){
        return(Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2)));
    }

    public void initialize(){
//        Person p=new Init(1);
        Person p=NewAgentSQ(xDim/2,0).Init(1, rng.Int(Constants.VISITORS), id, GetTick(), 0); // 0 Direction is to center
        id++;
        for (int i = 0; i < p.numVisitors; i++) {
            NewAgentSQ(xDim/2+i+1,0).Init(2,0, p.patid, GetTick(), -1);
        }
        if(Constants.FLOORPLAN){floorplan=initializeArray(Constants.floorFile);}
    }

    static public int[][] initializeArray(String filename){
        int[][]array=new int[Constants.xSIZE][Constants.ySIZE];
        try{
            FileIO matrixIn =  new FileIO (filename,"r");
            ArrayList<int[]> readerD = matrixIn.ReadInts(" ");

            for (int i=0;i<Constants.xSIZE;i++){
                for (int j=0;j<Constants.ySIZE;j++) {
                    int temp=readerD.get(i)[j];
                    array[i][j]=temp;
                }
            }
        }
        catch(Exception e){
            System.out.println("Failure loading initial conditions");
        }
        return array;
    }

    public int[] GetFloorplan(int[][] array, int x, int y){
        int[][] ns = new int[][]{{1, 1},{1, 0},{1, -1},{0, -1},{-1, -1},{-1, 0},{-1, 1},{0, 1}};
        boolean[] ns2 = new boolean[8];
        int total=0;
        for (int i=0;i<8;i++){
            if(x+ns[i][0]>=0 && x+ns[i][0]<Constants.xSIZE && y+ns[i][1]>=0 && y+ns[i][1]<Constants.ySIZE) {
                if (array[x + ns[i][0]][y + ns[i][1]] == 0) {
                    total++;
                    ns2[i] = true;
                }
            }
        }
        int[] finalVals = new int[total*3];
        for (int i=0;i<total;i++){
            finalVals[i]=0;
        }
        for(int i=0;i<8;i++){
            if(ns2[i]){
                finalVals[total]=ns[i][0];
                finalVals[total+1]=ns[i][1];
                total+=2;
            }
        }
        return finalVals;
    }

    public void DrawModel(GridWindow win){
        if(Constants.FLOORPLAN){
            for(int i=0;i<Constants.xSIZE;i++){
                for(int j=0;j<Constants.ySIZE;j++){
                    if(floorplan[i][j]==0){
                        win.SetPix(i,j,Util.BLACK);
                    }
                    else{
                        win.SetPix(i,j,Util.WHITE);
                    }
                }
            }
        }
        for (int i = 0; i < length; i++) {
            int color=Util.BLACK;
            Person p=GetAgent(i);
            if(p!=null){
                if(p.status==2){
                    color=Util.RED;
                }
                if(p.status==1){
                    color=Util.WHITE;
                }
                if(Constants.FLOORPLAN){win.SetPix(i,color);}
            }
            if(!Constants.FLOORPLAN){win.SetPix(i,color);}
        }
    }
}
