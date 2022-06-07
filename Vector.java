class Vector extends MultiDimensional{
    Vector(Constant... values){
        super(1, values.length);
        setCol(0, values);
    }
    Constant getValue(int i){
        return getValue(0, i);
    }
    Constant[] getValues(){
        return getCol(0);
    }
    void setValue(int i, Constant value){
        setValue(0, i, value);
    }
    void incValue(int i, Constant incValue){
        incValue(0, i, incValue);
    }
    Constant mult(Vector v){
        if (!v.getDim().equals(getDim()))
            throw new UnsupportedOperationException("Vector: Tried to multiply two vectors of different dimension");
        Matrix vMatrix = new Matrix(1, v.getValues().length);
        vMatrix.setCol(0, v.getValues());
        return (Constant)mult(getTransposed(), vMatrix);
    }
    void crossMult(Vector v){
        Constant sum = MathLib.sum(v.getValues());
        for (int i = 0; i < getRowCount(); i++) {
            setValue(i, sum.mult(getValue(i)));
        }
    }
    protected Vector clone(){
        return new Vector(getValues());
    }
}