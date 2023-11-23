import java.util.Scanner;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashSet;
public class Driver {
    private final static Topping[] toppings = Topping.values();
    private final static HashMap<Topping, Integer> count = new HashMap<>();
    private final static Scanner input = new Scanner(System.in);
    public static void main (String[] args) {

        System.out.println("Welcome to the DT Pizza Time program!");
        printToppings();
        System.out.println("Please choose the toppings that were added and input the number of people who wanted that topping. Once finished, type -1");
        int inputI = 0;
        boolean run = true;
        while (run) {
            try {
                inputI = Integer.parseInt(input.nextLine().strip());
            } catch (Exception e) {
                System.out.println("Please enter a valid numeric input!");
            }
            switch (inputI) {
                case (0): printToppings();
                          break;
                case (-1):  computePizzaOrder();
                            run = false;
                            break;
                default: try {
                    addToCount(inputI);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private static void printToppings() {
        System.out.println("0: Reprint the toppings list");
        for (int i = 0; i < toppings.length; ++i) {
            System.out.printf("%d: %s%n",i + 1, toppings[i]);
        }
        System.out.println("-1: Compute pizza order");
    }
    private static void addToCount(int index) {
        if (index < 0 || index > toppings.length) {
            throw new IllegalArgumentException("Please enter a valid topping index!");
        }
        System.out.println("Enter the number of people who want this topping.");
        int inputI;
        while (true) {
            try {
                inputI = Integer.parseInt(input.nextLine().strip());
                break;
            } catch (Exception e) {
                System.out.println("Please enter a valid numeric input!");
            }
        }
        count.put(toppings[index - 1], inputI);
        System.out.println("Please enter the number for the next topping!");
    }
    private static void computePizzaOrder() {
        LinkedList<Pizza> pizzas = new LinkedList<>();
        pizzas.addAll(computeVegPizzas());
        pizzas.addAll(computeMeatPizzas());
        pizzas.addAll(computeDietaryPizzas());

        countPizzas(pizzas);
    }


    private static LinkedList<Pizza> computeVegPizzas() {
        LinkedList<Pizza> pizzas = new LinkedList<>();
        Set<Topping> toppingSet = new HashSet<>();
        for (Topping t : count.keySet()) {
            if (t.getIsVeg() && !t.getIsDietary()) {
                toppingSet.add(t);
            }
        }
        for (Topping t : toppingSet) {
            int num = count.get(t);
            while (num >= 3) {
                pizzas.add(new Pizza(t, null));
                num = Math.max(num - 4, 0);
                count.put(t, num);
            }
        }
        removeEmpty(toppingSet);
        Topping prev = null;
        for (Topping t : toppingSet) {
            int num = count.get(t);
            if (prev == null && num != 0) {
                prev = t;
                continue;
            } else if (num == 0) {
                continue;
            }
            int numPrev = count.get(prev);
            num = Math.max(num - 2, 0);
            numPrev = Math.max(numPrev - 2, 0);
            pizzas.add(new Pizza(t, prev));
            count.put(t, num);
            count.put(prev, numPrev);
            prev = null;
        }
        while (toppingSet.size() != 0) {
            removeEmpty(toppingSet);
            if (toppingSet.size() == 0) {
                break;
            }
            Object[] toppingArr = toppingSet.toArray();
            if (toppingArr[0].equals(Topping.CHEESE)) {
                pizzas.add(new Pizza(Topping.CHEESE, null));
                count.put(Topping.CHEESE,Math.max(count.get(Topping.CHEESE) - 2, 0));
            } else {
                pizzas.add(new Pizza((Topping) toppingArr[0], Topping.CHEESE));
                count.put((Topping) toppingArr[0],Math.max(count.get(toppingArr[0]) - 2, 0));
            }
        }
        return pizzas;
    }
    private static LinkedList<Pizza> computeMeatPizzas() {
        LinkedList<Pizza> pizzas = new LinkedList<>();
        Set<Topping> toppingSet = new HashSet<>();
        for (Topping t : count.keySet()) {
            if (!t.getIsVeg() && !t.getIsDietary()) {
                toppingSet.add(t);
            }
        }
        for (Topping t : toppingSet) {
            int num = count.get(t);
            while (num >= 3) {
                pizzas.add(new Pizza(t, null));
                num = Math.max(num - 4, 0);
                count.put(t, num);
            }
        }
        removeEmpty(toppingSet);
        Topping prev = null;
        for (Topping t : toppingSet) {
            int num = count.get(t);
            if (prev == null && num != 0) {
                prev = t;
                continue;
            } else if (num == 0) {
                continue;
            }
            int numPrev = count.get(prev);
            num = Math.max(num - 2, 0);
            numPrev = Math.max(numPrev - 2, 0);
            pizzas.add(new Pizza(t, prev));
            count.put(t, num);
            count.put(prev, numPrev);
            prev = null;
        }
        while (toppingSet.size() != 0) {
            removeEmpty(toppingSet);
            if (toppingSet.size() == 0) {
                break;
            }
            Object[] toppingArr = toppingSet.toArray();
            pizzas.add(new Pizza((Topping) toppingArr[0], null));
            count.put((Topping) toppingArr[0],Math.max(count.get(toppingArr[0]) - 2, 0));
        }
        return pizzas;
    }
    private static LinkedList<Pizza> computeDietaryPizzas() {
        LinkedList<Pizza> pizzas = new LinkedList<>();
        Set<Topping> toppingSet = new HashSet<>();
        for (Topping t : count.keySet()) {
            if (t.getIsDietary()) {
                toppingSet.add(t);
            }
        }
        for (Topping t : toppingSet) {
            int num = count.get(t);
            while (num != 0) {
                pizzas.add(new Pizza(t, null));
                num = Math.max(num - 4, 0);
                count.put(t, num);
            }
        }
        removeEmpty(toppingSet);
        return pizzas;
    }
    private static void removeEmpty(Set<Topping> toppingSet) {
        Object[] toppingArr = toppingSet.toArray();
        for (Object o : toppingArr) {
            if (count.get((Topping) o) == 0) {
                toppingSet.remove((Topping) o);
            }
        }
    }
    private static void countPizzas(LinkedList<Pizza> pizzas) {
        HashMap<Pizza, Integer> countPizzas = new HashMap<>();
        for (Pizza p : pizzas) {
            countPizzas.merge(p, 1, Integer::sum);
        }
        String[] toPrint = new String[countPizzas.size()];
        int i = 0;
        for (Pizza p : countPizzas.keySet()) {
            toPrint[i] = String.format("Order %d %s", countPizzas.get(p), p);
            ++i;
        }
        Arrays.sort(toPrint);
        for (i = toPrint.length - 1; i > -1; --i) {
            System.out.println(toPrint[i]);
        }
    }
}