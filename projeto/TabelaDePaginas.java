package projeto;

public class TabelaDePaginas {
    private final int tamanho_quadro_de_bits = 32;
    private final int index_inicial = 0;
    public int[] isValid = new int[tamanho_quadro_de_bits]; // Armazena 1(se página válida) e 0(se página invalida)
    public int[] paginas = new int[tamanho_quadro_de_bits]; // Armazena o endereço base do quadro de memória
    private int index;
    

    private int byte_final_segmento_dados; // Começar o heap no byteFinalSegDados+1
    private int tamanho_segmento_texto;
    private int tamanho_segmento_dados;
    private int quantidade_quadros_texto;
    private int quantidade_quadros_dados;

    
    // vai ter q ter uma pilha


    public boolean trinta = true;

    public TabelaDePaginas(int tamanho_segmento_texto, int tamanho_segmento_dados) {
        this.set_index(this.get_index_inicial());
        this.set_tamanho_segmento_texto(tamanho_segmento_texto);
        this.set_tamanho_segmento_dados(tamanho_segmento_dados);
        this.set_quantidade_quadros_texto(tamanho_segmento_texto);
        this.set_quantidade_quadros_dados(tamanho_segmento_dados);
        //this.set_byte_final_segmento_dados();
    }

    public int get_tamanho_segmento_texto()
    {
        return this.tamanho_segmento_texto;
    }

    private void set_tamanho_segmento_texto(int tamanho_segmento_texto)
    {
        this.tamanho_segmento_texto = tamanho_segmento_texto;
    }

    public int get_tamanho_segmento_dados()
    {
        return this.tamanho_segmento_dados;
    }

    private void set_tamanho_segmento_dados(int tamanho_segmento_dados)
    {
        this.tamanho_segmento_dados = tamanho_segmento_dados;
    }

    public int get_quantidade_quadros_texto()
    {
        return this.quantidade_quadros_texto;
    }

    private void set_quantidade_quadros_texto(int tamanho_segmento_texto)
    {
        this.quantidade_quadros_texto = this.get_quantidade_de_quadros(tamanho_segmento_texto);
    }

    public int get_quantidade_quadros_dados()
    {
        return this.quantidade_quadros_dados;
    }

    private void set_quantidade_quadros_dados(int tamanho_segmento_dados)
    {
        this.quantidade_quadros_dados = this.get_quantidade_de_quadros(tamanho_segmento_dados);
    }

    public int get_byte_final_segmento_dados()
    {
        return this.byte_final_segmento_dados;
    }

    public void set_byte_final_segmento_dados()
    {
        this.byte_final_segmento_dados = this.getByteInicial() + (this.get_quantidade_quadros_texto() * this.get_tamanho_quadro_de_bits() ) + this.get_tamanho_segmento_dados() - 1;
    }

    public int get_quantidade_de_quadros(int total_de_bits) {
        return (total_de_bits % 32 != 0) ? (total_de_bits / 32) + 1 : (total_de_bits / 32);
    }

    private void set_index(int index_novo)
    {
        this.index = index_novo;
    }

    private int get_index()
    {
        return this.index;
    }

    private int get_index_inicial()
    {
        return this.index_inicial;
    }

    private int get_tamanho_quadro_de_bits()
    {
        return this.tamanho_quadro_de_bits;
    }

    public void alocar_segmento_texto(int i) {
        int index_atual = this.get_index();
        this.isValid[index_atual] = 1;
        this.paginas[index_atual] = i * this.get_tamanho_quadro_de_bits();
        this.set_index(index_atual + 1);
    }

    public void alocar_segmento_data(int i) {
        int index_atual = this.get_index();
        this.isValid[index_atual] = 1;
        this.paginas[index_atual] = i * this.get_tamanho_quadro_de_bits();
        this.set_index(index_atual + 1);
    }

    public void alocar_segmento_stack(int i) {
        if (this.trinta) {
            this.isValid[30] = 1;
            this.paginas[30] = i * this.get_tamanho_quadro_de_bits();
            this.trinta = false;
        }
        this.isValid[31] = 1;
        this.paginas[31] = i * this.get_tamanho_quadro_de_bits();
    }

    public int getByteInicial() {
        return this.paginas[0];
    }


    public void alocarHeap(int indiceNaMemoria) {
        //System.out.println("N :kkkkkkadkaskdasld " + n);
        int n;
        for (n = 0; n < this.paginas.length; n++) {
            if (this.isValid[n] == 0)
                break;
        }
        /*
        for () {

        }
        */
        //System.out.println("N :kkkkkkadkaskdasld " + n);
        this.paginas[n] = indiceNaMemoria * 32;
        this.isValid[n] = 1;
    }

    public int faltandoDosegmentoDeDadosEstatico() {
       return (this.get_byte_final_segmento_dados() % 32) == 0 ? 0 : (((this.get_quantidade_quadros_texto() + this.get_quantidade_quadros_dados()) * 32) - 1 - (this.get_byte_final_segmento_dados() + 1));
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
        return "Validos : " + validos + "\nPaginas : " + pages + ".\n" + "Pro heap : " + this.byte_final_segmento_dados;
    }

}