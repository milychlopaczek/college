package pl.edu.mimuw.matrix;

public class DoubleMatrixFactory {

  private DoubleMatrixFactory() {
  }

  public static IDoubleMatrix sparse(Shape shape, MatrixCellValue... values){
    return new SparseMatrix(shape.rows, shape.columns, values);
  }

  public static IDoubleMatrix full(double[][] values) {
    assert values != null;
    assert values.length > 0;
    return new FullMatrix(values);
  }

  public static IDoubleMatrix identity(int size) {
    return new IdentityMatrix(size);
  }

  public static IDoubleMatrix diagonal(double... diagonalValues) {
    return new DiagonalMatrix(diagonalValues);
  }

  public static IDoubleMatrix antiDiagonal(double... antiDiagonalValues) {
    return new AntiDiagonalMatrix(antiDiagonalValues);
  }

  public static IDoubleMatrix vector(double... values){
    double[][] val = new double[values.length][1];
    for (int i = 0; i < values.length; i++)
    {
      val[i][0] = values[i];
    }
    return new FullMatrix(val);
  }
  public static IDoubleMatrix rowMatrix(double... values)
  {
    return new RowMatrix(values);
  }
  public static IDoubleMatrix columnarMatrix(double... values)
  {
    return new ColumnarMatrix(values);
  }
  public static IDoubleMatrix constMatrix(int rows, int columns, double value)
  {
    return new ConstMatrix(value, rows, columns);
  }
  public static IDoubleMatrix DiagonalMatrix(double value, int rows)
  {
    return new ConstDiagonalMatrix(value, rows);
  }
  public static IDoubleMatrix zero(Shape shape) {
    return new ZeroMatrix(shape.rows, shape.columns);
  }
}
