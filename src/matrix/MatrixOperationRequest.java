package matrix;

import java.io.Serializable;

public class MatrixOperationRequest implements Serializable {
    private int[][] matrix;
    private boolean success;
    private String errorMessage;

    public MatrixOperationRequest(int[][] matrix, boolean success, String errorMessage) {
        this.matrix = matrix;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}