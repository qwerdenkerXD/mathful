interface Computable {
    public Computable add(Computable real);
    public Computable mult(Computable real);
    public Computable sub(Computable real);
    public Computable div(Computable real);
    public Computable power(int n);
    public Computable negate();
    public boolean equals(Computable real);
}