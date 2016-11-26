import java.util.LinkedList;

/**
 * Created by xupengfei on 26/11/2016.
 */
public class User {

    private String roll;
    private String name;
    private String id;
    private LinkedList<String> department;

    public User(String Roll, String Name, String Id)
    {
        this.roll = Roll;
        this.name = Name;
        this.id = Id;
        this.department=null;
    }

    public User(String Roll, String Name, String Id, LinkedList Department)
    {
        this.roll = Roll;
        this.name = Name;
        this.id = Id;
        this.department = Department;
    }

    public String getRoll() {
        return roll;
    }

    public String getName() {
        return name;
    }

    public LinkedList<String> getDepartment() {
        return department;
    }

    public String getId() {
        return id;
    }

}
