package baman.lankahomes.lk.zupportdeskticketsystem.Data;

/**
 * Created by Baman on 8/12/2016.
 */
public class PickupTicketsItemObject {
    private int status_priority;
    private String status_open;
    private String status_overdue;
    private String ticket_from;
    private String ticket_subject;
    private String ticket_assignedto;
    private String ticket_created_date;
    private String ticket_id;
    private boolean isSelected;



//    public PickupTicketsItemObject(int status_priority, String status_open, String status_overdue, String ticket_from, String ticket_subject, String ticket_assignedto, String ticket_created_date, String ticket_id) {
//        this.status_priority = status_priority;
//        this.status_open = status_open;
//        this.status_overdue = status_overdue;
//        this.ticket_from = ticket_from;
//        this.ticket_subject = ticket_subject;
//        this.ticket_assignedto = ticket_assignedto;
//        this.ticket_created_date = ticket_created_date;
//        this.ticket_id =ticket_id;
//    }

    public PickupTicketsItemObject(){

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setStatus_priority(int status_priority) {
        this.status_priority = status_priority;
    }

    public void setStatus_open(String status_open) {
        this.status_open = status_open;
    }

    public void setStatus_overdue(String status_overdue) {
        this.status_overdue = status_overdue;
    }

    public void setTicket_from(String ticket_from) {
        this.ticket_from = ticket_from;
    }

    public void setTicket_subject(String ticket_subject) {
        this.ticket_subject = ticket_subject;
    }

    public void setTicket_assignedto(String ticket_assignedto) {
        this.ticket_assignedto = ticket_assignedto;
    }

    public void setTicket_created_date(String ticket_created_date) {
        this.ticket_created_date = ticket_created_date;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

}
