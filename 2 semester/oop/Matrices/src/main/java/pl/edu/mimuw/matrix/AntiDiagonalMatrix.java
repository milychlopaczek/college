package pl.edu.mimuw.matrix;

public class AntiDiagonalMatrix extends ArrayMatrix{
    public AntiDiagonalMatrix(double[] antidiagonalValues) {
        super(antidiagonalValues);
    }
    public IDoubleMatrix plusReverse(IDoubleMatrix other) {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        if (other.getClass() == getClass()) {
            double[] antiDiagonal = new double[shape().rows];
            for (int i = 0; i < shape().rows; i++) {
                antiDiagonal[i] = get(i, i) + other.get(i, i);
            }
            return new AntiDiagonalMatrix(antiDiagonal);
        } else {
            return super.plusReverse(other);
        }
    }
    public AntiDiagonalMatrix NewArrayMatrix(double[] values)
    {
        return new AntiDiagonalMatrix(values);
    }

    public double get(int row, int column)
    {
        assert row < shape().rows;
        assert column < shape().columns;
        assert row >= 0;
        assert column >= 0;
        if (row == shape().columns - column - 1)
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
            if (shape().columns - i - 1 > 2)
            {
                result +=  "0 ... 0 ";
            }
            else
            {
                for (int j = 0; j < shape().columns - i - 1; j++)
                {
                    result +=  get(i, j) + " ";
                }
            }
            result += get(i, shape().columns - i - 1) + " ";
            if (i > 2)
            {
                result +=  "0 ... 0 ";
            }
            else
            {
                for (int j = shape().columns - i; j < shape().columns; j++)
                {
                    result +=  get(i, j) + " ";
                }
            }
            result += '\n';
        }
        return result;
    }
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
    public double normOne()
    {
        return normInfinity();
    }
}
