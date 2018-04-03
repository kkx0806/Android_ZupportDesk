package baman.lankahomes.lk.zupportdeskticketsystem.Data;

/**
 * Created by Baman on 7/18/2016.
 */
public class TicketsItemObject {
    private int status_priority;
    private String status_open;
    private String status_overdue;
    private String ticket_from;
    private String ticket_subject;
    private String ticket_assignedto;
    private String ticket_created_date;
    private String ticket_id;

    public TicketsItemObject(int status_priority, String status_open, String status_overdue, String ticket_from, String ticket_subject, String ticket_assignedto, String ticket_created_date, String ticket_id) {
        this.status_priority = status_priority;
        this.status_open = status_open;
        this.status_overdue = status_overdue;
        this.ticket_from = ticket_from;
        this.ticket_subject = ticket_subject;
        this.ticket_assignedto = ticket_assignedto;
        this.ticket_created_date = ticket_created_date;
        this.ticket_id =ticket_id;
    }

    public int getStatus_priority() {
        return status_priority;
    }

    public String getStatus_open() {
        return status_open;
    }

    public String getStatus_overdue() {
        return status_overdue;
    }

    public String getTicket_from() {
        return ticket_from;
    }

    public String getTicket_subject() {
        return ticket_subject;
    }

    public String getTicket_assignedto() {
        return ticket_assignedto;
    }

    public String getTicket_created_date(){return ticket_created_date;}

    public String getTicket_id(){return ticket_id;}

}
