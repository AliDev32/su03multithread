package ali.su.cft2j02;

public class Fraction implements Fractionable{
    private int num;
    private int denom;

    public Fraction(int num, int denom) {
        this.num = num;
        this.denom = denom;
    }

    @Override
    @Cache
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
}
