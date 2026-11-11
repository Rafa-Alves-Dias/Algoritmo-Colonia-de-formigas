
import static java.lang.Math.pow;
import static java.lang.System.arraycopy;
import static java.lang.System.out;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import classes.Item;


public class MochilaFormigas {
    private static final int NUM_iTENS = 6;
    private static final int NUM_FORMIGAS = 3;
    private static final int LIMITE_MOCHILA = 15;
    private static final int VIAGENS = 6;

    private static final double ALPHA = 3; // peso do feromonio
    private static final double BETA = 2; // peso da distancia
    private static final double REDUCAO_FEROMONIO = 0.01; // 10% de perda de feromonio
    private static final double AUMENTO_FEROMONIO = 2.0; // 200% de ganho de feromonio
    private static double[][] arestas; //valor por peso Ex: real/kilo
    private static final Item[] itens = new Item[NUM_iTENS];
    private static int[][] formigas;
    private static double[][] feromonios;
    private static int[] melhorRota;
    private static double melhorValor;
    private static final Random random = new Random(System.currentTimeMillis());
    private static Map<Integer, Double> resultados;
    
    private static int pesoTotalRota;
    private static double valorTotalRota;
    private static double[][] dadosItens = new double[NUM_FORMIGAS][2];
    private static int k = 0;
    
    private MochilaFormigas() {}

    public static void main(String[] args) {
        criaItens();
        criaArestas();
        imprimeTabelaArestas();
        distribuiFormigas();
        inicializaFeromonios();
        for (int i = 0; i < VIAGENS; i++) {
            out.printf("\n" + "Início da viagem %d\n\n", i + 1);
            if (i == 0) {
                atualizaRotasFormigas();
            }
            atualizaFeromonios();
            atualizaRotasFormigas();
            melhorRota();
            imprimeRotas();
            out.println();
            imprimeMelhorRota();
            out.println();
            imprimeTabelaFeromonios();
            out.printf("\n" + "Fim da viagem %d\n\n", i);
            armazenaResultados(i);
        }
    }

    private static void criaItens(){
        Item item;
        for (int i = 0; i < NUM_iTENS; i++) {
            //item = new Item(LIMITE_MOCHILA + 1);
            item = new Item();
            int peso = random.nextInt(LIMITE_MOCHILA) + 1;
            double valor = random.nextDouble() * 100;
            item.setPeso(peso);
            item.setValor(valor);
            out.println("\n item " + i + ":" 
                    + "\n" + "peso: " + item.getPeso() 
                    + "\n" + "valor: " + item.getValor() );
            itens[i] = item;
        }
    }
    
    /**
     * Cria randomicamente as distancias entre as cidades
     * 
     * Complexidade: O(n^2)
     */
    
    private static void criaArestas() {
        arestas = new double[NUM_iTENS][NUM_iTENS];
        for (int i = 0; i < NUM_iTENS; i++) {
            for (int j = i; j < NUM_iTENS; j++) {
                if (i == j) {
                    arestas[i][j] = 0;
                }else{
                    arestas[i][j] = itens[j].aresta(); //recebe o valor/peso do item de destino
                    arestas[j][i] = itens[i].aresta(); //recebe o valor/peso do item de destino
                }
            }
        }
    }

    /**
     * Cria rotas iniciais aleatórias para cada formiga
     * 
     * Complexidade: 2.O(n^2)
     */
    
    private static void distribuiFormigas() {
        formigas = new int[NUM_FORMIGAS][NUM_iTENS];
        for (int i = 0; i < NUM_FORMIGAS; i++) {
            for (int j = 0; j < NUM_iTENS; j++) {
                formigas[i][j] = j;
            }
        }
        for (int i = 0; i < NUM_FORMIGAS; i++) {
            for (int j = 0; j < NUM_iTENS; j++) {
                int dest = random.nextInt(NUM_iTENS);
                if (dest != j) {
                    int aux = formigas[i][j];
                    formigas[i][j] = formigas[i][dest];
                    formigas[i][dest] = aux;
                }
            }
        }
    }

    /**
     * Calcula a distância de uma rota
     * 
     * Complexidade: O(n)
     */
       
    private static double distanciaPercorrida(int[] itens) {
            double ret = 0;
            int i=0;
            if(itens[i] == 0){
                ret = arestas[1][itens[i]];
            }else{
                ret = arestas[0][itens[i]];
            }
            for (i = 1; i < itens.length - 1; i++) {
                if(itens[i + 1] == -1){
                    break;
                }
                ret += arestas[itens[i]][itens[i + 1]];
            }
            return ret;
        }

    /**
     * Atualiza a melhor rota avaliando a distância percorrida pelas formigas
     * 
     * Complexidade: O(n)
     */
    
