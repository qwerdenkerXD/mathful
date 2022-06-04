import java.math.BigInteger;
import java.math.BigDecimal;

class Decimal extends java.math.BigDecimal implements RealNumber{
    Decimal(BigInteger big){
        super(big);
    }
    Decimal(String dec){
        super(dec);
    }
    Decimal(){
        this(BigInteger.ZERO);
    }
    Rational getRational(){
        return new Rational(unscaledValue(),TEN.pow(scale()).unscaledValue());
    }
    public int compareTo(RealNumber real){
        if (real instanceof Rational) {
            try{
                return compareTo((BigDecimal)((Rational)real).getRealExact());
            } catch (ArithmeticException e) {
                return compareTo((BigDecimal)((Rational)real).getReal(scale()));
            }
        }
        return compareTo((BigDecimal) real);
    }
    public RealNumber add(RealNumber real){
        if (real instanceof Rational) {
            try{
                return (Decimal)add((BigDecimal)((Rational)real).getRealExact());
            } catch (ArithmeticException e) {
                return (Decimal)add((BigDecimal)((Rational)real).getReal());
            }
        }
        return (Decimal)add((BigDecimal) real);
    }
    public RealNumber mult(RealNumber real){
        if (real instanceof Rational) {
            try{
                return (Decimal)multiply(((Rational)real).getRealExact());
            } catch (ArithmeticException e) {
                return (Decimal)multiply(((Rational)real).getReal());
            }
        }
        return (Decimal)multiply((BigDecimal) real);
    }
    public RealNumber sub(RealNumber real){
        if (real instanceof Rational) {
            try{
                return (Decimal)subtract(((Rational)real).getRealExact());
            } catch (ArithmeticException e) {
                return (Decimal)subtract(((Rational)real).getReal());
            }
        }
        return (Decimal)subtract((BigDecimal) real);
    }
    public RealNumber div(RealNumber real){
        if (real instanceof Rational) {
            try{
                return (Decimal)divide(((Rational)real).getRealExact());
            } catch (ArithmeticException e) {
                return (Decimal)divide(((Rational)real).getReal());
            }
        }
        return (Decimal)divide((BigDecimal) real);
    }
    public RealNumber power(int n){
        return (Decimal)pow(n);
    }
    public RealNumber neg(){
        return (Decimal)negate();
    }
}