import client.Client;
import server.Server;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        int numThreads = Runtime.getRuntime().availableProcessors();

        ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
        ExecutorService clientExecutor = Executors.newFixedThreadPool(numThreads);

        startServerInSeparateThread(serverExecutor);
        waitABit(1000);

        startMultipleClients(clientExecutor, 3);

        demonstrateSendingRequests(clientExecutor, 10);

        shutdownExecutors(serverExecutor, clientExecutor);
    }

    private static void startServerInSeparateThread(ExecutorService executor) {
        executor.execute(() -> {
            System.out.println("server.Server started.");
            Server server = new Server();
            server.start();
        });
    }

    private static void waitABit(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startMultipleClients(ExecutorService executor, int numClients) {
        for (int i = 0; i < numClients; i++) {
            executor.execute(() -> {
                System.out.println("client.Client started.");
                Client client = new Client();
                client.start();
            });
        }
    }

    private static void demonstrateSendingRequests(ExecutorService executor, int numRequests) {
        for (int i = 0; i < numRequests; i++) {
            startClient(executor);
        }
    }

    private static void startClient(ExecutorService executor) {
        executor.execute(() -> {
            System.out.println("client.Client started.");
            Client client = new Client();
            client.start();
        });
    }

    private static void shutdownExecutors(ExecutorService... executors) {
        for (ExecutorService executor : executors) {
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}