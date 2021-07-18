package projeto;

public class TabelaDePaginas {
    public int[] isValid = new int[32]; // Armazena 1(se página válida) e 0(se página invalida)
    public int[] paginas = new int[32]; // Armazena o endereço base do quadro de memória
    public int index = 0;
    

    public int byteFinalSegDados; // Começar o heap no byteFinalSegDados+1 

    public int memoriaTotal;
    public boolean trinta = true;

    public TabelaDePaginas() {
        
    }

    public void allocateTextSegment(int i) {
        this.isValid[index] = 1;
        this.paginas[index] = i * 32;
        index++;
    }

    public void allocateDataSegment(int i) {
        this.isValid[index] = 1;
        this.paginas[index] = i * 32;
        index++;
    }

    public void allocateStackSegment(int i) {
        if (this.trinta) {
            this.isValid[30] = 1;
            this.paginas[30] = i * 32;
            this.trinta = false;
        }
        this.isValid[31] = 1;
        this.paginas[31] = i * 32;
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
        return "Validos : " + validos + "\nPaginas : " + pages + ".\n" + "Pro heap : " + this.byteFinalSegDados;
    }

}