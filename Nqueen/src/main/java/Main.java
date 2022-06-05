import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Main {
    private static int fitnessFunction(final Genotype<IntegerGene> board) {
        int fitnessValue = 0;
        for (int k = 1; k < board.chromosome().length(); k++) {
            fitnessValue = fitnessValue + k;
        }
        int fV = 0;
        for (int i = 0; i < board.chromosome().length(); i++) {
            for (int j = i + 1; j < board.chromosome().length(); j++) {
                if (board.chromosome().get(i).allele() == board.chromosome().get(j).allele()) {
                    fV++;
                }
                else if (Math.abs(board.chromosome().get(i).allele() - board.chromosome().get(j).allele()) == Math.abs(i - j)) {
                    fV++;
                }
            }
        }
        return fitnessValue - fV;
    }

    private static int fitnessFunctionClassic(final List<Integer> classic) {
        int classicValue = 0;
        for (int i = 0; i < classic.size(); i++) {
            for (int j = i + 1; j < classic.size(); j++) {
                if (classic.get(i) == classic.get(j)) {
                    classicValue++;
                }
                else if (Math.abs(classic.get(i) - classic.get(j)) == Math.abs(i - j)) {
                    classicValue++;
                }
            }
        }
        return classicValue;
    }

    public static List<List<Integer>> listPermutations(List<Integer> list) {
        if (list.size() == 0) {
            List<List<Integer>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        List<List<Integer>> returnMe = new ArrayList<>();

        Integer firstElement = list.remove(0);

        List<List<Integer>> recursiveReturn = listPermutations(list);
        for (List<Integer> li : recursiveReturn) {

            for (int index = 0; index <= li.size(); index++) {
                List<Integer> temp = new ArrayList<>(li);
                temp.add(index, firstElement);
                returnMe.add(temp);
            }
        }
        return returnMe;
    }

    private static void drawBoard(final Genotype<IntegerGene> board) {
        for (int i = 0; i < board.chromosome().length(); i++) {
            for (int j = 0; j < board.chromosome().length(); j++) {
                if (board.chromosome().get(j).allele() == i) {
                    System.out.print("X ");
                }
                else {
                    System.out.print("O ");
                }
            }
            System.out.println();
        }
    }

    private static void drawBoardClassic(List<Integer> board) {
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                if (board.get(j) == i) {
                    System.out.print("X ");
                }
                else {
                    System.out.print("O ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter number of Queens: ");
        int nrQ = keyboard.nextInt();
        System.out.print("Enter size of the population: ");
        int nrP = keyboard.nextInt();
        Instant startGenetics = Instant.now();
        final Genotype<IntegerGene> genotype = Genotype.of(IntegerChromosome.of(0, nrQ - 1, nrQ));
        Function<Genotype<IntegerGene>, Integer> ff = Main::fitnessFunction;
        final EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();
        Engine<IntegerGene, Integer> engine = Engine.builder(ff, genotype)
                .populationSize(nrP)
                .offspringFraction(0.7)
                .survivorsSelector(new TournamentSelector<>(Math.round(nrP/10)))
                .offspringSelector(new TournamentSelector<>(Math.round(nrP/10)))
                .alterers(new Mutator<>(0.03), new SinglePointCrossover<>(0.3))
                .build();
        final Genotype<IntegerGene> result = engine.stream()
                .limit(Limits.byFitnessThreshold(((nrQ-1)*(1+nrQ-1)/2)-1))
                .peek(statistics)
                .collect(EvolutionResult.toBestGenotype());
        Instant endGenetics = Instant.now();
        drawBoard(result);
        System.out.println(statistics);


        Instant startClassic = Instant.now();
        List<Integer> classic = new ArrayList<>();
        for (int i = 0; i < nrQ; i++) {
            classic.add(i);
        }
        List<List<Integer>> solved = listPermutations(classic);
        int fValueC = 1;
        for (List<Integer> list : solved) {
            fValueC = fitnessFunctionClassic(list);
            if (fValueC == 0) {
                drawBoardClassic(list);
                break;
            }
        }
        Instant endClassic = Instant.now();

        System.out.println("Genetic algorith duration: " + Duration.between(startGenetics, endGenetics).toMillis()
                + " milliseconds, classic algorithm duration: " + Duration.between(startClassic, endClassic).toMillis() + " milliseconds.");


    }
}