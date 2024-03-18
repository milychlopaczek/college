package pl.edu.mimuw;

import pl.edu.mimuw.matrix.*;

import static pl.edu.mimuw.matrix.MatrixCellValue.cell;

public class Main {

  public static void main(String[] args) {
    // Tu trzeba wpisać kod testujący toString dla poszczególnych macierzy i wyników operacji
    System.out.println("Korzystałem ze środowiska intellij, testowane na studentsie");
    double[][]values = new double[10][10];
    for (int i = 0; i < 10; i++)
    {
      for (int j = 0; j < 10; j++) {
        values[i][j] = i + j;
      }
    }
    FullMatrix matrix = new FullMatrix(values);
    System.out.println("Full matrix");
    System.out.println(matrix);
    double[]diag1 = {1, 2, 3, 4 , 5, 6, 7, 8, 9, 10};
    System.out.println("AntiDiagonal matrix");
    AntiDiagonalMatrix matrix3 = new AntiDiagonalMatrix(diag1);
    System.out.println(matrix3);
    System.out.println("Const matrix");
    ConstMatrix matrix5 = new ConstMatrix(1, 10, 10);
    System.out.println(matrix5);
    System.out.println("Diagonal matrix");
    DiagonalMatrix matrix4 = new DiagonalMatrix(diag1);
    System.out.println(matrix4);
    System.out.println("Zero matrix");
    ZeroMatrix matrix2 = new ZeroMatrix(10,10);
    System.out.println(matrix2);
    System.out.println("Row matrix");
    RowMatrix matrix6 = new RowMatrix(diag1);
    System.out.println(matrix6);
    System.out.println("Columnar matrix");
    ColumnarMatrix matrix7 = new ColumnarMatrix(diag1);
    System.out.println(matrix7);
    System.out.println("Identity");
    IdentityMatrix matrix8 = new IdentityMatrix(10);
    System.out.println(matrix8);
    System.out.println("Const Diagonal Matrix");
    ConstDiagonalMatrix matrix9 = new ConstDiagonalMatrix(2, 10);
    System.out.println(matrix9);
    System.out.println("Sparse Matrix");
    MatrixCellValue[] cells = {cell(0, 2, 1),
    cell(1, 2, 2),
           cell(0, 6, 3),
            cell(1, 5, 4),
            cell(3, 3, 5),
            cell(4, 4, 6)};
    SparseMatrix matrixF = new SparseMatrix(10, 10, cells);
    System.out.println(matrixF);
  }
}
