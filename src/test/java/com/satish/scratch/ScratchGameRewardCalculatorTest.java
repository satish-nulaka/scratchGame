package com.satish.scratch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satish.scratch.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ScratchGameRewardCalculatorTest {

    private Config config;
    private List<List<String>> matrix;
    private double bettingAmount;

    @BeforeEach
    public void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("config.json");
        config = mapper.readValue(is, Config.class);



        bettingAmount = 100.0;
    }

    @Test
    public void testRewardWithSameSymbolAndBonusMultiplier() {
        matrix = List.of(
                List.of("A", "A", "A"),
                List.of("B", "5x", "B"),
                List.of("B", "C", "D")
        );
        ScratchGameRewardCalculator calculator = new ScratchGameRewardCalculator(config, matrix, bettingAmount);
        GameResult result = calculator.calculateGameResult();

        assertNotNull(result);
        assertNotNull(result.getAppliedWinningCombinations());
        assertTrue(result.getAppliedWinningCombinations().containsKey("A"));
        assertEquals("5x", result.getAppliedBonusSymbol());


        double expectedReward = 6500.0;
        assertEquals(expectedReward, result.getReward());
    }

    @Test
    public void testRewardWithSameSymbolAndMISSBonusMultiplier() {
        matrix = List.of(
                List.of("A", "A", "A"),
                List.of("B", "MISS", "B"),
                List.of("B", "C", "D")
        );
        ScratchGameRewardCalculator calculator = new ScratchGameRewardCalculator(config, matrix, bettingAmount);
        GameResult result = calculator.calculateGameResult();

        assertNotNull(result);
        assertNotNull(result.getAppliedWinningCombinations());
        assertTrue(result.getAppliedWinningCombinations().containsKey("A"));
        assertEquals("MISS", result.getAppliedBonusSymbol());


        double expectedReward = 1300.0;
        assertEquals(expectedReward, result.getReward());
    }

    @Test
    public void testRewardWithSameSymbolAndNoBonusMultiplier() {
        matrix = List.of(
                List.of("A", "A", "A"),
                List.of("B", "E", "B"),
                List.of("B", "C", "D")
        );
        ScratchGameRewardCalculator calculator = new ScratchGameRewardCalculator(config, matrix, bettingAmount);
        GameResult result = calculator.calculateGameResult();

        assertNotNull(result);
        assertNotNull(result.getAppliedWinningCombinations());
        assertTrue(result.getAppliedWinningCombinations().containsKey("A"));
        assertNull(result.getAppliedBonusSymbol());


        double expectedReward = 1300.0;
        assertEquals(expectedReward, result.getReward());
    }

    @Test
    public void testRewardWithSameSymbolAndExtraBonus() throws JsonProcessingException {
        matrix = List.of(
                List.of("A", "A", "A"),
                List.of("B", "E", "B"),
                List.of("B", "+1000", "D")
        );
        ScratchGameRewardCalculator calculator = new ScratchGameRewardCalculator(config, matrix, bettingAmount);
        GameResult result = calculator.calculateGameResult();

        assertNotNull(result);
        assertNotNull(result.getAppliedWinningCombinations());
        assertTrue(result.getAppliedWinningCombinations().containsKey("A"));
        assertTrue(result.getAppliedWinningCombinations().containsKey("B"));
        assertEquals("+1000",result.getAppliedBonusSymbol());


        double expectedReward = 2300.0;
        assertEquals(expectedReward, result.getReward());
    }

    @Test
    public void testRewardWithDifferentSymbolAndNoBonusMultiplier() throws JsonProcessingException {
        matrix = List.of(
                List.of("A", "A", "C"),
                List.of("D", "E", "B"),
                List.of("B", "C", "D")
        );
        ScratchGameRewardCalculator calculator = new ScratchGameRewardCalculator(config, matrix, bettingAmount);
        GameResult result = calculator.calculateGameResult();

        assertNotNull(result);
        assertNotNull(result.getAppliedWinningCombinations());
        assertFalse(result.getAppliedWinningCombinations().containsKey("A"));
        assertNull(result.getAppliedBonusSymbol());


        double expectedReward = 0.0;
        assertEquals(expectedReward, result.getReward());
    }

    @Test
    public void testRewardWithLinearSymbolAndNoBonusMultiplier() {

        matrix = List.of(
                List.of("A", "D", "C"),
                List.of("D", "A", "B"),
                List.of("B", "C", "A")
        );
        ScratchGameRewardCalculator calculator = new ScratchGameRewardCalculator(config, matrix, bettingAmount);
        GameResult result = calculator.calculateGameResult();

        assertNotNull(result);
        assertNotNull(result.getAppliedWinningCombinations());
        assertTrue(result.getAppliedWinningCombinations().containsKey("A"));
        assertNull(result.getAppliedBonusSymbol());


        double expectedReward = 2500.0;
        assertEquals(expectedReward, result.getReward());
    }

}
