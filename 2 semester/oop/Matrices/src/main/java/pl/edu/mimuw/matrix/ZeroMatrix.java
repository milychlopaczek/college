package pl.edu.mimuw.matrix;

public class ZeroMatrix extends ConstMatrix{
    public ZeroMatrix(int rows, int columns)
    {
        super(0, rows, columns);
    }
    @Override
    public IDoubleMatrix plus(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        return other;
    }
    @Override
    public IDoubleMatrix plusReverse(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        return other;
    }
    @Override
    public IDoubleMatrix plus(double scalar)
    {
        if (scalar == 0)
        {
            return new ZeroMatrix(shape().rows, shape().columns);
        }
        else
        {
            return new ConstMatrix(scalar, shape().rows, shape().columns);
        }
    }
    public IDoubleMatrix times(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().rows;
        return new ZeroMatrix(shape().rows, other.shape().columns);
    }
    public IDoubleMatrix timesReverse(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().rows;
        return new ZeroMatrix(other.shape().rows, shape().columns);
    }
    public IDoubleMatrix times(double scalar)
    {
        return new ZeroMatrix(shape().rows, shape().columns);
    }
    public double frobeniusNorm()
    {
        return 0;
    }
    public double normInfinity()
    {
        return 0;
    }
    public double normOne()
    {
        return 0;
    }
}
