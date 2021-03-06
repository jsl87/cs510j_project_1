package edu.pdx.cs410J.jsl.client;

import edu.pdx.cs410J.AbstractAppointmentBook;
import java.util.ArrayList;
import java.util.List;

/**
 * This <code>AppointmentBook</code> class describes who owns a list of appointments
 * and his / her list of appointments. For appointments it uses the {@link Appointment} class
 * to represent appointments owned by an instance of appointmentBook.
 *
 * @author Jong Seong Lee
 * @version   %I%, %G%
 * @since     1.0
 */
public class AppointmentBook extends AbstractAppointmentBook<Appointment> implements Comparable<AppointmentBook>{
    private String owner_name = null;
    private List<Appointment> appointments = null;

    /**
     * Initialize an instance with a given name of owner.
     * @param owner a name of an owner of an appointment book
     */
    public AppointmentBook(String owner) {
        owner_name = owner;
        appointments = new ArrayList<Appointment>();
    }

    /**
     * This constructor is for GWT application.
     */
    public AppointmentBook() {
        this("My Owner");
    }

    /**
     * Returns a name of an owner.
     * @return a name of an owner
     */
    @Override
    public String getOwnerName() { return owner_name; }

    /**
     * Returns a list of appointments.
     * @return a <code>List</code> collection of appointments
     */
    @Override
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Adds a given appointment to a list of appointments.
     * @param appointment an instance of the {@link Appointment} class
     */
    @Override
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public int compareTo(AppointmentBook appointmentBook) {
        return this.getOwnerName().compareTo(appointmentBook.getOwnerName());
    }
}
