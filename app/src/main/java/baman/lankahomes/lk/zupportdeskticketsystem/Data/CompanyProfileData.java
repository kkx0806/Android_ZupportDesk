package baman.lankahomes.lk.zupportdeskticketsystem.Data;

/**
 * Created by baman on 6/25/16.
 */
public class CompanyProfileData {
    /* comName - Company Name
    *  hdName - Help Desk Name */
    private String comName, hdName, companyID;

    public CompanyProfileData(String comName, String hdName, String companyID) {
        this.comName = comName;
        this.hdName = hdName;
        this.companyID = companyID;
    }

    public String getComName(){
        return comName;
    }
    public String gethdName(){
        return hdName;
    }
    public String getcompanyID(){return companyID;}
    public void setcomName(String name){
        this.comName = name;
    }
    public void sethdName(String hdname){
        this.hdName = hdname;
    }
    public void setcompanyID (String companyID){this.companyID = companyID;}
}
