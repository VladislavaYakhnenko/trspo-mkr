package server;

import matrix.MatrixOperationRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345;

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            ExecutorService executorService = Executors.newCachedThreadPool();

            System.out.println("server.Server started. Listening for client requests...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            MatrixOperationRequest matrix1 = (MatrixOperationRequest) inputStream.readObject();
            MatrixOperationRequest matrix2 = (MatrixOperationRequest) inputStream.readObject();

            if (!matrix1.isSuccess() || !matrix2.isSuccess()) {
                final String errorMessage = "Error: Unable to receive matrices from the client";
                System.out.println("server.Server message: " + errorMessage);
                outputStream.writeObject(new MatrixOperationRequest(new int[0][0], false, errorMessage));
                return;
            }

            if (matrix1.getMatrix()[0].length != matrix2.getMatrix().length) {
                final String errorMessage = "Error: Matrices cannot be multiplied";
                System.out.println("server.Server message: " + errorMessage);
                outputStream.writeObject(new MatrixOperationRequest(new int[0][0], false, errorMessage));
                return;
            }

            int[][] resultMatrix = multiplyMatrices(matrix1.getMatrix(), matrix2.getMatrix());
            outputStream.writeObject(new MatrixOperationRequest(resultMatrix, true, ""));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int[][] multiplyMatrices(int[][] matrix1, int[][] matrix2) {
        int rows1 = matrix1.length;
        int cols1 = matrix1[0].length;
        int cols2 = matrix2[0].length;
        int[][] resultMatrix = new int[rows1][cols2];

        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols2; j++) {
                for (int k = 0; k < cols1; k++) {
                    resultMatrix[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return resultMatrix;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}