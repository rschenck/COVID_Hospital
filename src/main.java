import Framework.Gui.GifMaker;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;

/*
Information for parameterizations....
A person can leisurely walk 1-2ft per second. If we assume that each square is a 3 ft area where a human exists in it.
Then one change in square position is at most 6 ft difference. So a timestep here can be miniscule 2 second.
If we have patients at a Moffitt site (3 total?) that have 450,760 visits per year. So (450,760 / 3) / (52*5) = 577.897 per day.
That is 72 patients per hour. Let's assume that each visit a patient is there for 3 hours for now.



Leaves us with:
2s Timestep (30 timesteps per minute, 5400 timesteps)
72 Patients per hour
3hr Duration of Visit (30 timesteps per minute, 5400 timesteps)
 */

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
    final static int CAPACITY=2000;

    // Time spent in waiting area
    final static int VISITTIME=(60 / 2)*(60 * 3); // Timestep per minute multiplied by number of hours

    // Length of Simulation
    static int HOURS=32;
    static int TIME=(60 / 2)*(60 * HOURS);

    // New Patient Rate
    final static double NEWPATRATE=0; // 0.04 is 70.875 average over an hour

    // Visitors Possible (Max Number of Visitors). Equal probability.
    final static int VISITORS=1;

    // Probability that a patient or visitor is infected when showing up to the clinic
    final static double INFECTEDBEFOREVISITPROB=0.1;

    // Radius over which an infected person can infect others
    final static double INFECTIONRADIUS=1.;

    // Probability of infection if you are within INFECTIONRADIUS of an infected person
    final static double INFECTIONPROB=0.34;

    final static boolean GETGIF=false;

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
            win.TickPause(2);
            if (i%1800==0){
                System.out.println("Time: "+i/1800);
                g.record.PatientsPerHour.add(g.record.numPatientsEnter);
                System.out.println(g.record.numPatientsEnter);
                g.record.numPatientsEnter=0;
            }


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
