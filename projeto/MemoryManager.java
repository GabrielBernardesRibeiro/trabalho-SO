package projeto;


import exceptions.*;


import java.io.File;
import java.util.*;

public class MemoryManager implements ManagementInterface {
    public int total_quadros_para_gerenciar; // 32/64/128
    public int[] mapa_de_bits; // Pode ter tamanho = 32/64/128
    public HashMap<Integer,TabelaDePaginas> lista_tabela_de_paginas = new HashMap<Integer,TabelaDePaginas>();
    public HashMap<Integer,String> lista_de_projetos = new HashMap<Integer,String>();
    public int contador_de_id; // contagem do id do processo
    private int id_inicial = 0;

    private final int STACKSEGMENTSIZE = 64; // Tamanho do segmento da pilha
    private final int quantidade_quadros_pilha = 2;

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

    public MemoryManager(int total_quadros_para_gerenciar) {
        this.set_total_quadros_para_gerenciar(total_quadros_para_gerenciar);
        this.inicializar_mapa_de_bits(total_quadros_para_gerenciar);
        this.set_contador_id( this.get_id_inicial() );
    }

    @Override
    public int loadProcessToMemory(String processName) {
        int id_do_processo_atual = 0; // Id do processo que será criado
        int tamanho_segmento_texto = 0; // Tamanho do segmento de texto
        int tamanho_segmento_dados = 0; // Tamanho do segmento de dados
        String nome_do_arquivo = ""; // NomeDoArquivo

        try {   

            // ------------ Sessão de carregar e validar o arquivo -----
            File programa_do_arquivo = new File(processName + ".txt");

            if (programa_do_arquivo.exists() == false) // Se arquivo não for encontrado
            {
                throw new NoSuchFileException("Arquivo inválido ou não encontrado.");
            }

            // ---------------------------------//----------------------

            // ----------- Sessão de leitura do arquivo -----------
            Scanner scanner_arquivo = new Scanner(programa_do_arquivo);
            int contador_de_leitura = 1;
            while(true)
            {   
                int valor_leitura_atual;
                String titulo_atual;
                if (contador_de_leitura == 1)
                {
                    titulo_atual = scanner_arquivo.next();
                    if (new String("program").equals(titulo_atual))
                    {
                        nome_do_arquivo = scanner_arquivo.next();
                    } else
                    {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                } else if (contador_de_leitura == 3)
                {
                    titulo_atual = scanner_arquivo.next();
                    if (new String("text").equals(titulo_atual))
                    {
                        try {
                            valor_leitura_atual = scanner_arquivo.nextInt();
                            tamanho_segmento_texto = valor_leitura_atual;
                        } catch (InputMismatchException e) {
                            throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                        }
                    } else
                    {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                    
                } else if (contador_de_leitura == 5)
                {
                    titulo_atual = scanner_arquivo.next();
                    if (new String("data").equals(titulo_atual))
                    {
                        try {
                            valor_leitura_atual = scanner_arquivo.nextInt();
                            tamanho_segmento_dados = valor_leitura_atual;
                        } catch (InputMismatchException e) {
                            throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                        }
                    } else
                    {
                        throw new NoSuchFileException("Arquivo "+processName+" corrompido");
                    }
                }
                if (contador_de_leitura == 6)
                {
                    break;
                }
                contador_de_leitura++;
            }
            scanner_arquivo.close();

            System.out.println("\n");
            // ------------ Sessão de carregar o processo na memória -----------
            id_do_processo_atual = this.get_contador_id(); //this.contador_de_id

            // 1. criação de uma tabela de página para representar o processo:
            TabelaDePaginas tabela_pagina_atual = new TabelaDePaginas(tamanho_segmento_texto, tamanho_segmento_dados);

            this.set_nova_tabela_de_paginas(id_do_processo_atual, tabela_pagina_atual);
            this.set_novo_projeto(id_do_processo_atual, nome_do_arquivo);
            this.icrementar_contador_id();

            // 2.  alocar quadros para armazenar texto e dados
                // alocar para o texto
                // alocar para os dados
                // pilha sempre aloca 2 quadros

            // tamanho do processo: seg de texto + seg de dados + 64
            int quantidade_quadros_texto = tabela_pagina_atual.get_quantidade_quadros_texto();
            int quantidade_quadros_dados = tabela_pagina_atual.get_quantidade_quadros_dados();
            int tamanho_processo = quantidade_quadros_texto + quantidade_quadros_dados + this.quantidade_quadros_pilha;
            int retorno_worst_fit = this.worstFit(tamanho_processo);

            // ----------------- Parte de alocação da tabela de página
            int j = 1;
            for (int i = retorno_worst_fit; i < retorno_worst_fit + tamanho_processo; i++) 
            {
                System.out.println("i : " + i);
                this.mapa_de_bits[i] = 1;
                
                if (j <= quantidade_quadros_texto ) 
                {
                    tabela_pagina_atual.alocar_segmento_texto(i);
                    j++;
                    continue;
                }
                
                if (j > quantidade_quadros_texto + quantidade_quadros_dados ) 
                {
                    tabela_pagina_atual.alocar_segmento_stack(i);
                    j++;
                    continue;
                }
                
                if (j > quantidade_quadros_texto ) 
                {
                    tabela_pagina_atual.alocar_segmento_data(i);
                    j++;
                    continue;
                }
                
            }

            System.out.println( tabela_pagina_atual.toString() );
        
            
            // ---------------------------------//------------------------------
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("\n|-------------------------|\n");
        return id_do_processo_atual;
    }

    @Override
    public int allocateMemoryToProcess(int id_do_processo_atual, int size) {
        try {

            if(!this.lista_tabela_de_paginas.containsKey(id_do_processo_atual))
                throw new InvalidProcessException("O processo de Id = " + id_do_processo_atual + " é inválido.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 5;
    }

    @Override
    public int freeMemoryFromProcess(int id_do_processo_atual, int size) {
        return 5;
    }

    @Override
    public void excludeProcessFromMemory(int id_do_processo_atual) {
        
    }

    @Override
    public void resetMemory() {
        
    }

    @Override
    public int getPhysicalAddress(int id_do_processo_atual, int logicalAddress) {
        return 5;
    }

    @Override
    public String getBitMap() {
        String x = "[";
        for (int j = 0; j < this.mapa_de_bits.length; j++) {
            if (j == this.mapa_de_bits.length - 1) {
                x += this.mapa_de_bits[j] + "]";
                break;
            }
            x += this.mapa_de_bits[j] +  ", "; 
        }
        return x;
    }

    @Override
    public String getPageTable(int id_do_processo_atual) {
        try {

            if(!this.lista_tabela_de_paginas.containsKey(id_do_processo_atual))
                throw new InvalidProcessException("O processo de Id = " + id_do_processo_atual + " é inválido.");

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        TabelaDePaginas tabela_pagina_atual = this.lista_tabela_de_paginas.get(id_do_processo_atual);
            
        return "A tabela de páginas vínculada ao processo com id : " + id_do_processo_atual + " é a página : " + tabela_pagina_atual.toString();
    }

    @Override
    public String[] getProcessList() {
        String[] ab = new String[]{"a","b"};
        return ab;
    }


    private int worstFit(int tamanhoProcesso) {
        int i = 0;
        int j;
        int nroQuadros = 0;
        int indice;
        int indiceDoMax = -1;
        int nroMaxQuadros = 0;

        System.out.println("Tamanho do processo : " + tamanhoProcesso);

        System.out.println("Quadros para o processo : " + tamanhoProcesso);

        while(i < this.total_quadros_para_gerenciar) {
            if (this.mapa_de_bits[i] == 0) {
                nroQuadros = 0;
                indice = i;
                j = i;
                while (j < this.mapa_de_bits.length && this.mapa_de_bits[j] != 1 ) {
                    nroQuadros++;
                    j++;
                }
                if (nroQuadros >= tamanhoProcesso) {
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

    public int get_contador_id()
    {
        return ( this.contador_de_id );
    }

    public void set_contador_id(int contador_novo_valor)
    {
        this.contador_de_id = contador_novo_valor;
    }

    public void icrementar_contador_id()
    {
        this.set_contador_id( this.get_contador_id() + 1 );
    }

    public int get_total_quadros_para_gerenciar()
    {
        return ( this.total_quadros_para_gerenciar );
    }

    public void set_total_quadros_para_gerenciar(int total_quadros_para_gerenciar_novo_valor)
    {
        this.total_quadros_para_gerenciar = total_quadros_para_gerenciar_novo_valor;
    }

    public void inicializar_mapa_de_bits(int tamanho_mapa_de_bits)
    {
        this.mapa_de_bits = new int[tamanho_mapa_de_bits];
        for (int i = 0; i < this.mapa_de_bits.length; i++) 
        {
            this.mapa_de_bits[i] = 0;
        }
    }

    public int get_id_inicial()
    {
        return this.id_inicial;
    }

    public HashMap get_lista_tabela_de_paginas()
    {
        return this.lista_tabela_de_paginas;
    }

    public void set_nova_tabela_de_paginas(int id_tabela_paginas, TabelaDePaginas tabela_de_pagina_nova)
    {
        this.lista_tabela_de_paginas.put(id_tabela_paginas, tabela_de_pagina_nova);
    }

    public void set_novo_projeto(int id_novo_projeto, String nome_novo_projeto)
    {
        this.lista_de_projetos.put(id_novo_projeto, nome_novo_projeto);
    }
    
}