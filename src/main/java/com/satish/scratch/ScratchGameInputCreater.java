package com.satish.scratch;

import com.satish.scratch.domain.Config;
import com.satish.scratch.domain.Probability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;

public class ScratchGameInputCreater {

    private Config scratchGameConfig;

    public ScratchGameInputCreater(Config scratchGameConfig) {
        this.scratchGameConfig = scratchGameConfig;
    }

    public Config getScratchGameConfig() {
        return scratchGameConfig;
    }

    public void setScratchGameConfig(Config scratchGameConfig) {
        this.scratchGameConfig = scratchGameConfig;
    }

    public List<List<String>> initializeMatrix() {
        int rows = scratchGameConfig.getRows();
        int cols = scratchGameConfig.getColumns();
        Random rand = new Random();
        boolean isBonusAvailable = false;

        Map<String, Probability> probabilityMap = new HashMap<>();
        List<Probability> standardProbs = scratchGameConfig.getProbabilities().getStandardSymbols();
        Probability defaultProb = standardProbs.get(0);

        for (Probability p : standardProbs) {
            probabilityMap.put(p.getRow() + ":" + p.getColumn(), p);
        }

        List<List<String>> matrix = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                String key = i + ":" + j;
                Probability prob = probabilityMap.getOrDefault(key, defaultProb);
                String symbol = getSymbolFromProbabilities(prob.getSymbols(), rand);

                if (rand.nextDouble() < 0.1 && !isBonusAvailable) {
                    isBonusAvailable = true;
                    Map<String, Integer> bonusSymbols = scratchGameConfig.getProbabilities().getBonusSymbols().getSymbols();
                    symbol = getSymbolFromProbabilities(bonusSymbols, rand);
                }

                row.add(symbol);
            }
            matrix.add(row);
        }

        return matrix;
    }

    private String getSymbolFromProbabilities(Map<String, Integer> symbolProbs, Random rand) {
        int totalProb = symbolProbs.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = rand.nextInt(totalProb) + 1;
        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : symbolProbs.entrySet()) {
            cumulative += entry.getValue();
            if (randomValue <= cumulative) {
                return entry.getKey();
            }
        }
        return symbolProbs.keySet().iterator().next();
    }
}
