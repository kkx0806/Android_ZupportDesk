package baman.lankahomes.lk.zupportdeskticketsystem.Data;

/**
 * Created by Baman on 7/26/2016.
 */
public class TicketDetailsItemObject {

    private int     tk_dtls_avatar;
    private String  tk_dtls_name;
    private String  tk_dtls_reported_date;
    private String  tk_dtls_hide_show;
    private String  tk_dtls_cc_address;
    private String  tk_dtls_msg_date;
    private String  tk_dtls_message;

    public TicketDetailsItemObject(int tk_dtls_avatar, String tk_dtls_name, String tk_dtls_reported_date,
                                   String tk_dtls_hide_show, String tk_dtls_cc_address, String tk_dtls_msg_date,
                                   String tk_dtls_message) {
        this.tk_dtls_avatar = tk_dtls_avatar;
        this.tk_dtls_name = tk_dtls_name;
        this.tk_dtls_reported_date = tk_dtls_reported_date;
        this.tk_dtls_hide_show = tk_dtls_hide_show;
        this.tk_dtls_cc_address = tk_dtls_cc_address;
        this.tk_dtls_msg_date = tk_dtls_msg_date;
        this.tk_dtls_message = tk_dtls_message;
    }

    public int getTk_dtls_avatar() {
        return tk_dtls_avatar;
    }

    public String getTk_dtls_name() {
        return tk_dtls_name;
    }

    public String getTk_dtls_reported_date() {
        return tk_dtls_reported_date;
    }

    public String getTk_dtls_hide_show() {
        return tk_dtls_hide_show;
    }

    public String getTk_dtls_cc_address() {
        return tk_dtls_cc_address;
    }

    public String getTk_dtls_message() {
        return tk_dtls_message;
    }

    public String getTk_dtls_msg_date() {
        return tk_dtls_msg_date;
    }

}
