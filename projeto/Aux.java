package projeto;


public class Aux {
    public static void main(String[] args) {
        MemoryManager mm = new MemoryManager(32);
        int x = mm.loadProcessToMemory("a");
        System.out.println("X : " + x);
    }
}