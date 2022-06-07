import java.math.BigInteger;
import java.math.BigDecimal;

public class Rational extends Constant{
    final BigInteger nom, denom;
    Rational(BigInteger nom, BigInteger denom){
        if (denom.compareTo(BigInteger.ZERO) == 0)
            throw new IllegalArgumentException("Rational: Zero in denominator");
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
    Rational(Rational r){
        this(r.getNom(), r.getDenom());
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
        return new BigDecimal(nom).divide(new BigDecimal(denom), precision, java.math.RoundingMode.HALF_UP);
    }
    BigDecimal getReal(){
        return getReal(15);
    }
    BigDecimal getRealExact(){
        return new BigDecimal(nom).divide(new BigDecimal(denom));
    }
    public Rational add(Constant real){
        return new Rational(( nom.multiply(((Rational)real).getDenom()) ).add( ((Rational)real).getNom().multiply(denom) ), 
                              denom.multiply(((Rational)real).getDenom()) );
    }
    public Rational sub(Constant real){
        return add(((Rational)real).negate());
    }
    public Rational mult(Constant real){
        return new Rational(nom.multiply(((Rational)real).getNom()), denom.multiply(((Rational)real).getDenom()));
    }
    public Rational div(Constant real){
        return mult(((Rational)real).reciprocal());
    }
    public Rational pow(int n){
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
    Rational reciprocal(){
        return new Rational(denom, nom);
    }
    public Rational negate(){
        return new Rational(nom.negate(), denom);
    }
    public int compareTo(Rational r){
        return (nom.multiply(r.getDenom())).compareTo(r.getNom().multiply(denom));
    }
    public boolean equals(Constant real){
        return ((Rational)real).getNom().equals(nom) && ((Rational)real).getDenom().equals(denom);
    }
    public String toString(){
        if (denom.compareTo(BigInteger.ONE) == 0)
            return nom.toString();
        return nom.toString() + "/" + denom.toString();
    }
    protected Rational clone(){
        return new Rational(this);
    }
}