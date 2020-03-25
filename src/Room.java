import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;

public class Room extends AgentGrid2D<Person> {
    int xDim;
    int yDim;
    Rand rng=new Rand();
    int[]divHood= Util.VonNeumannHood(false);

    public Room(int x, int y) {
        super(x, y, Person.class);
        xDim = x;
        yDim = y;
        initialize();
    }

    public void initialize(){
//        Person p=new Init(1);
        NewAgentSQ(xDim/2,0).Init(1);
    }

    public void DrawModel(GridWindow win){
        for (int i = 0; i < length; i++) {
            int color=Util.BLACK;
            Person p=GetAgent(i);
            if(p!=null){
                color=Util.WHITE;
            }
            win.SetPix(i,color);
        }
    }
}
