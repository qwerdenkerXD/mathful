import java.math.BigInteger;

public class Matrix extends MultiDimensional{
    Matrix(int cols, int rows) {
        super(cols, rows);
    }
    Matrix(Matrix m){
        super(m);
    }
    Constant[] getRow(int r){
        Constant[] row = new Constant[getColCount()];
        for (int col = 0; col < getColCount(); col++)
            row[col] = getValue(col, r);

        return row;
    }
    void setRow(int r, Constant[] row) {
        if (row == null)
            throw new NullPointerException("Matrix: Null Pointer when setting a row");
        if (row.length != getColCount())
            throw new IllegalArgumentException("Matrix: Different counts of columns when setting a row");
        for (int col = 0; col < getColCount(); col++) {
            setValue(col, r, row[col]);
        }
    }
    void switchCols(int c1, int c2){
        Constant[] c = getCol(c1);
        setCol(c1, getCol(c2));
        setCol(c2, c);
    }
    void switchRows(int r1, int r2){
        Constant[] r = getRow(r1);
        setRow(r1, getRow(r2));
        setRow(r2, r);
    }
    void div(Matrix m){
        if (m == null)
            throw new NullPointerException("Matrix: Null Pointer when dividing matrices");
        if(getColCount() != m.getColCount() || getRowCount() != m.getRowCount())
            throw new UnsupportedOperationException("Matrix: Tried to divide values from matrix with different dimension");
        Matrix clone = m.clone();
        clone.invert();
        importValues((Matrix)mult(this, clone));
    }
    void pow(int n) {
        if (!isSquareMatrix()) {
            throw new UnsupportedOperationException("Matrix: Tried to power a non-squared matrix");
        }
        if (n == 0) {
            importValues(getIdentityMatrix(getColCount()));
            return;
        }
        if (n < 0) {
            invert();
            n = -n;
        }
        Matrix clone = clone();
        for (int i = 1; i < n; i++)
            importValues((Matrix)mult(this,clone));
    }
    void invert() {
        if (!isSquareMatrix())
            throw new UnsupportedOperationException("Matrix: Tried to invert a non-squared matrix");
        if (!isInvertable())
            throw new UnsupportedOperationException("Matrix: Matrix not invertable");
        //Gaussian elimination
        Matrix inverse = getIdentityMatrix(getColCount());
        Matrix clone = clone();
        for (int col = 0; col < getColCount(); col++) {
            int undoSwitch = -1;
            for (int row = col; undoSwitch == -1; row++) {
                if (!getValue(col, row).equals(new Rational())){
                    switchRows(row, 0);
                    inverse.switchRows(row, 0);
                    undoSwitch = row;
                } 
            }

            for (int row = 1; row < getRowCount(); row++) {
                if (!getValue(col, row).equals(new Rational())) {
                    Constant multiplier = getValue(col, row).div(getValue(col, 0));
                    for (int i = 0; i < getColCount(); i++) {
                        setValue(i, row, getValue(i, row).sub(multiplier.mult(getValue(i, 0))));
                        inverse.setValue(i, row, inverse.getValue(i, row).sub(multiplier.mult(inverse.getValue(i, 0))));
                    }
                }
            }
            Constant divider = getValue(col, 0);
            for (int c = 0; c < getColCount(); c++) {
                setValue(c, 0, getValue(c, 0).div(divider));
                inverse.setValue(c, 0, inverse.getValue(c, 0).div(divider));
            }
            switchRows(undoSwitch, 0);
            inverse.switchRows(undoSwitch, 0);
            switchRows(undoSwitch,col);
            inverse.switchRows(undoSwitch,col);
        }
        importValues(inverse);
        assert ((Matrix)mult(clone, inverse)).equals(getIdentityMatrix(getColCount())):"Wrong inverted matrix";
    }
    boolean isInvertable(){
        if (!isSquareMatrix())
            return false;
        return !det(this).equals(new Rational());
    }
    boolean isSquareMatrix(){
        return getColCount() == getRowCount();
    }
    static Constant det(Matrix m) {
        if(m == null)
            throw new NullPointerException("Matrix: Null Pointer when calculating the determinant");
        if (!m.isSquareMatrix())
            throw new UnsupportedOperationException("Matrix: Tried to calculate the determinant of a non-squared matrix");
        
        Constant det = new Rational();
        Constant sign = new Rational(1);
        for (int row = 0; row < m.getRowCount(); row++) {
            if (m.getColCount() == 2) {
                det = det.add(sign.mult(m.getValue(0, row).mult(m.getValue(1, 1-row))));
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
            throw new IllegalArgumentException("Matrix: Tried to create an identity matrix with dimension < 1x1");

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
    protected Matrix clone(){
        return new Matrix(this);
    }
}
