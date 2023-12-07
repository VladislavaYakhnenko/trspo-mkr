package client;

import matrix.MatrixOperationRequest;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Client {
    private static final int SERVER_PORT = 12345;
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int MIN_MATRIX_SIZE = 10;

    public void start() {
        try {
            Random random = new Random();
            int N = MIN_MATRIX_SIZE + random.nextInt(2);
            int M = MIN_MATRIX_SIZE + random.nextInt(2);
            int L = MIN_MATRIX_SIZE + random.nextInt(2);
            MatrixOperationRequest requestMatrix1 = createMatrixRequest(generateMatrix(N, M), true, "");
            MatrixOperationRequest requestMatrix2 = createMatrixRequest(generateMatrix(M, L), true, "");

            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

                sendRequestMatrices(outputStream, requestMatrix1, requestMatrix2);

                MatrixOperationRequest resultMatrix = receiveResult(inputStream);

                if (!resultMatrix.isSuccess()) {
                    System.out.println("Error: " + resultMatrix.getErrorMessage());
                    return;
                }

                displayResultMatrix(resultMatrix.getMatrix());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int[][] generateMatrix(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(10);
            }
        }
        return matrix;
    }

    private void displayResultMatrix(int[][] matrix) {
        System.out.println("Resulting Matrix:");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private MatrixOperationRequest createMatrixRequest(int[][] matrix, boolean success, String errorMessage) {
        return new MatrixOperationRequest(matrix, success, errorMessage);
    }

    private void sendRequestMatrices(ObjectOutputStream outputStream, MatrixOperationRequest requestMatrix1, MatrixOperationRequest requestMatrix2) throws IOException {
        outputStream.writeObject(requestMatrix1);
        outputStream.writeObject(requestMatrix2);
    }

    private MatrixOperationRequest receiveResult(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        return (MatrixOperationRequest) inputStream.readObject();
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}