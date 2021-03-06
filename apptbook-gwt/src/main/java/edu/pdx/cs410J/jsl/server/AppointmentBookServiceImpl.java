package edu.pdx.cs410J.jsl.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.jsl.client.Appointment;
import edu.pdx.cs410J.jsl.client.AppointmentBook;
import edu.pdx.cs410J.jsl.client.AppointmentBookService;
import edu.pdx.cs410J.jsl.client.DateUtility;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * The server-side implementation of the division service
 */
public class AppointmentBookServiceImpl extends RemoteServiceServlet implements AppointmentBookService
{
  HashMap<String, AppointmentBook> appointmentBooks = new HashMap<>();

  /**
   * Creates an appointment book for the owner provided, and saves the appointment book to the servlet.
   * Returns the owner name if successful, empty string otherwise.
   *
   * @param owner
   * @return
     */
  @Override
  public String createAppointmentBook(String owner) {
    AppointmentBook book = new AppointmentBook(owner);

    if (!appointmentBooks.containsKey(owner)) {
      appointmentBooks.put(owner, book);
      return book.getOwnerName();
    } else {
      return "";
    }
  }

  /**
   * Creates an appointment for the owner provided, and saves the appointment to the servlet.
   * Returns the owner name if successful, empty string otherwise.
   *
   * @param owner
   * @param description
   * @param beginTime
   * @param endTime
     * @return
     */
  @Override
  public String createAppointment(String owner, String description, String beginTime, String endTime) {
    AppointmentBook appointmentBook = appointmentBooks.get(owner);
    Appointment appointment = new Appointment(description, beginTime, endTime);
    appointmentBook.addAppointment(appointment);
    return appointment.toString();
  }

  /**
   * Returns all owner names saved in the servlet in a <code>Set</code> collection.
   *
   * @return
     */
  @Override
  public Set<String> getAllOwnerNames() {
    Set<String> ownerNames = new TreeSet<>();

    for (String ownerName : appointmentBooks.keySet()) {
      ownerNames.add(ownerName);
    }

    return ownerNames;
  }

  /**
   * Return a string of the appointment book and appointments for the owner provided in pretty print format.
   *
   * @param owner
   * @return
     */
  @Override
  public String prettyPrintAll(String owner) {

    AppointmentBook appointmentBook = appointmentBooks.get(owner);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    PrettyPrinter prettyPrinter = new PrettyPrinter(printWriter);

    try {
      prettyPrinter.dump(appointmentBook);
      return stringWriter.toString();
    } catch (IOException e) {
      return "Failed to print the searched appointment book for " + owner;
    }
  }

  /**
   * Returns an <code>AppointmentBook</code> object containing only <code>Appointment</code> objects within
   * <code>beginTime</code> and <code>endTime</code>.
   *
   * @param book an original <code>AppointmentBook</code> object
   * @param beginTime
   * @param endTime
   * @return a new <code>AppointmentBook</code> object containing only <code>Appointment</code> objects meets the condition
   * @throws ParseException
   */
  private AppointmentBook getAppointmentBookWithSearchedAppointments(AppointmentBook book, String beginTime, String endTime) throws ParseException {
    AppointmentBook tempAppointmentBook = new AppointmentBook(book.getOwnerName());
    Date begin_date = null;
    Date end_date = null;

    begin_date = DateUtility.parseStringToDate(beginTime);
    end_date = DateUtility.parseStringToDate(endTime);

    for (Appointment appointment: book.getAppointments()) {
      if (appointment.getBeginTime().compareTo(begin_date) >= 0
              && appointment.getEndTime().compareTo(end_date) <= 0) {
        tempAppointmentBook.addAppointment(appointment);
      }
    }

    return tempAppointmentBook;
  }

  /**
   * Return a string of the appointment book and appointments within a range specified for the owner provided in pretty print format.
   *
   * @param owner
   * @param beginTime
   * @param endTime
     * @return
     */
  @Override
  public String prettyPrintSearch(String owner, String beginTime, String endTime) {

    AppointmentBook appointmentBook = null;
    try {
      appointmentBook = getAppointmentBookWithSearchedAppointments(appointmentBooks.get(owner), beginTime, endTime);
    } catch (ParseException e) {
      return "Failed to search the appointment book for " + owner;
    }

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    PrettyPrinter prettyPrinter = new PrettyPrinter(printWriter);

    try {
      prettyPrinter.dump(appointmentBook);
      return stringWriter.toString();
    } catch (IOException e) {
      return "Failed to print the searched appointment book for " + owner;
    }
  }

  /**
   * Dump the appointment book and its appointment information to a file and expose its location to the client.
   *
   * @param owner
   * @return filename on the server side
     */
  @Override
  public String getDumpFileLocation(String owner) {
    AppointmentBook appointmentBook = appointmentBooks.get(owner);
    String filename = owner + ".txt";
    TextDumper textDumper = new TextDumper(getServletContext().getRealPath(filename));
    try {
      textDumper.dump(appointmentBook);
      return filename;
    } catch (IOException e) {
      return getServletContext().getContextPath();
    }
  }

  /**
   * Writes a file to the destination with the content.
   *
   * @param filename
   * @param content
   * @throws IOException
     */
  private void customWritingToFile(String filename, String content) throws IOException {
    File file = new File(filename);

    FileWriter fw = new FileWriter(file);
    PrintWriter pw = new PrintWriter(fw);

    pw.println(content);

    pw.close();
  }

  /**
   * Loads data from the content provided to create or restore the appointment book for the owner specified.
   * @param owner
   * @param fileContent
   * @return
     */
  @Override
  public String restoreAppointmentBook(String owner, String fileContent) {

    String filename = getServletContext().getRealPath(owner);

    // write a file to use it to the text parser
    try {
      customWritingToFile(filename, fileContent);
    } catch (IOException e) {
      return "";
    }

    TextParser textParser = new TextParser(filename, owner);
    AppointmentBook appointmentBook = null;
    try {
      appointmentBook = (AppointmentBook) textParser.parse();
    } catch (ParserException e) {
      return "";
    }

    // put should update the value if it already exists
    appointmentBooks.put(owner, appointmentBook);

    return owner;
  }

  @Override
  protected void doUnexpectedFailure(Throwable unhandled) {
    unhandled.printStackTrace(System.err);
    super.doUnexpectedFailure(unhandled);
  }

}
