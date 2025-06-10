package com.satish.scratch;

import com.satish.scratch.domain.Config;
import com.satish.scratch.domain.GameResult;
import com.satish.scratch.domain.Symbol;
import com.satish.scratch.domain.WinCombination;

import java.util.*;
import java.util.stream.Collectors;

public class ScratchGameRewardCalculator {

    private Config scratchGameconfig;

    private List<List<String>> inputMatrix;

    private double bettingAmount;

    private GameResult gameResult;

    public ScratchGameRewardCalculator(Config scratchGameconfig, List<List<String>> inputMatrix,
                                       double bettingAmount) {
        this.inputMatrix = inputMatrix;
        this.scratchGameconfig = scratchGameconfig;
        this.bettingAmount = bettingAmount;
        this.gameResult = new GameResult();
    }
    public GameResult calculateGameResult() {
        gameResult.setMatrix(this.inputMatrix);

        Map<String, WinCombination> linearCombinations = getSameSymbolWinCombinations("linear_symbols");
        Map<String, WinCombination> sameSymbolWinCombinations = getSameSymbolWinCombinations("same_symbols");
        Map<String, Long> groupedRepetativeChars = groupRepetativeChars();
        Map<String, Long> winningCombinations = filterSameSymbolMultipleTimes(groupedRepetativeChars);
        String bonus = filterBonus(groupedRepetativeChars);

        if (winningCombinations != null) {
            gameResult.setAppliedBonusSymbol(bonus);

            Map<String, List<String>> sameSymbolWinningCombination = mapWinningToCombinations(sameSymbolWinCombinations, winningCombinations);
            Map<String, List<String>> linearWinningCombinations = findLinearSymbolMatches(inputMatrix, linearCombinations);

            gameResult.setAppliedWinningCombinations( allWinningCombinations(linearWinningCombinations, sameSymbolWinningCombination));

        }
        gameResult.setReward(calculateReward());

        return gameResult;
    }

    private Double calculateReward() {
        Double totalReward = 0.0;
        Map<String,List<String>> appliedWinningCombinations = gameResult.getAppliedWinningCombinations();
        String bonus = gameResult.getAppliedBonusSymbol();
        for(Map.Entry<String,List<String>> entry: appliedWinningCombinations.entrySet()) {
            Double reward = 0.0;
            String symbol = entry.getKey();
            List<String> combinations = entry.getValue();
            reward = reward+ (bettingAmount * scratchGameconfig.getSymbols().get(symbol).getRewardMultiplier());
            for(String combination: combinations) {
                reward = reward * scratchGameconfig.getWinCombinations().get(combination).getRewardMultiplier();
            }
            totalReward=totalReward+reward;
        }

        if(bonus != null) {

            Symbol bonusSymbol = scratchGameconfig.getSymbols().get(bonus);
            switch (bonusSymbol.getImpact()) {

                case "multiply_reward":
                    totalReward = totalReward * bonusSymbol.getRewardMultiplier();
                    break;
                case "extra_bonus":
                    totalReward = totalReward + bonusSymbol.getExtra();
                    break;
                case "miss":
                    break;

                default:
                    break;

            }
        }
        return totalReward;
    }
    private Map<String, WinCombination> getSameSymbolWinCombinations(String type) {
        return scratchGameconfig.getWinCombinations()
                .entrySet()
                .stream()
                .filter(entry -> type.equals(entry.getValue().getWhen()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    private Map<String, Long> groupRepetativeChars()  {
        return inputMatrix.stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(
                        symbol -> symbol,
                        Collectors.counting()
                ));
    }
    private Map<String, Long> filterSameSymbolMultipleTimes(Map<String, Long> repetativeMap) {

        return repetativeMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= 3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private String filterBonus(Map<String, Long> repetativeMap) {

        Set<String> bonusSymbolsKeys = scratchGameconfig.getProbabilities()
                .getBonusSymbols().getSymbols().keySet();

        return repetativeMap.keySet()
                .stream()
                .filter(symbol -> bonusSymbolsKeys.contains(symbol))
                .findFirst()
                .orElse(null);

    }

    public String areAllSameSymbols(List<List<String>> matrix, List<String> coordinates) {
        if (coordinates.isEmpty()) return null;

        String firstSymbol = null;
        for (String coord : coordinates) {
            String[] parts = coord.split(":");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            String currentSymbol = matrix.get(x).get(y);
            if (firstSymbol == null) {
                firstSymbol = currentSymbol;
            } else if (!firstSymbol.equals(currentSymbol)) {
                return null;
            }
        }
        return firstSymbol;
    }



    public Map<String, List<String>> mapWinningToCombinations(
            Map<String, WinCombination> sameSymbolWinCombinations,
            Map<String, Long> winningCombinations) {
        Map<String, List<String>> result = new HashMap<>();

        for (Map.Entry<String, Long> winEntry : winningCombinations.entrySet()) {
            String symbol = winEntry.getKey();
            long maxCount = winEntry.getValue();
            List<String> matchedCombos = sameSymbolWinCombinations.entrySet().stream()
                    .filter(e -> e.getValue().getCount() <= maxCount)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            result.put(symbol, matchedCombos);
        }

        return result;
    }

    public Map<String, List<String>> findLinearSymbolMatches(
            List<List<String>> matrix,
            Map<String, WinCombination> linearCombinations
    ) {
        Map<String, List<String>> result = new HashMap<>();

        for (Map.Entry<String, WinCombination> entry : linearCombinations.entrySet()) {
            String combinationKey = entry.getKey();
            WinCombination winCombo = entry.getValue();

            List<List<String>> coveredAreas = winCombo.getCoveredAreas();

            for (List<String> areaCoordinates : coveredAreas) {
                String symbol = areAllSameSymbols(matrix, areaCoordinates);
                if (symbol != null) {
                    result.computeIfAbsent(symbol, k -> new ArrayList<>()).add(combinationKey);
                }
            }
        }

        return result;
    }


    private Map<String,List<String>> allWinningCombinations(Map<String,List<String>> linear, Map<String,List<String>> sameSymbol) {

        for (Map.Entry<String, List<String>> entry : sameSymbol.entrySet()) {
            linear.merge(entry.getKey(), entry.getValue(), (list1, list2) -> {
                List<String> merged = new ArrayList<>(list1);
                merged.addAll(list2);
                return merged;
            });
        }
        return linear;
    }


}
