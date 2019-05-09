package GUIMiddleware;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Stats {

    private List<List<String>> exams = new ArrayList<>();
    private List<Integer> scores = new ArrayList<>();
    private List<String> key = new ArrayList<>();
    private List<Integer> weight = new ArrayList<>();
    private String min, max, mean, range, median, variance, standardDeviation, kr20, kr21, cronbach = "";
    private List<List<String>> frequency = new ArrayList<>();
    private List<String> percentiles = new ArrayList<>();

    public void getStats() {
        MathContext m = new MathContext(2);

        min = Integer.toString(lowestScore(scores));
        max = Integer.toString(highestScore(scores));
        mean = meanInteger(scores).toString();
        range = Integer.toString(rangeOfScores(scores));
        median = Integer.toString(median(scores));
        variance = overallVariance(scores).toString();
        standardDeviation = String.valueOf(squareRoot(overallVariance(scores)));
        kr20 = kuderRichardson20(exams, key, weight).round(m).toString();
        kr21 = kuderRichardson21(exams, key, weight).round(m).toString();
        cronbach = cronbachsAlpha(exams, key, weight).round(m).toString();
        frequency = questionFrequency(exams);
        percentiles = percentiles(scores);
    }

    //This runs the stats and sets the class variables to the results

    public String getMax() {
        return max;
    }

    public String getMin() {
        return min;
    }

    public String getMean() {
        return mean;
    }

    public String getMedian() {
        return median;
    }

    public String getRange() {
        return range;
    }

    public String getVariance() {
        return variance;
    }

    public String getKr20() {
        return kr20;
    }

    public String getKr21() {
        return kr21;
    }

    public String getCronbach() {
        return cronbach;
    }

    public String getDeviation() {
        return standardDeviation;
    }

    public List<List<String>> getFrequency() {
        return frequency;
    }

    public List<String> getPercentiles() {
        return percentiles;
    }


    public void setScores(List<Integer> scores, List<String> key, List<Student> students) {
        this.scores = scores;
        this.weight.clear();

        for (int i = 0; i < key.size(); i++) {
            this.weight.add(1);
        }

        for (int i = 0; i < students.size(); i++) {
            this.exams.add(students.get(i).getAnswers());
        }

        this.key = key;
    }

    //this sets the variables used by the class

    static List<Integer> examGrader(List<List<String>> exams, List<String> answerKey, List<Integer> weight) {
        List<Integer> examScores = new ArrayList<>();
        for (int i = 0; i < exams.size(); i++) {
            int examScore = 0;
            for (int j = 0; j < 200; j++) {
                boolean matchesAll = true;
                try {
                    for (int k = 0; k < answerKey.get(j).length(); k++) {
                        CharSequence c = "" + answerKey.get(j).charAt(k);
                        if (!(exams.get(i).get(j).contains(c))) {
                            matchesAll = false;
                        }
                        if (exams.get(i).get(j).equals("error")) {
                            matchesAll = false;
                        }
                        if (exams.get(i).get(j).equals("-1")) {
                            matchesAll = false;
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    matchesAll = false;
                }
                if (matchesAll) {
                    examScore += weight.get(j);
                }
            }
            examScores.add(examScore);
        }
        return examScores;
    }
    /* Grades the list of exams using an answer key that is generated
     * exactly the same as any other randomly generated quiz.
     * The weight is a list of Bytes that affect the corresponding question
     * to be worth that many points */

    static List<Integer> weightGenerator(int questionCount) {
        Random r = new Random();
        List<Integer> weight = new ArrayList<>();
        for (int i = 0; i < questionCount; i++) {
            //weight.add((r.nextInt(2) + 2));
            weight.add(1);
        }
        return weight;
    }
    /* Generates a List of Bytes, size depends on the questionCount parameter. */

    static BigDecimal meanDecimal(List<BigDecimal> scores) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal score : scores) {
            total = total.add(score);
        }
        return total.divide(BigDecimal.valueOf(scores.size()), 8, RoundingMode.HALF_UP);
    }
    /* Takes in a List of BigDecimals and calculates the mean, outputs a BigDecimal */

    static BigDecimal meanInteger(List<Integer> scores) {
        int total = 0;
        for (Integer score : scores) {
            total += score;
        }
        return BigDecimal.valueOf(total).divide(BigDecimal.valueOf(scores.size()), 8, RoundingMode.HALF_UP);
    }
    /* Takes in a List of Integers and calculates the mean, outputs a BigDecimal */

    static BigDecimal overallVariance(List<Integer> scores) {
        BigDecimal mean = meanInteger(scores);
        List<BigDecimal> squaredDifference = new ArrayList<>();
        for (Integer score : scores) {
            squaredDifference.add(mean.subtract(BigDecimal.valueOf(score)).pow(2));
        }
        return meanDecimal(squaredDifference);
    }
    /* Takes in a List of Integers and calculates the Variance of them, outputs a BigDecimal */

    static BigDecimal overallStandardDeviation(List<Integer> scores) {
        return babylonianSqrt(overallVariance(scores));
    }

    static List<List<Integer>> gradesByQuestion(List<List<String>> exams, List<String> answerKey, List<Integer> weight) {

        System.out.println();
        for (List<String> a : exams) {
            System.out.println(a);
        }
        System.out.println();
        System.out.println(answerKey);
        System.out.println();
        System.out.println(weight);
        System.out.println();
        List<List<Integer>> examGradesByQuestion = new ArrayList<>();
        for (int i = 0; i < exams.size(); i++) {
            List<Integer> examScore = new ArrayList<>();
            for (int j = 0; j < 200; j++) {
                boolean matchesAll = true;
                try {
                    for (int k = 0; k < answerKey.get(j).length(); k++) {
                        CharSequence c = "" + answerKey.get(j).charAt(k);
                        if (!(exams.get(i).get(j).contains(c))) {
                            matchesAll = false;
                        }
                        if (exams.get(i).get(j).equals("error")) {
                            matchesAll = false;
                        }
                        if (exams.get(i).get(j).equals("-1")) {
                            matchesAll = false;
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    matchesAll = false;
                }
                if (matchesAll) {
                    examScore.add(weight.get(j));
                } else {
                    examScore.add(0);
                }
            }
            examGradesByQuestion.add(examScore);
        }
        return examGradesByQuestion;
    }
    /* Takes in a List of Lists of Strings (exams)
     * and a List of Strings (answer key)
     * and a List of Bytes (weight) and outputs
     * a corresponding List of Lists of Bytes for the
     * grades by question for every exam.
     * This is critical for Cronbach's Alpha to function correctly. */

    static List<BigDecimal> meanByQuestion(List<List<Integer>> gradesByQuestion) {
        List<BigDecimal> meanByQuestion = new ArrayList<>();
        for (int j = 0; j < gradesByQuestion.get(0).size(); j++) {
            List<Integer> questionScores = new ArrayList<>();
            for (int i = 0; i < gradesByQuestion.size(); i++) {
                questionScores.add(gradesByQuestion.get(i).get(j));
            }
            meanByQuestion.add(meanInteger(questionScores));
        }
        return meanByQuestion;
    }
    /* Takes in a List of Lists of Bytes and calculates the mean grade for each question across multiple exams,
     * outputs a List of BigDecimals corresponding to the mean for that question.
     * This is another critical part of Cronbach's Alpha. */

    static List<List<BigDecimal>> differenceFromMean(List<List<Integer>> gradesByQuestion, List<BigDecimal> meanByQuestion) {
        List<List<BigDecimal>> differenceFromMean = new ArrayList<>();
        for (int i = 0; i < gradesByQuestion.size(); i++) {
            List<BigDecimal> examMeanDifferences = new ArrayList<>();
            for (int j = 0; j < gradesByQuestion.get(0).size(); j++) {
                examMeanDifferences.add(meanByQuestion.get(j).subtract(BigDecimal.valueOf(gradesByQuestion.get(i).get(j))));
            }
            differenceFromMean.add(examMeanDifferences);
        }
        return differenceFromMean;
    }
    /* Takes in a List of Lists of Bytes (grades by question)
     * and a List of BigDecimals (mean by question) and
     * calculates the difference from the mean for each question on each exam across all exams.
     * outputs a List of Lists of BigDecimals corresponding to this. */

    static List<List<BigDecimal>> squaredData(List<List<BigDecimal>> input) {
        List<List<BigDecimal>> output = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            List<BigDecimal> tempList = new ArrayList<>();
            for (int j = 0; j < input.get(0).size(); j++) {
                tempList.add(input.get(i).get(j).pow(2));
            }
            output.add(tempList);
        }
        return output;
    }
    /* Takes in a List of Lists of BigDecimals and squares each BigDecimal. Outputs a List of Lists of BigDecimals
     * corresponding to this. */

    static List<BigDecimal> additionOfData(List<List<BigDecimal>> input) {
        List<BigDecimal> output = new ArrayList<>();
        for (int i = 0; i < input.get(0).size(); i++) {
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = 0; j < input.size(); j++) {
                sum = sum.add(input.get(j).get(i));
            }
            output.add(sum);
        }
        return output;
    }
    /* Takes in a List of Lists of BigDecimals and adds all the children lists in a parent list together.
     * outputs the corresponding list */

    static List<BigDecimal> divisionOfData(List<BigDecimal> input, int divisor) {
        List<BigDecimal> output = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            output.add(input.get(i).divide(BigDecimal.valueOf(divisor), 8, RoundingMode.HALF_UP));
        }
        return output;
    }
    /* Takes in a List of BigDecimals and divides each BigDecimal by the divisor.
     * outputs the corresponding list */

    static BigDecimal summationOfList(List<BigDecimal> input) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < input.size(); i++) {
            sum = sum.add(input.get(i));
        }
        return sum;
    }
    /* Takes in a List of BigDecimals and adds each BigDecimal together. Outputs the corresponding BigDecimal */

    static BigDecimal cronbachsAlpha(List<List<String>> exams, List<String> answerKey, List<Integer> weight) {
        List<List<Integer>> gradesByQuestion = gradesByQuestion(exams, answerKey, weight);
        List<BigDecimal> meanByQuestion = meanByQuestion(gradesByQuestion);
        List<List<BigDecimal>> differencesFromMean = differenceFromMean(gradesByQuestion, meanByQuestion);
        List<List<BigDecimal>> squaredDifferencesFromMean = squaredData(differencesFromMean);
        List<BigDecimal> additionOfSDFTM = additionOfData(squaredDifferencesFromMean);
        List<BigDecimal> varianceIndividual = divisionOfData(additionOfSDFTM, exams.size());

        BigDecimal sigmaVI = summationOfList(varianceIndividual);
        BigDecimal vTest = overallVariance(examGrader(exams, answerKey, weight));
        BigDecimal numberOfQuestions = BigDecimal.valueOf(exams.get(0).size()).subtract(BigDecimal.valueOf(0));

        BigDecimal rightHand = BigDecimal.ONE.subtract((sigmaVI.divide(vTest, 8, RoundingMode.HALF_UP)));
        BigDecimal leftHand = numberOfQuestions.divide((numberOfQuestions.subtract(BigDecimal.ONE)), 8, RoundingMode.HALF_UP);

        return rightHand.multiply(leftHand);
    }
    /* This method uses many other methods in this class to, in this order:
     * Calculate the Grade on each question of each exam, using the exam data, the answer key, and a weight.
     * Calculate the mean grade on each question
     * Calculate each exam's question's difference from the mean
     * Square the Differences From The Mean
     * Add the SDFTM across questions together
     * Divide the added SDFTM by the total number of exams, which should be provided by the examCount parameter (Individual Variance)
     * Add the Individual Variance together (Sigma VI)
     * Calculate the Variance of the overall test scores (VTest)
     * Calculates the number of questions on the exam (n) and then performs n/(n-1) (Left hand side of Cronbach's Alpha equation)
     * Calculates 1-(SigmaVI/VTest) (Right hand side of Cronbach's Alpha equation
     * Multiplies the right hand and left hand sides together
     * Outputs the corresponding value
     *
     * Should be between 0 and 1 but negative values are possible when your exam is complete nonsense like my random exam data generator.*/

    static int lowestScore(List<Integer> scores) {
        int lowestScore = 200;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) < lowestScore) {
                lowestScore = scores.get(i);
            }
        }
        return lowestScore;
    }
    /* Takes in a List of Integer, outputs the lowest value */

    static int highestScore(List<Integer> scores) {

        int highestScore = 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) > highestScore) {
                highestScore = scores.get(i);
            }
        }
        return highestScore;
    }
    /* Takes in a List of Integers, outputs the highest value */

    static int median(List<Integer> scores) {
        return scores.get(scores.size() / 2);
    }
    /* Takes in a List of Integers, outputs the Median value */

    static int rangeOfScores(List<Integer> scores) {
        return highestScore(scores) - lowestScore(scores);
    }
    /* Takes in a List of Integers, outputs the range of scores */

    static List<BigDecimal> proportionPassingByQuestion(List<List<String>> exams, List<String> answerKey, List<Integer> weight) {
        List<List<Integer>> gradesByQuestion = gradesByQuestion(exams, answerKey, weight);
        List<BigDecimal> proportionPassingByQuestion = new ArrayList<>();
        for (int i = 0; i < gradesByQuestion.get(0).size(); i++) {
            BigDecimal currentProportion = BigDecimal.ZERO;
            for (int j = 0; j < gradesByQuestion.size(); j++) {
                if (gradesByQuestion.get(j).get(i) != 0) {
                    currentProportion = (currentProportion.add(BigDecimal.ONE));
                }
            }
            proportionPassingByQuestion.add(currentProportion.divide(BigDecimal.valueOf(exams.size()), 8, RoundingMode.HALF_UP));
        }
        return proportionPassingByQuestion;
    }
    /* Tales in the List of Lists of Strings for the exams and the List of Strings for the answer key and the
     * List of Bytes for the weight and outputs a List of BigDecimals corresponding to the proportion of exams where
     * that question was answered correctly */

    static List<BigDecimal> proportionFailingByQuestion(List<List<String>> exams, List<String> answerKey, List<Integer> weight) {
        List<List<Integer>> gradesByQuestion = gradesByQuestion(exams, answerKey, weight);
        List<BigDecimal> proportionFailingByQuestion = new ArrayList<>();
        for (int i = 0; i < gradesByQuestion.get(0).size(); i++) {
            BigDecimal currentProportion = BigDecimal.ZERO;
            for (int j = 0; j < gradesByQuestion.size(); j++) {
                if (gradesByQuestion.get(j).get(i) == 0) {
                    currentProportion = (currentProportion.add(BigDecimal.ONE));
                }
            }
            proportionFailingByQuestion.add((currentProportion.divide((BigDecimal.valueOf(exams.size())), 8, RoundingMode.HALF_UP)));
        }
        return proportionFailingByQuestion;
    }
    /* Tales in the List of Lists of Strings for the exams and the List of Strings for the answer key and the
     * List of Bytes for the weight and outputs a List of BigDecimals corresponding to the proportion of exams where
     * that question was answered incorrectly */

    static BigDecimal kuderRichardson21(List<List<String>> exams, List<String> answerKey, List<Integer> weight) {
        BigDecimal numberOfQuestions = BigDecimal.valueOf(exams.get(0).size() - 0);
        BigDecimal overallVariance = overallVariance(examGrader(exams, answerKey, weight));
        BigDecimal meanScore = meanInteger(examGrader(exams, answerKey, weight));
        BigDecimal meanWeight = meanInteger(weight);

        BigDecimal leftHand = numberOfQuestions.divide(numberOfQuestions.subtract(BigDecimal.ONE), 8, RoundingMode.HALF_UP);
        BigDecimal rightHand = BigDecimal.ONE.subtract((meanScore.multiply((numberOfQuestions.subtract((meanScore)))).divide((numberOfQuestions.multiply((overallVariance))), 8, RoundingMode.HALF_UP)));

        return ((leftHand.multiply(rightHand, MathContext.UNLIMITED)).divide(meanWeight, 8, RoundingMode.HALF_UP));
    }
    /* This method does, in this order:
     * Calculates the number of questions (n)
     * Calculates the overall variance of test scores (var)
     * Calculates the mean of the test scores (M)
     * Calculates n/(n-1), called leftHand
     * Calculates 1-(M*(n-M)/(n*Var)), called rightHand
     * returns leftHand * rightHand
     *
     * Note that this statistic is not completely accurate unless the exam is entirely 1 point questions
     * Should be a value between 0 and 1 but can be negative when fed a very terrible exam */

    static BigDecimal kuderRichardson20(List<List<String>> exams, List<String> answerKey, List<Integer> weight) {
        BigDecimal numberOfQuestions = BigDecimal.valueOf(exams.get(0).size() - 0);
        BigDecimal overallVariance = overallVariance(examGrader(exams, answerKey, weight));
        List<BigDecimal> proportionPassingList = proportionPassingByQuestion(exams, answerKey, weight);
        List<BigDecimal> proportionFailingList = proportionFailingByQuestion(exams, answerKey, weight);
        BigDecimal meanWeight = meanInteger(weight);

        List<BigDecimal> passingXfailing = new ArrayList<>();
        for (int i = 0; i < proportionFailingList.size(); i++) {
            passingXfailing.add((proportionPassingList.get(i).multiply(proportionFailingList.get(i))));
        }
        BigDecimal sigmaPxQ = BigDecimal.ZERO;
        for (BigDecimal a : passingXfailing) {
            sigmaPxQ = ((sigmaPxQ.add(a)));
        }

        BigDecimal leftHand = (numberOfQuestions).divide((numberOfQuestions.subtract(BigDecimal.ONE)), 8, RoundingMode.HALF_UP);
        BigDecimal rightHand = (BigDecimal.ONE.subtract((sigmaPxQ.divide((overallVariance), 8, RoundingMode.HALF_UP))));

        return ((leftHand.multiply(rightHand)).divide(meanWeight, 8, RoundingMode.HALF_UP));
    }
    /* This method does, in this order:
     * Calculates the number of questions (n)
     * Calculates the variance of test scores (var)
     * Calculates a list of the proportion of people that passed each question (p)
     * Calculates a list of the proportion of people that failed each question (q)
     * Calculates a list of the elements of p and q multiplied together (p*q)
     * Adds each element of p*q together (Σp*q)
     * Calculates n/(n-1), called leftHand
     * Calculates 1-(Σp*q)/Var, called rightHand
     * Returns leftHand * rightHand
     *
     * Note that this statistic is not completely accurate unless the exam is entirely 1 point questions
     * Should be between 0 and 1 but can be negative when you are a really bad test maker and your students are also really bad ツ */

    static List<List<String>> questionFrequency(List<List<String>> exams) {

        List<List<String>> questionFrequency = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            List<String> tempList = new ArrayList<>();

            int a = 0;
            int b = 0;
            int c = 0;
            int d = 0;
            int e = 0;

            for (int j = 0; j < exams.size(); j++) {
                try {
                    if (exams.get(j).get(i).contains("0") && !(exams.get(j).get(i).contains("-"))) {
                        a++;
                    } else if (exams.get(j).get(i).contains("1") && !(exams.get(j).get(i).contains("-"))) {
                        b++;
                    } else if (exams.get(j).get(i).contains("2") && !(exams.get(j).get(i).contains("-"))) {
                        c++;
                    } else if (exams.get(j).get(i).contains("3") && !(exams.get(j).get(i).contains("-"))) {
                        d++;
                    } else if (exams.get(j).get(i).contains("4") && !(exams.get(j).get(i).contains("-"))) {
                        e++;
                    }
                } catch (IndexOutOfBoundsException ex) {

                }
            }

            tempList.add(String.valueOf(a));
            tempList.add(String.valueOf(b));
            tempList.add(String.valueOf(c));
            tempList.add(String.valueOf(d));
            tempList.add(String.valueOf(e));

            questionFrequency.add(tempList);
        }

        for (int i = 0; i < 200; i++) {
            boolean allZeros = true;
            for (int j = 0; j < 5; j++) {
                if (!(questionFrequency.get(i).get(j).equals("0"))) {
                    allZeros = false;
                }
            }
            if (allZeros) {
                for (int j = 0; j < 200 - i; j++) {
                    questionFrequency.remove(i);
                }
                break;
            }

        }

        return questionFrequency;
    }
    /* This method takes in the List<List<Strings>> that are the exams in the form of exams<question<answer>> and the answers are in 01234 format,
     * and returns a List<List<String>> which is the question<choice<frequency>> of the exam.
     * For example, questionFrequency(exams).get(5).get(3) will give you an integer corresponding to question 6 choice D frequency.
     * Remember that a 200 question exam is indexed 0-199 */

    static List<String> percentiles(List<Integer> scores) {

        List<String> percentiles = new ArrayList<>();

        for (int i = 0; i < scores.size(); i++) {
            BigDecimal numberLessThan = BigDecimal.ZERO;

            for (int j = 0; j < scores.size(); j++) {
                if (scores.get(j) < scores.get(i)) {
                    numberLessThan = numberLessThan.add(BigDecimal.ONE);
                }
            }
            String tempPercentile = (numberLessThan.divide(BigDecimal.valueOf(scores.size()), 4, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100)).toString();
            percentiles.add(tempPercentile.substring(0, tempPercentile.length() - 2));
        }

        return percentiles;
    }
    /* This method takes in a List<Integer> corresponding to the scores and returns a list of BigDecimals corresponding
     * to that exam's percentile.
     * For example, percentiles(scores).get(5) will give you the 6th exam's percentile.
     * Remember index 0-1999 for a 2000 count exam.
     */

    static BigDecimal squareRoot(BigDecimal input) {
        BigDecimal first = new BigDecimal("0");
        BigDecimal second = new BigDecimal(Math.sqrt(input.doubleValue()));
        while (!first.equals(second)) {
            first = second;
            second = input.divide(first, 8, RoundingMode.HALF_UP);
            second = second.add(first);
            second = second.divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_UP);

        }
        return second;
    }
    /* This method takes in a BigDecimal as input and returns the square root of the BigDecimal */


    /**
     * Creates a list of quartiles for a given list of scores.
     *
     * @param scores list of scores to compute quartiles for
     * @return list of [Q1, median, Q3]
     */
    static List<BigDecimal> quartiles(List<Integer> scores) {
        if (scores == null) return null;
        if (scores.size() == 0) return new ArrayList<BigDecimal>();

        if (scores.size() == 1) {
            BigDecimal score = BigDecimal.valueOf(scores.get(0));
            List<BigDecimal> result = new ArrayList<>();
            for (int i = 0; i < 4; i++)
                result.add(score);
            return result;
        }

        List<Integer> sortedScores = new ArrayList<>(scores);
        Collections.sort(sortedScores);

        List<BigDecimal> result = new ArrayList<>();

        int medianIndex = sortedScores.size() / 2;
        BigDecimal median;
        if (sortedScores.size() % 2 == 0)
            median = BigDecimal.valueOf(sortedScores.get(medianIndex))
                    .add(BigDecimal.valueOf(sortedScores.get(medianIndex - 1)))
                    .divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_UP);
        else
            median = BigDecimal.valueOf(sortedScores.get(medianIndex));

        int firstQuartileIndex;
        int thirdQuartileIndex;
        BigDecimal firstQuartile;
        BigDecimal thirdQuartile;
        firstQuartileIndex = (medianIndex - 1) / 2;
        thirdQuartileIndex = (scores.size() + medianIndex) / 2;
        firstQuartile = BigDecimal.valueOf(sortedScores.get(firstQuartileIndex))
                .add(BigDecimal.valueOf(sortedScores.get(firstQuartileIndex + 1)))
                .divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_UP);
        thirdQuartile = BigDecimal.valueOf(sortedScores.get(thirdQuartileIndex))
                .add(BigDecimal.valueOf(sortedScores.get(thirdQuartileIndex + 1)))
                .divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_UP);

        result.add(firstQuartile);
        result.add(median);
        result.add(thirdQuartile);
        return result;
    }


    /**
     * Calculates an estimate of the square root of <code>s</code> within a given error threshold.
     *
     * @param s         the number to calculate the square root of.
     * @param threshold the threshold of acceptable error
     * @return an estimate of the square root of <code>s</code>
     */
    private static BigDecimal babylonianSqrt(BigDecimal s, BigDecimal threshold) {
        BigDecimal x = BigDecimal.valueOf(Math.sqrt(s.doubleValue())); // initial estimate

        //loop while error is greater than threshold
        while (s.subtract(x.pow(2)).divide(x.multiply(BigDecimal.valueOf(2)), threshold.scale(), RoundingMode.HALF_EVEN).compareTo(threshold) > 1) {
            x = x.add(x.add(s.divide(x))).divide(BigDecimal.valueOf(2), threshold.scale(), RoundingMode.HALF_EVEN); // x_{n+1} = (1/2)(x_n + S/x_n)
        }

        return x;
    }

    /**
     * Calculates an estimate of the square root of <code>s</code> within 10e-32.
     *
     * @param s the number to calculate the square root of.
     * @return an estimate of the square root of <code>s</code>
     */
    private static BigDecimal babylonianSqrt(BigDecimal s) {
        final BigDecimal THRESHOLD = new BigDecimal("10e-32");
        return babylonianSqrt(s, THRESHOLD);
    }
}
