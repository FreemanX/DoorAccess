import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by xupengfei on 26/11/2016.
 */
public class Door {

    static final String TIME_FORMAT = "HH:mm:ss";
    private String id;
    private String startTime;
    private String endTime;
    private LinkedList<String> departments;
    private LinkedList<String> rolls;
    private LinkedList<String> exceptionList;

    public Door(String id, String startTime, String endTime, LinkedList<String> departments, LinkedList<String> rolls, LinkedList<String> exceptionList) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.departments = departments;
        this.rolls = rolls;
        this.exceptionList = exceptionList;
    }

    boolean authenticate(User user)
    {
        return !inTimeRange() && ((isInList(this.rolls, user.getRoll()) && listItemMatch(user.getDepartment())) || isInList(this.exceptionList, user.getId()));
    }

    private boolean listItemMatch(LinkedList<String> userDpt)
    {
       for (String item1 : userDpt)
           for (String item2 : this.departments)
               if (item2.toLowerCase().equals("all") || item1.toLowerCase().equals(item2.toLowerCase())) return true;
       return false;
    }

    public void recordLog(User u)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String log = u.getId() + "\t" + this.id + "\t" + dateFormat.format(date);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("doorLog.dat", true));
            bw.write(log);
            bw.newLine();
            bw.flush();
        }catch (IOException e) {

        }finally {
            if (bw != null) try {
                bw.close();
            } catch (IOException e) {

            }
        }
    }

    private boolean inTimeRange()
    {
        Calendar calendar = Calendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        SimpleDateFormat parser = new SimpleDateFormat(TIME_FORMAT);
        try {
            Date now = parser.parse(h + ":" + m + ":" + s);
            Date start = parser.parse(this.startTime);
            Date end = parser.parse(this.endTime);
            return start.after(now) && end.before(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isInList(LinkedList<String> list, String item)
    {
        for (String s : list)
            if (s.toLowerCase().equals(item.toLowerCase()) || s.equals("ALL")) return true;
        return false;
    }

    public String getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public LinkedList<String> getDepartments() {
        return departments;
    }

    public LinkedList<String> getRolls() {
        return rolls;
    }

    public LinkedList<String> getExceptionList() {
        return exceptionList;
    }
}
