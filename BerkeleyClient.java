import java.io.*;
import java.net.*;
import java.util.*;

public class BerkeleyClient {
    static final String HOST = "localhost";
    static final int PORT = 5070;

    public static void main(String[] args) throws Exception {
        ExibicaoRelogios exibicaoRelogios = new ExibicaoRelogios();
        Random rand = new Random();
        long localClock = System.currentTimeMillis() + rand.nextInt(21_000) - 10_000; // ±10s
        // System.out.println("Cliente - Tempo inicial: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new Date(localClock)));
        exibicaoRelogios.printClockRange("Cliente - Tempo inicial: ", localClock);

        Socket socket = new Socket(HOST, PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String msg;
        while ((msg = in.readLine()) != null) {
            if (msg.equals("TIME_REQUEST")) {
                out.println(localClock);
            } else if (msg.startsWith("ADJUST_TIME")) {
                long avgTime = Long.parseLong(msg.split(" ")[1]);
                long offset = avgTime - localClock;
                localClock += offset;
                // System.out.println("Cliente - Tempo ajustado: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new Date(localClock)));
                exibicaoRelogios.printClockRange("Cliente - Tempo ajustado: ", localClock);
                break;
            }
        }

        socket.close();
    }
}
