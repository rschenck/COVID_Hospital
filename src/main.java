import Framework.Gui.GifMaker;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;

class Constants {
    // Waiting room size
    final static int xSIZE=80;
    final static int ySIZE=40; // assume square this is SIZE*SIZE

    // Floorplan option
    final static boolean FLOORPLAN=true; // Needs 80 x 40 currently
    final static String floorFile = "src/floorplan_waiting_rooms.txt";

    // Locations in Hospital
    // xy center, xy treat area 1, xy treat area 2, Leaving
//    final static int[] LOCATIONS=new int[]{Constants.xSIZE/2, Constants.ySIZE/2, 0, Constants.ySIZE, Constants.xSIZE, Constants.ySIZE, Constants.xSIZE/2, 0}; // Use if no floorplan
    final static int[] LOCATIONS=new int[]{Constants.xSIZE/2+1, Constants.ySIZE/2+2,
            Constants.xSIZE/10, Constants.ySIZE/2,
            (Constants.xSIZE/10)*9, Constants.ySIZE/2,
            Constants.xSIZE/2-2, 0,
            Constants.xSIZE/2-1, Constants.ySIZE/2-3};

    // Hospital Capacity
    final static int CAPACITY=200;

    // Time spent in waiting area
    final static int VISITTIME=100;

    // Length of Simulation
    final static int TIME=5000;

    // New Patient Rate
    final static double NEWPATRATE=0.1;

    // Transmission Rate
    final static double TRANSMISSION=0.3;

    // Visitors Possible (Max Number of Visitors). Equal probability.
    final static int VISITORS=3;

    // Movement Probability
    final static double MOVE=0.25;

    // Probability that a patient or visitor is infected when showing up to the clinic
    final static double INFECTEDBEFOREVISITPROB=0.1;

    // Radius over which an infected person can infect others
    final static double INFECTIONRADIUS=1.;

    // Probability of infection if you are within INFECTIONRADIUS of an infected person
    final static double INFECTIONPROB=0.1;

    final static boolean GETGIF=true;

    /*
    Color Scheme
     */
    final public static int PATIENT = Util.RGB256(35, 88, 148), VISITOR = Util.RGB256(114, 176, 222), INFECTED = Util.RGB256(164, 44, 37), NEWINFECT = Util.RGB256(229, 100, 95);
}

public class main {

    public static void main(String[] args) {
        GridWindow win=new GridWindow("COVID-19",Constants.xSIZE, Constants.ySIZE,10);
        Room g=new Room(Constants.xSIZE, Constants.ySIZE);
        GifMaker maker = new GifMaker("./this.gif",1,true);
        for (int i = 0; i < Constants.TIME; i++) {
            win.TickPause(1);
            g.TimeStep();

            //draw
            g.DrawModel(win);

            if(Constants.GETGIF){
                maker.AddFrame(win);
            }
        }
        if(Constants.GETGIF) {
            maker.Close();
        }

        win.Close();
    }

}
