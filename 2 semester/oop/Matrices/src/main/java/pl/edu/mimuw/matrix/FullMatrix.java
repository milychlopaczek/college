package pl.edu.mimuw.matrix;

public class FullMatrix extends Matrix{
    private final double[][] values;

    public FullMatrix(double[][] values)
    {
        super(values.length, values[0].length);
        for (int i = 0; i < values.length; i++)
        {
            assert values[i].length == values[0].length;
        }
        this.values = values;
    }

    public IDoubleMatrix times(double scalar)
    {
        if (scalar == 0)
        {
            return new ZeroMatrix(shape().rows, shape().columns);
        }
        double[][] resultValues = new double[Mshape.rows][Mshape.columns];
        for (int i = 0; i < Mshape.rows; i++)
        {
            for (int j = 0; j < Mshape.columns; j++)
            {
                resultValues[i][j] = scalar * values[i][j];
            }
        }
        return new FullMatrix(resultValues);
    }

    public double get(int row, int column)
    {
        assert row < shape().rows;
        assert column < shape().columns;
        assert row >= 0;
        assert column >= 0;
        return values[row][column];
    }
}
