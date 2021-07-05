package projeto;

import exceptions.*;

public class MemoryManager implements ManagementInterface {
    public int qtdQuadrosASeremGerenciados; // Múltiplo de 32
    //public HashMap<bit,unidadeDeAlocacao> mapaDeBits; // ?

    //public TabelaDePágina[] listaTabelasDePáginas;


    public MemoryManager(int qtdQuadrosASeremGerenciados) {
        this.qtdQuadrosASeremGerenciados = qtdQuadrosASeremGerenciados;
    }

    @Override
    public int loadProcessToMemory(String processName) {
        try {
            if (processName == "a")
                throw new NoSuchFileException("Nosuchfileexception");
        } catch (Exception ex) {
            System.out.println("Erro : " + ex);
        }
        return 5;
    }

    @Override
    public int allocateMemoryToProcess(int processId, int size) {
        return 5;
    }

    @Override
    public int freeMemoryFromProcess(int processId, int size) {
        return 5;
    }

    @Override
    public void excludeProcessFromMemory(int processId) {
        
    }

    @Override
    public void resetMemory() {
        
    }

    @Override
    public int getPhysicalAddress(int processId, int logicalAddress) {
        return 5;
    }

    @Override
    public String getBitMap() {
        return "";
    }

    @Override
    public String getPageTable(int processId) {
        return "";
    }

    @Override
    public String[] getProcessList() {
        String[] ab = new String[]{"a","b"};
        return ab;
    }

}