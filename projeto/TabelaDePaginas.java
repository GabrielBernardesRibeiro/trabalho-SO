package projeto;

import java.util.Stack;

import java.util.ArrayList;

import java.util.*;

public class TabelaDePaginas {
    private final int tamanhoQuadroDeBits = 32;
    //private final int indexInicial = 0;
    public int[] isValid = new int[tamanhoQuadroDeBits]; // Armazena 1(se página válida) e 0(se página invalida)
    public int[] paginas = new int[tamanhoQuadroDeBits]; // Armazena o endereço base do quadro de memória
    
    private int index = 0;

    public Stack<Integer> stackByteFinal = new Stack<Integer>();
    
     // Começar o heap no byteFinalSegDados+1

    public int byteFinalSegmentoDadosEstatico;

    public int byteFinalHeap;

    private int tamanhoSegmentoTexto;
    private int tamanhoSegmentoDados;
    private int quantidadeQuadrosTexto;
    private int quantidadeQuadrosDados;
    
    // vai ter q ter uma pilha


    public boolean trinta = true;

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
        
        this.byteFinalSegmentoDadosEstatico = this.getByteInicial() + (this.getQuantidadeQuadrosTexto() * this.getTamanhoQuadroDeBits() ) + this.getTamanhoSegmentoDados() - 1;

    }

    public void setByteFinalHeap(int byteFinal) {
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


        double byteFinalHeap = this.byteFinalHeap;// = (double)this.stackByteFinal.pop();

        double diferenca;// byteFinalHeap -  ( (Math.floor(byteFinalHeap/32) ) * 32 );
        //sum += diferenca;

        //this.isValid[( (Math.floor(byteFinalHeap/32) ) * 32 )] = 

        boolean primeiraVez = true;

        double limite = Math.floor( this.byteFinalSegmentoDadosEstatico/32 );
        System.out.println("--------------");
        System.out.println("Limite = " + limite);
        System.out.println("--------------");

        for (int i = 29; i >= limite; i--) {

            if (sum == size) {
                System.out.println("::::::Sum = " + sum + ". Size = " + size);
                break;
            }

            if (this.isValid[i] == 1) {

                if (!this.stackByteFinal.empty() && this.stackByteFinal.peek() > this.paginas[i]) {
                    byteFinalHeap = this.stackByteFinal.pop();
                    System.out.println("--------------");
                    System.out.println("I :  = " + i + ". ByteFInalHeap : " + byteFinalHeap);
                    System.out.println("--------------");
                }

                if (primeiraVez) {
                    diferenca = byteFinalHeap - this.paginas[i]; // Ver se a subtração tá certa por causa daquilo de contar do 0
                    sum += diferenca;

                    if (size >= diferenca) { // Maior ou maior igual
                        indices.add(this.paginas[i] / 32);
                        this.isValid[i] = 0;
                    }
                    
                    primeiraVez = false;
                    System.out.println("--------------");
                    System.out.println("Primeira Vez");
                    System.out.println("I :  = " + i + ". Diferenca = " + diferenca + ". Sum = " + sum);
                    System.out.println("--------------");
                    continue;
                }


                if (sum + 32 <= size) { // Ver se posso tirar um bloco inteiro
                    System.out.println("--------------");
                    System.out.println("I :  = " + i + ". Sum + 32 = " + (sum+32));
                    indices.add(this.paginas[i] / 32);
                    this.isValid[i] = 0;
                    sum += 32;
                    System.out.println("Sum novo : " + sum);
                    System.out.println("--------------");
                } else { // Se não puder, Tiro só a quantidade que falta
                    int restante = size - (int)sum;
                    int novoByteFinalHeap = (this.paginas[i] + 32) - restante;
                    sum += restante;

                    System.out.println("--------------");
                    System.out.println("Else");
                    System.out.println("I :  = " + i + ". Restante : " + restante);
                    System.out.println("Paginas[i] = " + this.paginas[i] + ". paginas[i] + 32 : " + (this.paginas[i]+32));
                    System.out.println("Novo byte final heap : " + novoByteFinalHeap);
                    System.out.println("--------------");

                    // --------- Esses dois devem ser colocados em um método, pois sempre acontecem juntos
                    this.stackByteFinal.push(novoByteFinalHeap);
                    this.byteFinalHeap = novoByteFinalHeap;
                    // ---------
                }

            }
        }
        System.out.println("--------------");
        System.out.println("Sum = " + sum);
        System.out.println("ByteFinalHeap : " + this.byteFinalHeap);
        System.out.println("--------------");

        return indices;
    }

    public int getHeapTotal() {
        return this.byteFinalHeap - this.byteFinalSegmentoDadosEstatico;
    }

    public double faltando() {
        double byteFinal = (double)this.getByteFinalSegmentoDados();
        double a = byteFinal / 32;
        return (byteFinal % 32 != 0) ? ( Math.ceil(a) * 32) - 1 - byteFinal : 0;//
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