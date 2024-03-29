package controller;
import java.io.IOException;
import java.util.List;
import model.ApiModel;
import view.IniciaInterface;
import view.Jogo;
import view.PlayersInfo;
import view.SuperJogador;

public class ControladorJogo implements Observer{
    ApiModel partida;
    IniciaInterface interfaceJogo;
    PlayersInfo playersInfo;
    Jogo telaJogo;

    SuperJogador superJogador;

    boolean fasePosicionamento = false, faseAtaque = false, faseMovimentoAtaque = false, faseMovimento = false;

    int posicionamentoInicial = 0;

    List<String> tempContinentes;
    int tempExercitos;

    Observable obs;
    Object[] dados;
    String tipo;
    boolean hack = false;


    ControladorJogo(){
        // Cria uma instancia do PlayersInfo
        playersInfo = new PlayersInfo();

        //Inicia Interface
        interfaceJogo = new IniciaInterface(playersInfo);

        // Cria uma instancia do jogo
        partida = ApiModel.getInstance();


        // Adiciona o IniciaJogo como observador do PlayersInfo
        playersInfo.addObserver(this);
        partida.addObserver(this);

        interfaceJogo.f.addObserver(this);



    }

    /*
    public static void main(String[] args) {
        new ControladorJogo();
    }
     */




    /**
     * Método de notificação chamado quando um objeto observável é alterado.
     * Este método é parte central do padrão Observer, permitindo que o objeto ControladorJogo
     * seja notificado de mudanças em objetos observáveis.
     *
     * @param arg O objeto observável que notificou a mudança.
     */
    @Override
    public void notify(Observable arg) {
        // Recebe o objeto observável que notificou a mudança.
        obs = arg;

        // Obtém os dados transmitidos pelo objeto observável.
        dados = (Object []) obs.get();

        // Obtém o tipo de notificação, que determina a natureza da mudança.
        tipo = (String) dados[0];

        // Um switch para lidar com diferentes tipos de notificações.

        switch(tipo){
            // Cada caso no switch corresponde a um tipo específico de notificação.
            // Para cada tipo, um método 'handle' correspondente é chamado para lidar com a mudança específica.
            // Os campos de 'dados' mudam pra cada tipo de notificação

            case "NovoJogo":

                handleNovoJogo((List<String>) dados[1], (List<String>) dados[2]);
                break;
            case "AtualizaExercitos":

                handleAtualizaExercitos((Integer)dados[1]);
                break;

            case "FasePosicionamentoContinente":
                handleFasePosicionamentoContinente((String) dados[1], (String) dados[2]);
                break;

            case "FasePosicionamento":

                handleFasePosicionamento((String) dados[1], (String) dados[2]);
                break;

            case "FaseAtaque":
                handleFaseAtaque((String) dados[1], (String) dados[2]);
                break;

            case "AtualizaTerritorio":
                handleAtualizaTerritorio((String) dados[1], (Integer) dados[2], (Integer) dados[3]);
                break;

            case "FaseMovimento":
                handleFaseMovimento((String) dados[1], (String) dados[2]);
                break;

            case "MudancaDeDono":
                handleMudancaDeDono((String) dados[1]);
                break;

            case "TrocaTurno":

                handleTrocaTurno();
                break;

            case "LancamentoDados":
                handleLancamentoDados((List<Integer>) dados[1], (List<Integer>) dados[2]);
                break;

            case "TrocaFase":
                handleTrocaFase();
                break;

            case "Click":
                handleClick((Integer) dados[1], (Integer) dados[2]);
                break;

            case "SuperJogador":
                handleSuperJogador();
                break;

            case "dadosSuperJogador":
               hack = !hack;
               break;

            case "FimJogo":
                telaJogo.fimJogo((String) dados[1]);
                break;

            case "Salvar":
                handleSaveGame();
                break;

            case "Continuar":
                handleContinueGame();
                break;

            case "Load":
                handleLoadGame();
                break;

            case "Reiniciar":
                handleResetGame();
                break;

            case "Menu":
                handleMenu();
                break;
                
            case "Trocar Cartas":
            	handleTrocaCarta();
            	break;
            
        }
    }

