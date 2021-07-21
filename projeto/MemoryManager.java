package projeto;
import exceptions.*;
import java.io.File;
import java.util.*;

public class MemoryManager implements ManagementInterface {
    public int totalQuadrosParaGerenciar; // 32/64/128
    public int[] mapaDeBits; // Pode ter tamanho = 32/64/128
    public HashMap<Integer,TabelaDePaginas> listaTabelaDePaginas = new HashMap<Integer,TabelaDePaginas>();
    public HashMap<Integer,String> listaDeProcessos = new HashMap<Integer,String>();
    public int contadorDeId; // contagem do id do processo
    private int idInicial = 0;

    private final int STACKSEGMENTSIZE = 64; // Tamanho do segmento da pilha
    private final int quantidadeQuadrosPilha = 2;

    /*
        primeira vez que ele roda p1:
        [t,t,d1,d1,d1,d1,pi1,pi1,0,0,,0,0,0,0,0,.......]

        segunda vez que ele roda p2:
        [t,t,d1,d1,d1,d1,pi1,pi1,asda,dasdasd,0,0,0d2,d2,d2,d2,pi2,pi2,0,00,00,0.........]
    */

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

    public MemoryManager(int totalQuadrosParaGerenciar) {
        this.setTotalQuadrosParaGerenciar(totalQuadrosParaGerenciar);
        this.inicializarMapaDeBits(totalQuadrosParaGerenciar);
        this.setContadorId( this.getIdInicial() );
    }

