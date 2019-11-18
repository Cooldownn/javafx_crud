package sample.Model;

public class Unit {
    private int id;
    private String unitName;
    private String unitCode;
    private String unitExam;
    private String unitLec;

    public Unit() {
    }

    public Unit(int id, String unitName, String unitCode, String unitExam, String unitLec) {
        this.id = id;
        this.unitName = unitName;
        this.unitCode = unitCode;
        this.unitExam = unitExam;
        this.unitLec = unitLec;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitExam() {
        return unitExam;
    }

    public void setUnitExam(String unitExam) {
        this.unitExam = unitExam;
    }

    public String getUnitLec() {
        return unitLec;
    }

    public void setUnitLec(String unitLec) {
        this.unitLec = unitLec;
    }
}
