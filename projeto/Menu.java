package projeto;

import java.util.Scanner;
import java.util.InputMismatchException;

public class Menu {
    public static void main(String[] args) {

        Scanner scMenu = new Scanner (System.in);
        int opcao = 0;
        int processId = 0;
        int logicalAddress = 0;
        int size = 0;
        int auxInt = 0;
        String auxStr = "";
        String processName = "";

        MemoryManager mm = new MemoryManager(64);

        do {
            try {
                System.out.print("\n##--------------------------------------------Interface de Gerenciamento--------------------------------------------##\n\n");
                System.out.print("|--------------------------------------------------------------------------------------------------------------------|\n");
                System.out.print("| Opção 1 - Carregar um processo para a memoria de acordo com a especificacao definida em arquivo.                   |\n");
                System.out.print("| Opção 2 - Alocar memoria dinamica (heap) para um processo virtual carregado na memoria principal.                  |\n");
                System.out.print("| Opção 3 - Liberar um bloco de memoria dinâmica (heap) ocupado por um processo.                                     |\n");
                System.out.print("| Opção 4 - Excluir um processo da memoria, liberando toda a memoria utilizada por esse processo de forma exclusiva. |\n");
                System.out.print("| Opção 5 - Excluir todos os processo da memoria, liberando toda a memoria utilizada por esses processos.            |\n");
                System.out.print("| Opção 6 - Traduzir um endereco logico de um processo para um endereco fisico.                                      |\n");
                System.out.print("| Opção 7 - Obter o mapa de bits dos quadros da memoria.                                                             |\n");
                System.out.print("| Opção 8 - Obter a tabela de paginas de um processo.                                                                |\n");
                System.out.print("| Opção 9 - Obter informações sobre os processos carregados na memoria (nome e identificadores).                     |\n");
                System.out.print("| Opção 10 - Sair                                                                                                    |\n");
                System.out.print("| Opção 11 - Créditos/Informações adicionais sobre o trabalho                                                        |\n");
                System.out.print("|--------------------------------------------------------------------------------------------------------------------|\n");

                System.out.print("\nDigite uma opção: \n");
                opcao = scMenu.nextInt();

                switch (opcao) {
                    case 1:
                        System.out.print("\n|---------------------|\n");
                        System.out.print("| Opção 1 Selecionada |");
                        System.out.print("\n|---------------------|\n");
                        System.out.print("\nDigite o nome do arquivo contendo os dados do processo: \n");

                        processName = scMenu.next();
                        auxInt = mm.loadProcessToMemory(processName);
                        
                        System.out.print("Identificador do processo carregado na memoria: " + auxInt + "\n");
                        
                        
                        break;

                    case 2:
                        System.out.print("\n|---------------------|\n");
                        System.out.print("| Opção 2 Selecionada |");
                        System.out.print("\n|---------------------|\n");

                        System.out.print("\nDigite o identificador do processo: \n");
                        processId = scMenu.nextInt();

                        System.out.print("\nDigite o tamanho do bloco de memoria a ser alocado: \n");
                        size = scMenu.nextInt();

                        auxInt = mm.allocateMemoryToProcess(processId, size);
                        
                        System.out.print("Quantidade de memoria alocada: " + auxInt + "\n");
                        

                        break;

                    case 3:
                        System.out.print("\n|---------------------|\n");
                        System.out.print("| Opção 3 Selecionada |");
                        System.out.print("\n|---------------------|\n");

                        System.out.print("\nDigite o identificador do processo: \n");
                        processId = scMenu.nextInt();

                        System.out.print("\nDigite o tamanho do bloco de memoria a ser liberado: \n");
                        size = scMenu.nextInt();

                        auxInt = mm.freeMemoryFromProcess(processId, size);
                        
                        System.out.print("Quantidade de memoria liberada: " + auxInt + "\n");
                        
                        break;

                    case 4:
                        System.out.print("\n|---------------------|\n");
                        System.out.print("| Opção 4 Selecionada |");
                        System.out.print("\n|---------------------|\n");

                        System.out.print("\nDigite o identificador do processo: \n");
                        processId = scMenu.nextInt();
                        mm.excludeProcessFromMemory(processId);
                        break;

                    case 5:
                        System.out.print("\n|-----------------------------------------------------------------------------------|\n");
                        System.out.print("|                                Opção 5 Selecionada                                |\n");
                        System.out.print("|                                                                                   |\n");
                        System.out.print("| Você tem certeza que deseja continuar com esta operação ?                         |\n");
                        System.out.print("|                                                                                   |\n");
                        System.out.print("| Caso você continue todos os processos serão excluidos permanentemente da memória ! |\n");
                        System.out.print("|-----------------------------------------------------------------------------------|\n");

                        System.out.print("Digite 's' para continuar a operação: \n");
                        auxStr = scMenu.next();
                        
                        mm.resetMemory();
                        
                        break;

                    case 6:
                        System.out.print("\n|---------------------|\n");
                        System.out.print("| Opção 6 Selecionada |");
                        System.out.print("\n|---------------------|\n");

                        System.out.print("\nDigite o identificador do processo: \n");
                        processId = scMenu.nextInt();

                        System.out.print("\nDigite o endereco logico do processo (entre 0 e 1023): \n");
                        logicalAddress = scMenu.nextInt();

                        auxInt = mm.getPhysicalAddress(processId, logicalAddress);
                        
                        System.out.print("Endereco fisico correspondente: " + auxInt + "\n");
                        
                        break;

                    case 7:
                        System.out.print("\n|---------------------|\n");
                        System.out.print("| Opção 7 Selecionada |");
                        System.out.print("\n|---------------------|\n");

                        auxStr = mm.getBitMap();
                        
                        System.out.print("Mapa de bits da memoria: \n" + auxStr + "\n");
                        
                        break;

                    case 8:
                        System.out.print("\n|---------------------|\n");
                        System.out.print("| Opção 8 Selecionada |");
                        System.out.print("\n|---------------------|\n");

                        System.out.print("\nDigite o identificador do processo: \n");
                        processId = scMenu.nextInt();

                        auxStr = mm.getPageTable(processId);
                        
                        System.out.print("Tabela de paginas do processo:\n" + auxStr + "\n");
                        

                        break;

                    case 9:
                        System.out.print("\n|---------------------|\n");
                        System.out.print("| Opção 9 Selecionada |");
                        System.out.print("\n|---------------------|\n");
                        
                        System.out.print("Lista de processos carregados na memoria:\n" + mm.getProcessList() + "\n");
                        
                        break;

                    case 10:
                        System.out.print("\n|-----------------------------------------------------|\n");
                        System.out.print("| Obrigado por usar nossa interface de gerenciamento! |");
                        System.out.print("\n|-----------------------------------------------------|\n");
                        scMenu.close();
                        break;

                    case 11:
                        System.out.print("\n##----------------------------------Créditos/Informações adicionais sobre o trabalho--------------------------------##\n\n");
                        System.out.print("|--------------------------------------------------------------------------------------------------------------------|\n");
                        System.out.print("|                      USP - Faculdade de Filosofia Ciências e Letras de Ribeirão Preto (FFCLRP)                     |\n");
                        System.out.print("|                                                                                                                    |\n");
                        System.out.print("|                                               Trabalho: Memory Manager                                             |\n");
                        System.out.print("|                                                                                                                    |\n");
                        System.out.print("| Disciplina: Sistemas Operacionais                                                                                  |\n");
                        System.out.print("| Docente: Clever Ricardo Guareis de Farias                                                                          |\n");
                        System.out.print("|                                                                                                                    |\n");
                        System.out.print("| Autores:                                                                                                           |\n");
                        System.out.print("|     Gabriel Bernardes Ribeiro                                                                                      |\n");
                        System.out.print("|     Luiz Felipe Triques                                                                                            |\n");
                        System.out.print("|     Mateus Miquelino da Silva                                                                                      |\n");
                        System.out.print("|     Yuri Schwab                                                                                                    |\n");
                        System.out.print("|                                                                                                                    |\n");
                        System.out.print("| Data: 30/07/2021                                                                                                   |\n");
                        System.out.print("|--------------------------------------------------------------------------------------------------------------------|\n");
                        break;
                        
                    default:
                        System.out.print("\n|---------------------------------|\n");
                        System.out.print("| Opção Inválida! Tente novamente |");
                        System.out.print("\n|---------------------------------|\n");
                        break;
                }

            } catch(InputMismatchException e) {
                System.out.print("\n|---------------------------------|\n");
                System.out.print("| Opção Inválida! Tente novamente |");
                System.out.print("\n|---------------------------------|\n");
                scMenu.nextLine();
                continue;
            }

        } while(opcao != 10);
    }
}