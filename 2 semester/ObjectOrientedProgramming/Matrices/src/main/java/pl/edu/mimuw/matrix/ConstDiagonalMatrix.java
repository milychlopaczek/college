package pl.edu.mimuw.matrix;

public class ConstDiagonalMatrix extends Matrix{
    protected final double value;
    public ConstDiagonalMatrix(double value, int rows) {
        super(rows, rows);
        this.value = value;
    }
    @Override
    public IDoubleMatrix plusReverse(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        if (other.getClass() == getClass())
        {
            return new ConstDiagonalMatrix(value, shape().rows);
        }
        else {
            return super.plusReverse(other);
        }
    }
    public IDoubleMatrix times(double scalar)
    {
        if (scalar == 0)
        {
            return new ZeroMatrix(shape().rows, shape().columns);
        }
        else if (scalar * value == 1)
        {
            return new IdentityMatrix(shape().rows);
        }
        return new ConstDiagonalMatrix(value * scalar, shape().rows);
    }
    @Override
    public IDoubleMatrix timesReverse(IDoubleMatrix other)
    {
        assert shape().rows == other.shape().columns;
        return other.times(value);
    }
    @Override
    public IDoubleMatrix times(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().rows;
        return other.times(value);
    }

    public double get(int row, int column)
    {
        assert row < shape().rows;
        assert column < shape().columns;
        assert row >= 0;
        assert column >= 0;
        if (row == column)
        {
            return value;
        }
        return 0;
    }

    @Override
    public String toString()
    {
        String result = "Length: " + shape().rows + " Width: " + shape().columns +"\n";
        for (int i = 0; i < shape().rows; i++)
        {
            if (i > 2)
            {
                result +=  "0.0 ... 0.0 ";
            }
            else
            {
                for (int j = 0; j < i; j++)
                {
                    result +=  get(i, j) + " ";
                }
            }
            result += get(i, i) + " ";
            if (shape().columns - i - 1 > 2)
            {
                result +=  "0.0 ... 0.0 ";
            }
            else
            {
                for (int j = i + 1; j < shape().columns; j++)
                {
                    result +=  get(i, j) + " ";
                }
            }
            result += '\n';
        }
        return result;
    }
    @Override
    public double frobeniusNorm()
    {
        double result = value * value * shape().rows;
        return Math.sqrt(result);
    }
    @Override
    public double normInfinity()
    {
        return Math.abs(value);
    }
    @Override
    public double normOne()
    {
        return Math.abs(value);
    }
}
