interface Constant {
    public Constant add(Constant real);
    public Constant mult(Constant real);
    public Constant sub(Constant real);
    public Constant div(Constant real);
    public Constant power(int n);
    public Constant negate();
    public boolean equals(Constant real);
}