    private static void melhorRota() {
        for (int i = 0; i < NUM_FORMIGAS; i++) {
            double somaV = somaValores(formigas[i]);
            // double somaV = dadosItens[i][0];
            if (melhorValor == 0 || somaV > melhorValor) {
                if (melhorRota == null) {
                    melhorRota = new int[NUM_iTENS];
                }
                melhorValor = somaV;
                arraycopy(formigas[i], 0, melhorRota, 0, formigas[i].length);
            }
        }
    }
    private static double somaValores(int[] rotaItens){
        double somaValor = 0;
        Item aux;
        int index;
        for(int i = 0; i < NUM_iTENS; i++){
            index = rotaItens[i];
            if(index == -1){
                break;
            }
            aux = itens[index];
            somaValor += aux.getValor();
        }
        return somaValor;
    }
    private static Item melhoresDados_MelhorRota(){
        int index;
        Item aux;
        Item melhoresDados = new Item();
        melhoresDados.setPeso(0);
        melhoresDados.setValor(0);
        for(int i=0; i < NUM_iTENS; i ++){
            if(melhorRota[i] == -1){
                break;
            }
            index = melhorRota[i];
            aux = itens[index];
            melhoresDados.setPeso(melhoresDados.getPeso() + aux.getPeso());
            melhoresDados.setValor(melhoresDados.getValor() + aux.getValor());
        }
        return melhoresDados;
    }
    
    /**
     * Inicializa a quantidade de feromonios nos trajetos
     * 
     * Complexidade: O(n^2)
     */
    
    private static void inicializaFeromonios() {
        feromonios = new double[NUM_iTENS][NUM_iTENS];
        for (int i = 0; i < NUM_iTENS; i++) {
            for (int j = 0; j < NUM_iTENS; j++) {
                feromonios[i][j] = 0.01;
            }
        }
    }

    /**
     * Inicia uma nova viagem das formigas
     *
     * Complexidade: O(n) + complexidade da fun��o viagemFormiga
     */
    private static void atualizaRotasFormigas() {
        reinicializaGlobais();
        for (int i = 0; i < NUM_FORMIGAS; i++) {
            // cidade atual da formiga
            // int cidade = formigas[i][numCidades - 1];
            int posicaoItem = random.nextInt(NUM_iTENS);
            formigas[i] = viagemFormiga(posicaoItem);
        }
    }
    
    private static void reinicializaGlobais(){
        pesoTotalRota = 0;
        valorTotalRota = 0;
        k = 0;
        dadosItens = new double[NUM_FORMIGAS][2];
    }
    
    /**
     * Cria uma rota a partir de uma cidade inicial
     * 
     * Complexidade: O(n) + complexidade da função proximaCidade
     */
    
    private static int[] viagemFormiga(int inicio) {
        boolean[] visitados = new boolean[NUM_iTENS];
        int[] novaRota = new int[NUM_iTENS];
        int origem;
        int destino;
        int i;
        pesoTotalRota = itens[inicio].getPeso();
        valorTotalRota = itens[inicio].getValor();
        novaRota[0] = inicio;
        visitados[inicio] = true;
        for (i = 0; i < NUM_iTENS - 1; i++){
            origem = novaRota[i];
            destino = proximoItem(origem, visitados);
            if(destino == -1 || !cabeNaMochila(destino)){
                break;
            }
            novaRota[i + 1] = destino;
            pesoTotalRota += itens[destino].getPeso();
            valorTotalRota += itens[destino].getValor();
            out.println("proximo item: " + destino);
            out.println("peso proximo item: " + itens[destino].getPeso());
            out.println("peso total: " + pesoTotalRota);
            visitados[destino] = true;
        }
        for(; i < NUM_iTENS - 1; i++){
            novaRota[i + 1] = -1;
        }
        guardaPesosEValores();
        return novaRota;
    }
    
    private static void guardaPesosEValores(){
        dadosItens[k][0] = valorTotalRota;
        dadosItens[k][1] = pesoTotalRota;
        k++;
    }
    
    /**
     * Define a proxima cidade a ser visitada levando em consideração os
     * feromonios e a distância do trajeto
     * 
     * Complexidade: O(n^3) + O(n/2)
     */
    
    private static int proximoItem(int origem, boolean[] visitados) {
        double[] aux = new double[NUM_iTENS];
        double soma = 0.0;
        for (int i = 0; i < NUM_iTENS; i++) {
            if (i != origem && !visitados[i]) {
                aux[i] = pow(feromonios[origem][i], ALPHA) * pow(0.1 / arestas[origem][i], BETA);
                soma += aux[i];
            }
        }
        for (int i = 0; i < NUM_iTENS; i++) {
            aux[i] /= soma;
        }
        
        double[] acum = new double[NUM_iTENS + 1];
        for (int i = 0; i < NUM_iTENS; i++) {
            acum[i + 1] = acum[i] + aux[i];
        }
        double p = random.nextDouble();

        for (int i = 0; i < acum.length - 1; ++i) {
            if (p >= acum[i] && p < acum[i + 1]) {
                return i;
            }
        }
        return -1;
    }
    