    public void handleTrocaCarta() {
        partida.trocarCartas();
        telaJogo.setExercitos(partida.getExercitosAtuais());
        telaJogo.exibeMao(partida.getCartasJogadorAtual());
        telaJogo.repaint();
    }
    
    private void handleMenu() {
        interfaceJogo.f.dispose();
        playersInfo = new PlayersInfo();
        playersInfo.addObserver(this);
        interfaceJogo = new IniciaInterface(playersInfo);
        interfaceJogo.f.addObserver(this);
    }

    private void handleResetGame() {
        partida.resetGame();
        telaJogo.resetGame();
        handleNovoJogo(partida.getNomesJogadores(), partida.getCoresJogadores());
    }

    private void handleLoadGame() {
        interfaceJogo.f.dispose();
        partida.addObserverToAllTerritories(this);
        partida.addObserverToTabuleiro(this);
        partida.addObserverToDice(this);

        telaJogo = new Jogo();
        telaJogo.addObserver(this);

        superJogador = new SuperJogador();

        superJogador.addObserver(this);

        for (String player : partida.getNomesJogadores()) {
            telaJogo.setCorDono(partida.getTerritoriosPorDono(player), partida.getCoresJogadores().get(partida.getNomesJogadores().indexOf(player)));
            for(String pais : partida.getTerritoriosPorDono(player)){
                telaJogo.setExercitosPais(pais, partida.getExercitosPais(pais));
            }
        }

        fasePosicionamento = true;

        partida.turno(partida.getJogadorAtual());
        telaJogo.atualizaJogadorAtual(partida.getCorJogadorAtual());
        telaJogo.setExercitos(partida.getExercitosAtuais());
        telaJogo.exibeMao(partida.getCartasJogadorAtual());
        telaJogo.setObjetivo(partida.getObjetivoJogadorAtual());
        telaJogo.setFase("Posicionamento");
        telaJogo.repaint();
    }

    private void handleContinueGame() {
        partida.loadGameState();
    }

    private void handleSaveGame(){
        System.out.println("Salvando jogo...");
        partida.saveGameState();
    }

    private void handleSuperJogador() {
        superJogador.alternaVisibilidade();
    }

    private void handleFasePosicionamentoContinente(String pais, String sinal) {
        partida.manipulaExercitos(pais, sinal);
        telaJogo.setExercitos(partida.getExercitosAtuais());
        telaJogo.repaint();
        if(partida.getExercitosAtuais() == 0){
            if(tempContinentes.size() > 1){
                handleTrocaContinente();
            }
            else {
                partida.turno(partida.getJogadorAtual());
                telaJogo.setExercitos(partida.getExercitosAtuais());
                telaJogo.setFase("Posicionamento");
                telaJogo.repaint();
            }
        }
    }

    private void handleTrocaContinente() {
        tempContinentes.remove(0);
        telaJogo.setContinente(tempContinentes.get(0));
        telaJogo.setExercitos(partida.getExercitosContinente(tempContinentes.get(0)));
        partida.setExercitosAtuais(partida.getExercitosContinente(tempContinentes.get(0)));
        telaJogo.repaint();
    }

    /**
     * Gerencia a mudança de dono de um território no jogo.
     * Este método é chamado quando um território muda de dono após um ataque bem-sucedido
     * E o defensor chegou a 0 exércitos.
     *
     * @param nomeTerritorio O nome do território conquistado.
     */
    public void handleMudancaDeDono(String nomeTerritorio) {
        partida.trocaDono(nomeTerritorio);
        partida.verificarObjetivo();
        partida.ganhaCarta();
        telaJogo.setExercitos(Math.min(3,partida.getExercitosAtuais()));
        telaJogo.exibeMao(partida.getCartasJogadorAtual());
        telaJogo.repaint();
    }


    /**
     * Lida com o clique do mouse na interface do jogo.
     * @param x
     * @param y
     */

    private void handleClick(int x, int y) {
        if(fasePosicionamento){
            telaJogo.handlePosicionamentoClick(x,y);
        }
        else if(faseMovimentoAtaque){
            telaJogo.handleMovimentoAtaqueClick(x,y);
        }
        else if(faseAtaque){
            telaJogo.handleAtaqueClick(x,y);
        }
        else if(faseMovimento){
            telaJogo.handleMovimentoClick(x,y);
        }
    }


