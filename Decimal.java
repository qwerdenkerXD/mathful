import java.math.BigInteger;
import java.math.BigDecimal;

class Decimal extends java.math.BigDecimal implements RealNumber{
    Decimal(BigInteger big){
        super(big);
    }
    Decimal(String dec){
        super(dec);
    }
    public int compareTo(RealNumber real){
        if (real instanceof Rational) {
            try{
                return compareTo(((Rational)real).getRealExact());
            } catch (ArithmeticException e) {
                return compareTo(((Rational)real).getReal(scale()));
            }
        }
        return compareTo((BigDecimal) real);
    }
}