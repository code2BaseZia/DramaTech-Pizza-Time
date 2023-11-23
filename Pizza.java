public class Pizza {
    private final Topping topping1;
    private final Topping topping2;

    public Pizza(Topping topping1, Topping topping2) {
        this.topping1 = topping1;
        this.topping2 = topping2;
    }

    public String toString() {
        return topping2 == null ? String.format("%s pizza(s).", topping1.name())
                : String.format("half %s and half %s pizza(s).", topping1.name(), topping2.name());
    }

    public boolean equals(Object toCompare) {
        if (toCompare == null) {
            return false;
        }
        if (!(this.getClass().equals(toCompare.getClass()))) {
            return false;
        }
        Pizza compare = (Pizza) toCompare;
        if (!(topping1.equals(compare.topping1))) {
            return false;
        }
        if (topping2 == null) {
            return compare.topping2 == null;
        }
        return topping2.equals(compare.topping2);
    }
    public int hashCode() {
        if (topping2 != null) {
            return topping1.hashCode() + topping2.hashCode();
        }
        return topping1.hashCode();
    }
}
