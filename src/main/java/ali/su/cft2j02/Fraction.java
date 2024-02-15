package ali.su.cft2j02;

import java.util.Objects;

public class Fraction implements Fractionable{
    private int num;
    private int denom;

    public Fraction(int num, int denom) {
        this.num = num;
        this.denom = denom;
    }

    @Override
    @Cache(1000)
    public double doubleValue() {
        return (double) num / denom;
    }

    @Override
    @Mutator
    public void setNum(int num) {
        this.num = num;
    }

    @Override
    @Mutator
    public void setDenom(int denom) {
        this.denom = denom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fraction fraction = (Fraction) o;
        return num == fraction.num && denom == fraction.denom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, denom);
    }
}
