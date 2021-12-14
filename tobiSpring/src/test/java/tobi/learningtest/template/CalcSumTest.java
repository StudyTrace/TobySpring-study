package tobi.learningtest.template;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

class CalcSumTest {

    @Test
    void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        int sum = calculator.calcSum(getClass().getResource("/numbers.txt").getPath());
        assertThat(sum, is(10));
    }
}
