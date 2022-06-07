import java.math.BigInteger;
import java.math.BigDecimal;
class Test{
    public static void main(String[] args) {
        TestRational.main();
        TestConstant.main();
        testMathLib();
        TestMatrix.main();
        testVector();
    }
    public static void testVector(){}
    public static void testMathLib(){}
}

class TestMatrix{
    public static void main(String... args) {
        Matrix mat = new Matrix(5,5);
        assert mat.clone() != mat:"Matrix: clone() test failed";
        assert mat.clone().equals(mat):"Matrix: equals() test failed";
        assert !mat.equals(new Matrix(5,4)):"Matrix: equals() test failed";
        assert new Matrix(mat).equals(mat) &&
               new Matrix(mat) != mat:"Matrix: Constructor test failed";
        assert mat.equals(mat):"Matrix: equals() test failed";
        assert mat.getValue(3,3).equals(new Rational(0)):"Matrix: getValue() test failed";
        mat.setValue(3,3,new Rational(87346,-2378459));
        assert mat.getValue(3,3).equals(new Rational(87346,-2378459)):"Matrix: setValue() test failed";
        mat.incValue(0,0,new Rational(234567));
        mat.incValue(0,0,new Rational(-34567));
        assert mat.getValue(0,0).equals(new Rational(200000)):"Matrix: incValue() test failed";
        assert new Matrix(3,4).getDim().equals(new Rational(12));
        assert new Matrix(3,4).getColCount() == 3:"Matrix: getColCount() test failed";
        assert new Matrix(3,4).getRowCount() == 4:"Matrix: getRowCount() test failed";
        assert !(new Matrix(3,4).isSquareMatrix()):"Matrix: isSquareMatrix() test failed";
        assert mat.isSquareMatrix():"Matrix: isSquareMatrix() test failed";
        testSetGetSwitchCol();  // test setCol(), getCol(), switchCols()
        testSetGetSwitchRow();  // test setRow(), getRow(), switchRows()
        testGetTransposed();  // test getTransposed()
        testIsInvertableAndDet();  // test isInvertable(), det()
        testAddSubMultDivInvert();  // test add(), sub(), mult(Constant), mult(Matrix), invert(), div(Constant), div(Matrix);
        Matrix matClone = new Matrix(5,5);
        matClone.importValues(mat);
        assert mat.equals(matClone):"Matrix: importValues() test failed";
        mat.incValue(0,0,new Rational(123));  // test aliasing
        assert !mat.equals(matClone):"Matrix: importValues() test failed";
        mat.incValue(0,0,new Rational(-123));  // test aliasing
        mat = (Matrix)Matrix.mult(mat,matClone);
        mat = (Matrix)Matrix.mult(mat,matClone);
        matClone.pow(3);
        assert mat.equals(matClone):"Matrix: pow() test failed";
        mat = Matrix.getIdentityMatrix(5);
        for (int i = 0; i < 5; i++) {
            mat.incValue(i,i,new Rational(-1));
        }
        assert mat.equals(new Matrix(5,5)):"Matrix: getIdentityMatrix() test failed";
    }
    public static void testSetGetSwitchCol() {
        Matrix invertable = new Matrix(3,3);
        Constant[] row1 = {new Rational(3),new Rational(0),new Rational(2)};
        Constant[] row2 = {new Rational(2),new Rational(0),new Rational(-2)};
        Constant[] row3 = {new Rational(0),new Rational(1),new Rational(1)};

        Constant[] fetchedCol = {new Rational(3), new Rational(2), new Rational(0)};
        invertable.setCol(0,fetchedCol);
        assert invertable.getCol(0).length == invertable.getRowCount():"Matrix: getCol() test failed";
        for (int i = 0; i < invertable.getRowCount(); i++) {
            assert invertable.getValue(0,i).equals(fetchedCol[i]):"Matrix: setCol() test failed";
        }
        fetchedCol[0] = null; // test aliasing
        assert invertable.getValue(0,0) != null:"Matrix: setCol() test failed";
        fetchedCol[0] = invertable.getValue(0,0);
        for (int i = 0; i < invertable.getRowCount(); i++) {
            assert fetchedCol[i].equals(invertable.getCol(0)[i]):"Matrix: getCol() test failed";
        }
        fetchedCol=invertable.getCol(0);
        fetchedCol[0]=null;
        assert invertable.getValue(0,0) != null:"Matrix: getCol() test failed";
        fetchedCol[0]=invertable.getValue(0,0);
        Constant[] r2 = invertable.getCol(2);
        invertable.switchCols(0,2);
        for (int i = 0; i < invertable.getColCount(); i++) {
            assert invertable.getCol(0)[i].equals(r2[i]):"Matrix: switchCol() test failed";
            assert invertable.getCol(2)[i].equals(fetchedCol[i]):"Matrix: switchCol() test failed";
        }
        invertable.switchCols(0,2);
    }
    public static void testSetGetSwitchRow() {
        Matrix invertable = new Matrix(3,3);
        Constant[] row1 = {new Rational(3),new Rational(0),new Rational(2)};
        Constant[] row2 = {new Rational(2),new Rational(0),new Rational(-2)};
        Constant[] row3 = {new Rational(0),new Rational(1),new Rational(1)};

        invertable.setRow(0,row1);
        row1[2]=null;  // test aliasing
        assert invertable.getValue(2,0) != null:"Matrix: setRow test failed";
        row1[2]=invertable.getValue(2,0);
        for (int i = 0; i < invertable.getColCount(); i++) {
            assert invertable.getValue(i,0).equals(row1[i]):"Matrix: setRow() test failed";
        }
        Constant[] fetchedRow = invertable.getRow(0);
        assert fetchedRow.length == invertable.getColCount():"Matrix: getRow() test failed";
        fetchedRow[0]=null;
        assert invertable.getValue(0,0) != null:"Matrix: getRow() test failed";
        fetchedRow[0]=invertable.getValue(0,0);
        for (int i = 0; i < invertable.getColCount(); i++) {
            assert fetchedRow[i].equals(row1[i]):"Matrix: getRow() test failed";
        }
        invertable.setRow(1,row2);
        invertable.setRow(2,row3);
        invertable.switchRows(0,2);
        for (int i = 0; i < invertable.getColCount(); i++) {
            assert invertable.getRow(0)[i].equals(row3[i]):"Matrix: switchRow() test failed";
            assert invertable.getRow(2)[i].equals(row1[i]):"Matrix: switchRow() test failed";
        }
        invertable.switchRows(0,2);
    }
    public static void testGetTransposed() {
        Matrix mat = new Matrix(5,5);
        for (int i=0; i<5; i++) {
            mat.setValue(i,4-i,new Rational(i*i));
            mat.setValue(i,3,new Rational(i*i*i));
        }
        for (int col = 0; col < mat.getColCount(); col++) {
            for (int row = 0; row < mat.getRowCount(); row++) {
                assert mat.getValue(col, row).equals(mat.getTransposed().getValue(row, col)):"Matrix: getTransposed() test failed";
            }
        }
    }
    public static void testIsInvertableAndDet() {
        Matrix mat = new Matrix(5,5);
        Matrix mat2 = new Matrix(5,5);
        for (int i=0; i<5; i++) {
            mat.setValue(i,4-i,new Rational(i*i));
            mat.setValue(4-i,i,new Rational(10*i));
        }
        assert Matrix.det(mat).equals(new Rational(3456000)):"Matrix: det() test failed";
        assert mat.isInvertable():"Matrix: isInvertable() test failed";
        assert !mat2.isInvertable() && Matrix.det(mat2).equals(new Rational()):"Matrix: isInvertable() test failed";
    }
    public static void testAddSubMultDivInvert() {
        Matrix m = new Matrix(5,5);
        for (int i=0; i<25; i++) {
            m.setValue(i%m.getColCount(),i/m.getColCount(),new Rational(i+1));
        }
        Matrix mClone = m.clone();
        m.add(m);
        for (int i=0; i<25; i++) {
            assert m.getValue(i%m.getColCount(),i/m.getColCount()).equals(new Rational(2*(i+1))):"Matrix: add(this) test failed";
        }
        m.sub(mClone);
        assert m.equals(mClone):"Matrix: sub() test failed";
        m.sub(m);
        m.add(mClone);
        assert m.equals(mClone):"Matrix: add() sub(this) test failed";
        m.add(mClone);
        m.add(mClone);
        m.div(new Rational(3));
        assert m.equals(mClone):"Matrix: div(Constant) test failed";
        m.mult(new Rational(3));
        m.sub(mClone);
        m.sub(mClone);
        assert m.equals(mClone):"Matrix: mult(Constant) test failed";
        for (int i = 0; i < 5; i++) {  // make it invertable
            m.setValue(i,i,new Rational());
        }
        mClone = m.clone();
        m.invert();
        m=(Matrix)Matrix.mult(m,mClone);
        for (int i = 0; i < 5; i++) {
            m.incValue(i,i,new Rational(-1));
        }
        assert m.equals(new Matrix(5,5)):"Matrix: invert(), mult(Matrix) test failed";
        m = mClone.clone();
        m = (Matrix)Matrix.mult(m,m);
        m.div(mClone);
        assert m.equals(mClone):"Matrix: mult(Matrix this) test failed";
        m.div(m);
        for (int i = 0; i < 5; i++) {
            m.incValue(i,i,new Rational(-1));
        }
        assert m.equals(new Matrix(5,5)):"Matrix: div(Matrix) test failed";
        m = mClone.clone();
        m = (Matrix)Matrix.mult(m,m);
        m.div(mClone);
        assert m.equals(mClone):"Matrix: mult(Matrix this) test failed";
    }
}

