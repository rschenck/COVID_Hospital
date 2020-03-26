import Framework.Gui.GifMaker;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;

class Constants {
    // Waiting room size
    final static int SIZE=40; // assume square this is SIZE*SIZE
    final static int CAPACITY=200;

    // Length of Simulation
    final static int TIME=500;

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
}

public class main {

    public static void main(String[] args) {
        GridWindow win=new GridWindow(Constants.SIZE, Constants.SIZE,10);
        Room g=new Room(Constants.SIZE, Constants.SIZE);
        GifMaker giffy=new GifMaker("./infection.gif",10,true);

        for (int i = 0; i < Constants.TIME; i++) {
            win.TickPause(100);
            g.TimeStep();

            //draw
            g.DrawModel(win);
            giffy.AddFrame(win);


        }
        giffy.Close();
        win.Close();
    }

}
