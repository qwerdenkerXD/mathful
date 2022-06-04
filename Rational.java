import java.math.BigInteger;

public class Rational implements RealNumber{
    final BigInteger nom, denom;
    Rational(BigInteger nom, BigInteger denom){
        if (denom.compareTo(BigInteger.ZERO) == 0)
            throw new IllegalArgumentException("Zero in denominator");
        BigInteger m = nom;
        BigInteger n = denom;
        BigInteger r = m.remainder(n);
        while (r.compareTo(BigInteger.ZERO) != 0) {
            m = n;
            n = r;
            r = m.remainder(n);
        }
        this.nom = nom.divide(n);
        this.denom = denom.divide(n);
        assert (this.denom).compareTo(BigInteger.ZERO) >= 0:"Denominator negative";
        assert (this.denom).compareTo(BigInteger.ZERO) != 0:"Denominator zero";
        assert (this.nom).gcd(denom).compareTo(BigInteger.ONE) == 0:"Rational not shortened correctly";
    }
    Rational(BigInteger nom){
        this(nom, BigInteger.ONE);
    }
    BigInteger getNom(){
        return nom;
    }
    BigInteger getDenom(){
        return denom;
    }
    Decimal getReal(int precision){
        return (Decimal)new Decimal(nom).div(new Decimal(denom));
    }
    Decimal getReal(){
        return getReal(15);
    }
    Decimal getRealExact(){
        return (Decimal)new Decimal(nom).div(new Decimal(denom));
    }
    public RealNumber add(RealNumber real){
        if (real instanceof Decimal){
            try{
                return ((Decimal)real).add(this);
            } catch (ArithmeticException e) {
                return ((Decimal)real).add(this);
            }
        }
        return new Rational(nom.multiply(((Rational)real).getDenom()).add(((Rational)real).getNom().multiply(denom)), denom.multiply(((Rational)real).getDenom()));
    }
    public RealNumber sub(RealNumber real){
        if (real instanceof Decimal){
            try{
                return ((Decimal)real).add(negate());
            } catch (ArithmeticException e) {
                return ((Decimal)real).add(negate());
            }
        }
        return add(((Rational)real).negate());
    }
    public RealNumber mult(RealNumber real){
        if (real instanceof Decimal){
            try{
                return ((Decimal)real).mult(getRealExact());
            } catch (ArithmeticException e) {
                return ((Decimal)real).mult(getReal());
            }
        }
        return new Rational(nom.multiply(((Rational)real).getNom()), denom.multiply(((Rational)real).getDenom()));
    }
    public RealNumber div(RealNumber real){
        if (real instanceof Decimal){
            try{
                return ((Decimal)real).div(getRealExact());
            } catch (ArithmeticException e) {
                return ((Decimal)real).div(getReal());
            }
        }
        return mult(((Rational)real).reciprocal());
    }
    public RealNumber power(int n){
        Rational result = this;
        if (n == 0){
            return new Rational(BigInteger.ONE, BigInteger.ONE);
        }
        if (n < 0) {
            result = result.reciprocal();
            n = -n;
        }
        for (int i = 1; i < n; i++) {
            result = (Rational)result.mult(this);
        }
        return result;
    }
    public Rational reciprocal(){
        return new Rational(denom, nom);
    }
    Rational negate(){
        return new Rational(nom.negate(), denom);
    }
    public int compareTo(RealNumber real){
        if (real instanceof Decimal){
            try{
                return ((Decimal)real).compareTo(this);
            } catch (ArithmeticException e) {
                return ((Decimal)real).compareTo(this);
            }
        }
        return (nom.multiply(((Rational)real).getDenom())).compareTo(((Rational)real).getNom().multiply(denom));
    }
    boolean gr(Rational r){
        return compareTo(r) == 1;
    }
    boolean lo(Rational r){
        return compareTo(r) == -1;
    }
    boolean ge(Rational r){
        return compareTo(r) > -1;
    }
    boolean le(Rational r){
        return compareTo(r) < 1;
    }
    boolean ne(Rational r){
        return compareTo(r) != 0;
    }
    public boolean equals(Rational r){
        return compareTo(r) == 0;
    }
    public String toString(){
        return nom.toString() + "/" + denom.toString();
    }
}