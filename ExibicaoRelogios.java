import java.sql.Date;
import java.text.SimpleDateFormat;

public class ExibicaoRelogios {
    
    public void printClockRange(String label, long clockMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(label + ": " + sdf.format(new Date(clockMillis)));

        for (int i = 0; i <= 10; i++) {
            long time = clockMillis + i * 1000; // incrementa 1 segundo a cada iteração
            System.out.println(sdf.format(new Date(time)));
        }
    }
}
