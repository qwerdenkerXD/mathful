import java.math.BigDecimal;

class MathLib{
    // public static final BigDecimal PI = new BigDecimal("3.141592653589793238462643383279502884197169399375105821");
    // public static final BigDecimal E = new BigDecimal("2.718281828459045235360287471352662497757247093699959575");
    public static Constant max(Constant d, Constant... bigD){
        Constant max = null;
        if (d instanceof Rational) {
            max = (Rational)d;
            if (bigD instanceof Rational[]) {
                for (Rational i : (Rational[])bigD) {
                    if (i.compareTo((Rational)max) > 0)
                        max = i;
                }
            }
        }
        return max;
    }
    public static Constant min(Constant d, Constant... bigD){
        Constant min = null;
        if (d instanceof Rational) {
            min = (Rational)d;
            if (bigD instanceof Rational[]) {
                for (Rational i : (Rational[])bigD) {
                    if (i.compareTo((Rational)min) < 0)
                        min = i;
                }
            }
        }
        return min;
    }
    public static Constant abs(Constant d){
        if (d instanceof Rational) {
            if (((Rational)d).compareTo(new Rational()) > 0)
                return d;
        }
        return d.negate();
    }
    public static Constant sum(Constant... d){
        Constant sum = new Rational();
        for (Constant c : d) {
            sum = sum.add(c);
        }
        return sum;
    }
}