package pl.edu.mimuw.matrix;

public class ColumnarMatrix extends ArrayMatrix{
    public ColumnarMatrix(double[] values) {
        super(values);
    }
    public IDoubleMatrix plusReverse(IDoubleMatrix other) {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        if (other.getClass() == getClass()) {
            double[] values = new double[shape().columns];
            for (int i = 0; i < shape().columns; i++) {
                values[i] = get(0, i) + other.get(0, i);
            }
            return new ColumnarMatrix(values);
        } else {
            return super.plusReverse(other);
        }
    }
    @Override
    public IDoubleMatrix plus(double scalar)
    {
        double[] values = new double[shape().columns];
        for (int i = 0; i < shape().columns; i++) {
            values[i] = get(0, i) + scalar;
        }
        return new ColumnarMatrix(values);
    }
    public ColumnarMatrix NewArrayMatrix(double[] values)
    {
        return new ColumnarMatrix(values);
    }

    public double get(int row, int column)
    {
        assert row < shape().rows;
        assert column < shape().columns;
        assert row >= 0;
        assert column >= 0;
        return values[column];
    }
    @Override
    public double normInfinity()
    {
        double norm = 0;
        for (int i = 0; i < values.length; i++)
        {
            if (values[i] > norm)
            {
                norm = values[i];
            }
        }
        return norm * shape().columns;
    }
    @Override
    public double normOne()
    {
        double norm = 0;
        for (int i = 0; i < values.length; i++)
        {
            norm += values[i];
        }
        return norm;
    }
}