    /**
     * Lida com a troca de fase no jogo.
     * Resetando os botões e os triângulos.
     */

    private void handleTrocaFase() {
        if(fasePosicionamento){
            fasePosicionamento = false;
            telaJogo.resetTriangulos();
            telaJogo.resetBotoes();
            faseAtaque = true;
            telaJogo.setFase("Ataque");
        }
        else if(faseAtaque){
            faseAtaque = false;
            faseMovimento = true;
            telaJogo.setFase("Movimento");
        }
        else if(faseMovimento){
            faseMovimento = false;
            telaJogo.resetBotoes();
            fasePosicionamento = true;
            handleTrocaTurno();
        }
    }

    private void handleLancamentoDados(List<Integer> ataque, List<Integer> defesa) {
        telaJogo.setDados(ataque, defesa);
        telaJogo.repaint();
    }


    /**
     * Inicializa um novo jogo com as informações dos jogadores.
     * Este método configura o jogo com os nomes e cores dos jogadores,
     * atualiza a interface do jogo e inicia o jogo.
     *
     * @param playerNames Lista de nomes dos jogadores.
     * @param playerColors Lista de cores associadas a cada jogador.
     */
    public void handleNovoJogo(List<String> playerNames, List<String> playerColors) {
        partida.setGame(playerNames, playerColors);
        partida.addObserverToAllTerritories(this);
        partida.addObserverToTabuleiro(this);
        partida.addObserverToDice(this);

        telaJogo = new Jogo();
        telaJogo.addObserver(this);

        superJogador = new SuperJogador();

        superJogador.addObserver(this);

        for (String player : playerNames) {
            telaJogo.setCorDono(partida.getTerritoriosPorDono(player), playerColors.get(playerNames.indexOf(player)));
            posicionamentoInicial ++;
        }

        telaJogo.repaint();
        fasePosicionamento = true;

        partida.turno(partida.getJogadorAtual());
        telaJogo.atualizaJogadorAtual(partida.getCorJogadorAtual());
        telaJogo.setExercitos(partida.getExercitosAtuais());
        telaJogo.exibeMao(partida.getCartasJogadorAtual());
        telaJogo.setObjetivo(partida.getObjetivoJogadorAtual());
        telaJogo.setFase("Posicionamento");
        telaJogo.repaint();
    }



    /**
     * Gerencia a troca de turnos no jogo.
     * Este método é responsável por avançar o jogo para o próximo turno,
     * atualizando o estado do jogo e a interface do usuário de acordo.
     */
    public void handleTrocaTurno(){
        partida.proximoTurno();
        if(!partida.getContinentesAtuais().isEmpty()){
            tempContinentes = partida.getContinentesAtuais();
            telaJogo.atualizaJogadorAtual(partida.getCorJogadorAtual());
            partida.setExercitosAtuais(partida.getExercitosContinente(tempContinentes.get(0)));
            telaJogo.setExercitos(partida.getExercitosAtuais());
            telaJogo.exibeMao(partida.getCartasJogadorAtual());
            telaJogo.setObjetivo(partida.getObjetivoJogadorAtual());
            telaJogo.setFase("Posicionamento Continente");
            telaJogo.setContinente(tempContinentes.get(0));
            telaJogo.repaint();
            return;
        }
        partida.turno(partida.getJogadorAtual());
        telaJogo.atualizaJogadorAtual(partida.getCorJogadorAtual());
        telaJogo.setExercitos(partida.getExercitosAtuais());
        telaJogo.exibeMao(partida.getCartasJogadorAtual());
        telaJogo.setObjetivo(partida.getObjetivoJogadorAtual());
        telaJogo.setFase("Posicionamento");
        telaJogo.repaint();
    }



    /**
     * Atualiza a quantidade de exércitos na interface do jogo
     * Na fase de posicionamento
     *
     *
     * @param qtd A quantidade de exércitos a serem atualizados na interface do jogo.
     */
    public void handleAtualizaExercitos(int qtd) {
        telaJogo.setExercitos(qtd);
        telaJogo.repaint();
    }