    private static boolean cabeNaMochila(int proximo_item){
         Item item = itens[proximo_item];
         int peso = item.getPeso();
         boolean resposta;
         resposta = peso <= LIMITE_MOCHILA - pesoTotalRota;
         out.println("item "+ proximo_item + " de peso: " + peso + " cabe na mochila? (espaco livre:" + (LIMITE_MOCHILA - pesoTotalRota) + "): " + resposta);
         return resposta;
    }
    
    /**
     * Atualiza a tabela de feromonios
     * 
     * Complexidade: O(n^2) * O(m) * complexidade da função distanciaPercorrida *
     *               complexidade da função formigaFezCaminho
     */
    
    private static void atualizaFeromonios() {
        for (int i = 0; i < NUM_iTENS; i++) {
            for (int j = 0; j < NUM_iTENS; j++) {
                if (j == i) {
                    continue;
                }
                for (int f = 0; f < NUM_FORMIGAS; f++) {
                    double dist = distanciaPercorrida(formigas[f]);
                    double reducao = feromonios[i][j] * (1 - REDUCAO_FEROMONIO);
                    double aumento = 0.0;
                    if (formigaFezCaminho(f, i, j)) {
                        aumento = AUMENTO_FEROMONIO / dist;
                    }
                    feromonios[i][j] = reducao + aumento;
                    if (feromonios[i][j] < 0.0001) {
                        feromonios[i][j] = 0.0001;
                    }
                }
            }
        }
    }

    /**
     * Verfica se a formiga fez o trajeto da cidade origem a destino
     * 
     * Complexidade: O(n)
     */
    
    private static boolean formigaFezCaminho(int formiga, int origem, int destino) {
        if (origem == destino) {
            return false;
        }
        for (int i = 0; i < NUM_iTENS; i++) {
            if (origem == formigas[formiga][i]) {
                if (i == 0 && formigas[formiga][i + 1] == destino) {
                    return true;
                } else if (i == NUM_iTENS - 1 && formigas[formiga][i - 1] == destino) {
                    return true;
                } else if (i > 0 && i < NUM_iTENS - 1) {
                    if (formigas[formiga][i - 1] == destino || formigas[formiga][i + 1] == destino) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Armazena o melhores caminhos para desenho do grafico
     */
    
    private static void armazenaResultados(int numViagem) {
        if (resultados == null) {
            resultados = new LinkedHashMap<>();
        }
        resultados.put(numViagem + 1, distanciaPercorrida(melhorRota));
    }

    private static void imprimeTabelaArestas() {
        out.printf("\nArestas (valor/peso) :\n");
        out.printf("  |");
        for (int i = 0; i < NUM_iTENS; i++) {
            out.printf("%7d|", i);
        }
        out.printf("\n--+");
        for (int i = 0; i < NUM_iTENS; i++) {
            out.printf("-------+", i);
        }
        out.printf("\n");
        for (int i = 0; i < NUM_iTENS; i++) {
            out.printf("%1$2d|", i);
            for (int j = 0; j < NUM_iTENS; j++) {
                if(arestas[i][j] < 10)
                    out.printf("0" + "%1$1.4f|", arestas[i][j] );
                else
                    out.printf("%1$1.4f|",arestas[i][j] );
            }
            out.printf("\n");
        }
        out.printf("--+");
        for (int i = 0; i < NUM_iTENS; i++) {
            out.printf("-------+", i);
        }
        out.printf("\n");
    }

    private static void imprimeTabelaFeromonios() {
        out.printf("Feromonios:\n");
        out.printf("  |");
        for (int i = 0; i < NUM_iTENS; i++) {
            out.printf("%1$6d|", i);
        }
        out.printf("\n--+");
        for (int i = 0; i < NUM_iTENS; i++) {
            out.printf("------+", i);
        }
        out.printf("\n");
        for (int i = 0; i < NUM_iTENS; i++) {
            out.printf("%1$2d|", i);
            for (int j = 0; j < NUM_iTENS; j++) {
                out.printf("%1$1.4f|", feromonios[i][j]);
            }
            out.printf("\n");
        }
        out.printf("--+");
        for (int i = 0; i < NUM_iTENS; i++) {
            out.printf("------+", i);
        }
        out.printf("\n");
    }

    private static void imprimeRotas() {
        for (int i = 0; i < NUM_FORMIGAS; i++) {
            out.printf("Formiga %2d fez a rota: %s de distancia %f, valor total: %f, peso total: %d \n", i, Arrays.toString(formigas[i]),
                    distanciaPercorrida(formigas[i]), dadosItens[i][0], (int) dadosItens[i][1]);
        }
    }

    private static void imprimeMelhorRota() {
        Item melhoresDados = melhoresDados_MelhorRota();
        out.printf("melhor rota: %s de valor %f, peso: %d\n", Arrays.toString(melhorRota), melhoresDados.getValor(), melhoresDados.getPeso() );
    }

}