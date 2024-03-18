package pl.edu.mimuw.matrix;

public abstract class ArrayMatrix extends Matrix{
    protected final double[] values;
    public ArrayMatrix(double[] values) {
        super(values.length, values.length);
        this.values = values;
    }
    public abstract ArrayMatrix NewArrayMatrix(double[] values);
    @Override
    public IDoubleMatrix times(double scalar)
    {
        if (scalar == 0)
        {
            return new ZeroMatrix(shape().rows, shape().columns);
        }
        double[] result = new double[shape().rows];
        for (int i = 0; i < shape().rows; i++)
        {
            result[i] = scalar * values[i];
        }
        return NewArrayMatrix(result);
    }
    @Override
    public double frobeniusNorm()
    {
        double result = 0;
        for (int i = 0; i < shape().rows; i++)
        {
            result += values[i] * values[i];
        }
        return Math.sqrt(result);
    }
}
