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

    // mode variable responsible for the mode of the program:
    // 0 = Invalid/Unset
    // 1 = By the slice
    // 2 = Pizza order (by person)
    // 3 = Aborted
    private static short mode = 0;

    private static boolean select = true;
    public static void main (String[] args) {


        // Start of main driver code, asking user to select a mode
        System.out.println("Welcome to the DT Pizza Time program!");
        System.out.println("Select by the slice (BS) or pizza order (PO) mode!");
        String inputS = "";
        while (select) {
            try {
                inputS = input.nextLine().strip().toLowerCase();
            } catch (Exception e) {
                System.out.println("Please enter a valid mode input!");
            }
            switch (inputS) {
                case ("bs"): select = false;
                             mode = 1;
                             break;
                case ("po"): select = false;
                             mode = 2;
                             break;
                default: System.out.println("Please enter a valid mode input!");
            }
        }
        // Based on mode selected, run either by the slice or pizza order
        run();
    }




    private static void run() {
        printToppings();
        System.out.printf("Please choose the toppings that you would like to add and input the number of %s that topping. Once finished, type -1\n", (mode == 1) ? "slices requested of" : "people who wanted");
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
            System.out.printf("%d: %s%n", i + 1, toppings[i]);
        }
        System.out.println("-1: Finish entering values");
    }
    private static void addToCount(int index) {
        if (index < 0 || index >= toppings.length) {
            throw new IllegalArgumentException("Please enter a valid topping index!");
        }
        System.out.printf("Enter %s this topping.\n", (mode == 1) ? "the number of slices for" : "the number of people who want");
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
//        pizzas.addAll(computeMeatPizzas());
//        pizzas.addAll(computeDietaryPizzas());

        if (mode == 3) {
            System.out.println("Pizza aborted.");
            return;
        }

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
            if (mode == 1) {
                // Handle adding whole pizzas
                while (num >= 8) {
                    pizzas.add(new Pizza(t, null));
                    num = Math.max(num - 8, 0);
                    count.put(t, num);
                }
            } else if (mode == 2) {
                // Handle adding whole pizzas
                while (num >= 3) {
                    pizzas.add(new Pizza(t, null));
                    num = Math.max(num - 4, 0);
                    count.put(t, num);
                }
            }
        }
        removeEmpty(toppingSet);
        Topping prev = null;
        for (Topping t : toppingSet) {
            int num = count.get(t);
            if (mode == 1) {
                if (prev == null && num != 0) {
                    prev = t;
                    continue;
                } else if (num == 0) {
                    continue;
                }
                // Handle adding half pizzas
                int numPrev = count.get(prev);
                num = Math.max(num - 4, 0);
                numPrev = Math.max(numPrev - 4, 0);
                pizzas.add(new Pizza(t, prev));
                count.put(t, num);
                count.put(prev, numPrev);
                prev = null;
            } else if (mode == 2) {
                if (prev == null && num != 0) {
                    prev = t;
                    continue;
                } else if (num == 0) {
                    continue;
                }
                // Handle adding half pizzas
                int numPrev = count.get(prev);
                num = Math.max(num - 2, 0);
                numPrev = Math.max(numPrev - 2, 0);
                pizzas.add(new Pizza(t, prev));
                count.put(t, num);
                count.put(prev, numPrev);
                prev = null;
            }
        }
        if (mode == 1) {
            handleStraySlices(toppingSet);
        } else if (mode == 2) {
            while (!toppingSet.isEmpty()) {
                removeEmpty(toppingSet);
                if (toppingSet.isEmpty()) {
                    break;
                }
                Object[] toppingArr = toppingSet.toArray();
                if (toppingArr[0].equals(Topping.CHEESE)) {
                    // Handle cheese pizzas last and add whole ones
                    pizzas.add(new Pizza(Topping.CHEESE, null));
                    count.put(Topping.CHEESE,Math.max(count.get(Topping.CHEESE) - 4, 0));
                } else {
                    // Handle adding any outstanding half pizzas by filling with cheese
                    pizzas.add(new Pizza((Topping) toppingArr[0], Topping.CHEESE));
                    count.put((Topping) toppingArr[0],Math.max(count.get(toppingArr[0]) - 2, 0));
                }
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
            if (mode == 1) {
                while (num >= 8) {
                    pizzas.add(new Pizza(t, null));
                    num = Math.max(num - 8, 0);
                    count.put(t, num);
                }
            } else if (mode == 2) {
                while (num >= 3) {
                    pizzas.add(new Pizza(t, null));
                    num = Math.max(num - 4, 0);
                    count.put(t, num);
                }
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
        while (!toppingSet.isEmpty()) {
            removeEmpty(toppingSet);
            if (toppingSet.isEmpty()) {
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

    private static void handleStraySlices(Set<Topping> toppingSet) {
        Object[] toppingArr = toppingSet.toArray();
        int count_non_zero = 0;
        for (Object o : toppingArr) {
            if (count.get((Topping) o) != 0) {
                count_non_zero++;
            }
        }
        if (count_non_zero == 0) {
            return;
        }
        System.out.println("!!!WARNING!!! STRAY SLICES DETECTED !!!WARNING!!!");
        for (Object o : toppingArr) {
            System.out.printf("%d slices of %s topping are unaccounted for.\n", count.get((Topping) o), o);
        }
        System.out.println("Enter -1 if you want to continue! Otherwise, enter any other number to cancel.");
        int inputI;
        try {
            inputI = Integer.parseInt(input.nextLine().strip());
            if (inputI != -1) {
                mode = 3;
            }
        } catch (Exception e) {
            System.out.println("Please enter a valid numeric input!");
        }
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