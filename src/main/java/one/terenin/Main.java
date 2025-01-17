package one.terenin;

public class Main {
    public static void main(String[] args) {
        Compiler compiler = new Compiler("{a = 6; m = 283; s = m - 2; b = a; c = 1; while(0 < s) { if (0 < s - (s/2*2)){c = c * b; c = c - (c / m * m);} s = s / 2; b = b * b; b = b - (b / m * m);} print(c);}".toCharArray(), 166);
        compiler.compile();
        System.out.println("Hello world!");
    }
}