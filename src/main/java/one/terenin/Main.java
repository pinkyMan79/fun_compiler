package one.terenin;

public class Main {
    public static void main(String[] args) {
        Compiler compiler = new Compiler("{ x = 1; c = 1; while(x<10) { c = c * x; x = x + 1; print(c)}}".toCharArray(),63);
        compiler.compile(new StringBuilder());
        System.out.println("Hello world!");
    }
}