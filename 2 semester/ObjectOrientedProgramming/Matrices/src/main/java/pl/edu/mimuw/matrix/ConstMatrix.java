package pl.edu.mimuw.matrix;

public class ConstMatrix extends Matrix{
    protected final double value;
    public ConstMatrix(double value, int rows, int columns) {
        super(rows, columns);
        this.value = value;
    }

    public IDoubleMatrix plusReverse(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        return other.plus(value);
    }
    public IDoubleMatrix plus(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        return other.plus(value);
    }
    public IDoubleMatrix times(double scalar)
    {
        if (scalar == 0)
        {
            return new ZeroMatrix(shape().rows, shape().columns);
        }
        return new ConstMatrix(value * scalar, shape().rows, shape().columns);
    }

    @Override
    public IDoubleMatrix plus(double scalar)
    {
        if (value == -scalar)
        {
            return new ZeroMatrix(shape().rows, shape().columns);
        }
        return new ConstMatrix(value + scalar, shape().rows, shape().columns);
    }

    public double get(int row, int column)
    {
        assert row < shape().rows;
        assert column < shape().columns;
        assert row >= 0;
        assert column >= 0;
        return value;
    }

    @Override
    public String toString()
    {
        if (shape().columns >= 3)
        {
            String result = "Length: " + shape().rows + " Width: " + shape().columns +"\n";
            for (int i = 0; i < shape().rows; i++)
            {
                result += value + " ... " + value + "\n";
            }
            return result;
        }
        else
        {
            return super.toString();
        }
    }
    @Override
    public double normInfinity()
    {
        return Math.abs(value * shape().columns);
    }
    @Override
    public double normOne()
    {
        return Math.abs(value * shape().rows);
    }
}