    /**
     * Gerencia a fase de posicionamento de exércitos no jogo.
     * Este método é chamado durante a fase de posicionamento e
     * é responsável por manipular os exércitos de acordo com as ações do jogador.
     *
     * @param pais O país onde os exércitos serão posicionados ou removidos.
     * @param sinal Indica se os exércitos serão adicionados ('+') ou removidos ('-').
     */
    public void handleFasePosicionamento(String pais, String sinal) {
        partida.manipulaExercitos(pais, sinal);

        telaJogo.setExercitos(partida.getExercitosAtuais());
        telaJogo.repaint();

        if(partida.getExercitosAtuais() == 0){
            if(posicionamentoInicial > 0){
                posicionamentoInicial --;
                telaJogo.resetTriangulos();
                handleTrocaTurno();
            }
            else {
                handleTrocaFase();
            }
        }
    }



    /**
     * Gerencia a fase de ataque no jogo.
     * Este método é chamado durante a fase de ataque e verifica se o ataque é válido.
     *
     * @param paisAtacante O país que está atacando.
     * @param paisDefensor O país que está sendo atacado.
     */
    public void handleFaseAtaque(String paisAtacante, String paisDefensor) {
        List<String> vizinhos = partida.getVizinhos(paisAtacante);
        paisAtacante = paisAtacante.toLowerCase();
        paisDefensor = paisDefensor.toLowerCase();

        if(hack){
            if(partida.getTerritoriosAtuais().contains(paisAtacante) && vizinhos.contains(paisDefensor) && partida.getExercitosPais(paisAtacante) > 1){
                partida.superJogador(paisAtacante, paisDefensor, superJogador.getDadosAtaque(), superJogador.getDadosDefesa());
            } else if(partida.getTerritoriosAtuais().contains(paisAtacante)){
                telaJogo.mostrarVizinhos(paisAtacante, vizinhos);
            }
            telaJogo.repaint();
            return;
        }

        if(partida.getTerritoriosAtuais().contains(paisAtacante) && vizinhos.contains(paisDefensor) && partida.getExercitosPais(paisAtacante) > 1){
            partida.validaAtaque(paisAtacante, paisDefensor);
        } else if(partida.getTerritoriosAtuais().contains(paisAtacante)){
            telaJogo.mostrarVizinhos(paisAtacante, vizinhos);
        }
        telaJogo.exibeMao(partida.getCartasJogadorAtual());
        telaJogo.repaint();
    }



    /**
     * Atualiza as informações de um território no jogo.
     * Este método é chamado quando um território é atualizado, seja por conquista ou movimentação de tropas.
     *
     * @param nome O nome do território.
     * @param idJogadorDono O ID do jogador que possui o território.
     * @param qtdExercito A quantidade de exércitos no território.
     */
    public void handleAtualizaTerritorio(String nome, int idJogadorDono, int qtdExercito) {
        telaJogo.setPais(nome, idJogadorDono, qtdExercito);
        telaJogo.repaint();
    }



    /**
     * Gerencia a fase de movimento de exércitos entre territórios.
     * Este método é chamado durante a fase de movimentação de exércitos.
     *
     * @param paisOrigem O país de origem dos exércitos.
     * @param paisDestino O país de destino dos exércitos.
     */
    public void handleFaseMovimento(String paisOrigem, String paisDestino) {
        List<String> vizinhos = partida.getVizinhos(paisOrigem);

        if(vizinhos.contains(paisDestino.toLowerCase())){
            partida.movimenta(paisOrigem, paisDestino);
        } else {
            telaJogo.mostrarVizinhos(paisOrigem, vizinhos);
        }
        telaJogo.repaint();
    }




    /**
     * Gerencia a fase de posicionamento de exércitos no jogo.
     * Este método é chamado durante a fase de posicionamento e
     * é responsável por manipular os exércitos de acordo com as ações do jogador.
     *
     * @param pais O país onde os exércitos serão posicionados ou removidos.
     * @param sinal Indica se os exércitos serão adicionados ('+') ou removidos ('-').
     */
    public void handlePassaExercitosVitoria(String pais, String sinal) {
        partida.movimentaVitoria(pais, sinal);

        telaJogo.setExercitos(Math.min(partida.getExercitosPais(pais), partida.getExercitosMovimentadosVitoria(pais)));
        telaJogo.repaint();

        if(partida.getExercitosMovimentadosVitoria(pais) == 3){
            //telaJogo.terminaMovimentacaoAtaque();
        }
    }

}
