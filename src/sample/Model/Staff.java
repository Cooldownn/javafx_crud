package sample.Model;

public class Staff {
    private String id;
    private String staffName;
    private String staffAddress;

    public Staff() {
    }

    public Staff(String id, String staffName, String staffAddress) {
        this.id = id;
        this.staffName = staffName;
        this.staffAddress = staffAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffAddress() {
        return staffAddress;
    }

    public void setStaffAddress(String staffAddress) {
        this.staffAddress = staffAddress;
    }
}
