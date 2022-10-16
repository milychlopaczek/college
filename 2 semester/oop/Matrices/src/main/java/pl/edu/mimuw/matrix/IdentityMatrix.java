package pl.edu.mimuw.matrix;

public class IdentityMatrix extends ConstDiagonalMatrix{
    public IdentityMatrix(int rows) {
        super(1, rows);
    }
    @Override
    public IDoubleMatrix timesReverse(IDoubleMatrix other)
    {
        assert shape().rows == other.shape().columns;
        return other;
    }
    @Override
    public IDoubleMatrix times(IDoubleMatrix other)
    {
        assert shape().columns == other.shape().rows;
        return other;
    }
}