    @Override
    public int loadProcessToMemory(String processName) {
        int idDoProcessoAtual = 0; // Id do processo que será criado
        int tamanhoSegmentoTexto = 0; // Tamanho do segmento de texto
        int tamanhoSegmentoDados = 0; // Tamanho do segmento de dados
        String nomeDoArquivo = ""; // NomeDoArquivo

        try {   

            // ------------ Sessão de carregar e validar o arquivo -----
            File programaDoArquivo = new File(processName + ".txt");

            if (programaDoArquivo.exists() == false) // Se arquivo não for encontrado
            {
                throw new NoSuchFileException("Arquivo inválido ou não encontrado.");
            }

            // ---------------------------------//----------------------

            // ----------- Sessão de leitura do arquivo -----------
            Scanner scannerArquivo = new Scanner(programaDoArquivo);
            int contadorDeLeitura = 1;
            while(true)
            {   
                int valorLeituraAtual;
                String tituloAtual;
                if (contadorDeLeitura == 1)
                {
                    tituloAtual = scannerArquivo.next();
                    if (new String("program").equals(tituloAtual))
                    {
                        nomeDoArquivo = scannerArquivo.next();
                    } else
                    {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                } else if (contadorDeLeitura == 3)
                {
                    tituloAtual = scannerArquivo.next();
                    if (new String("text").equals(tituloAtual))
                    {
                        try {
                            valorLeituraAtual = scannerArquivo.nextInt();
                            tamanhoSegmentoTexto = valorLeituraAtual;
                        } catch (InputMismatchException e) {
                            throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                        }
                    } else
                    {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                    
                } else if (contadorDeLeitura == 5)
                {
                    tituloAtual = scannerArquivo.next();
                    if (new String("data").equals(tituloAtual))
                    {
                        try {
                            valorLeituraAtual = scannerArquivo.nextInt();
                            tamanhoSegmentoDados = valorLeituraAtual;
                        } catch (InputMismatchException e) {
                            throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                        }
                    } else
                    {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                }
                if (contadorDeLeitura == 6)
                {
                    break;
                }
                contadorDeLeitura++;
            }
            scannerArquivo.close();

            System.out.println("\n");
            // ------------ Sessão de carregar o processo na memória -----------
            idDoProcessoAtual = this.getContadorId(); //this.contadorDeId

            // 1. criação de uma tabela de página para representar o processo:
            TabelaDePaginas tabelaPaginaAtual = new TabelaDePaginas(tamanhoSegmentoTexto, tamanhoSegmentoDados);

            this.listaTabelaDePaginas.put(idDoProcessoAtual, tabelaPaginaAtual);
            this.listaDeProcessos.put(idDoProcessoAtual, nomeDoArquivo);
            this.icrementarContadorId();

            // 2.  alocar quadros para armazenar texto e dados
                // alocar para o texto
                // alocar para os dados
                // pilha sempre aloca 2 quadros

            // tamanho do processo: seg de texto + seg de dados + 64
            int quantidadeQuadrosTexto = tabelaPaginaAtual.getQuantidadeQuadrosTexto();
            int quantidadeQuadrosDados = tabelaPaginaAtual.getQuantidadeQuadrosDados();
            int tamanhoProcesso = quantidadeQuadrosTexto + quantidadeQuadrosDados + this.quantidadeQuadrosPilha;
            int retornoWorstFit = this.worstFit(tamanhoProcesso);

            // ----------------- Parte de alocação da tabela de página
            int j = 1;
            for (int i = retornoWorstFit; i < retornoWorstFit + tamanhoProcesso; i++) 
            {
                System.out.println("i : " + i);
                this.mapaDeBits[i] = 1;
                
                if (j <= quantidadeQuadrosTexto ) 
                {
                    tabelaPaginaAtual.alocarSegmentoTexto(i);
                    j++;
                    continue;
                }
                
                if (j > quantidadeQuadrosTexto + quantidadeQuadrosDados ) 
                {
                    tabelaPaginaAtual.alocarSegmentoStack(i);
                    j++;
                    continue;
                }
                
                if (j > quantidadeQuadrosTexto )
                {
                    tabelaPaginaAtual.alocarSegmentoData(i);
                    j++;
                    continue;
                }
                
            }
            tabelaPaginaAtual.setByteFinalSegmentoDados(0, true);

            System.out.println( tabelaPaginaAtual.toString() );
        
            
            // ---------------------------------//------------------------------
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("\n|-------------------------|\n");
        return idDoProcessoAtual;
    }

    @Override
    public int allocateMemoryToProcess(int idDoProcessoAtual, int size) {
        try {

            if(!this.listaTabelaDePaginas.containsKey(idDoProcessoAtual))
                throw new InvalidProcessException("O processo de Id = " + idDoProcessoAtual + " é inválido.");
                
            TabelaDePaginas tbP = this.listaTabelaDePaginas.get(idDoProcessoAtual);

            int restoParaAlocar = (size < tbP.faltandoDosegmentoDeDadosEstatico()) ? 0 : size - tbP.faltandoDosegmentoDeDadosEstatico();

            System.out.println("100 : " + size);

            int piu = size - restoParaAlocar + 2;

            System.out.println("14 : " + piu);

            System.out.println("Resto pra alocar : " + restoParaAlocar);
            System.out.println("Byte final : " + tbP.getByteFinalSegmentoDados());

            int indiceParaAlocarHeap = this.worstFit(tbP.getQuantidadeDeQuadros(restoParaAlocar));

            System.out.println("");

            //piu = (indiceParaAlocarHeap * 32) - piu;

            int essaPorra = (((indiceParaAlocarHeap * 32) - (piu + tbP.getByteFinalSegmentoDados())) + tbP.getByteFinalSegmentoDados() + size);

            System.out.println("Porra : " + essaPorra);
            //exit(0);

            for (int i = indiceParaAlocarHeap; i < indiceParaAlocarHeap + tbP.getQuantidadeDeQuadros(restoParaAlocar); i++) {
                System.out.println( "I do allocate : " + i );  
                tbP.alocarHeap(i);
                this.mapaDeBits[i] = 1;
            } // Falta setar o final do segmento de dados novo
            tbP.setByteFinalSegmentoDados(essaPorra, false);

            System.out.println("byte final tbP : " + tbP.getByteFinalSegmentoDados());

            System.out.println("Antes alocar heap");
            //String penis = tbP.alocarHeap(indiceParaAlocarHeap);
            //System.out.println( "Penis : " + penis );
            System.out.println( tbP.toString() );


           System.out.println("Stack : " + tbP.stackByteFinal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 5;
    }

    @Override
    public int freeMemoryFromProcess(int idDoProcessoAtual, int size) {
        return 5;
    }

    @Override
    public void excludeProcessFromMemory(int idDoProcessoAtual) {
        
    }

    @Override
    public void resetMemory() {
        for (int i = 0; i < this.totalQuadrosParaGerenciar; i++)
            this.mapaDeBits[i] = 0;

        this.listaTabelaDePaginas.clear();
        this.listaDeProcessos.clear(); 

        // zerar var que controla ids dos processos da classe manager?
    }

    @Override
    public int getPhysicalAddress(int idDoProcessoAtual, int logicalAddress) {
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
    public String getPageTable(int idDoProcessoAtual) {
        try {

            if(!this.listaTabelaDePaginas.containsKey(idDoProcessoAtual))
                throw new InvalidProcessException("O processo de Id = " + idDoProcessoAtual + " é inválido.");

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        TabelaDePaginas tabelaPaginaAtual = this.listaTabelaDePaginas.get(idDoProcessoAtual);
            
        return "A tabela de páginas vínculada ao processo com id : " + idDoProcessoAtual + " é a página : " + tabelaPaginaAtual.toString();
    }

    @Override
    public String[] getProcessList() {
        String[] ab = new String[]{"a","b"};
        return ab;
    }


    private int worstFit(int tamanhoProcesso) {
        int i = 0;
        int j;
        int nroQuadros = 0;
        int indice;
        int indiceDoMax = -1;
        int nroMaxQuadros = 0;

        System.out.println("Tamanho do processo : " + tamanhoProcesso);

        System.out.println("Quadros para o processo : " + tamanhoProcesso);

        while(i < this.totalQuadrosParaGerenciar) {
            if (this.mapaDeBits[i] == 0) {
                nroQuadros = 0;
                indice = i;
                j = i;
                while (j < this.mapaDeBits.length && this.mapaDeBits[j] != 1 ) {
                    nroQuadros++;
                    j++;
                }
                if (nroQuadros >= tamanhoProcesso) {
                    if (nroQuadros > nroMaxQuadros) {
                        nroMaxQuadros = nroQuadros;
                        indiceDoMax = indice;
                    }
                }
                i = j;
            }
            i++;
        }
        return indiceDoMax;
    }

    public int getContadorId()
    {
        return ( this.contadorDeId );
    }

    public void setContadorId(int contadorNovoValor)
    {
        this.contadorDeId = contadorNovoValor;
    }

    public void icrementarContadorId()
    {
        this.setContadorId( this.getContadorId() + 1 );
    }

    public int getTotalQuadrosParaGerenciar()
    {
        return ( this.totalQuadrosParaGerenciar );
    }

    public void setTotalQuadrosParaGerenciar(int totalQuadrosParaGerenciar)
    {
        this.totalQuadrosParaGerenciar = totalQuadrosParaGerenciar;
    }

    public void inicializarMapaDeBits(int tamanhoMapaDeBits)
    {
        this.mapaDeBits = new int[tamanhoMapaDeBits];
        for (int i = 0; i < this.mapaDeBits.length; i++) 
        {
            this.mapaDeBits[i] = 0;
        }
    }

    public int getIdInicial()
    {
        return this.idInicial;
    }

    public void setNovoProcesso(int idNovoProcesso, String nomeNovoProcesso)
    {
        this.listaDeProcessos.put(idNovoProcesso, nomeNovoProcesso);
    }

    public HashMap getListaTabelaDePaginas()
    {
        return this.listaTabelaDePaginas;
    }
    
}