package com.satish.scratch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.satish.scratch.domain.Config;
import com.satish.scratch.domain.GameResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Scratch {
    public static void main(String[] args) throws IOException {

        String filePath = null;
        double bettingAmount = 0;

        if (args.length > 4) {
            System.err.println("Unknown arguments \n Usage: java -jar your-jar-file.jar --config config.json --betting-amount 100");
            return;
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--config":
                    if (i + 1 < args.length) {
                        filePath = args[++i];
                    } else {
                        System.err.println("Missing value for --config");
                        return;
                    }
                    break;
                case "--betting-amount":
                    if (i + 1 < args.length) {
                        try {
                            bettingAmount = Double.parseDouble(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number for --betting-amount");
                            return;
                        }
                    } else {
                        System.err.println("Missing value for --betting-amount");
                        return;
                    }
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
                    return;
            }
        }

        if (bettingAmount <=0 || filePath == null) {
            System.err.println("Unexpected arguments \n Usage: java -jar your-jar-file.jar --config config.json --betting-amount 100");
            return;
        }

        try{


            ObjectMapper objectMapper = new ObjectMapper();
            Config config = objectMapper.readValue(new File(filePath), Config.class);
            ScratchGameInputCreater scratchGameInputCreater = new ScratchGameInputCreater(config);
            List<List<String>>  inputMatrix= scratchGameInputCreater.initializeMatrix();

            ScratchGameRewardCalculator scratchGameRewardCalculator
                    = new ScratchGameRewardCalculator(config, inputMatrix , bettingAmount);
            GameResult gameResult = scratchGameRewardCalculator.calculateGameResult();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gameResult);
            System.out.println(json);
        } catch (FileNotFoundException notFoundException) {
            System.err.println("Provided file path is not correct please provide a vaild file path ");
        } catch (Exception exception) {
            System.err.println("Something went wrong please try again");
        }



    }

}
