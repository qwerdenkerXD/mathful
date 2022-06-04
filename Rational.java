import java.math.BigInteger;

public class Rational implements RealNumber{
    final BigInteger nom, denom;
    Rational(BigInteger nom, BigInteger denom){
        if (denom.compareTo(BigInteger.ZERO) == 0)
            throw new IllegalArgumentException("Zero in denominator");
        if (nom.compareTo(BigInteger.ZERO) == 0){
            this.nom = nom;
            this.denom = BigInteger.ONE;
            return;
        }
        BigInteger m = nom.abs();
        BigInteger n = denom.abs();
        BigInteger r = m.remainder(n);
        while (r.compareTo(BigInteger.ZERO) != 0) {
            m = n;
            n = r;
            r = m.remainder(n);
        }
        if (denom.compareTo(BigInteger.ZERO) < 0){
            nom = nom.negate();
            denom = denom.negate();
        }
        this.nom = nom.divide(n);
        this.denom = denom.divide(n);
        assert (this.denom).compareTo(BigInteger.ZERO) >= 0:"Denominator negative";
        assert (this.denom).compareTo(BigInteger.ZERO) != 0:"Denominator zero";
        assert (this.nom).gcd(this.denom).compareTo(BigInteger.ONE) == 0:"Rational not shortened correctly";
    }
    Rational(int nom, int denom){
        this(new BigInteger(nom + ""), new BigInteger(denom + ""));
    }
    Rational(BigInteger nom){
        this(nom, BigInteger.ONE);
    }
    Rational(int nom){
        this(new BigInteger(nom + ""));
    }
    Rational(){
        this(BigInteger.ZERO);
    }
    BigInteger getNom(){
        return nom;
    }
    BigInteger getDenom(){
        return denom;
    }
    Decimal getReal(int precision){
        return new Decimal(new Decimal(nom).divide(new Decimal(denom), precision, Decimal.ROUND_HALF_UP));
    }
    Decimal getReal(){
        try{
            return getRealExact();
        } catch (ArithmeticException e) {
            return getReal(15);
        }
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
                return ((Decimal)real).add(neg());
            } catch (ArithmeticException e) {
                return ((Decimal)real).add(neg());
            }
        }
        return add(((Rational)real).neg());
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
    public RealNumber neg(){
        return new Rational(nom.negate(), denom);
    }
    public int compareTo(RealNumber real){
        if (real instanceof Decimal){
            return compareTo(((Decimal)real).getRational());
        }
        return (nom.multiply(((Rational)real).getDenom())).compareTo(((Rational)real).getNom().multiply(denom));
    }
    public String toString(){
        if (denom.compareTo(BigInteger.ONE) == 0)
            return nom.toString();
        return nom.toString() + "/" + denom.toString();
    }
}