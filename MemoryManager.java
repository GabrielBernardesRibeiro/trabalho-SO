package projeto;


import exceptions.*;


import java.io.File;
import java.util.*;

public class MemoryManager implements ManagementInterface {
    public int qtdQuadrosASeremGerenciados; // 32/64/128
    public int[] mapaDeBits; // Pode ter tamanho = 32/64/128
    //public HashMap<Integer,TabelaDePaginas> listaTabelasDePaginas = new HashMap<Integer,TabelaDePaginas>(); // Mudar os tipos
    
    public HashMap<Integer,HashMap> listaTabelasDePaginas = new HashMap<Integer,HashMap>(); // Mudar os tipos
            /*
            { 
                0 : {
                    nome: "p1",
                    tabela: tablePage,
                },
                1 : {
                    nome: "p2",
                    tabela: tablePage2,
                },
            }
            */
    //public TabelaDePagina[] listaTabelasDePaginas = new TabelaDePaginas[]; //cada processo tem uma tabela de página;

    public int n; // contagem do id do processo

    private final int STACKSEGMENTSIZE = 64; // Tamanho do segmento da pilha


    // cada quadro tem 32 bytes 

    // página do processo
    // tabela de páginas dos processos


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
        for (int i = 0; i < this.mapaDeBits.length; i++) 
            this.mapaDeBits[i] = 0;
        this.n = 0;
    }

    @Override
    public int loadProcessToMemory(String processName) {
        int processId = 0; // Id do processo que será criado
        int textSegmentSize = 0; // Tamanho do segmento de texto
        int dataSegmentSize = 0; // Tamanho do segmento de dados
        String fileName; // NomeDoArquivo


        HashMap<String, TabelaDePaginas> informacoesProjeto = new HashMap<String, TabelaDePaginas>();
        HashMap<String, String> nomeProjeto = new HashMap<String, String>();
        // precisar checar se programas são idênticos

        try {   

            // ------------ Sessão de carregar e validar o arquivo -----
            File programFile = new File(processName + ".txt");

            //System.out.println("File exists: " + programFile.exists());

            if (!programFile.exists()) // Se arquivo não for encontrado
                throw new NoSuchFileException("Arquivo inválido ou não encontrado.");

            // ---------------------------------//----------------------

            // ----------- Sessão de leitura do arquivo -----------
            Scanner scanner = new Scanner(programFile);
            int index = 1;
            while(true)
            {   
                Integer currentValue;
                String currentTitle;
                if (index == 1)
                {
                    currentTitle = scanner.next();
                    if (new String("program").equals(currentTitle))
                    {
                        fileName = scanner.next();
                    } else
                    {
                        throw new CorruptedFileException("Arquivo "+processName+" corrompido");
                    }
                } else if (index == 3)
                {
                    currentTitle = scanner.next();
                    if (new String("text").equals(currentTitle))
                    {
                        try {
                            currentValue = scanner.nextInt();
                            textSegmentSize = currentValue;
                        } catch (InputMismatchException e) {
                            throw new CorruptedFileException("Arquivo "+processName+" corrompido");
                        }
                    } else
                    {
                        throw new CorruptedFileException("Arquivo "+processName+" corrompido");
                    }
                    
                } else if (index == 5)
                {
                    currentTitle = scanner.next();
                    if (new String("data").equals(currentTitle))
                    {
                        try {
                            currentValue = scanner.nextInt();
                            dataSegmentSize = currentValue;
                        } catch (InputMismatchException e) {
                            throw new CorruptedFileException("Arquivo "+processName+" corrompido");
                        }
                    } else
                    {
                        throw new CorruptedFileException("Arquivo "+processName+" corrompido");
                    }
                }
                if (index == 6)
                {
                    break;
                }
                index++;
            }
            scanner.close(); // Fechar o arquivo

            /*
            scanner.next();
            System.out.println("Projeto: "+scanner.next());

            scanner.next();
            textSegmentSize = scanner.nextInt();
            System.out.println("textSegmentSize : " + textSegmentSize);

            scanner.next();
            dataSegmentSize = scanner.nextInt();
            System.out.println("dataSegmentSize : " + dataSegmentSize);
            
            scanner.close(); // Fechar o arquivo
            */
            System.out.println("\n");

            // -------------------//---------------------------------

            // ------------ Sessão de carregar o processo na memória -----------
            processId = this.n;
            // 1. criação de uma tabela de página para representar o processo:

            TabelaDePaginas tablePage = new TabelaDePaginas();

            informacoesProjeto.put("Tabela", tablePage);
            this.listaTabelasDePaginas.put(processId,informacoesProjeto);
            
            this.n += 1;

            this.listaTabelasDePaginas.forEach(
                (k,v)->System.
                out.println("Id : " + k + "\nTabela ligada ao processo : " + v )
            );

            //System.out.println("Lista : " + this.listaTabelasDePaginas.toString());

            // 2. alocar quadros para armazenar texto e dados
                // alocar para o texto
                // alocar para os dados
                // pilha sempre aloca 2 quadros

            if (textSegmentSize % 32 == 0)
                System.out.println("\nNro de quadros pra alocar o texto : " + (textSegmentSize / 32));
            else {
                System.out.println("\nNro de quadros pra alocar o texto : " + (textSegmentSize / 32 + 1));
            }
                
            if (dataSegmentSize % 32 == 0)
                System.out.println("Nro de quadros pra alocar os dados : " + (dataSegmentSize / 32));
            else {
                System.out.println("Nro de quadros pra alocar os dados : " + (dataSegmentSize / 32 + 1));
            }

            // ---------------------------------//------------------------------
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }
            System.out.println("\n|-------------------------|\n");
        return processId;
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
        String x = "[";
        for (int j = 0; j < this.mapaDeBits.length; j++) {
            if (j == this.mapaDeBits.length - 1) {
                x += this.mapaDeBits[j] + "]";
                break;
            }
            x += this.mapaDeBits[j] +  ", "; 
        }
        return x;
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