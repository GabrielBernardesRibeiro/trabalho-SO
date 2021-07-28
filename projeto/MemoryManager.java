package projeto;
import exceptions.*;
import java.io.File;
import java.util.*;

public class MemoryManager implements ManagementInterface {
    public int totalQuadrosParaGerenciar; // 32/64/128
    public int[] mapaDeBits; // Pode ter tamanho = 32/64/128
    private HashMap<Integer,TabelaDePaginas> listaTabelaDePaginas = new HashMap<Integer,TabelaDePaginas>();
    private HashMap<Integer,String> listaDeProcessos = new HashMap<Integer,String>();

    private int contadorDeId; // contagem do id do processo
    
    private int idInicial = 0;

    private final int quantidadeQuadrosPilha = 2;


    public MemoryManager(int totalQuadrosParaGerenciar) {
        this.setTotalQuadrosParaGerenciar(totalQuadrosParaGerenciar);
        this.inicializarMapaDeBits(totalQuadrosParaGerenciar);
        this.setContadorId( this.idInicial );
    }

    @Override
    public int loadProcessToMemory(String processName) {
        int idDoProcessoAtual = 0; // Id do processo que será criado
        int tamanhoSegmentoTexto = 0; // Tamanho do segmento de texto
        int tamanhoSegmentoDados = 0; // Tamanho do segmento de dados
        String nomeDoArquivo = ""; // NomeDoArquivo
        boolean processoIgual = false; // Caso o processo a ser carregado for igual a um processo já carregado

        try {   

            // ------------ Sessão de carregar e validar o arquivo -----
            File programaDoArquivo = new File(processName);

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

            ArrayList<Integer> posicoesNaMemoriaDoTexto = new ArrayList<Integer>(); // Novo array de memorias compartilhadas
            
            for (HashMap.Entry<Integer,TabelaDePaginas> entrada : this.listaTabelaDePaginas.entrySet()) {
                System.out.println("\n\n Entrada : " + entrada + " \n\n");
                int id = entrada.getKey();
                TabelaDePaginas valor = (TabelaDePaginas)entrada.getValue();
                int entradaTamSegTexto = entrada.getValue().getTamanhoSegmentoTexto();
                int entradaTamSegDados = entrada.getValue().getTamanhoSegmentoDados();
                
                // Se processos iguais
                if (entradaTamSegTexto == tamanhoSegmentoTexto && entradaTamSegDados == tamanhoSegmentoDados && this.listaDeProcessos.get(id).equals(nomeDoArquivo) ) {
                    System.out.println("\n\n Entrei no If, programa é igual " + "\n\n");
                    
                    // Segmento de texto compartilhado entre eles
                    processoIgual = true;
                    int quadrosTexto = entrada.getValue().getQuantidadeQuadrosTexto();

                    System.out.println("\n quadrosTexto  : " + quadrosTexto );
                    for (int p = 0; p < quadrosTexto; p++) // Guarda os bytes base da memória do segmento de texto do programa que já foi carregado
                        posicoesNaMemoriaDoTexto.add(entrada.getValue().paginas[p]);
                }
                
                
            }
            

            // 1. criação de uma tabela de página para representar o processo:
            TabelaDePaginas tabelaPaginaAtual = new TabelaDePaginas(tamanhoSegmentoTexto, tamanhoSegmentoDados);

            this.listaTabelaDePaginas.put(idDoProcessoAtual, tabelaPaginaAtual);
            this.listaDeProcessos.put(idDoProcessoAtual, nomeDoArquivo);
            this.icrementarContadorId();

            // 2.  alocar quadros para armazenar texto e dados

            // tamanho do processo: seg de texto + seg de dados + 64
            int quantidadeQuadrosTexto = tabelaPaginaAtual.getQuantidadeQuadrosTexto();
            int quantidadeQuadrosDados = tabelaPaginaAtual.getQuantidadeQuadrosDados();
            int tamanhoProcesso;

            if (processoIgual) { // Se processo for igual só precisa alocar dados e pilha
                tamanhoProcesso = quantidadeQuadrosDados + this.quantidadeQuadrosPilha;
            } else {
                tamanhoProcesso = quantidadeQuadrosTexto + quantidadeQuadrosDados + this.quantidadeQuadrosPilha;
            }

            int retornoWorstFit = this.worstFit(tamanhoProcesso);

            // ----------------- Parte de alocação da tabela de página
            int j = 0;
            for (int i = retornoWorstFit; i < retornoWorstFit + tamanhoProcesso; i++) 
            {
                System.out.println("i : " + i);
                this.mapaDeBits[i] = 1;

                if (j < quantidadeQuadrosTexto ) 
                {
                    if (processoIgual) {
                        System.out.println( j + "Tabela atual : " + tabelaPaginaAtual.toString() );
                        tabelaPaginaAtual.alocarSegmentoTextoCompartilhado(j, posicoesNaMemoriaDoTexto.get(j));    
                        j++;
                    } else {
                        tabelaPaginaAtual.alocarSegmentoTexto(i);
                        j++;
                    }
                    continue;
                }
                
                if (j >= quantidadeQuadrosTexto + quantidadeQuadrosDados ) 
                {
                    tabelaPaginaAtual.alocarSegmentoStack(i);
                    j++;
                    continue;
                }
                
                if (j >= quantidadeQuadrosTexto )
                {
                    tabelaPaginaAtual.alocarSegmentoData(i);
                    j++;
                    continue;
                }
                
            }
            tabelaPaginaAtual.setByteFinalSegmentoDadosEstatico();

            System.out.println("\n\n\ndado Estatico : " + tabelaPaginaAtual.getByteFinalSegmentoDados());

            System.out.println( tabelaPaginaAtual.toString() );
        
            System.out.println("Mapa de bits : " + this.getBitMap());
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
            
            double restoParaAlocar = (size < tbP.faltando()) ? 0 : size - tbP.faltando();

            int indiceParaAlocarHeap = this.worstFit(tbP.getQuantidadeDeQuadros((int)restoParaAlocar));

            if (indiceParaAlocarHeap == -1)
                return 5000;


            int proximoByteFinal = ( indiceParaAlocarHeap * 32 ) + (int)restoParaAlocar - 1;

            
            for (int i = indiceParaAlocarHeap; i < indiceParaAlocarHeap + tbP.getQuantidadeDeQuadros((int)restoParaAlocar); i++) { 
                tbP.alocarHeap(i);
                this.mapaDeBits[i] = 1;
            }
            tbP.setByteFinalHeap(proximoByteFinal, true, size);


            System.out.println("Depois de alocar na memória. Byte final tbP : " + tbP.getByteFinalSegmentoDados());


        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
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

            int novoByteFinal = tbP.byteFinalHeap - size;

            ArrayList<Integer> indicesNaMemoria = tbP.removerHeap(size);

            indicesNaMemoria.forEach(indice -> this.mapaDeBits[indice] = 0);
            
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
            
            boolean processoIgual = false;

            // Lembrar de fazer de forma exclusiva (quando dois processos iguais são carregados, a parte de texto é compartilhada)

            ArrayList<Integer> indicesDeTextoCompartilhado = new ArrayList<Integer>(); // Novo array de memorias compartilhadas
            for (HashMap.Entry<Integer,TabelaDePaginas> entrada : this.listaTabelaDePaginas.entrySet()) {
                int id = entrada.getKey();
                int entradaTamSegTexto = entrada.getValue().getTamanhoSegmentoTexto();
                int entradaTamSegDados = entrada.getValue().getTamanhoSegmentoDados();
                // Se processos iguais
                if (entradaTamSegTexto == tbP.getTamanhoSegmentoTexto() && entradaTamSegDados == tbP.getTamanhoSegmentoDados() && this.listaDeProcessos.get(id).equals(this.listaDeProcessos.get(processId)) ) {
                    // Segmento de texto compartilhado entre eles
                    processoIgual = true;
                    int quadrosTexto = entrada.getValue().getQuantidadeQuadrosTexto();

                    for (int p = 0; p < quadrosTexto; p++) // Guarda os bytes base da memória do segmento de texto do programa que já foi carregado
                            indicesDeTextoCompartilhado.add(entrada.getValue().paginas[p]/32);
                }
            }

            ArrayList<Integer> indices = tbP.excluirProcessoDaMemoria();

            boolean compartilhado = false;
            for (int indice : indices) {
                for (int elemento : indicesDeTextoCompartilhado) {
                    compartilhado = false;
                    if (elemento == indice) {
                        compartilhado = true;
                        break;
                    }
                }

                if (!compartilhado)
                    this.mapaDeBits[indice] = 0;
            }

            this.listaTabelaDePaginas.remove(processId);
            this.listaDeProcessos.remove(processId); 

            System.out.println("lista tabela de páginas : " + this.listaTabelaDePaginas.toString());

            System.out.println("lista de processos : " + this.listaDeProcessos.toString());

            System.out.println("bit map : " + this.getBitMap());
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

        this.setContadorId( this.idInicial );
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
        String bitMap = "[";
        for (int j = 0; j < this.mapaDeBits.length; j++) {
            if (j == this.mapaDeBits.length - 1) {
                bitMap += this.mapaDeBits[j] + "]";
                break;
            }
            bitMap += this.mapaDeBits[j] +  ", "; 
        }
        return bitMap;
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
        Object[] processos = this.listaDeProcessos.entrySet().toArray();//.forEach( v -> "vv" );
        String[] a = new String[this.listaDeProcessos.size()];

        for (int i = 0; i < this.listaDeProcessos.size(); i++) {
            a[i] = processos[i].toString();
        }
        
        return a;
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
    
}