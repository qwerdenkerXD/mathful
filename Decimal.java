import java.math.BigInteger;
import java.math.BigDecimal;

class Decimal extends java.math.BigDecimal implements RealNumber{
    Decimal(BigInteger big){
        super(big);
    }
    Decimal(String dec){
        super(dec);
    }
    Decimal(BigDecimal dec){
        super(dec.toString());
    }
    Decimal(){
        this(BigInteger.ZERO);
    }
    Rational getRational(){
        return new Rational(unscaledValue(),TEN.pow(scale()).unscaledValue());
    }
    public int compareTo(RealNumber real){
        if (real instanceof Rational)
            return compareTo((BigDecimal)((Rational)real).getReal(scale()));
        
        return compareTo((BigDecimal) real);
    }
    public RealNumber add(RealNumber real){
        if (real instanceof Rational)
            return new Decimal(add((BigDecimal)((Rational)real).getReal()));
        
        return new Decimal(add((BigDecimal) real));
    }
    public RealNumber mult(RealNumber real){
        if (real instanceof Rational)
            return new Decimal(multiply(((Rational)real).getReal()));
            
        return new Decimal(multiply((BigDecimal) real));
    }
    public RealNumber sub(RealNumber real){
        if (real instanceof Rational)
            return new Decimal(subtract(((Rational)real).getReal()));

        return new Decimal(subtract((BigDecimal) real));
    }
    public RealNumber div(RealNumber real){
        if (real instanceof Rational)
            return new Decimal(divide(((Rational)real).getReal()));
        
        return new Decimal(divide((BigDecimal) real));
    }
    public RealNumber power(int n){
        return new Decimal(pow(n));
    }
    public RealNumber neg(){
        return new Decimal(negate());
    }
}