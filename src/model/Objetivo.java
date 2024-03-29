package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Objetivo {
    int objetivoId;
    String descricao;

    Objetivo(int objetivoId, String descricao) {
        this.objetivoId = objetivoId;
        this.descricao = descricao;
    }

    int getObjetivoId() {
        return objetivoId;
    }

    String getDescricao() {
        return descricao;
    }

    void setObjetivoId(int objetivoId) {
        this.objetivoId = objetivoId;
    }

    void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    static List<Objetivo> criarObjetivos() {
        int[] objetivoId = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

        String[] descricao = {"Destruir todos os exércitos Azuis", "Destruir todos os exércitos Amarelos", "Destruir todos os exércitos Brancos",
                "Destruir todos os exércitos Verdes", "Destruir todos os exércitos Pretos", "Destruir todos os exércitos Vermelhos",
                "Conquistar na totalidade a America do Norte e a Africa", "Conquistar na totalidade a Asia e a Africa",
                "Conquistar na totalidade a America do Norte e a Oceania", "Conquistar na totalidade a Europa, a America do Sul e mais um continente a sua escolha",
                "Conquistar na totalidade a Asia e a America do Sul", "Conquistar na totalidade a Europa e a Oceania e mais um continente a sua escolha",
                "Conquistar 18 territorios com pelo menos 2 exercitos em cada", "Conquistar 24 territorios a sua escolha"};

        List<Objetivo> objetivos = new ArrayList<>();
        for (int i = 0; i < objetivoId.length; i++) {
            objetivos.add(new Objetivo(objetivoId[i], descricao[i]));
        }
        Collections.shuffle(objetivos);
        return objetivos;
    }

    static void mudarObjetivo(Jogador jogador, Objetivo objetivo){
        jogador.objetivo.setObjetivoId(14);
        jogador.objetivo.setDescricao("Conquistar 24 territorios a sua escolha");
    }

    static boolean verificaObjetivo(Objetivo objetivo, Jogador jogador, List<Jogador> jogadores){
        List<String> obj = new ArrayList<>();
        List<String> plyCnt = jogador.getContinentesString();
        List<Integer> idList = new ArrayList<>();
        for(Jogador temp : jogadores) {
        	idList.add(temp.idJogador);
        }
        switch (objetivo.getObjetivoId()){
            case 1: case 2: case 3: case 4: case 5: case 6:
                if(objetivo.getObjetivoId() == jogador.getCorId() || !idList.contains(objetivo.getObjetivoId())) {
                    mudarObjetivo(jogador,objetivo);
                }
                break;
            case 7:
                obj = List.of("America do Norte", "Africa");
                for (String item : obj) {
                    if (!plyCnt.contains(item)) {
                        return false;
                    }
                }
                return true;
            case 8:
                obj = List.of("Asia", "Africa");
                for (String item : obj) {
                    if (!plyCnt.contains(item)) {
                        return false;
                    }
                }
                return true;
            case 9:
                obj = List.of("America do Norte", "Oceania");
                for (String item : obj) {
                    if (!plyCnt.contains(item)) {
                        return false;
                    }
                }
                return true;
            case 10:
                obj = List.of("Europa", "America do Sul");
                for (String item : obj) {
                    if (!plyCnt.contains(item)) {
                        return false;
                    }
                }
                return jogador.getContinentes().size() >= 3;
            case 11:
                obj = List.of("Asia", "America do Sul");
                for (String item : obj) {
                    if (!plyCnt.contains(item)) {
                        return false;
                    }
                }
                return true;
            case 12:
                obj = List.of("Europa", "Oceania");
                for (String item : obj) {
                    if (!plyCnt.contains(item)) {
                        return false;
                    }
                }
                return jogador.getContinentes().size() >= 3;
            case 13:
                if(jogador.getTerritorios().size() < 18) {
                    return false;
                }
                else {
                    int qtd = jogador.getTerritorios().size();
                	for(Territorio territorio : jogador.getTerritorios()){
                    	if(territorio.getQtdExercito() < 2) {
                    		qtd--;
                    	}
                	}
                    return qtd >= 18;
                }
            case 14:
                return jogador.getTerritorios().size() >= 24;
        }
        return false;
    }
}