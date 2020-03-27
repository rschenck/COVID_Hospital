import Framework.GridsAndAgents.AgentPT2D;
import Framework.Rand;
import Framework.Util;

public class Person extends AgentPT2D<Room> {
    int patid; // patient ID or visitor for that patient
    int status; // 1 for patient, 2 for visitor,
    int numVisitors;
    int entered;
    int infectionStatus; // 0 for healthy, 1 for infected, 2 for newly infected
    int direction;
    int LeaveTrigger;
    boolean PatientWaited;
    int posCount;
    int[] holdPos;
    int beenThere;
    Rand rng=new Rand();

    public Person Init(int stat, int numVisitors, int id, int tick, int direction, int infectionStatus){
        this.status=stat;
        this.numVisitors=numVisitors;
        this.patid=id;
        this.entered=tick;
        this.infectionStatus=infectionStatus;
        this.direction=direction;
        this.posCount=0;
        this.LeaveTrigger=0;
        this.PatientWaited=false;
        this.beenThere=0;
        this.holdPos=new int[]{rng.Double()<0.5 ? 0: Constants.xSIZE,rng.Double()<0.5 ? 0: Constants.ySIZE};
        return(this);
    }


}
