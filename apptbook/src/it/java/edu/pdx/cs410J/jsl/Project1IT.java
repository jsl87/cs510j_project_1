package edu.pdx.cs410J.jsl;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for the {@link Project1} main class.
 */
public class Project1IT extends InvokeMainTestCase {
  String owner;
  String description;
  String begin_time;
  String end_time;
  String print;
  String read_me;

  Appointment appointment;

  @Before
  public void initialization() {
    owner = "owner";
    description = "description";
    begin_time = "00/00/0000 00:00";
    end_time = "11/11/1111 11:11";
    print = "-print";
    read_me = "-README";

    appointment = new Appointment(description, begin_time, end_time);
  }

  /**
   * Invokes the main method of {@link Project1} with the given arguments.
   */
  private MainMethodResult invokeMain(String... args) {
    return invokeMain( Project1.class, args );
  }

  /**
   * Tests that invoking the main method with no arguments issues an error
   */
  @Test
  public void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();
    assertThat(result.getExitCode(), equalTo(1));
    assertThat(result.getErr(), containsString("Missing command line arguments"));
  }

  public void testTooManyCommandLineArguments() {
    MainMethodResult result = invokeMain(owner, description, begin_time, end_time, owner);
    assertThat(result.getExitCode(), equalTo(1));
    assertThat(result.getErr(), containsString("Too many command line arguments"));
  }

  @Test
  public void shouldFailWithIncorrectDateFormat() {
    String date = "wrong format";
    MainMethodResult result = invokeMain(owner, description, date, end_time);
    assertThat(result.getExitCode(), is(equalTo(1)));
    assertThat(result.getErr(), containsString(date));
  }

  @Test
  public void shouldFailWith2DigitYearFormat() {
    String date = "11/11/11 14:00";
    MainMethodResult result = invokeMain(owner, description, date, end_time);
    assertThat(result.getExitCode(), is(equalTo(1)));
    assertThat(result.getOut(), containsString(date));
  }

  @Test
  public void shouldWorkWith24HourFormat() {
    String date = "11/11/1111 14:00";
    MainMethodResult result = invokeMain(owner, description, date, end_time, print);
    assertThat(result.getExitCode(), is(equalTo(null)));
    assertThat(result.getOut(), containsString(date));
  }

  @Test
  public void shouldWorkWith12HourFormat() {
    String date = "11/11/1111 1:00";
    MainMethodResult result = invokeMain(owner, description, date, end_time, print);
    assertThat(result.getExitCode(), is(equalTo(null)));
    assertThat(result.getOut(), containsString(date));
  }

  @Test
  public void shouldWorkWith1DigitMonthFormat() {
    String date = "1/11/1111 00:00";
    MainMethodResult result = invokeMain(owner, description, date, end_time, print);
    assertThat(result.getExitCode(), is(equalTo(null)));
    assertThat(result.getOut(), containsString(date));
  }

  @Test
  public void shouldWorkWith2DigitMonthFormat() {
    String date = "01/11/1111 00:00";
    MainMethodResult result = invokeMain(owner, description, date, end_time, print);
    assertThat(result.getExitCode(), is(equalTo(null)));
    assertThat(result.getOut(), containsString(date));
  }

  @Test
  public void printOptionShouldPrintAppointmentDescription() {
    MainMethodResult result = invokeMain(owner, description, begin_time, end_time, print);
    assertThat(result.getOut(), is(equalTo(appointment.toString() + "\n")));
  }

  @Test
  public void printOptionCouldBeAtFront() {
    MainMethodResult result = invokeMain(print, owner, description, begin_time, end_time);
    assertThat(result.getOut(), is(equalTo(appointment.toString() + "\n")));
  }

  @Test
  public void printOptionCouldBeInTheMiddle() {
    MainMethodResult result = invokeMain(owner, description, print, begin_time, end_time);
    assertThat(result.getOut(), is(equalTo(appointment.toString() + "\n")));
  }

  @Test
  public void appointmentDescriptionShouldNotPrintWithoutPrintOption() {
    MainMethodResult result = invokeMain(owner, description, begin_time, end_time);
    assertThat(result.getExitCode(), is(equalTo(null)));
    assertThat(result.getOut(), is(equalTo("")));
  }
}