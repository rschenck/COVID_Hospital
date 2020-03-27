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
    int[]hood;
    int nOptions;
    int[][] floorplan = new int[xDim][yDim];
    double closest; // For movement
    int closestIdx; // For movement
    double distance; // For movement

    /*
    Init function
     */
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
            if(Constants.FLOORPLAN){hood= GetFloorplan(floorplan,p.Xsq(),p.Ysq());}
            else{hood=this.mooreHood;}
            if (p.status == 1) {
                nOptions = MapEmptyHood(hood, p.Xsq(), p.Ysq());
                if (nOptions > 0) {
                    posIdx = GetPatientPosition(p, nOptions);
                    p.MoveSQ(hood[posIdx]);
                    System.out.println(p.posCount);
                }
            } else {
                nOptions = MapEmptyHood(hood, p.Xsq(), p.Ysq());
                if (nOptions > 0) {
                    posIdx = GetPosition(p, nOptions);
                    p.MoveSQ(hood[posIdx]);
                }
            }

            /*
            Infection Dynamics during movement
             */
            if(p.infectionStatus==1 && 8-nOptions>0) { // The 8 is specific to a Moore neighbourhood. Needs to be changed for other ngb types
                for (Person e : this.IterAgentsRad(p.Xsq(), p.Ysq(), Constants.INFECTIONRADIUS)) {
                    if (e.infectionStatus == 0 && rng.Double() < Constants.INFECTIONPROB) {
                        e.infectionStatus = 2;
                    }
                }
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

        /*
        Bring in new patients and their visitors
         */
        if(Pop()<Constants.CAPACITY && rng.Double()<Constants.NEWPATRATE){
            Person p=NewAgentSQ(xDim/2,0).Init(1, rng.Int(Constants.VISITORS), id, GetTick(), 0, rng.Double()<Constants.INFECTEDBEFOREVISITPROB ? 1: 0);
            id++;
            for (int i = 0; i < p.numVisitors; i++) {
                NewAgentSQ(xDim/2+i+1,0).Init(2,0,p.patid, GetTick(), -1, rng.Double()<Constants.INFECTEDBEFOREVISITPROB ? 1: 0);
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
        closest = 10000000;
        closestIdx = -10;
        for (int i = 0; i < nOptions; i++) {
            // Distance function is being passed specific coordinates based on the "objectives" of the patients
            distance = Distance(Constants.LOCATIONS[2*p.direction], ItoX(hood[i]), Constants.LOCATIONS[2*p.direction+1], ItoY(hood[i]));
            if (closest > distance) {
                closest = distance;
                closestIdx = i;
            }
        }

        // Portion of Code Handles changing in direction
        // 0 = middle; 1 = right waiting room, 2 = left waiting room, 3 = main door
        if(closest<3 & p.direction==0 & !p.PatientWaited){
            // Picking a waiting room. 0 is center, thus +1
            p.direction=rng.Int(2)+1;
            p.posCount++;
        }

        // If patient has reached the waiting room they are able to persist there randomly
        else if(closest<4 & (p.direction==1 | p.direction==2)) {
            // If LeaveTrigger is not true
            if (p.LeaveTrigger != 0 & GetTick() - p.LeaveTrigger > Constants.VISITTIME & p.PatientWaited==false) { // 5 is duration. Will depend on parameterization
                // This is the main sorting point in hallway
                p.direction = 4;
                p.PatientWaited = true;
            }
            // If they haven't gotten the signal to leave yet wander randomly or some other function
            else if(p.PatientWaited==false & GetTick() - p.LeaveTrigger < Constants.VISITTIME & p.LeaveTrigger!=0) {
                return (rng.Int(nOptions));
            }
            // If they just reached the appropriate spot. Change the leave trigger.
            else if(p.LeaveTrigger == 0 & p.PatientWaited==false) {
                p.LeaveTrigger = GetTick();
                p.posCount++;
            }
        }

        // If patient waited and can now leave
        else if(closest<3 & p.direction==4 & p.PatientWaited){
            p.direction=3;
            p.posCount++;
        }
        // If patient waited, made way down hallway and can now leave (i.e. be disposed of)
        else if(closest<2 & p.direction==3 & p.PatientWaited){

            // signal to leave, meaning people are now within the distance to the exit
            p.direction=-2;

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
        closest = 10000000;
        closestIdx = -10;
        for (Person p : this) {
            if (p.patid == thisPerson.patid && p.status == 1) {
                for (int i = 0; i < nOptions; i++) {
                    distance = Distance(p.Xsq(), ItoX(hood[i]), p.Ysq(), ItoY(hood[i]));
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

    /*
    Initialization function when grid is called.
     */
    public void initialize(){
        Person p=NewAgentSQ(xDim/2-4,0).Init(1, rng.Int(Constants.VISITORS), id, GetTick(), 0, rng.Double()<Constants.INFECTEDBEFOREVISITPROB ? 1: 0); // 0 Direction is to center
        id++;
        for (int i = 0; i < p.numVisitors; i++) {
            NewAgentSQ(xDim/2+i+1,0).Init(2,0, p.patid, GetTick(), -1, rng.Double()<Constants.INFECTEDBEFOREVISITPROB ? 1: 0);
        }

        // Draw floorplan if using that option.
        if(Constants.FLOORPLAN){floorplan=initializeArray(Constants.floorFile);}
    }

    /*
    Loading of Floorplan into the Model
     */
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

    /*
    Floorplan for movement function.
     */
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


    /*
    Drawing Function
     */
    public void DrawModel(GridWindow win){
        // Drawing of the floorplan
        if(Constants.FLOORPLAN){
            for(int i=0;i<Constants.xSIZE;i++){
                for(int j=0;j<Constants.ySIZE;j++){
                    if(floorplan[i][j]==0){
                        win.SetPix(i,j,Util.WHITE);
                    }
                    else{
                        win.SetPix(i,j,Util.BLACK);
                    }
                }
            }
        }

        // Drawing of the people
        for (int i = 0; i < length; i++) {
            int color=Util.WHITE;
            Person p=GetAgent(i);
            if(p!=null){
                if (p.infectionStatus!=0){ // Infection Coloring
                    if(p.infectionStatus==1) {
                        color = Constants.INFECTED;
                    } else if(p.infectionStatus==2) {
                        color = Constants.NEWINFECT;
                    }
                } else { // Non-Infected Coloring
                  if(p.status==2) {
                      color=Constants.VISITOR;
                  }
                  if(p.status==1) {
                      color = Constants.PATIENT;
                  }
                }
                if(Constants.FLOORPLAN){win.SetPix(i,color);}
            }
            if(!Constants.FLOORPLAN){win.SetPix(i,color);}
        }
    }
}
