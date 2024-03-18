package pl.edu.mimuw.matrix;

public abstract class Matrix implements IDoubleMatrix{
    protected final Shape Mshape;
    public abstract double get(int row, int column);
    public Matrix(int rows, int columns)
    {
        Mshape = Shape.matrix(rows, columns);
    }
    public  Shape shape()
    {
        return Mshape;
    }
    public IDoubleMatrix times(IDoubleMatrix other)
    {
        return other.timesReverse(this);
    }
    public IDoubleMatrix plus(IDoubleMatrix other)
    {
        return other.plusReverse(this);
    }
    public IDoubleMatrix plus(double scalar)
    {
        double[][] resultValues = new double[shape().rows][shape().columns];
        for (int i = 0; i < shape().rows; i++)
        {
            for (int j = 0; j < shape().columns; j++) {
                resultValues[i][j] = scalar + get(i, j);
            }
        }
        return new FullMatrix(resultValues);
    }
    public IDoubleMatrix minus(double scalar)
    {
        return plus(-scalar);
    }
    public IDoubleMatrix plusReverse(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        double[][] resultValues = new double[shape().rows][shape().columns];
        for (int i = 0; i < shape().rows; i++)
        {
            for (int j = 0; j < shape().columns; j++)
            {
                resultValues[i][j] = get(i, j) + other.get(i, j);
            }
        }
        return new FullMatrix(resultValues);
    }
    public IDoubleMatrix minus(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().columns;
        assert shape().rows == other.shape().rows;
        return this.plus(other.times(-1));
    }
    public IDoubleMatrix timesReverse(IDoubleMatrix other)
    {
        assert shape().rows == other.shape().columns;
        double[][] resultValues = new double[other.shape().rows][shape().columns];
        for (int i = 0; i < other.shape().rows; i++)
        {
            for (int j = 0; j < shape().columns; j++)
            {
                resultValues[i][j] = 0;
                for (int g = 0; g < other.shape().columns; g++)
                {
                    resultValues[i][j] += other.get(i, g) * get(g, j);
                }
            }
        }
        return new FullMatrix(resultValues);
    }
    public double normOne()
    {
        double result = 0;
        for (int i = 0; i < shape().columns; i++)
        {
            double norm = 0;
            for (int j = 0; j < shape().rows; j++)
            {
                norm += Math.abs(get(j, i));
            }
            if (norm > result)
            {
                result = norm;
            }
        }
        return (result);
    }

    public double normInfinity()
    {
        double result = 0;
        for (int i = 0; i < shape().rows; i++)
        {
            double norm = 0;
            for (int j = 0; j < shape().columns; j++)
            {
                norm += Math.abs(get(i, j));
            }
            if (norm > result)
            {
                result = norm;
            }
        }
        return (result);
    }

    public double frobeniusNorm()
    {
        double result = 0;
        for (int i = 0; i < shape().rows; i++)
        {
            for (int j = 0; j < shape().columns; j++)
            {
                result += get(i, j) * get(i, j);
            }
        }
        return Math.sqrt(result);
    }

    public double[][] data()
    {
        double[][] result =  new double[shape().rows][shape().columns];
        for (int i = 0; i < shape().rows; i ++)
        {
            for (int j = 0; j < shape().columns; j++)
            {
                result[i][j] = get(i, j);
            }
        }
        return result;
    }
    public String toString()
    {
        String result = "Length: " + shape().rows + " Width: " + shape().columns +"\n";
        for (int i = 0; i < shape().rows; i++)
        {
            for (int j = 0; j < shape().columns; j++)
            {
                result +=  get(i, j) + " ";
            }
            result += '\n';
        }
        return result;
    }
}
