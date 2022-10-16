package pl.edu.mimuw.matrix;

public class DiagonalMatrix extends ArrayMatrix{
    public DiagonalMatrix(double[] diagonalValues) {
        super(diagonalValues);
    }
    public IDoubleMatrix plusReverse(IDoubleMatrix other) {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        if (other.getClass() == getClass()) {
            double[] diagonal = new double[shape().rows];
            for (int i = 0; i < shape().rows; i++) {
                diagonal[i] = get(i, i) + other.get(i, i);
            }
            return new DiagonalMatrix(diagonal);
        } else {
            return super.plusReverse(other);
        }
    }
    public DiagonalMatrix NewArrayMatrix(double[] values)
    {
        return new DiagonalMatrix(values);
    }

    public double get(int row, int column)
    {
        assert row < shape().rows;
        assert column < shape().columns;
        assert row >= 0;
        assert column >= 0;
        if (row == column)
        {
            return values[row];
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
                result +=  "0 ... 0 ";
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
                result +=  "0 ... 0 ";
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
    public double normInfinity()
    {
        double norm = 0;
        for (int i = 0; i < shape().rows; i++)
        {
            if (Math.abs(values[i]) > norm)
            {
                norm = Math.abs(values[i]);
            }
        }
        return norm;
    }
    @Override
    public double normOne()
    {
        return normInfinity();
    }
}
