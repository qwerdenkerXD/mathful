import java.math.BigInteger;

public class Matrix{
	private RealNumber[][] mat;
    private final int colCount, rowCount;
    Matrix(int cols, int rows) {
        if(cols < 1 || rows < 1)
            throw new IllegalArgumentException("Tried to create a matrix with dimension < 1x1");

        colCount = cols;
        rowCount = rows;
        mat = new RealNumber[cols][rows];
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
    RealNumber getValue(int col, int row){
        return mat[col][row];
    }
    RealNumber[] getCol(int c){
        return mat[c];
    }
    RealNumber[] getRow(int r){
        RealNumber[] row = new RealNumber[colCount];
        for (int col = 0; col < colCount; col++)
            row[col] = mat[col][r];

        return row;
    }
    void setValue(int col, int row, RealNumber value){
        mat[col][row] = value;
    }
    void incValue(int col, int row, RealNumber incValue){
        mat[col][row].add(incValue);
    }
    void setCol(int c, RealNumber[] col) {
        if (col == null)
            throw new NullPointerException("Null Pointer when setting a column");
        if (col.length != rowCount)
            throw new IllegalArgumentException("Different counts of rows when setting a column");
        mat[c] = new RealNumber[rowCount];
        for (int row = 0; row < rowCount; row++) {
            mat[c][row] = col[row];
        }
        basicAssertions();
    }
    void setRow(int r, RealNumber[] row) {
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
        RealNumber[] c = getCol(c1);
        setCol(c1, getCol(c2));
        setCol(c2, c);
    }
    void switchRows(int r1, int r2){
        RealNumber[] r = getRow(r1);
        setRow(r1, getRow(r2));
        setRow(r2, r);
    }
    void add(Matrix m){
        addMultiplied(m,new Rational(BigInteger.ONE));
    }
    void sub(Matrix m){
        addMultiplied(m,new Rational(BigInteger.ONE.negate()));
    }
    void mult(RealNumber x){
        RealNumber checksum = sumOfValues();
        addMultiplied(this, x.sub(new Rational(BigInteger.ONE)));
        assert sumOfValues().compareTo(x.mult(checksum)) == 0:"Multiplication Error with constant";
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
    void invert() { //integer division included, matrix not correct in many cases
        if (!isSquareMatrix())
            throw new UnsupportedOperationException("Tried to invert a non-squared matrix");
        if (!isInvertable())
            throw new UnsupportedOperationException("Matrix not invertable");
        //Gaussian elimination
        Matrix inverse = getIdentityMatrix(colCount);
        Matrix clone = clone();
        for (int col = 0; col < colCount; col++) {
            RealNumber[] solutionRow = null;
            for (int row = col; solutionRow == null; row++) {
                if (getValue(col, row).compareTo(new Rational()) != 0){
                    solutionRow = getRow(row);
                    switchRows(row, 0);
                    inverse.switchRows(row, 0);
                } 
            }
            for (int row = 1; row < rowCount; row++) {
                if (getValue(col, row).compareTo(new Rational()) != 0) {
                    RealNumber multiplier = getValue(col, row).div(solutionRow[col]);
                    for (int i = col; i < colCount; i++) {
                        setValue(i, row, getValue(i, row).sub(multiplier.mult(solutionRow[i])));
                        inverse.setValue(i, row, getValue(i, row).sub(multiplier.mult(solutionRow[i])));
                    }
                }
            }
            switchRows(0,col);
            inverse.switchRows(0,col);
        }
        importValues(inverse);
        assert mult(clone, inverse).equals(getIdentityMatrix(colCount));
        basicAssertions();
    }
    boolean isInvertable(){
        if (!isSquareMatrix())
            return false;
        return det(this).compareTo(new Rational()) != 0;
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
                if (getValue(col, row).compareTo(m.getValue(col, row)) != 0)
                    equal = false;
        return equal;
    }
    public Matrix clone(){
        return new Matrix(this);
    }
    public String toString(){
        String matOut = "%n";
        for (RealNumber[] col: mat) {  // preparing formatted String
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
    static RealNumber det(Matrix m) {
        if(m == null)
            throw new NullPointerException("Null Pointer when calculating the determinant");
        if (!m.isSquareMatrix())
            throw new UnsupportedOperationException("Tried to calculate the determinant of a non-squared matrix");
        
        RealNumber det = new Rational();
        RealNumber sign = new Rational(BigInteger.ONE);
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
            sign = sign.neg();
        }
        return det;
    }
    static Matrix getIdentityMatrix(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("Tried to create an identity matrix with dimension < 1x1");

        Matrix im = new Matrix(n, n);
        for (int i = 0; i < n; i++)
            im.setValue(i, i, new Rational(BigInteger.ONE));

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
    protected void addMultiplied(Matrix m, RealNumber valueMultiplier) {
        if (m == null)
            throw new NullPointerException("Null Pointer when adding values");
        if(colCount != m.getColCount() || rowCount != m.getRowCount())
            throw new UnsupportedOperationException("Tried to add values from matrix with different dimension");

        RealNumber checksum = sumOfValues();
        for (int col = 0;col<m.getColCount() ;col++ )
            for (int row = 0; row<getRowCount(); row++)
                incValue(col, row, valueMultiplier.mult(m.getValue(col, row)));

        if(m==this) assert sumOfValues()==checksum.add(valueMultiplier.mult(checksum)):"Addition Error of same object";
        else        assert sumOfValues()==checksum.add(valueMultiplier.mult(m.sumOfValues())):"Addition Error of different objects";
        basicAssertions();
    }
    private RealNumber sumOfValues(){
        Rational sum = new Rational();

        for (RealNumber[] col: mat)
            for (RealNumber value: col)
                sum.add(value);

        return sum;
    }
    private void basicAssertions(){
        try{
            assert false;
        } catch (AssertionError e) {
            assert mat[0].length == rowCount:"Count of rows changed";
            assert mat.length == colCount:"Count of columns changed";

            for (RealNumber[] col : mat)
                assert col != null:"A column is a null pointer";
        }
    }
}
