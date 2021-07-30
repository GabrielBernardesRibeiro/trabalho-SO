package projeto;
import java.util.Stack;
import java.util.ArrayList;

public class TabelaDePaginas {
    private final int tamanhoQuadroDeBytes = 32;

    private int index = 0;

    private int heapTotal = 0;

    private int tamanhoSegmentoTexto;
    private int tamanhoSegmentoDados;
    private int quantidadeQuadrosTexto;
    private int quantidadeQuadrosDados;

    private boolean trinta = true;

    private int byteFinalHeap;
    private int byteFinalSegmentoDadosEstatico;

    private Stack<Integer> stackByteFinal = new Stack<Integer>(); // Armazena os bytes finais de cada heap alocado

    public int[] isValid = new int[tamanhoQuadroDeBytes]; // Armazena 1(se página válida) e 0(se página invalida)
    public int[] paginas = new int[tamanhoQuadroDeBytes]; // Armazena o endereço base do quadro de memória

    public TabelaDePaginas(int tamanhoSegmentoTexto, int tamanhoSegmentoDados) {
        this.setIndex(this.index);
        this.setTamanhoSegmentoTexto(tamanhoSegmentoTexto);
        this.setTamanhoSegmentoDados(tamanhoSegmentoDados);
        this.setQuantidadeQuadrosTexto(tamanhoSegmentoTexto);
        this.setQuantidadeQuadrosDados(tamanhoSegmentoDados);
    }

    public int getTamanhoSegmentoTexto()
    {
        return this.tamanhoSegmentoTexto;
    }

    private void setTamanhoSegmentoTexto(int tamanhoSegmentoTexto)
    {
        this.tamanhoSegmentoTexto = tamanhoSegmentoTexto;
    }

    public int getTamanhoSegmentoDados()
    {
        return this.tamanhoSegmentoDados;
    }

    private void setTamanhoSegmentoDados(int tamanhoSegmentoDados)
    {
        this.tamanhoSegmentoDados = tamanhoSegmentoDados;
    }

    public int getQuantidadeQuadrosTexto()
    {
        return this.quantidadeQuadrosTexto;
    }

    private void setQuantidadeQuadrosTexto(int tamanhoSegmentoTexto)
    {
        this.quantidadeQuadrosTexto = this.getQuantidadeDeQuadros(tamanhoSegmentoTexto);
    }

    public int getQuantidadeQuadrosDados()
    {
        return this.quantidadeQuadrosDados;
    }

    private void setQuantidadeQuadrosDados(int tamanhoSegmentoDados)
    {
        this.quantidadeQuadrosDados = this.getQuantidadeDeQuadros(tamanhoSegmentoDados);
    }

    public int getByteFinalSegmentoDados()
    {
        return this.byteFinalHeap == 0 ? this.byteFinalSegmentoDadosEstatico : this.byteFinalHeap;
    }

    public int getHeapTotal() {
        return this.heapTotal;
    }

    private void setIndex(int indexNovo)
    {
        this.index = indexNovo;
    }

    private int getTamanhoQuadroDeBytes()
    {
        return this.tamanhoQuadroDeBytes;
    }

    /** 
	 *  	
	 * Define o byte final do segmento de dados estático 
	 */
    public void setByteFinalSegmentoDadosEstatico() {
        int comecoSegmentoDeDados = this.getQuantidadeQuadrosTexto(); // Se tem 2, o primeiro seg de dado tá no índice 2

        this.byteFinalSegmentoDadosEstatico = this.paginas[comecoSegmentoDeDados] + this.getTamanhoSegmentoDados() - 1;

    }

    /** 
	 * Define o byte final do heap
	 * @param byteFinal é o ultimo byte alocado
     * @param aumentar controle para saber se aumenta ou diminui o heap total
     * @param size é o tamanho que irá aumentar ou diminuir do heap
	 */
    public void setByteFinalHeap(int byteFinal, boolean aumentar, int size) {
        if (aumentar)
            this.aumentaHeapTotal(size);
        else
            this.diminuiHeapTotal(size);
        this.stackByteFinal.push(byteFinal);
        this.byteFinalHeap = byteFinal;
    }

    /** 
	 * Pega a quantidade de quadros	
	 * @param totalDeBytes total de bytes que devem ser alocados
     * @return retorna o total de quadros ncessários para alocar a quantidade de bytes passado
	 */
    public int getQuantidadeDeQuadros(int totalDeBytes) {
        return (totalDeBytes % 32 != 0) ? (totalDeBytes / 32) + 1 : (totalDeBytes / 32);
    }


