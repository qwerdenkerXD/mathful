import java.math.BigDecimal;

class MathLib{
    public static final BigDecimal PI = new BigDecimal("3.141592653589793238462643383279502884197169399375105821");
    public static final BigDecimal E = new BigDecimal("2.718281828459045235360287471352662497757247093699959575");
    public static Computable max(Computable d, Computable... bigD){
        Computable max = null;
        if (d instanceof Rational) {
            max = (Rational)d;
            for (Rational i : (Rational[])bigD) {
                if (i.compareTo((Rational)max) > 0)
                    max = i;
            }
        }
        return max;
    }
    public static Computable min(Computable d, Computable... bigD){
        Computable min = null;
        if (d instanceof Rational) {
            min = (Rational)d;
            for (Rational i : (Rational[])bigD) {
                if (i.compareTo((Rational)min) < 0)
                    min = i;
            }
        }
        return min;
    }
    public static Computable abs(Computable d){
        if (d instanceof Rational) {
            if (((Rational)d).compareTo(new Rational()) > 0)
                return d;
        }
        return d.negate();
    }
}