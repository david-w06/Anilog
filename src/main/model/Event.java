package model;

import java.util.Calendar;
import java.util.Date;

// As I was off campus at the time of working on this phase
// I couldn't access the domain associated with the Alarm System Github page
// Thus I used AI to help me figure out the basic implementation of the Event and EventLog classes
// I am not sure if these are identical to the ones provided in the edx module
// But the code works as stated in the instruction

/**
 * Represents an application event.
 */
public class Event {
    private static final int HASH_MULTIPLIER = 13;
    private Date dateLogged;
    private String description;

    /**
     * Creates an event with the given description and the current date/time stamp.
     * @param description description of the event
     */
    public Event(String description) {
        dateLogged = Calendar.getInstance().getTime();
        this.description = description;
    }

    /**
     * Gets the date this event was logged.
     * @return the date/time the event was logged
     */
    public Date getDate() {
        return dateLogged;
    }

    /**
     * Gets the description of this event.
     * @return the description of the event
     */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object theObject) {
        if (theObject == null) {
            return false;
        }

        if (theObject.getClass() != this.getClass()) {
            return false;
        }

        Event other = (Event) theObject;

        return (this.dateLogged.equals(other.dateLogged)
                && this.description.equals(other.description));
    }

    @Override
    public int hashCode() {
        return HASH_MULTIPLIER * dateLogged.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return dateLogged.toString() + "\n" + description;
    }
}