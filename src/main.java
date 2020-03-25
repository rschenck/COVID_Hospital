import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Util;

class Constants {
    // Waiting room size
    final static int SIZE=100; // assume square this is SIZE*SIZE

    // Length of Simulation
    final static int TIME=1000;

    // New Patient Rate
    final static double NEWPATRATE=0.5;

    // Transmission Rate
    final static double TRANSMISSION=0.3;

    // Visitors Possible (Max Number of Visitors). Equal probability.
    final static int VISITORS=2;

    // Movement Probability
    final static double MOVE=0.25;
}

public class main {

    public static void main(String[] args) {
        GridWindow win=new GridWindow(Constants.SIZE, Constants.SIZE,5);
        Room g=new Room(Constants.SIZE, Constants.SIZE);

        for (int i = 0; i < Constants.TIME; i++) {


            //draw
            g.DrawModel(win);
        }
    }

}
