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
	 * @param quantidadeHeap  
	 */
    public void setByteFinalSegmentoDadosEstatico() {
        int comecoSegmentoDeDados = this.getQuantidadeQuadrosTexto(); // Se tem 2, o primeiro seg de dado tá no índice 2

        this.byteFinalSegmentoDadosEstatico = this.paginas[comecoSegmentoDeDados] + this.getTamanhoSegmentoDados() - 1;

    }

    /** 
	 *  	
	 * @param quantidadeHeap  
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
	 *  	
	 * @param quantidadeHeap  
	 */
    public int getQuantidadeDeQuadros(int totalDeBytes) {
        return (totalDeBytes % 32 != 0) ? (totalDeBytes / 32) + 1 : (totalDeBytes / 32);
    }


    /** 
	 *  	
	 * @param quantidadeHeap  
	 */
    public void alocarSegmentoTexto(int i) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = i * this.getTamanhoQuadroDeBytes();
        this.setIndex(indexAtual + 1);
    }

    /** 
	 *  	
	 * @param quantidadeHeap  
	 */
    public void alocarSegmentoTextoCompartilhado(int i, int byteBase) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = byteBase;
        this.setIndex(indexAtual + 1);
    }

    /** 
	 *  	
	 * @param quantidadeHeap  
	 */
    public void alocarSegmentoData(int i) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = i * this.getTamanhoQuadroDeBytes();
        this.setIndex(indexAtual + 1);
    }

    /** 
	 *  	
	 * @param quantidadeHeap  
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

    public int getByteInicial() {
        return this.paginas[0];
    }

    /** 
	 *  	
	 * @param quantidadeHeap  
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
	 *  	
	 * @param quantidadeHeap  
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
	 *  	
	 * @param quantidadeHeap
	 */
    private void aumentaHeapTotal(int quantidadeHeap) {
        this.heapTotal += quantidadeHeap;
    }

    /** 
	 *  	
	 * @param quantidadeHeap  
	 */
    private void diminuiHeapTotal(int quantidadeHeap) {
        this.heapTotal -= quantidadeHeap;
    }

    /** 
	 *  	
	 * @return indices da memória que devem ser invalidados
	 */
    public double faltando() {
        double byteFinal = (double)this.getByteFinalSegmentoDados();
        return (byteFinal % 32 != 0) ? ( Math.ceil( (byteFinal / 32) ) * 32) - 1 - byteFinal : 0;
    }

    /** 
	 * Aloca memoria dinamica (heap) para um processo virtual carregado na memoria principal 	
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
            if (this.isValid[n] == 0 && this.paginas[n] != 0) {
                pages += "x" + ",";
            } else {
                pages += this.paginas[n] + ",";
            }
            validos += this.isValid[n] + ",";
        }
        return "Validos : " + validos + "\nPaginas : " + pages + ".\n";
    }

}