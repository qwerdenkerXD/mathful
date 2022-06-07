abstract class Constant implements Dimensional {
    public Constant getDim(){
        return new Rational(1);
    }
    abstract Constant add(Constant real);
    abstract Constant mult(Constant real);
    abstract Constant sub(Constant real);
    abstract Constant div(Constant real);
    abstract Constant pow(int n);
    abstract Constant negate();
    abstract boolean equals(Constant c);
}