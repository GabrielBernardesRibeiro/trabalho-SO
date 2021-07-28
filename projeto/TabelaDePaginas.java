package projeto;

import java.util.Stack;

import java.util.ArrayList;

import java.util.*;

public class TabelaDePaginas {
    public int[] isValid = new int[tamanhoQuadroDeBits]; // Armazena 1(se página válida) e 0(se página invalida)
    public int[] paginas = new int[tamanhoQuadroDeBits]; // Armazena o endereço base do quadro de memória
    
    private final int tamanhoQuadroDeBits = 32;

    private int index = 0;

    private int heapTotal = 0;

    private int tamanhoSegmentoTexto;
    private int tamanhoSegmentoDados;
    private int quantidadeQuadrosTexto;
    private int quantidadeQuadrosDados;

    private boolean trinta = true;

    private int byteFinalHeap;
    private int byteFinalSegmentoDadosEstatico;

    private Stack<Integer> stackByteFinal = new Stack<Integer>();


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

    public void setByteFinalSegmentoDadosEstatico() {
        int comecoSegmentoDeDados = this.getQuantidadeQuadrosTexto(); // Se tem 2, o primeiro seg de dado tá no índice 2

        this.byteFinalSegmentoDadosEstatico = this.paginas[comecoSegmentoDeDados] + this.getTamanhoSegmentoDados() - 1;

    }

    public void setByteFinalHeap(int byteFinal, boolean aumentar, int size) {
        if (aumentar)
            this.aumentaHeapTotal(size);
        else
            this.diminuiHeapTotal(size);
        this.stackByteFinal.push(byteFinal);
        this.byteFinalHeap = byteFinal;
    }

    public int getQuantidadeDeQuadros(int totalDeBits) {
        return (totalDeBits % 32 != 0) ? (totalDeBits / 32) + 1 : (totalDeBits / 32);
    }

    private void setIndex(int indexNovo)
    {
        this.index = indexNovo;
    }

    private int getTamanhoQuadroDeBits()
    {
        return this.tamanhoQuadroDeBits;
    }

    public void alocarSegmentoTexto(int i) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = i * this.getTamanhoQuadroDeBits();
        this.setIndex(indexAtual + 1);
    }

    public void alocarSegmentoTextoCompartilhado(int i, int byteBase) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = byteBase;
        this.setIndex(indexAtual + 1);
    }

    public void alocarSegmentoData(int i) {
        int indexAtual = this.index;
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = i * this.getTamanhoQuadroDeBits();
        this.setIndex(indexAtual + 1);
    }

    public void alocarSegmentoStack(int i) {
        if (this.trinta) {
            this.isValid[30] = 1;
            this.paginas[30] = i * this.getTamanhoQuadroDeBits();
            this.trinta = false;
        }
        this.isValid[31] = 1;
        this.paginas[31] = i * this.getTamanhoQuadroDeBits();
    }

    public int getByteInicial() {
        return this.paginas[0];
    }

    public void alocarHeap(int indiceNaMemoria) {
        int n;
        for (n = 0; n < this.paginas.length; n++) {
            if (this.isValid[n] == 0)
                break;
        }
        this.paginas[n] = indiceNaMemoria * 32;
        this.isValid[n] = 1;
    }

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

                if (!this.stackByteFinal.empty() && this.stackByteFinal.peek() > this.paginas[i]) {
                    byteFinalHeap = this.stackByteFinal.pop();
                }

                if (primeiraVez) {
                    diferenca = byteFinalHeap - this.paginas[i]; // Ver se a subtração tá certa por causa daquilo de contar do 0
                    sum += diferenca;

                    if (size >= diferenca) { // Maior ou maior igual
                        indices.add(this.paginas[i] / 32);
                        this.isValid[i] = 0;
                    }
                    
                    primeiraVez = false;
                    continue;
                }

                if (sum + 32 <= size) { // Ver se posso tirar um bloco inteiro
                    indices.add(this.paginas[i] / 32);
                    this.isValid[i] = 0;
                    sum += 32;
                } else { // Se não puder, Tiro só a quantidade que falta
                    int restante = size - (int)sum;
                    int novoByteFinalHeap = (this.paginas[i] + 32) - restante;
                    sum += restante;

                    this.setByteFinalHeap(novoByteFinalHeap, false, size);
                }

            }
        }

        return indices;
    }

    private void aumentaHeapTotal(int quantidadeHeap) {
        this.heapTotal += quantidadeHeap;
    }

    private void diminuiHeapTotal(int quantidadeHeap) {
        this.heapTotal -= quantidadeHeap;
    }

    public int getHeapTotal() {
        return this.heapTotal;
    }

    public double faltando() {
        double byteFinal = (double)this.getByteFinalSegmentoDados();
        return (byteFinal % 32 != 0) ? ( Math.ceil( (byteFinal / 32) ) * 32) - 1 - byteFinal : 0;//
    }

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
            pages += this.paginas[n] + ",";
            validos += this.isValid[n] + ",";
        }
        return "Validos : " + validos + "\nPaginas : " + pages + ".\n" + "Pro heap : " + (this.getByteFinalSegmentoDados());
    }

}