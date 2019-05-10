package GUIMiddlewareTest

import GUIMiddleware.LetterConverter
import spock.lang.Specification

class LetterConverterTest extends Specification {

    LetterConverter lc = new LetterConverter()

    def "generate a letter grade"() {
        when:
        def list = Arrays.asList((float)76.9, (float)86.8, (float)65.1, (float)57.0, (float)94.1)
        def returnedList = lc.genLetterGrade(list)

        then:
        returnedList.size() == 5
        returnedList.get(0) == "C"
        returnedList.get(1) == "B"
        returnedList.get(2) == "D"
        returnedList.get(3) == "F"
        returnedList.get(4) == "A"
    }

    def "fail with string input array"() {
        when:
        def list = Arrays.asList("76.9", "86.8", "65.1", "57.0", "94.1")
        def returnedList = lc.genLetterGrade(list)

        then:
        thrown Exception
    }

    //BVA test for genLetterGrade
    def "in point for each grade boundary"() {
        when:
        def list = Arrays.asList((float)62.9, (float)72.9, (float)82.9, (float)92.9)
        def returnedList = lc.genLetterGrade(list)

        then:
        returnedList.size() == 4
        returnedList.get(0) == "F"
        returnedList.get(1) == "D"
        returnedList.get(2) == "C"
        returnedList.get(3) == "B"
    }

    //BVA test for genLetterGrade
    def "on point for each grade boundary"() {
        when:
        def list = Arrays.asList((float)63.0, (float)73.0, (float)83.0, (float)93.0)
        def returnedList = lc.genLetterGrade(list)

        then:
        returnedList.size() == 4
        returnedList.get(0) == "D"
        returnedList.get(1) == "C"
        returnedList.get(2) == "B"
        returnedList.get(3) == "A"
    }

    //BVA test for genLetterGrade
    def "off point for each grade boundary"() {
        when:
        def list = Arrays.asList((float)63.1, (float)73.1, (float)83.1, (float)93.1)
        def returnedList = lc.genLetterGrade(list)

        then:
        returnedList.size() == 4
        returnedList.get(0) == "D"
        returnedList.get(1) == "C"
        returnedList.get(2) == "B"
        returnedList.get(3) == "A"
    }

    //testing letterDistribution
    def "set letter breakpoints"() {
        when:
        def breakpoints = "95,85,75,65";
        lc.letterDistribution(breakpoints)

        then:
        lc.getAboveForA() == (float)95.0
        lc.getAboveForB() == (float)85.0
        lc.getAboveForC() == (float)75.0
        lc.getAboveForD() == (float)65.0
    }

    def "only 3 breakpoints"() {
        when:
        def breakpoints = "95,85,75";
        lc.letterDistribution(breakpoints)

        then:
        thrown Exception
        lc.getAboveForA() == (float)95.0
        lc.getAboveForB() == (float)85.0
        lc.getAboveForC() == (float)75.0
        //default for D is 63
        lc.getAboveForD() == (float)63.0
    }

    def "5 breakpoints"() {
        when:
        def breakpoints = "95,85,75,65,55";
        lc.letterDistribution(breakpoints)

        then:
        lc.getAboveForA() == (float)95.0
        lc.getAboveForB() == (float)85.0
        lc.getAboveForC() == (float)75.0
        lc.getAboveForD() == (float)65.0
        //it will just ignore all after the 4th
    }

    def "breakpoints aren't a string"() {
        when:
        def breakpoints = Arrays.asList(76.9, 86.8, 65.1, 57.0, 94.1);
        lc.letterDistribution(breakpoints)

        then:
        thrown Exception
    }
}
