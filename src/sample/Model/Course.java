package sample.Model;

public class Course {
    private Integer id;
    private String courseName;
    private String courseCode;
    private String courseDesc;

    public Course() {

    }

    public Course(Integer id, String courseName, String courseCode, String courseDesc) {
        this.id = id;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseDesc = courseDesc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    public void setCourseDesc(String courseDesc) {
        this.courseDesc = courseDesc;
    }
}