    /** 
	 * Aloca o segmento de texto
	 * @param i é a quantidade de quadros que será alocado para um determinado seguimento de texto 
	 */
    public void alocarSegmentoTexto(int i) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = i * this.getTamanhoQuadroDeBytes();
        this.setIndex(indexAtual + 1);
    }

    /** 
	 * Aloca o segmento de texto compartilhado
	 * @param i é a quantidade de quadros que será alocado para um determinado seguimento de texto compartilhado
     * @param byteBase é a posição de uma página na memória
	 */
    public void alocarSegmentoTextoCompartilhado(int i, int byteBase) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = byteBase;
        this.setIndex(indexAtual + 1);
    }

    /** 
	 * Aloca o segmento de data 	
	 * @param i é a quantidade de quadros que será alocado para um determinado seguimento de dados   
	 */
    public void alocarSegmentoData(int i) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = i * this.getTamanhoQuadroDeBytes();
        this.setIndex(indexAtual + 1);
    }

    /** 
     * Aloca o segmento de pilha 	
	 * @param i é a quantidade de quadros que será alocado para um determinado seguimento de pilha  
	 */
    public void alocarSegmentoStack(int i) {
        if (this.trinta) {
            this.isValid[30] = 1;
            this.paginas[30] = i * this.getTamanhoQuadroDeBytes();
            this.trinta = false;
        }
        this.isValid[31] = 1;
        this.paginas[31] = i * this.getTamanhoQuadroDeBytes();
    }

    /**
     * Pega o byte inicial
     * @return retorna o byte inicial
     */
    public int getByteInicial() {
        return this.paginas[0];
    }

    /** 
	 * Aloca a pilha	
	 * @param indiceNaMemoria o indice do processo na memória  
	 */
    public void alocarHeap(int indiceNaMemoria) {
        int n;
        for (n = 0; n < this.paginas.length; n++) {
            if (this.isValid[n] == 0)
                break;
        }
        this.paginas[n] = indiceNaMemoria * 32;
        this.isValid[n] = 1;
    }

    /** 
	 * Remove o heap de uma tabela de processo 	
	 * @param size  a quantidade reduzida para o heap de uma tabela de processo
     * @return retorna um ArrayList de indices
	 */
    public ArrayList<Integer> removerHeap(int size) {
        ArrayList<Integer> indices = new ArrayList<Integer>(); 

        double sum = 0;

        double byteFinalHeap = this.byteFinalHeap;

        double diferenca;

        boolean primeiraVez = true;

        double limite = Math.floor( this.byteFinalSegmentoDadosEstatico/32 );

        for (int i = 29; i >= limite; i--) { // Começa-se a procura do índice 29, pois os índices 30 e 31 referem a pilha

            if (sum == size) {
                break;
            }

            if (this.isValid[i] == 1) {

                /* Quando se retira mais do que o tamanho do heap mais recentemente alocado */
                if (!this.stackByteFinal.empty() && this.stackByteFinal.peek() > this.paginas[i]) {
                    byteFinalHeap = this.stackByteFinal.pop();
                }

                /*  */
                if (primeiraVez) {
                    diferenca = byteFinalHeap - this.paginas[i];
                    sum += diferenca;

                    if (size >= diferenca) {
                        indices.add(this.paginas[i] / 32);
                        this.isValid[i] = 0;
                    }
                    
                    primeiraVez = false;
                    continue;
                }

                if (sum + 32 <= size) { // Ver se é possível retirar um bloco inteiro
                    indices.add(this.paginas[i] / 32);
                    this.isValid[i] = 0;
                    sum += 32;
                } else { // Se não puder, retira-se só a quantidade que falta dentro do bloco
                    int restante = size - (int)sum;
                    int novoByteFinalHeap = (this.paginas[i] + 32) - restante;
                    sum += restante;

                    this.setByteFinalHeap(novoByteFinalHeap, false, size);
                }

            }
        }

        return indices;
    }

    /** 
	 * Aumenta o heap total 	
	 * @param quantidadeHeap o tamanho alocado do heap para a tabela de processo de um processo
	 */
    private void aumentaHeapTotal(int quantidadeHeap) {
        this.heapTotal += quantidadeHeap;
    }

    /** 
	 * Diminui o heap total 
	 * @param quantidadeHeap  o tamanho reduzido do heap para a tabela de processo de um processo
	 */
    private void diminuiHeapTotal(int quantidadeHeap) {
        this.heapTotal -= quantidadeHeap;
    }

    /** 
	 * Define a quantidade de indices livres da memória 	
	 * @return indices da memória que devem ser invalidados
	 */
    public double faltando() {
        double byteFinal = (double)this.getByteFinalSegmentoDados();
        return (byteFinal % 32 != 0) ? ( Math.ceil( (byteFinal / 32) ) * 32) - 1 - byteFinal : 0;
    }

    /** 
	 * Seleciona todas as páginas válidas para a exclusão do processo na memória	
	 * @return indices da memória que devem ser invalidados
	 */
    public ArrayList<Integer> excluirProcessoDaMemoria() {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        
        for (int i = 0; i < this.paginas.length; i++) {
            if (this.isValid[i] == 1) {
                int indiceNaMemoria = this.paginas[i] / 32;
                indices.add(indiceNaMemoria);
            }
        }
        
        return indices;
    }
    
    @Override
    public String toString() {
        String validos = "[";
        String pages = "[";
        for (int n = 0; n < 32; n++) {
            if (n == 31) {
                pages += this.paginas[n] + "]";
                validos += this.isValid[n] + "]";
                break;
            }
            if (this.isValid[n] == 0) {
                pages += " " + ",";
            } else {
                pages += this.paginas[n] + ",";
            }
            validos += this.isValid[n] + ",";
        }
        return "Validos : " + validos + "\nPaginas : " + pages + ".\n";
    }
}