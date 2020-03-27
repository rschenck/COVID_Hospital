import java.util.ArrayList;

public class Data {
    int numPatientsEnter;
    int numPatientsLeave;

    ArrayList<Integer> PatientsPerHour=new ArrayList<>();

    public double GetPatientAverage(){
        double sum = 0;
        for (int i = 0; i < PatientsPerHour.size(); i++) {
            sum+=PatientsPerHour.get(i);
        }
        return(sum/PatientsPerHour.size());
    }
}
