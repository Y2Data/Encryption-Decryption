import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        boolean b = sc.nextBoolean();
        if (!b)
            System.out.println(10 <= a && a <= 20);
        else
            System.out.println(15 <= a && a <= 25);
    }
}