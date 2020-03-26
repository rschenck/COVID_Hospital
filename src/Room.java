import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;

public class Room extends AgentGrid2D<Person> {
    int xDim;
    int yDim;
    int id=0;
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
        // New Patients and Visitors
        if(Pop()<Constants.CAPACITY && rng.Double()<Constants.NEWPATRATE){
            Person p=NewAgentSQ(xDim/2,0).Init(1, rng.Int(Constants.VISITORS), id, GetTick());
            id++;
            for (int i = 0; i < p.numVisitors; i++) {
                NewAgentSQ(xDim/2+i+1,0).Init(2,0,p.patid, GetTick());
            }
        }

        for(Person p: this){
            if(p.status==1){
                nOptions=MapEmptyHood(this.mooreHood, p.Xsq(), p.Ysq());
                if(nOptions>0){
                    p.MoveSQ(mooreHood[rng.Int(nOptions)]);
                }
            } else {
                nOptions=MapEmptyHood(this.mooreHood, p.Xsq(), p.Ysq());
                int posIdx = GetPosition(p, nOptions);
                if(nOptions>0){
                    p.MoveSQ(mooreHood[posIdx]);
                }
            }

            // Leave Patient & Visitors
//            if(GetTick()-p.entered>50 && rng.Double()<0.2){
//                p.Dispose();gt
//            }
        }

        IncTick();
        CleanAgents();
        ShuffleAgents(rng);
    }

    public int GetPosition(Person thisPerson, int nOptions, int objective) {
        double closest = 10000000;
        int closestIdx = -10;
        double distance;
        for (Person p : this) {
            if (p.patid == thisPerson.patid && p.status == 1) {
                for (int i = 0; i < nOptions; i++) {
                    if(thisPerson.direction==-1){
                        distance = Distance(p.Xsq(), ItoX(mooreHood[i]), p.Ysq(), ItoY(mooreHood[i]));
                    }  else {
                        distance = Distance()
                    }
                    if (closest > distance) {
                        closest = distance;
                        closestIdx = i;
                    }
                }

                if (rng.Double() < 0.8) { // 80% chance you go to the empty position closest to your patient
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
        Person p=NewAgentSQ(xDim/2,0).Init(1, rng.Int(Constants.VISITORS), id, GetTick());
        id++;
            for (int i = 0; i < p.numVisitors; i++) {
                NewAgentSQ(xDim/2+i+1,0).Init(2,0, p.patid, GetTick());
            }

    }

    public void DrawModel(GridWindow win){
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
            }
            win.SetPix(i,color);
        }
    }
}
