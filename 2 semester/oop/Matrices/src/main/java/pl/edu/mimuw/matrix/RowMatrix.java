package pl.edu.mimuw.matrix;

public class RowMatrix extends ArrayMatrix{
    public RowMatrix(double[] values) {
        super(values);
    }
    public IDoubleMatrix plusReverse(IDoubleMatrix other) {
        if (shape().columns != other.shape().columns || shape().rows != other.shape().rows) {
            return null;
        }
        if (other.getClass() == getClass()) {
            double[] values = new double[shape().rows];
            for (int i = 0; i < shape().rows; i++) {
                values[i] = get(i, 0) + other.get(i, 0);
            }
            return new ColumnarMatrix(values);
        } else {
            return super.plusReverse(other);
        }
    }
    @Override
    public IDoubleMatrix plus(double scalar)
    {
        double[] values = new double[shape().rows];
        for (int i = 0; i < shape().rows; i++) {
            values[i] = get(i, 0) + scalar;
        }
        return new RowMatrix(values);
    }
    public RowMatrix NewArrayMatrix(double[] values)
    {
        return new RowMatrix(values);
    }

    public double get(int row, int column)
    {
        assert row < shape().rows;
        assert column < shape().columns;
        assert row >= 0;
        assert column >= 0;
        return values[row];
    }
    public double normInfinity()
    {
        double norm = 0;
        for (int i = 0; i < values.length; i++)
        {
            norm += values[i];
        }
        return norm;
    }
    public double normOne()
    {
        double norm = 0;
        for (int i = 0; i < values.length; i++)
        {
            if (values[i] > norm)
            {
                norm = values[i];
            }
        }
        return norm * shape().rows;
    }
}
