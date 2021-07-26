package projeto;


public class Aux {
    public static void main(String[] args) {
        try {
            MemoryManager mm = new MemoryManager(32);
            int id = mm.loadProcessToMemory("p1");


            System.out.println(mm.getBitMap());


            //System.out.println("Id do processo: " + id);
            //int id2 = mm.loadProcessToMemory("p1");
            //int id2 = mm.loadProcessToMemory("p1");
            mm.allocateMemoryToProcess(0,100);

            //mm.allocateMemoryToProcess(0,11);

            //System.out.println("Id2 do processo: " + id2);
            System.out.println(mm.getBitMap());

            mm.allocateMemoryToProcess(0,32);

            System.out.println(mm.getBitMap());

            mm.freeMemoryFromProcess(0,120);

            //mm.excludeProcessFromMemory(0);

            //mm.resetMemory();

            System.out.println(mm.getBitMap());


            System.out.println("Process List : ");
            for (String i : mm.getProcessList())
                System.out.println("Processo : "+ i);

            // int y = mm.getPhysicalAddress(0,1023);
            // System.out.println("Y : " + y);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}