package pl.edu.mimuw.matrix;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Arrays;

public class SparseMatrix extends Matrix{
    private final List<List<MatrixCellValue>> rowsList;
    private final int numberOfCells;
    public SparseMatrix(int rows, int columns, MatrixCellValue[] cells)
    {
        super(rows, columns);
        numberOfCells = cells.length;
        for (int i = 0; i < numberOfCells; i++)
        {
            assert cells[i].row < shape().rows;
            assert cells[i].column < shape().columns;
            assert cells[i].row >= 0;
            assert cells[i].column >= 0;
        }
        if (numberOfCells == 0)
        {
            rowsList = new ArrayList<>();
            return;
        }
        Comparator<MatrixCellValue> compareRows = Comparator.comparing(MatrixCellValue::getRow);
        Comparator<MatrixCellValue> compareColumns = Comparator.comparing(MatrixCellValue::getColumn);
        Comparator<MatrixCellValue> compareCells = compareRows.thenComparing(compareColumns);
        Arrays.sort(cells, compareCells);
        rowsList = new ArrayList<>();
        List<MatrixCellValue> pom = new ArrayList<>();
        pom.add(cells[0]);
        int last = cells[0].row;
        for (int i = 1; i < cells.length; i++)
        {
            if (cells[i].row != last) {
                rowsList.add(pom);
                pom = new ArrayList<>();
                last = cells[i].row;
            }
            pom.add(cells[i]);
        }
        rowsList.add(pom);
    }
    public IDoubleMatrix times(double scalar)
    {
        if (scalar == 0)
        {
            return new ZeroMatrix(shape().rows, shape().columns);
        }
        int i = 0;
        MatrixCellValue[] result = new MatrixCellValue[numberOfCells];
        for (List<MatrixCellValue> Lrow: rowsList) {
            for (MatrixCellValue e : Lrow) {
                result[i] = new MatrixCellValue(e.row, e.column, e.value * scalar);
                i++;
            }
        }
        return new SparseMatrix(shape().rows, shape().columns, result);
    }
    public IDoubleMatrix plus(double scalar)
    {
        int i = 0;
        MatrixCellValue[] result = new MatrixCellValue[numberOfCells];
        for (List<MatrixCellValue> Lrow: rowsList) {
            for (MatrixCellValue e : Lrow) {
                result[i] = new MatrixCellValue(e.row, e.column, e.value + scalar);
                i++;
            }
        }
        return new SparseMatrix(shape().rows, shape().columns, result);
    }
    @Override
    public String toString()
    {
        String result = "Length: " + shape().rows + " Width: " + shape().columns +"\n";
        int Lrow = -1;
        for (List<MatrixCellValue> row: rowsList)
        {
            if (row.get(0).row != Lrow + 1)
            {
                for (int j = 0;  j < row.get(0).row - Lrow - 1; j++)
                {
                    result += "0.0 ... 0.0\n";
                }
            }
            int i = 0;
            int lastColumn = -1;
            for (MatrixCellValue e: row)
            {
                if (e.column - lastColumn > 3)
                {
                    result += "0.0 ... 0.0 ";
                }
                else
                {
                    for (int j = 0; j < e.column - lastColumn - 1; j++)
                    {
                        result += "0.0 ";
                    }
                }
                result += e.value + " ";
                i++;
                Lrow = e.row;
                lastColumn = e.column;
            }
            if (shape().columns - 1 - lastColumn > 2)
            {
                result += "0.0 ... 0.0 ";
            }
            else
            {
                for (int j = 0; j < shape().columns - 1 - lastColumn; j++)
                {
                    result += "0.0 ";
                }
            }
            result += "\n";
        }
        for (int j = 0;  j < shape().rows - Lrow - 1; j++)
        {
            result += "0.0 ... 0.0 \n";
        }
        return result;
    }
    @Override
    public double get(int row, int column)
    {
        assert row < shape().rows;
        assert column < shape().columns;
        assert row >= 0;
        assert column >= 0;
        double result = 0;
        for (List<MatrixCellValue> Lrow: rowsList)
        {
            for (MatrixCellValue e: Lrow)
            {
                if (e.getRow() == row && e.getColumn() == column)
                {
                    result += e.value;
                }
            }
        }
        return result;
    }
    public double normInfinity()
    {
        double result = 0;
        for (List<MatrixCellValue> Lrow: rowsList)
        {
            double sum = 0;
            for (MatrixCellValue e: Lrow)
            {
                sum += Math.abs(e.value);
            }
            if (sum > result)
            {
                result = sum;
            }
        }
        return Math.abs(result);
    }
    public double normOne()
    {
        double[] sums = new double[shape().rows];
        for (List<MatrixCellValue> Lrow: rowsList)
        {
            for (MatrixCellValue e: Lrow)
            {
                sums[e.column] += Math.abs(e.value);
            }
        }
        double result = 0;
        for (int i = 0; i < shape().columns; i++)
        {
            if (sums[i] > result)
            {
                result = sums[i];
            }
        }
        return result;
    }
    public double frobeniusNorm()
    {
        double result = 0;
        for (List<MatrixCellValue> Lrow: rowsList)
        {
            for (MatrixCellValue e: Lrow)
            {
                result += e.value * e.value;
            }
        }
        return Math.sqrt(result);
    }
}
