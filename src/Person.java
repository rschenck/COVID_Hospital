import Framework.GridsAndAgents.AgentPT2D;

public class Person extends AgentPT2D<Room> {
    int patid; // patient ID or visitor for that patient
    int status; // 1 for patient, 2 for visitor,

    public void Init(int stat){
        this.status=stat;
    }


}
