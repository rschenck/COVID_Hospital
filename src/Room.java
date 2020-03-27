import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;

public class Room extends AgentGrid2D<Person> {
    int xDim;
    int yDim;
    int id=0;
    int posIdx;
    Rand rng=new Rand();
    int[]mooreHood= Util.MooreHood(false);
    int nOptions;

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
            if (p.status == 1) {
                nOptions = MapEmptyHood(this.mooreHood, p.Xsq(), p.Ysq());
                if (nOptions > 0) {
                    posIdx = GetPatientPosition(p, nOptions);
                    p.MoveSQ(mooreHood[posIdx]);
                }
            } else {
                nOptions = MapEmptyHood(this.mooreHood, p.Xsq(), p.Ysq());
                if (nOptions > 0) {
                    posIdx = GetPosition(p, nOptions);
                    p.MoveSQ(mooreHood[posIdx]);
                }
            }

            if(p.infectionStatus==1 && 8-nOptions>0) { // The 8 is specific to a Moore neighbourhood. Needs to be changed for other ngb types
                for (Person e : this.IterAgentsRad(p.Xsq(), p.Ysq(), Constants.INFECTIONRADIUS)) {
                    if (e.infectionStatus == 0 && rng.Double() < Constants.INFECTIONPROB) {
                        e.infectionStatus = 2;
                    }
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

        /*
        Patients will leave once they've completed Objectives
         */
        ArrayList<Integer> DeadIDs = new ArrayList<>();
        for(Person p: this){
            // Check if this person is infected, and if so if it passes on its infection
            if(p.infectionStatus==1 && 8-nOptions>0){ // The 8 is specific to a Moore neighbourhood. Needs to be changed for other ngb types
                for(Person e: this.IterAgentsRad(p.Xsq(), p.Ysq(), Constants.INFECTIONRADIUS)){
                    if(e.infectionStatus==0 && rng.Double()<Constants.INFECTIONPROB){
                        e.infectionStatus=2;
                    }
                }
            }
          
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
        Person p=NewAgentSQ(xDim/2,0).Init(1, rng.Int(Constants.VISITORS), id, GetTick(), 0, rng.Double()<Constants.INFECTEDBEFOREVISITPROB ? 1: 0); // 0 Direction is to center
        id++;
            for (int i = 0; i < p.numVisitors; i++) {
                NewAgentSQ(xDim/2+i+1,0).Init(2,0, p.patid, GetTick(), -1, rng.Double()<Constants.INFECTEDBEFOREVISITPROB ? 1: 0);
            }
    }

    public void DrawModel(GridWindow win){
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
            }
            win.SetPix(i,color);
        }
    }
}
