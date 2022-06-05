import java.math.BigInteger;

public class Matrix{
	private Computable[][] mat;
    private final int colCount, rowCount;
    Matrix(int cols, int rows) {
        if(cols < 1 || rows < 1)
            throw new IllegalArgumentException("Tried to create a matrix with dimension < 1x1");

        colCount = cols;
        rowCount = rows;
        mat = new Computable[cols][rows];
        for (int col = 0; col < colCount; col++) {
            for (int row = 0; row < rowCount; row++) {
                setValue(col, row, new Rational());
            }
        }
    }
    Matrix(Matrix m){
        this(m.getColCount(), m.getRowCount());
        importValues(m);
    }
    int getRowCount(){
        return rowCount;
    }
    int getColCount(){
        return colCount;
    }
    Computable getValue(int col, int row){
        return mat[col][row];
    }
    Computable[] getCol(int c){
        return mat[c];
    }
    Computable[] getRow(int r){
        Computable[] row = new Computable[colCount];
        for (int col = 0; col < colCount; col++)
            row[col] = mat[col][r];

        return row;
    }
    void setValue(int col, int row, Computable value){
        mat[col][row] = value;
    }
    void incValue(int col, int row, Computable incValue){
        mat[col][row] = mat[col][row].add(incValue);
    }
    void setCol(int c, Computable[] col) {
        if (col == null)
            throw new NullPointerException("Null Pointer when setting a column");
        if (col.length != rowCount)
            throw new IllegalArgumentException("Different counts of rows when setting a column");
        mat[c] = new Computable[rowCount];
        for (int row = 0; row < rowCount; row++) {
            mat[c][row] = col[row];
        }
        basicAssertions();
    }
    void setRow(int r, Computable[] row) {
        if (row == null)
            throw new NullPointerException("Null Pointer when setting a row");
        if (row.length != colCount)
            throw new IllegalArgumentException("Different counts of columns when setting a row");
        for (int col = 0; col < colCount; col++) {
            mat[col][r] = row[col];
        }
        basicAssertions();
    }
    Matrix getTransposed(){
        Matrix trans = new Matrix(rowCount, colCount);
        for (int col = 0; col < colCount; col++)
            for (int row = 0; row < rowCount; row++)
                trans.setValue(row, col, getValue(col, row));
        return trans;
    }
    void switchCols(int c1, int c2){
        Computable[] c = getCol(c1);
        setCol(c1, getCol(c2));
        setCol(c2, c);
    }
    void switchRows(int r1, int r2){
        Computable[] r = getRow(r1);
        setRow(r1, getRow(r2));
        setRow(r2, r);
    }
    void add(Matrix m){
        addMultiplied(m,new Rational(1));
    }
    void sub(Matrix m){
        addMultiplied(m,new Rational(-1));
    }
    void mult(Computable x){
        Computable checksum = sumOfValues();
        addMultiplied(this, x.sub(new Rational(1)));
        assert sumOfValues().equals(x.mult(checksum)):"Multiplication Error with constant";
    }
    void pow(int n) {
        if (!isSquareMatrix()) {
            throw new UnsupportedOperationException("Tried to power a non-squared matrix");
        }
        if (n == 0) {
            importValues(getIdentityMatrix(colCount));
            return;
        }
        if (n < 0) {
            invert();
            n = -n;
        }
        Matrix clone = clone();
        for (int i = 1; i < n; i++)
            mult(this,clone);
    }
    void invert() {
        if (!isSquareMatrix())
            throw new UnsupportedOperationException("Tried to invert a non-squared matrix");
        if (!isInvertable())
            throw new UnsupportedOperationException("Matrix not invertable");
        //Gaussian elimination
        Matrix inverse = getIdentityMatrix(colCount);
        Matrix clone = clone();
        for (int col = 0; col < colCount; col++) {
            int undoSwitch = -1;
            for (int row = col; undoSwitch == -1; row++) {
                if (!getValue(col, row).equals(new Rational())){
                    switchRows(row, 0);
                    inverse.switchRows(row, 0);
                    undoSwitch = row;
                } 
            }
            for (int row = 1; row < rowCount; row++) {
                if (!getValue(col, row).equals(new Rational())) {
                    Computable multiplier = getValue(col, row).div(getValue(col, 0));
                    for (int i = 0; i < colCount; i++) {
                        setValue(i, row, getValue(i, row).sub(multiplier.mult(getValue(i, 0))));
                        inverse.setValue(i, row, inverse.getValue(i, row).sub(multiplier.mult(inverse.getValue(i, 0))));
                    }
                }
            }
            Computable divider = getValue(col, 0);
            for (int c = 0; c < colCount; c++) {
                setValue(c, 0, getValue(c, 0).div(divider));
                inverse.setValue(c, 0, inverse.getValue(c, 0).div(divider));
            }
            switchRows(undoSwitch, 0);
            inverse.switchRows(undoSwitch, 0);
            switchRows(undoSwitch,col);
            inverse.switchRows(undoSwitch,col);
        }
        importValues(inverse);
        assert mult(clone, inverse).equals(getIdentityMatrix(colCount)):"Wrong inverted matrix";
        basicAssertions();
    }
    boolean isInvertable(){
        if (!isSquareMatrix())
            return false;
        return !det(this).equals(new Rational());
    }
    boolean isSquareMatrix(){
        return colCount == rowCount;
    }
    public boolean equals(Matrix m){
        if (m == this)
            return true;
        if (m == null || rowCount != m.getRowCount() || colCount != m.getColCount())
            return false;
        boolean equal = true;
        for (int col = 0; col < colCount && equal; col++)
            for (int row = 0; row < rowCount; row++)
                if (!getValue(col, row).equals(m.getValue(col, row)))
                    equal = false;
        return equal;
    }
    public Matrix clone(){
        return new Matrix(this);
    }
    public String toString(){
        String matOut = "%n";
        for (Computable[] col: mat) {  // preparing formatted String
            int minLength = (MathLib.min(col[0],col).toString()).length()+1;
            int maxLength = (MathLib.max(col[0],col).toString()).length()+1;
            if (maxLength > minLength)
                matOut += " %" + maxLength + "s";
            else
                matOut += " %" + minLength + "s";
        }
        if (true) {
            String row = matOut;
            for (int i = 0; i < rowCount-1; i++)
                matOut += row;
        }
        Object[] values = new Object[colCount * rowCount];
        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++)
                values[row * colCount + col] = getValue(col, row).toString();
        return String.format(matOut + "%n", values);
    }
    static Matrix mult(Matrix m1, Matrix m2) {
        if(m1 == m2){
            m1.pow(2);
            return m1;
        }
        if (m1.getColCount() != m2.getRowCount())
            throw new UnsupportedOperationException("Matrix multiplication not possible");
        
        Matrix result = new Matrix(m2.getColCount(), m1.getRowCount());
        for (int rowM1 = 0; rowM1 < m1.getRowCount(); rowM1++)
            for (int colM2 = 0; colM2 < m2.getColCount(); colM2++)
                for (int i = 0; i < m1.getColCount(); i++)
                    result.incValue(colM2, rowM1, m1.getValue(i, rowM1).mult(m2.getValue(colM2, i)));

        return result;
    }
    static Computable det(Matrix m) {
        if(m == null)
            throw new NullPointerException("Null Pointer when calculating the determinant");
        if (!m.isSquareMatrix())
            throw new UnsupportedOperationException("Tried to calculate the determinant of a non-squared matrix");
        
        Computable det = new Rational();
        Computable sign = new Rational(1);
        for (int row = 0; row < m.getRowCount(); row++) {
            if (m.getColCount() == 1) {
                det = det.add(sign.mult(m.getValue(0, row)));
            } else{
                Matrix subDet = new Matrix(m.getColCount()-1, m.getRowCount()-1);
                for (int subCol = 1; subCol < m.getColCount(); subCol++) {
                    for (int subRow = 0; subRow < m.getRowCount(); subRow++) {
                        if (subRow < row)
                            subDet.setValue(subCol-1, subRow, m.getValue(subCol, subRow));
                        if (subRow > row)
                            subDet.setValue(subCol-1, subRow-1, m.getValue(subCol, subRow));
                    }
                }
                det = det.add(sign.mult(m.getValue(0, row)).mult(det(subDet)));
            }
            sign = sign.negate();
        }
        return det;
    }
    static Matrix getIdentityMatrix(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("Tried to create an identity matrix with dimension < 1x1");

        Matrix im = new Matrix(n, n);
        for (int i = 0; i < n; i++)
            im.setValue(i, i, new Rational(1));

        try{
            assert false;
        } catch (AssertionError e) {
            Matrix imClone = im.clone();
            imClone.pow(2);
            assert imClone.equals(im):"Identity Matrix generation failed";
        }
        return im;
    }
    protected void importValues(Matrix m) {
        if (m == null)
            throw new NullPointerException("Null Pointer when importing values");
        if(colCount != m.getColCount() || rowCount != m.getRowCount())
            throw new UnsupportedOperationException("Tried to import values from matrix with different dimension");

        for (int col = 0; col < colCount; col++)
            for (int row = 0; row < rowCount; row++)
                setValue(col, row, m.getValue(col, row));

        assert equals(m):"Wrong value import";
    }
    protected void addMultiplied(Matrix m, Computable valueMultiplier) {
        if (m == null)
            throw new NullPointerException("Null Pointer when adding values");
        if(colCount != m.getColCount() || rowCount != m.getRowCount())
            throw new UnsupportedOperationException("Tried to add values from matrix with different dimension");
        Computable checksum = sumOfValues();
        for (int col = 0;col<m.getColCount() ;col++ )
            for (int row = 0; row<getRowCount(); row++)
                incValue(col, row, valueMultiplier.mult(m.getValue(col, row)));
        if (m==this) assert sumOfValues().equals(checksum.add(valueMultiplier.mult(checksum))):"Addition Error of same object";
        else        assert sumOfValues().equals(checksum.add(valueMultiplier.mult(m.sumOfValues()))):"Addition Error of different objects";
        basicAssertions();
    }
    private Computable sumOfValues(){
        Computable sum = new Rational();

        for (Computable[] col: mat)
            for (Computable value: col)
                sum = sum.add(value);

        return sum;
    }
    private void basicAssertions(){
        try{
            assert false;
        } catch (AssertionError e) {
            assert mat[0].length == rowCount:"Count of rows changed";
            assert mat.length == colCount:"Count of columns changed";

            for (Computable[] col : mat){
                assert col != null:"A column is a null pointer";
                for (Computable row : col) {
                    assert row != null:"Value is a null pointer";
                }
            }
        }
    }
}
