package projeto;

import exceptions.*;

public class MemoryManager implements ManagementInterface {
    public int qtdQuadrosASeremGerenciados; // 32/64/128
    public int[] mapaDeBits; // Pode ter tamanho = 32/64/128

    //public TabelaDePágina[] listaTabelasDePáginas;

    /*
    Cada programa virtual:
    - Texto : (segmento de texto com tamanho int > 1 e <= 960 bytes)
    - Dados : (segmento de dados com tamanho int >= 0 e <= 928 bytes)
    - Pilha : (segmento de pliha com tamanho == 64 bytes)
    -- Tamanho máximo de 1024 bytes
    */


    public MemoryManager(int qtdQuadrosASeremGerenciados) {
        this.qtdQuadrosASeremGerenciados = qtdQuadrosASeremGerenciados;
        this.mapaDeBits = new int[qtdQuadrosASeremGerenciados];
        for (int i = 0; i < this.mapaDeBits.length; i++) // Talvez não seja tudo inicializado como 0
            this.mapaDeBits[i] = 0;
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