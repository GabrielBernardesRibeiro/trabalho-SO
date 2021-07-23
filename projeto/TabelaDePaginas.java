package projeto;

import java.util.Stack;

public class TabelaDePaginas {
    private final int tamanhoQuadroDeBits = 32;
    private final int indexInicial = 0;
    public int[] isValid = new int[tamanhoQuadroDeBits]; // Armazena 1(se página válida) e 0(se página invalida)
    public int[] paginas = new int[tamanhoQuadroDeBits]; // Armazena o endereço base do quadro de memória
    private int index;

    public Stack<Integer> stackByteFinal = new Stack<Integer>();
    
    private int byteFinalSegmentoDados; // Começar o heap no byteFinalSegDados+1
    private int tamanhoSegmentoTexto;
    private int tamanhoSegmentoDados;
    private int quantidadeQuadrosTexto;
    private int quantidadeQuadrosDados;

    public int heapTotal = 0;
    
    // vai ter q ter uma pilha


    public boolean trinta = true;

    public TabelaDePaginas(int tamanhoSegmentoTexto, int tamanhoSegmentoDados) {
        this.setIndex(this.getIndexInicial());
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
        return this.byteFinalSegmentoDados;
    }

    public void setByteFinalSegmentoDados(int byteFinal, boolean estatico)
    {
        this.byteFinalSegmentoDados = estatico ? this.getByteInicial() + (this.getQuantidadeQuadrosTexto() * this.getTamanhoQuadroDeBits() ) + this.getTamanhoSegmentoDados() - 1 : byteFinal;

        if (!estatico)
            this.stackByteFinal.push(this.byteFinalSegmentoDados);
    }

    public int getQuantidadeDeQuadros(int totalDeBits) {
        return (totalDeBits % 32 != 0) ? (totalDeBits / 32) + 1 : (totalDeBits / 32);
    }

    private void setIndex(int indexNovo)
    {
        this.index = indexNovo;
    }

    private int getIndex()
    {
        return this.index;
    }

    private int getIndexInicial()
    {
        return this.indexInicial;
    }

    private int getTamanhoQuadroDeBits()
    {
        return this.tamanhoQuadroDeBits;
    }

    public void alocarSegmentoTexto(int i) {
        int indexAtual = this.getIndex();
        this.isValid[indexAtual] = 1;
        this.paginas[indexAtual] = i * this.getTamanhoQuadroDeBits();
        this.setIndex(indexAtual + 1);
    }

    public void alocarSegmentoData(int i) {
        int indexAtual = this.getIndex();
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

    public void removerHeap(int indiceNaMemoria) {
        int n;
        

        this.isValid[n] = 0;
    }

    public int faltandoDosegmentoDeDadosEstatico() {
       return (this.getByteFinalSegmentoDados() % 32) == 0 ? 0 : (((this.getQuantidadeQuadrosTexto() + this.getQuantidadeQuadrosDados()) * 32) - 1 - (this.getByteFinalSegmentoDados() + 1));
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
        return "Validos : " + validos + "\nPaginas : " + pages + ".\n" + "Pro heap : " + this.byteFinalSegmentoDados;
    }

}