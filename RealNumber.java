interface RealNumber {
    public int compareTo(RealNumber real);
    public RealNumber add(RealNumber real);
    public RealNumber mult(RealNumber real);
    public RealNumber sub(RealNumber real);
    public RealNumber div(RealNumber real);
    public RealNumber power(int n);
    public RealNumber neg();
}