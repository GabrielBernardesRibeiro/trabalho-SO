package projeto;


public class Aux {
    public static void main(String[] args) {
        try {
            MemoryManager mm = new MemoryManager(32);
            int id = mm.loadProcessToMemory("p1");
            //System.out.println("Id do processo: " + id);
            //int id2 = mm.loadProcessToMemory("p1");
            //int id2 = mm.loadProcessToMemory("p1");
            mm.allocateMemoryToProcess(0,100);
            //System.out.println("Id2 do processo: " + id2);
            System.out.println(mm.getBitMap());

            mm.allocateMemoryToProcess(0,100);

            System.out.println(mm.getBitMap());

            mm.freeMemoryFromProcess(0,100);
            //mm.resetMemory();
            //System.out.println(mm.getBitMap());
            // int y = mm.getPhysicalAddress(0,1023);
            // System.out.println("Y : " + y);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}