class TestConstant{
    public static void main(String... args) {
        Constant c = new Rational(1);
        assert c.getDim().equals(c):"Constant: getDim() test failed";
    }
}

class TestRational{
    public static void main(String... args){
        Rational r = new Rational(12,13);
        assert r.clone() != r:"TestRational: clone() test failed";
        assert r.clone().equals(r):"TestRational: equals() test failed";
        assert r.equals(r):"TestRational: equals() test failed";
        assert !r.equals(new Rational(13,12)):"TestRational: equals() test failed";
        assert r.equals(new Rational(-12,-13)):"TestRational: Constructor test failed";
        assert new Rational(12,-13).equals(new Rational(-12,13)):"TestRational: Constructor test failed";
        assert new Rational(0,16836847).equals(new Rational(0,-62354)):"TestRational: Constructor test failed";
        assert new Rational(0,16836847).equals(new Rational(BigInteger.ZERO)):"TestRational: Constructor test failed";
        assert new Rational(0,16836847).equals(new Rational(0)):"TestRational: Constructor test failed";
        assert new Rational(0,16836847).equals(new Rational()):"TestRational: Constructor test failed";
        assert r.getNom().equals(new BigInteger("12")):"TestRational: getNom() test failed";
        assert r.getDenom().equals(new BigInteger("13")):"TestRational: getDenom() test failed";
        Rational r2 = r.add(new Rational(1,4));
        assert r2.equals(new Rational(61,52)):"TestRational: add() test failed";
        assert r2.sub(r).equals(new Rational(10,40)):"TestRational: sub() and gcd in constructor test failed";
        r2 = r2.add(new Rational(4,52));
        assert r2.getReal().compareTo(new BigDecimal(1.25)) == 0:"TestRational: getReal() test failed";
        assert new Rational(BigInteger.ONE,new BigInteger("10000000000000000"))
               .getRealExact().compareTo(new BigDecimal("1e-16")) == 0:"TestRational: getRealExact() test failed";
        assert new Rational(BigInteger.ONE,new BigInteger("10000000000000000")).getReal()
               .compareTo(new BigDecimal("1e-16")) != 0:"TestRational: getReal() test failed";
        assert new Rational(BigInteger.ONE,new BigInteger("10000000000000000"))
               .getReal(16).compareTo(new BigDecimal("1e-16")) == 0:"TestRational: getReal(int) test failed";
        assert r2.getReal(1).equals(new BigDecimal("1.3")):"TestRational: getReal(int) test failed";
        assert r2.equals(new Rational(5,4)):"TestRational: gcd in constructor test failed";
        assert r.div(r).equals(new Rational(1)):"TestRational: div() test failed";
        assert r.reciprocal().equals(new Rational(13,12)):"TestRational: reciprocal() test failed";
        assert r.div(r.reciprocal()).equals(r.mult(r)):"TestRational: mult() test failed";
        assert r.div(r.reciprocal()).equals(r.pow(2)):"TestRational: pow() test failed";
        assert r.negate().equals(new Rational(12,-13)):"TestRational: negate() test failed";
        assert r2.compareTo(r) > 0:"TestRational: compare() test failed";
        assert r.compareTo(r) == 0:"TestRational: compare() test failed";
        assert r.compareTo(r2) < 0:"TestRational: compare() test failed";
    }
}