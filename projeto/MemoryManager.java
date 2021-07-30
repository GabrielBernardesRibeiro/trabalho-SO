package projeto;
import exceptions.*;
import java.io.File;
import java.util.*;

public class MemoryManager implements ManagementInterface {
    private int[] mapaDeBits;

    private int totalQuadrosParaGerenciar; 

    private HashMap<Integer,TabelaDePaginas> listaTabelaDePaginas = new HashMap<Integer,TabelaDePaginas>();
    private HashMap<Integer,String> listaDeProcessos = new HashMap<Integer,String>();

    private int contadorDeId; // Mantém o id dos processos
    
    private int idInicial = 0;

    private final int quantidadeQuadrosPilha = 2;

    private final int quadrosDaMemoriaInsuficiente = -2;  


    public MemoryManager(int totalQuadrosParaGerenciar) {
        this.setTotalQuadrosParaGerenciar(totalQuadrosParaGerenciar);
        this.inicializarMapaDeBits(totalQuadrosParaGerenciar);
        this.setContadorId( this.idInicial );
    }

    @Override
    public int loadProcessToMemory(String processName) {
        int idDoProcessoAtual = this.contadorDeId; // Id do processo que será criado
        int tamanhoSegmentoTexto = 0; // Tamanho do segmento de texto
        int tamanhoSegmentoDados = 0; // Tamanho do segmento de dados
        String nomeDoArquivo = ""; // NomeDoArquivo
        boolean processoIgual = false; // Caso o processo a ser carregado for igual a um processo já carregado

        try {   

            // ------------ Sessão de carregar e validar o arquivo -----
            File programaDoArquivo = new File(processName);

            if (programaDoArquivo.exists() == false) { // Se arquivo não for encontrado
                throw new NoSuchFileException("Arquivo" + programaDoArquivo + " não encontrado.");
            }

            // ---------------------------------//----------------------

            // ----------- Sessão de leitura do arquivo -----------
            if (processName.contains(".txt") == false) {
                throw new FileFormatException("Arquivo invalido.");
            }

            Scanner scannerArquivo = new Scanner(programaDoArquivo);//p1.txt [.txt]
            int contadorDeLeitura = 1;
            while(true) {   
                int valorLeituraAtual;
                String tituloAtual;
                if (contadorDeLeitura == 1) {
                    tituloAtual = scannerArquivo.next();
                    if (new String("program").equals(tituloAtual)) {
                        nomeDoArquivo = scannerArquivo.next();
                    } else {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                } else if (contadorDeLeitura == 3) {
                    tituloAtual = scannerArquivo.next();
                    if (new String("text").equals(tituloAtual)) {
                        try {
                            valorLeituraAtual = scannerArquivo.nextInt();
                            tamanhoSegmentoTexto = valorLeituraAtual;
                        } catch (InputMismatchException e) {
                            throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                        }
                    } else {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                    
                } else if (contadorDeLeitura == 5) {
                    tituloAtual = scannerArquivo.next();
                    if (new String("data").equals(tituloAtual)) {
                        try {
                            valorLeituraAtual = scannerArquivo.nextInt();
                            tamanhoSegmentoDados = valorLeituraAtual;
                        } catch (InputMismatchException e) {
                            throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                        }
                    } else {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                }
                if (contadorDeLeitura == 6) {
                    break;
                }
                contadorDeLeitura++;
            }
            scannerArquivo.close();

            System.out.println("\n");
            // ---------------------------------------------

            // ------------ Sessão de carregar o processo na memória -----------

            ArrayList<Integer> posicoesNaMemoriaDoTexto = new ArrayList<Integer>(); // Novo array de memorias compartilhadas
            
            for (HashMap.Entry<Integer,TabelaDePaginas> entrada : this.listaTabelaDePaginas.entrySet()) {
                int id = entrada.getKey();
                int entradaTamSegTexto = entrada.getValue().getTamanhoSegmentoTexto();
                int entradaTamSegDados = entrada.getValue().getTamanhoSegmentoDados();
                
                // Se processos iguais
                if (entradaTamSegTexto == tamanhoSegmentoTexto && entradaTamSegDados == tamanhoSegmentoDados && this.listaDeProcessos.get(id).equals(nomeDoArquivo) ) {
                    
                    processoIgual = true;
                    int quadrosTexto = entrada.getValue().getQuantidadeQuadrosTexto();

                    for (int i = 0; i < quadrosTexto; i++) // Guarda os bytes base da memória do segmento de texto do programa que já foi carregado
                        posicoesNaMemoriaDoTexto.add(entrada.getValue().paginas[i]);
                }

            }
            

            // 1. Criação de uma tabela de página para representar o processo:
            TabelaDePaginas tabelaPaginaAtual = new TabelaDePaginas(tamanhoSegmentoTexto, tamanhoSegmentoDados);

            this.listaTabelaDePaginas.put(idDoProcessoAtual, tabelaPaginaAtual);
            this.listaDeProcessos.put(idDoProcessoAtual, nomeDoArquivo);
            //this.icrementarContadorId();

            // 2. Alocar quadros para armazenar texto, dado e pilha
            int quantidadeQuadrosTexto = tabelaPaginaAtual.getQuantidadeQuadrosTexto();
            int quantidadeQuadrosDados = tabelaPaginaAtual.getQuantidadeQuadrosDados();
            int tamanhoProcesso;

            if (processoIgual) { // Se processo for igual só precisa alocar dados e pilha
                tamanhoProcesso = quantidadeQuadrosDados + this.quantidadeQuadrosPilha;
            } else {
                tamanhoProcesso = quantidadeQuadrosTexto + quantidadeQuadrosDados + this.quantidadeQuadrosPilha;
            }

            int retornoWorstFit = this.worstFit(tamanhoProcesso);

            if (retornoWorstFit == this.quadrosDaMemoriaInsuficiente) {
                throw new MemoryOverflowException("Quantidade requisitada superior a disponivel na memoria.");
            }

            // ----------------- Parte de alocação da tabela de página

            if (processoIgual) {
                for (int posicoes : posicoesNaMemoriaDoTexto) {
                    tabelaPaginaAtual.alocarSegmentoTextoCompartilhado(0, posicoes);
                }
            } 

            int j = 0;
            for (int i = retornoWorstFit; i < retornoWorstFit + tamanhoProcesso; i++) 
            {
                this.mapaDeBits[i] = 1;


                if (!processoIgual) {
                    if (j < quantidadeQuadrosTexto ) { // (1 <= 2) (0 < 2)
                        tabelaPaginaAtual.alocarSegmentoTexto(i);
                        j++;
                        continue;
                    }
                    
                    System.out.println("Antes de entrar na verificação de stack : j = " + j);
                    if (j >= quantidadeQuadrosTexto + quantidadeQuadrosDados ) { 
                        tabelaPaginaAtual.alocarSegmentoStack(i);
                        j++;
                        continue;
                    }
                    
                    if (j >= quantidadeQuadrosTexto ) {
                        tabelaPaginaAtual.alocarSegmentoData(i);
                        j++;
                        continue;
                    }
                } else { // Se processo igual
                    if (j < quantidadeQuadrosDados ) { 
                        tabelaPaginaAtual.alocarSegmentoData(i);
                        j++;
                        continue;
                    }

                    if (j >= quantidadeQuadrosDados) {
                        tabelaPaginaAtual.alocarSegmentoStack(i);
                    }
                }
                
            }
            tabelaPaginaAtual.setByteFinalSegmentoDadosEstatico();

            this.icrementarContadorId();
            System.out.print("Identificador do processo carregado na memoria: " + idDoProcessoAtual + "\n");
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return idDoProcessoAtual;
    }

    @Override
    public int allocateMemoryToProcess(int processId, int size) {
        try {

            if(!this.listaTabelaDePaginas.containsKey(processId))
                throw new InvalidProcessException("O processo de Id = " + processId + " é inválido.");
                
            TabelaDePaginas tbP = this.listaTabelaDePaginas.get(processId);
            
            double restoParaAlocar = (size < tbP.faltando()) ? 0 : size - tbP.faltando();

            int indiceParaAlocarHeap = this.worstFit(tbP.getQuantidadeDeQuadros((int)restoParaAlocar));

            if (indiceParaAlocarHeap == this.quadrosDaMemoriaInsuficiente) {
                throw new MemoryOverflowException("Quantidade requisitada superior a disponivel na memoria.");
            }

            int proximoByteFinal = ( indiceParaAlocarHeap * 32 ) + (int)restoParaAlocar - 1;

            for (int i = indiceParaAlocarHeap; i < indiceParaAlocarHeap + tbP.getQuantidadeDeQuadros((int)restoParaAlocar); i++) { 
                tbP.alocarHeap(i);
                this.mapaDeBits[i] = 1;
            }
            tbP.setByteFinalHeap(proximoByteFinal, true, size);

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

            if ( ( size - tbP.getHeapTotal() ) > 0)
                throw new NoSuchMemoryException("Tamanho : " + size + " é maior que o tamanho total do heap.");

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

            ArrayList<Integer> indicesDeTextoCompartilhado = new ArrayList<Integer>(); // Novo array de memorias compartilhadas
            for (HashMap.Entry<Integer,TabelaDePaginas> entrada : this.listaTabelaDePaginas.entrySet()) {
                int id = entrada.getKey();
                int entradaTamSegTexto = entrada.getValue().getTamanhoSegmentoTexto();
                int entradaTamSegDados = entrada.getValue().getTamanhoSegmentoDados();

                System.out.println("entradaTamSegTexto : " + entradaTamSegTexto);
                System.out.println("entradaTamSegDados : " + entradaTamSegDados);
                System.out.println("Antes do for");
                
                if (id == processId) // Analisando os mesmos processos
                    continue; 

                // Se processos iguais
                if (entradaTamSegTexto == tbP.getTamanhoSegmentoTexto() && entradaTamSegDados == tbP.getTamanhoSegmentoDados() && this.listaDeProcessos.get(id).equals(this.listaDeProcessos.get(processId)) ) {

                    System.out.println("Dentro do if");
                    int quadrosTexto = entrada.getValue().getQuantidadeQuadrosTexto();
                    System.out.println("Quadro de texto = " + quadrosTexto);

                    for (int p = 0; p < quadrosTexto; p++) // Guarda os bytes base da memória do segmento de texto do programa que já foi carregado
                            indicesDeTextoCompartilhado.add(entrada.getValue().paginas[p]/32);
                }
            }

            System.out.println("Vai começar o for dos indices de texto compartilhados");
            for (int indice : indicesDeTextoCompartilhado) {
                System.out.println("Indice : " + indice);
            }

            // indices : todos os índices do processo
            // indicesDeTextoCompartilhado : [0,1]

            //[TC,TC,D,D,D,D,D,2TC,2TC,D,D,D,D,D]
            ArrayList<Integer> indices = tbP.excluirProcessoDaMemoria();
            
            for (int indice1 : indices) {
                System.out.println("Indice : " + indice1);
            }


            boolean compartilhado = false;
            for (int indice : indices) {
                for (int elemento : indicesDeTextoCompartilhado) {
                    compartilhado = false;
                    System.out.println("Elemento : " + elemento+ "=" + indice+ "(indice)?");
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

            int indicePagina = Integer.parseInt(primeiros5Bits, 2);

            if (tabelaDoProcesso.isValid[indicePagina] == 0)
                throw new InvalidAddressException("Endereço lógico inválido dentro do processo. Endereço igual a : " + logicalAddress + ".");  
            
            int endereco = tabelaDoProcesso.paginas[indicePagina];

            enderecoFisico = endereco + Integer.parseInt(ultimos5Bits, 2);
         
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
    public String getPageTable(int processId) {
        try {

            if(!this.listaTabelaDePaginas.containsKey(processId))
                throw new InvalidProcessException("O processo de Id = " + processId + " é inválido.");

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        TabelaDePaginas tabelaPaginaAtual = this.listaTabelaDePaginas.get(processId);
            
        return "A tabela de páginas vínculada ao processo com id : " + processId + " é a página : " + tabelaPaginaAtual.toString();
    }

    @Override
    public String[] getProcessList() {
        ArrayList<String> a = new ArrayList<String>();

        for (HashMap.Entry<Integer,String> entrada : this.listaDeProcessos.entrySet()) {
            int id = entrada.getKey();
            String nome = entrada.getValue();
            a.add("[Id : " + id + ";Nome : " + nome + "]");
        }
        return a.toArray(new String[0]);
    }


    /** 
     * Determina o índice ao qual os dados passados devem ser alocados deixando o maior espaço livre possível
	 * @return primeiro indice da memória ao qual os dados devem ser alocados 
	 * @param tamanhoDeQuadros quantos quadros devem ser alocados
	 */
    private int worstFit(int tamanhoDeQuadrosProcesso) {
        int i = 0;
        int j;
        int nroQuadros = 0;
        int indice;
        int indiceDoMax = 0;
        int nroMaxQuadros = 0;

        if (tamanhoDeQuadrosProcesso == 0)
            return -1;

        while(i < this.totalQuadrosParaGerenciar) {
            if (this.mapaDeBits[i] == 0) {
                nroQuadros = 0;
                indice = i;
                j = i;
                /* Conta quantos quadros estão livres em um buraco */
                while (j < this.mapaDeBits.length && this.mapaDeBits[j] != 1 ) {
                    nroQuadros++;
                    j++;
                }

                if (nroQuadros < tamanhoDeQuadrosProcesso) {
                    return quadrosDaMemoriaInsuficiente;
                }

                if (nroQuadros >= tamanhoDeQuadrosProcesso) { // Se o processo caber nesse buraco

                    /* Se esse buraco for maior do que um já "avaliado" guarda o índice do primeiro quadro desse buraco */
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

    /** 
	 *  Incrementa o contador de Id em uma unidade  
	 */
    public void icrementarContadorId()
    {
        this.contadorDeId += 1 ;
    }

    /** 
	 * Atribui o valor "0" a todos os bits do mapa de bits
     * @param tamanhoMapaDeBits tamanho total do mapa de bits
	 */
    private void inicializarMapaDeBits(int tamanhoMapaDeBits)
    {
        this.mapaDeBits = new int[tamanhoMapaDeBits];
        for (int i = 0; i < this.mapaDeBits.length; i++) 
        {
            this.mapaDeBits[i] = 0;
        }
    }

    public int getContadorId()
    {
        return ( this.contadorDeId );
    }

    public void setContadorId(int contadorNovoValor)
    {
        this.contadorDeId = contadorNovoValor;
    }

    public int getTotalQuadrosParaGerenciar()
    {
        return ( this.totalQuadrosParaGerenciar );
    }

    public void setTotalQuadrosParaGerenciar(int totalQuadrosParaGerenciar)
    {
        this.totalQuadrosParaGerenciar = totalQuadrosParaGerenciar;
    }
    
}