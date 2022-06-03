public class Matrix{
	private int[][] mat;
    private final int colCount, rowCount;
    Matrix(int cols, int rows) {
        if(cols < 1 || rows < 1)
            throw new IllegalArgumentException("Tried to create a matrix with dimension < 1x1");

        mat = new int[cols][rows];
        colCount = cols;
        rowCount = rows;
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
    int[] getCol(int c){
        return mat[c];
    }
    int[] getRow(int r){
        int[] row = new int[colCount];
        for (int col = 0; col < colCount; col++)
            row[col] = mat[col][r];

        return row;
    }
    int getValue(int col, int row){
        return mat[col][row];
    }
    Matrix getTransposed(){
        Matrix trans = new Matrix(rowCount, colCount);
        for (int col = 0; col < colCount; col++)
            for (int row = 0; row < rowCount; row++)
                trans.setValue(row, col, getValue(col, row));
        return trans;
    }
    void setCol(int c, int[] col) {
        if (col == null)
            throw new NullPointerException("Null Pointer when setting a column");
        if (col.length != rowCount)
            throw new IllegalArgumentException("Different counts of rows when setting a column");
        mat[c] = new int[rowCount];
        for (int row = 0; row < rowCount; row++) {
            mat[c][row] = col[row];
        }
        basicAssertions();
    }
    void setRow(int r, int[] row) {
        if (row == null)
            throw new NullPointerException("Null Pointer when setting a row");
        if (row.length != colCount)
            throw new IllegalArgumentException("Different counts of columns when setting a row");
        for (int col = 0; col < colCount; col++) {
            mat[col][r] = row[col];
        }
        basicAssertions();
    }
    void setValue(int col, int row, int value){
        mat[col][row] = value;
    }
    void incValue(int col, int row, int incValue){
        mat[col][row] += incValue;
    }
    void switchCols(int c1, int c2){
        int[] c = getCol(c1);
        setCol(c1, getCol(c2));
        setCol(c2, c);
    }
    void switchRows(int r1, int r2){
        int[] r = getRow(r1);
        setRow(r1, getRow(r2));
        setRow(r2, r);
    }
    void add(Matrix m){
        addMultiplied(m,1);
    }
    void sub(Matrix m){
        addMultiplied(m,-1);
    }
    void mult(int x){
        int checksum = sumOfValues();
        addMultiplied(this, x-1);
        assert sumOfValues()==x*checksum:"Multiplication Error with constant";
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
            int[] solutionRow = null;
            for (int row = col; solutionRow == null; row++) {
                if (getValue(col, row) != 0){
                    solutionRow = getRow(row);
                    switchRows(row, 0);
                    inverse.switchRows(row, 0);
                } 
            }
            for (int row = 1; row < rowCount; row++) {
                if (getValue(col, row) != 0) {
                    int multiplier = getValue(col, row) / solutionRow[col];
                    for (int i = col; i < colCount; i++) {
                        setValue(i, row, getValue(i, row) - multiplier * solutionRow[i]);
                        inverse.setValue(i, row, getValue(i, row) - multiplier * solutionRow[i]);
                    }
                }
            }
            switchRows(0,col);
            inverse.switchRows(0,col);
        }
        importValues(inverse);
        // assert mult(clone, inverse).equals(getIdentityMatrix(colCount)); // cannot assert because of integer
        basicAssertions();
    }
    boolean isInvertable(){
        if (!isSquareMatrix())
            return false;
        return det(this) != 0;
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
                if (getValue(col, row) != m.getValue(col, row))
                    equal = false;
        return equal;
    }
    public Matrix clone(){
        return new Matrix(this);
    }
    public String toString(){
        String matOut = "%n";
        for (int[] col: mat) {  // preparing formatted String
            int minLength = (min(col) + "").length()+1;
            int maxLength = (max(col) + "").length()+1;
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
                values[row * colCount + col] = "" + getValue(col, row);
        return String.format(matOut + "%n", values);
    }
    protected void addMultiplied(Matrix m, int valueMultiplier) {
        if (m == null)
            throw new NullPointerException("Null Pointer when adding values");
        if(colCount != m.getColCount() || rowCount != m.getRowCount())
            throw new UnsupportedOperationException("Tried to add values from matrix with different dimension");

        int checksum = sumOfValues();
        for (int col = 0;col<m.getColCount() ;col++ )
            for (int row = 0; row<getRowCount(); row++)
                incValue(col, row, valueMultiplier*m.getValue(col, row));

        if(m==this) assert sumOfValues()==checksum+valueMultiplier*checksum:"Addition Error of same object";
        else        assert sumOfValues()==checksum+valueMultiplier*m.sumOfValues():"Addition Error of different objects";
        basicAssertions();
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
    private int sumOfValues(){
        int sum = 0;

        for (int[] col: mat)
            for (int value: col)
                sum += value;

        return sum;
    }
    private void basicAssertions(){
        try{
            assert false;
        } catch (AssertionError e) {
            assert mat[0].length == rowCount:"Count of rows changed";
            assert mat.length == colCount:"Count of columns changed";

            for (int[] col : mat)
                assert col != null:"A column is a null pointer";
        }
    }
    private static int min(int... values){
        int min = values[0];
        for (int x : values)
            if (x < min)
                min = x;
        return min;
    }
    private static int max(int... values){
        int max = values[0];
        for (int x : values)
            if (x > max)
                max = x;
        return max;
    }
    static Matrix getIdentityMatrix(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("Tried to create an identity matrix with dimension < 1x1");

        Matrix im = new Matrix(n, n);
        for (int i = 0; i < n; i++)
            im.setValue(i, i, 1);

        try{
            assert false;
        } catch (AssertionError e) {
            Matrix imClone = im.clone();
            imClone.pow(2);
            assert imClone.equals(im):"Identity Matrix generation failed";
        }
        return im;
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
                    result.incValue(colM2, rowM1, m1.getValue(i, rowM1) * m2.getValue(colM2, i));

        return result;
    }
    static int det(Matrix m) {
        if(m == null)
            throw new NullPointerException("Null Pointer when calculating the determinant");
        if (!m.isSquareMatrix())
            throw new UnsupportedOperationException("Tried to calculate the determinant of a non-squared matrix");
        
        int det = 0;
        int sign = 1;
        for (int row = 0; row < m.getRowCount(); row++) {
            if (m.getColCount() == 1) {
                det += sign * m.getValue(0, row);
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
                det += sign * m.getValue(0, row) * det(subDet);
            }
            sign *= -1;
        }
        return det;
    }
}
