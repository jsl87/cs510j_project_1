package edu.pdx.cs410J.jsl.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Set;

/**
 * The client-side interface to the ping service
 */
public interface AppointmentBookServiceAsync {

  /**
   * Return the current date/time on the server
   */
  void createAppointmentBook2(int numberOfAppointments, AsyncCallback<AppointmentBook> async);

  void createAppointmentBook(String owner, AsyncCallback<String> async);

  void receiveAllOwnerNames(AsyncCallback<Set<String>> async);
}
