import java.math.BigInteger;
import java.math.BigDecimal;

public class Rational{
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
    BigDecimal getReal(int precision){
        return new BigDecimal(nom).divide(new BigDecimal(denom),precision,BigDecimal.ROUND_HALF_UP);
    }
    BigDecimal getReal(){
        return getReal(10);
    }
    BigDecimal getRealExact(){
        return new BigDecimal(nom).divide(new BigDecimal(denom));
    }
    Rational add(Rational r){
        return new Rational(nom.multiply(r.getDenom()).add(r.getNom().multiply(denom)), denom.multiply(r.getDenom()));
    }
    Rational sub(Rational r){
        return add(r.negate());
    }
    Rational mult(Rational r){
        return new Rational(nom.multiply(r.getNom()), denom.multiply(r.getDenom()));
    }
    Rational div(Rational r){
        return mult(r.reciprocal());
    }
    Rational pow(int n){
        Rational result = this;
        if (n == 0){
            return new Rational(BigInteger.ONE, BigInteger.ONE);
        }
        if (n < 0) {
            result = result.reciprocal();
            n = -n;
        }
        for (int i = 1; i < n; i++) {
            result = result.mult(this);
        }
        return result;
    }
    Rational reciprocal(){
        return new Rational(denom, nom);
    }
    Rational negate(){
        return new Rational(nom.negate(), denom);
    }
    int compareTo(Rational r){
        return (nom.multiply(r.getDenom())).compareTo(r.getNom().multiply(denom));
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