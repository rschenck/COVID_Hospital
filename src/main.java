import Framework.Gui.GifMaker;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;

import static Framework.Util.RGB;
import static Framework.Util.RGB256;

class Constants {
    // Waiting room size
    final static int xSIZE=80;
    final static int ySIZE=40; // assume square this is SIZE*SIZE

    // Locations in Hospital
    final static int[] LOCATIONS=new int[]{Constants.xSIZE/2, Constants.ySIZE/2, 0, Constants.ySIZE, Constants.xSIZE, Constants.ySIZE, Constants.xSIZE/2, 0}; // xy waiting area, xy treat area 1, xy treat area 2, Leaving

    final static int CAPACITY=200;

    // Length of Simulation
    final static int TIME=10000;

    // New Patient Rate
    final static double NEWPATRATE=0.1;

    // Transmission Rate
    final static double TRANSMISSION=0.3;

    // Visitors Possible (Max Number of Visitors). Equal probability.
    final static int VISITORS=3;

    // Movement Probability
    final static double MOVE=0.25;

    final static boolean GETGIF=true;

    /*
    Color Scheme
     */
    final public static int PATIENT = RGB256(35, 88, 148), VISITOR = RGB256(114, 176, 222), INFECTED = RGB256(164, 44, 37), NEWINFECT = RGB256(229, 100, 95);
}

public class main {

    public static void main(String[] args) {
        GridWindow win=new GridWindow("COVID-19",Constants.xSIZE, Constants.ySIZE,10);
        Room g=new Room(Constants.xSIZE, Constants.ySIZE);
        GifMaker maker = new GifMaker("./this.gif",10,true);
        for (int i = 0; i < Constants.TIME; i++) {
            win.TickPause(10);
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
    }

}
