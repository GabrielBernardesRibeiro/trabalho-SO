package projeto;


public class Aux {
    public static void main(String[] args) {
        MemoryManager mm = new MemoryManager(32);
        int id = mm.loadProcessToMemory("p1");
        System.out.println("Id : " + id);
    }
}