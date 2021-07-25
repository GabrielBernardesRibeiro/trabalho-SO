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
                throw new NoSuchFileException("Arquivo" + programaDoArquivo + " não encontrado.");
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
            tabelaPaginaAtual.setByteFinalSegmentoDadosEstatico();

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

            System.out.println("Faltando do segmento de dados : " + tbP.faltando());
            
            
            double restoParaAlocar = (size < tbP.faltando()) ? 0 : size - tbP.faltando();

            System.out.println("Size : " + size);

            System.out.println("RestoParaAlocar : " + restoParaAlocar);


            int indiceParaAlocarHeap = this.worstFit(tbP.getQuantidadeDeQuadros((int)restoParaAlocar));

            if (indiceParaAlocarHeap == -1)
                return 5000;

            System.out.println("Indice para alocar : " + indiceParaAlocarHeap + ".");


            int proximoByteFinal = ( indiceParaAlocarHeap * 32 ) + (int)restoParaAlocar - 1;


            System.out.println("Próximo byte final : " + proximoByteFinal);
            
            //int essaPorra = (((indiceParaAlocarHeap * 32) - (piu + tbP.getByteFinalSegmentoDados())) + tbP.getByteFinalSegmentoDados() + size);

            
            //System.out.println("Porra : " + essaPorra);
            //exit(0);

            
            for (int i = indiceParaAlocarHeap; i < indiceParaAlocarHeap + tbP.getQuantidadeDeQuadros((int)restoParaAlocar); i++) {
                System.out.println( "I do allocate : " + i );  
                tbP.alocarHeap(i);
                this.mapaDeBits[i] = 1;
            } // Falta setar o final do segmento de dados novo
            tbP.setByteFinalHeap(proximoByteFinal);


            System.out.println("Depois de alocar na memória. Byte final tbP : " + tbP.getByteFinalSegmentoDados());

            //System.out.println("tbP : " + tbP.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return size;
    }

    @Override
    public int freeMemoryFromProcess(int processId, int size) {

        try {
            if(!this.listaTabelaDePaginas.containsKey(processId))
                throw new InvalidProcessException("O processo de Id = " + processId + " é inválido.");

            TabelaDePaginas tbP = this.listaTabelaDePaginas.get(processId);

            if ( ( size - tbP.getHeapTotal() ) > 0) // Faltou essa parte que é importante
                throw new NoSuchMemoryException("Tamanho passado maior que o heap."); // mudar depois

            /*
            int topo = tbP.stackByteFinal.pop();

            int nProTopo = tbP.getQuantidadeDeQuadros(topo);

            //int restinho = (topo % 32 != 0) ? topo - nProTopo * 32 : 0; // else: 0 só por enquanto

            int antesDele = tbP.stackByteFinal.peek();
            */

            int novoByteFinal = tbP.byteFinalHeap - size;

            System.out.println("-----------Remover------------");

            System.out.println("tbP.byteFinalHeap : " + tbP.byteFinalHeap);

            System.out.println("Novo byte final : " + novoByteFinal);


            ArrayList<Integer> aadd = tbP.removerHeap(size);

            aadd.forEach(value -> this.mapaDeBits[value] = 0);
            
            /*
            int resultado = 0;

            resultado = topo - size - (1); // Menos 1 pq conta o próprio
            tbP.stackByteFinal.push(resultado);

            // tirar o size de memória

            int aaoo = tbP.getQuantidadeDeQuadros(resultado);

            

            while (tbP.stackByteFinal.length == 1) {
                int i = tbP.removerHeap();
                this.mapaDeBits[i] = 0;
            }
            
            for (int i = nProTopo; i > ( nProTopo - aaoo )  ; i--) {
                this.mapaDeBits[i] = 0;
                tbP.removerHeap(i);
            }

            tbP.setByteFinalHeap();

            */
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return size;
    }

    @Override
    public void excludeProcessFromMemory(int processId) {
        try {
            if(!this.listaTabelaDePaginas.containsKey(processId))
                throw new InvalidProcessException("O processo de Id = " + processId + " é inválido.");

            TabelaDePaginas tbP = this.listaTabelaDePaginas.get(processId);

            // Lembrar de fazer de forma exclusiva (quando dois processos iguais são carregados, a parte de texto é compartilhada)

            ArrayList<Integer> indices = tbP.excluirProcessoDaMemoria();

            indices.forEach(value -> this.mapaDeBits[value] = 0);

            this.listaTabelaDePaginas.remove(processId);
            this.listaDeProcessos.remove(processId); 

            System.out.println("lista tabela de páginas : " + this.listaTabelaDePaginas.toString());

            System.out.println("lista de processos : " + this.listaDeProcessos.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void resetMemory() {
        for (int i = 0; i < this.totalQuadrosParaGerenciar; i++)
            this.mapaDeBits[i] = 0;

        this.listaTabelaDePaginas.clear();
        this.listaDeProcessos.clear(); 

        this.setContadorId( this.getIdInicial() );
    }

    @Override
    public int getPhysicalAddress(int processId, int logicalAddress) {
        int enderecoFisico = 0;

        try {

            if(!this.listaTabelaDePaginas.containsKey(processId))
                throw new InvalidProcessException("O processo de Id = " + processId + " é inválido.");

            TabelaDePaginas tabelaDoProcesso = this.listaTabelaDePaginas.get(processId);

            if (logicalAddress > 1023)
                throw new InvalidAddressException("Endereço lógico inválido. Endereço maior que : " + logicalAddress + ".");

            if (logicalAddress < 0)
                throw new InvalidAddressException("Endereço lógico inválido. Endereço menor que : " + logicalAddress + ".");

            String binario = Integer.toBinaryString(logicalAddress);
            int binarioLenght = binario.length();

            String zeros = "";
            
            for (int i = 0; i < (10 - binarioLenght); i++)
                zeros += "0";

            String binario10Casas = zeros + binario;
            
            System.out.println("Binario : " + binario10Casas);

            String primeiros5Bits = binario10Casas.substring(0,5);
            String ultimos5Bits = binario10Casas.substring(5,10);

            System.out.println("Primeiros 5 : " + primeiros5Bits);
            System.out.println("Últimos 5 : " + ultimos5Bits);

            int indicePagina = Integer.parseInt(primeiros5Bits, 2);

            if (tabelaDoProcesso.isValid[indicePagina] == 0)
                throw new InvalidAddressException("Endereço lógico inválido dentro do processo. Endereço igual a : " + logicalAddress + ".");  
            
            int endereco = tabelaDoProcesso.paginas[indicePagina];

            enderecoFisico = endereco + Integer.parseInt(ultimos5Bits, 2);
            
            System.out.println("Primeiros 5 Decimal : " + indicePagina);
         
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return enderecoFisico;
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


    private int worstFit(int tamanhoDeQuadrosProcesso) {
        int i = 0;
        int j;
        int nroQuadros = 0;
        int indice;
        int indiceDoMax = 0;
        int nroMaxQuadros = 0;

        if (tamanhoDeQuadrosProcesso == 0)
            return -1;

        //System.out.println("Tamanho do processo : " + tamanhoDeQuadrosProcesso);

        System.out.println("Quadros para o processo : " + tamanhoDeQuadrosProcesso);

        while(i < this.totalQuadrosParaGerenciar) {
            if (this.mapaDeBits[i] == 0) {
                nroQuadros = 0;
                indice = i;
                j = i;
                while (j < this.mapaDeBits.length && this.mapaDeBits[j] != 1 ) {
                    nroQuadros++;
                    j++;
                }
                if (nroQuadros > tamanhoDeQuadrosProcesso) {
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