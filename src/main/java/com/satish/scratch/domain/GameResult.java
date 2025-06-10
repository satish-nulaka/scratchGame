package com.satish.scratch.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GameResult {
    private double reward;
    @JsonProperty("applied_winning_combinations")
    private Map<String, List<String>> appliedWinningCombinations;
    @JsonProperty("applied_bonus_symbol")
    private String appliedBonusSymbol;
    private List<List<String>> matrix;

    public double getReward() {
        return reward;
    }
    public void setReward(double reward) {
        this.reward = reward;
    }
    public Map<String, List<String>> getAppliedWinningCombinations() {
        return appliedWinningCombinations;
    }
    public void setAppliedWinningCombinations(Map<String, List<String>> appliedWinningCombinations) {
        this.appliedWinningCombinations = appliedWinningCombinations;
    }
    public String getAppliedBonusSymbol() {
        return appliedBonusSymbol;
    }
    public void setAppliedBonusSymbol(String appliedBonusSymbol) {
        this.appliedBonusSymbol = appliedBonusSymbol;
    }
    public List<List<String>> getMatrix() {
        return matrix;
    }
    public void setMatrix(List<List<String>> matrix) {
        this.matrix = matrix;
    }
}

