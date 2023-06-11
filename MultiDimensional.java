abstract class MultiDimensional implements Dimensional{
    private final Constant[][] mat;
    private final int colCount, rowCount;

    /**
     * @param cols Number of columns
     * @param rows Number of rows
     * @throws IllegalArgumentException if dimension <= 1x1
     */
    MultiDimensional(int cols, int rows){
        if(cols < 1 || rows < 1)
            throw new IllegalArgumentException("MultiDimensional: Tried to create a MultiDimensional with dimension < 1x1");
        if(cols*rows == 1)
            throw new IllegalArgumentException("MultiDimensional: Tried to create 1-dimensional MultiDimensional");
        colCount = cols;
        rowCount = rows;
        mat = new Constant[cols][rows];
        for (int col = 0; col < colCount; col++) {  // initialize with zeros
            for (int row = 0; row < rowCount; row++) {
                setValue(col, row, new Rational());
            }
        }
    }
    MultiDimensional(MultiDimensional m){
        this(m.getColCount(), m.getRowCount());
        importValues(m);
    }
    public Constant getDim(){
        return new Rational(colCount*rowCount);
    }

    /**
     * @param col index of column, starting at zero
     * @param row index of row, starting at zero
     */
    Constant getValue(int col, int row){
        return mat[col][row];
    }
    int getRowCount(){
        return rowCount;
    }
    int getColCount(){
        return colCount;
    }

    /**
     * @param c index of column, starting at zero
     * @return returns flat copy of the specified column
     */
    Constant[] getCol(int c){
        Constant[] col = new Constant[rowCount];
        for (int i = 0; i < rowCount; i++) {
            col[i] = mat[c][i];
        }
        return col;
    }

    MultiDimensional getTransposed(){
        MultiDimensional trans = clone();
        for (int col = 0; col < colCount; col++)
            for (int row = 0; row < rowCount; row++)
                trans.setValue(row, col, getValue(col, row));
        return trans;
    }

    /**
     * @param col index of column, starting at zero
     * @param row index of column, starting at zero
     * @throws NullPointerException if value is null
     * @throws ArrayIndexOutOfBoundsException if row, col < 0 or row >= rowCount or col >= colCount
     */
    void setValue(int col, int row, Constant value){
        if (value == null)
            throw new NullPointerException("Matrix: Null Pointer when setting a value");
        mat[col][row] = value;
    }

    /**
     * @param col index of column, starting at zero
     * @param row index of column, starting at zero
     * @throws NullPointerException if value is null
     * @throws ArrayIndexOutOfBoundsException if row, col < 0 or row >= rowCount or col >= colCount
     */
    void incValue(int col, int row, Constant incValue){
        mat[col][row] = mat[col][row].add(incValue);
    }

    /**
     * Sets the values of the specified column with index c to the col's values
     * @param c index of column, starting at zero
     * @throws NullPointerException if col is null
     * @throws IllegalArgumentException if col contains more or less values than colCount
     */
    void setCol(int c, Constant[] col) {
        if (col == null)
            throw new NullPointerException("Matrix: Null Pointer when setting a column");
        if (col.length != rowCount)
            throw new IllegalArgumentException("Matrix: Different counts of rows when setting a column");
        mat[c] = new Constant[rowCount];
        for (int row = 0; row < rowCount; row++) {
            mat[c][row] = col[row];
        }
    }

    /**
     * Adds m to this object
     */
    void add(MultiDimensional m){
        addMultiplied(m,new Rational(1));
    }

    /**
     * Subtracts m from this object
     */
    void sub(MultiDimensional m){
        addMultiplied(m,new Rational(-1));
    }

    /**
     * Multiplicates this object with x
     * @throws NullPointerException if x is null
     */
    void mult(Constant x){
        Constant checksum = sumOfValues();
        addMultiplied(this, x.sub(new Rational(1)));
        assert sumOfValues().equals(x.mult(checksum)):"Multiplication Error with constant";
    }

    /**
     * Divides this object by x
     * @throws NullPointerException if x is null
     */
    void div(Constant x){
        mult(x.reciprocal());
    }

    /**
     * Checks if this object is deep equal to m
     * @return returns false if m is null or of different dimension
     */
    public boolean equals(MultiDimensional m){
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
    public String toString(){
        String matOut = "%n";
        for (Constant[] col: mat) {  // preparing formatted String
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
    abstract protected MultiDimensional clone();

    /**
     * Sets the values of this object to m's values
     * @throws NullPointerException if m is null
     * @throws UnsupportedOperationException if m of different dimension
     */
    protected void importValues(MultiDimensional m) {
        if (m == null)
            throw new NullPointerException("MultiDimensional: Null Pointer when importing values");
        if(colCount != m.getColCount() || rowCount != m.getRowCount())
            throw new UnsupportedOperationException("MultiDimensional: Tried to import values from MultiDimensional with different dimension");

        for (int col = 0; col < colCount; col++)
            for (int row = 0; row < rowCount; row++)
                setValue(col, row, m.getValue(col, row));

        assert equals(m):"Wrong value import";
    }

    /**
     * Multiplicates m1 with m2 like m1 * m2
     * @return returns Constant if dim(m1 * m2) is 1x1
     * @throws UnsupportedOperationException if colCount of m1 doesn't match m2's rowCount
     */
    static Dimensional mult(MultiDimensional m1, MultiDimensional m2) {
        if(m1 == m2){
            m2 = m2.clone();
        }
        if (m1.getColCount() != m2.getRowCount())
            throw new UnsupportedOperationException("MultiDimensional: MultiDimensional multiplication not possible");
        
        MultiDimensional result = new Matrix(m2.getColCount(), m1.getRowCount());
        for (int rowM1 = 0; rowM1 < m1.getRowCount(); rowM1++)
            for (int colM2 = 0; colM2 < m2.getColCount(); colM2++)
                for (int i = 0; i < m1.getColCount(); i++)
                    result.incValue(colM2, rowM1, m1.getValue(i, rowM1).mult(m2.getValue(colM2, i)));
        if (result.getDim().equals(new Rational(1)))
            return new Rational((Rational)result.getValue(0, 0));
        return result;
    }

    /**
     * Calculates this + m * valueMultiplier
     * @throws NullPointerException if m or valueMultiplier are null
     * @throws UnsupportedOperationException if m of different dimension
     */
    private void addMultiplied(MultiDimensional m, Constant valueMultiplier) {
        if (m == null || valueMultiplier == null)
            throw new NullPointerException("MultiDimensional: Null Pointer when adding values");
        if(colCount != m.getColCount() || rowCount != m.getRowCount())
            throw new UnsupportedOperationException("MultiDimensional: Tried to add values from MultiDimensional with different dimension");

        Constant checksum = sumOfValues();

        for (int col = 0;col<m.getColCount() ;col++ )
            for (int row = 0; row<getRowCount(); row++)
                incValue(col, row, valueMultiplier.mult(m.getValue(col, row)));

        if (m==this) assert sumOfValues().equals(checksum.add(valueMultiplier.mult(checksum))):"Addition Error of same object";
        else         assert sumOfValues().equals(checksum.add(valueMultiplier.mult(m.sumOfValues()))):"Addition Error of different objects";
    }

    /**
     * Calculates the sum of all values this object contains
     */
    private Constant sumOfValues(){
        Constant sum = new Rational();

        for (Constant[] col: mat)
            sum = sum.add(MathLib.sum(col));

        return sum;
    }
}