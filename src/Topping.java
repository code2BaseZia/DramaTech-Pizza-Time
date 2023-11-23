public enum Topping {

    CHEESE(true, false),
    PEPPERONI (false, false),
    SPICY_SAUSAGE(false, false),
    BEEF(false, false),
    BACON(false, false),
    SAUSAGE(false, false),
    ANCHOVIES(false, false),
    GRILLED_CHICKEN(false, false),
    SALAMI(false, false),
    MEATBALL(false, false),
    CANADIAN_BACON(false, false),
    STEAK(false, false),
    MUSHROOMS(true, false),
    ONIONS(true, false),
    GREEN_PEPPERS(true, false),
    BLACK_OLIVES(true, false),
    BANANA_PEPPERS(true, false),
    JALAPENO_PEPPERS(true, false),
    PINEAPPLE(true, false),
    ROMA_TOMATOES(true, false),
    FRESH_SPINACH(true, false),
    NO_CHEESE(true, true);


    private final boolean isVeg;
    private final boolean isDietary;
    Topping(boolean isVeg, boolean isDietary) {
        this.isVeg = isVeg;
        this.isDietary = isDietary;
    }
    public boolean getIsVeg() {
        return this.isVeg;
    }
    public boolean getIsDietary() {
        return this.isDietary;
    }
    public String toString() {
        return String.format("%s%s", this.name().charAt(0), this.name().substring(1).toLowerCase()).replaceAll("_", " ");
    }
}
