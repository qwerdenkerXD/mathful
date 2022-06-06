class Vector implements MultiDimensional{
    private final Matrix vector;
    Vector(Constant... values){
        vector = new Matrix(1, values.length);
        vector.setCol(0, values);
    }
    public Constant getDim(){
        return vector.getDim();
    }
    Constant getValue(int i){
        return vector.getValue(0, i);
    }
    Constant[] getValues(){
        return vector.getCol(0);
    }
    void setValue(int i, Constant value){
        vector.setValue(0, i, value);
    }
    void add(Vector v){
        vector.add(vector);
    }
    void sub(Vector v){
        vector.add(vector);
    }
    Constant mult(Vector v){
        if (!v.getDim().equals(getDim()))
            throw new UnsupportedOperationException("Vector: Tried to multiply two vectors of different dimension");
        Matrix vMatrix = new Matrix(1, v.getValues().length);
        vMatrix.setCol(0, v.getValues());
        return (Constant)Matrix.mult(vector.getTransposed(), vMatrix);
    }
    void crossMult(Vector v){
        Constant sum = MathLib.sum(v.getValues());
        for (int i = 0; i < vector.getRowCount(); i++) {
            setValue(i, sum.mult(getValue(i)));
        }
    }
}