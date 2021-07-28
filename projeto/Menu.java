package projeto;

import java.util.Scanner;
import java.util.InputMismatchException;

public class Menu {

    public static void main(String[] args) {

        Scanner scannerMenu = new Scanner (System.in);
        String[] listaDeComandos;
        int resultadoInt;
        String resultadoString;
        String nomeDoProcesso;
        int tamanhoDaMemoria = 0;
        int idDoProcesso;
        int enderecoLogico;
        int tamanho;
        MemoryManager memoryManager;

        while(true) {
            try {
                System.out.println("Digite o tamanho de memoria que deseja utilizar:'32, 64 ou 128'");
                tamanhoDaMemoria = scannerMenu.nextInt();
            } catch (Exception e) {
                System.out.println("\nOpção invalida...\n");
                scannerMenu.nextLine();
            }
            if (tamanhoDaMemoria == 32 || tamanhoDaMemoria == 64 || tamanhoDaMemoria == 128){
                memoryManager = new MemoryManager(tamanhoDaMemoria);
                scannerMenu.nextLine();
                break;
            }
        }
        
        System.out.println("\n##---Interface de Gerenciamento---##\n\n");
        while(true) {
            System.out.println("\nDigite o comando:");
            listaDeComandos = scannerMenu.nextLine().split(" ");

            try {
                switch (listaDeComandos[0]) {
                    case "loadProcessToMemory":
                        nomeDoProcesso = listaDeComandos[1];
                        resultadoInt = memoryManager.loadProcessToMemory(nomeDoProcesso);
                        System.out.print("Identificador do processo carregado na memoria: " + resultadoInt + "\n");
                        break;
                    
                    case "allocateMemoryToProcess":
                        idDoProcesso = Integer.parseInt(listaDeComandos[1]);
                        tamanho = Integer.parseInt(listaDeComandos[2]);
                        resultadoInt = memoryManager.allocateMemoryToProcess(idDoProcesso, tamanho);
                        System.out.print("Quantidade de memoria alocada: " + resultadoInt + "\n");
                        break;
                    
                    case "freeMemoryFromProcess":
                        idDoProcesso = Integer.parseInt(listaDeComandos[1]);
                        tamanho = Integer.parseInt(listaDeComandos[2]);
                        resultadoInt = memoryManager.freeMemoryFromProcess(idDoProcesso, tamanho);
                        System.out.print("Quantidade de memoria liberada: " + resultadoInt + "\n");
                        break;
    
                    case "excludeProcessFromMemory":
                        idDoProcesso = Integer.parseInt(listaDeComandos[1]);
                        memoryManager.excludeProcessFromMemory(idDoProcesso);
                        break;
                    
                    case "resetMemory":
                        memoryManager.resetMemory();
                        break;
                    
                    case "getPhysicalAddress":
                        idDoProcesso = Integer.parseInt(listaDeComandos[1]);
                        enderecoLogico = Integer.parseInt(listaDeComandos[2]);
                        resultadoInt = memoryManager.getPhysicalAddress(idDoProcesso, enderecoLogico);
                        System.out.print("Endereco fisico correspondente: " + resultadoInt + "\n");
                        break;
                    
                    case "getBitMap":
                        resultadoString = memoryManager.getBitMap();
                        System.out.print("Mapa de bits da memoria: \n" + resultadoString + "\n");
                        break;
                    
                    case "getPageTable":
                        idDoProcesso = Integer.parseInt(listaDeComandos[1]);
                        resultadoString = memoryManager.getPageTable(idDoProcesso);
                        System.out.print("Tabela de paginas do processo:\n" + resultadoString + "\n");
                        break;
                    
                    case "getProcessList":
                        for (String processo : memoryManager.getProcessList()) {
                            System.out.print(processo + "\n");
                        }
                        break;

                    case "exit":
                        System.out.print("| Obrigado por usar nossa CLI de gerenciamento! |");
                        scannerMenu.close();
                        break;

                    case "creditos":
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
                        System.out.print("\n\n|---Comando Invalido! Tente novamente---|\n\n");
                        break;
                }
            } catch (Exception e) {
                System.out.println("\n\n|---Verifique o comando digitado---|\n\n");
            }

        }

        
    }
}