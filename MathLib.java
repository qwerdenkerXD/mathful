import java.math.BigDecimal;

class MathLib{
    public static final BigDecimal PI = new BigDecimal("3.14159265358979323846264338327950288419716939937510582097494");
    public static RealNumber max(RealNumber d, RealNumber... bigD){
        RealNumber max = d;
        for (RealNumber i : bigD) {
            if (i.compareTo(max) > 0)
                max = i;
        }
        return max;
    }
    public static RealNumber min(RealNumber d, RealNumber... bigD){
        RealNumber min = d;
        for (RealNumber i : bigD) {
            if (i.compareTo(min) < 0)
                min = i;
        }
        return min;
    }
    public static RealNumber abs(RealNumber d){
        if (d.compareTo(new Rational()) > 0)
            return d;
        return d.neg();
    }
}