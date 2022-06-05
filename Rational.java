import java.math.BigInteger;
import java.math.BigDecimal;

public class Rational implements Computable{
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
    BigDecimal getReal(int precision){
        return new BigDecimal(nom).divide(new BigDecimal(denom), precision, BigDecimal.ROUND_HALF_UP);
    }
    BigDecimal getReal(){
        return getReal(15);
    }
    BigDecimal getRealExact(){
        return new BigDecimal(nom).divide(new BigDecimal(denom));
    }
    public Computable add(Computable real){
        return new Rational(( nom.multiply(((Rational)real).getDenom()) ).add( ((Rational)real).getNom().multiply(denom) ), 
                              denom.multiply(((Rational)real).getDenom()) );
    }
    public Computable sub(Computable real){
        return add(((Rational)real).negate());
    }
    public Computable mult(Computable real){
        return new Rational(nom.multiply(((Rational)real).getNom()), denom.multiply(((Rational)real).getDenom()));
    }
    public Computable div(Computable real){
        return mult(((Rational)real).reciprocal());
    }
    public Computable power(int n){
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
    public Computable negate(){
        return new Rational(nom.negate(), denom);
    }
    public int compareTo(Rational r){
        return (nom.multiply(r.getDenom())).compareTo(r.getNom().multiply(denom));
    }
    public boolean equals(Computable real){
        return ((Rational)real).getNom().equals(nom) && ((Rational)real).getDenom().equals(denom);
    }
    public String toString(){
        if (denom.compareTo(BigInteger.ONE) == 0)
            return nom.toString();
        return nom.toString() + "/" + denom.toString();
    }
}