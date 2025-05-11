import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class BerkeleyCoordinator {
    static final int PORT = 5070;
    static final int NUM_CLIENTS = 3;
    

    public static void main(String[] args) throws Exception {
        ExibicaoRelogios exibicaoRelogios = new ExibicaoRelogios();
        Random rand = new Random();
        long localClock = System.currentTimeMillis() + rand.nextInt(21_000) - 10_000; // ±10s
        // System.out.println("Coordenador - Tempo inicial: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new Date(localClock)));
        exibicaoRelogios.printClockRange("Coordenador - Tempo inicial: ", localClock);
        // System.out.println("Coordenador - Tempo inicial: " + exibicaoRelogios.);

        ServerSocket serverSocket = new ServerSocket(PORT);
        List<Socket> clients = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CLIENTS);

        // Aceitar conexões dos clientes
        for (int i = 0; i < NUM_CLIENTS; i++) {
            Socket client = serverSocket.accept();
            clients.add(client);
        }

        List<Long> allTimes = new ArrayList<>();
        allTimes.add(localClock); // inclui o tempo do coordenador

        // Solicitar tempo dos clientes em paralelo
        for (Socket client : clients) {
            executor.submit(() -> {
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    out.println("TIME_REQUEST");
                    long clientTime = Long.parseLong(in.readLine());
                    synchronized (allTimes) {
                        allTimes.add(clientTime);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Calcular tempo médio
        long avgTime = allTimes.stream().mapToLong(Long::longValue).sum() / allTimes.size();
        long offset = avgTime - localClock;

        // Ajustar relógio do coordenador
        localClock += offset;

        // Enviar ajustes aos clientes
        for (Socket client : clients) {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println("ADJUST_TIME " + avgTime);
        }

        // System.out.println("Coordenador - Tempo ajustado: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new Date(localClock)));
        exibicaoRelogios.printClockRange("Coordenador - Tempo ajustado: ", localClock);
        
        // Fechar conexões
        for (Socket client : clients) client.close();
        serverSocket.close();
    }
}