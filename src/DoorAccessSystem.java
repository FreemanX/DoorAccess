import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by xupengfei on 26/11/2016.
 */
public class DoorAccessSystem {
    private LinkedList<User> userList;
    private LinkedList<Door> doorList;

    public DoorAccessSystem() {
        this.userList = new LinkedList<>();
        this.doorList = new LinkedList<>();
        extractUsers("user.dat");
        extractDoors("door.dat");
    }

    private LinkedList<String> extractSemiInfo(String in)
    {
        LinkedList<String> buffer = new LinkedList<>();
        for (String str : in.split(";"))
            buffer.add(str);
        return buffer;
    }

    private void extractDoors(String fileName)
    {
        LinkedList<String> buffer = this.readDatFile(fileName);
        for (String line : buffer)
        {
            String [] doorInfo = line.split("\t");
            if (doorInfo.length == 6) {
                this.doorList.add(new Door(doorInfo[0], doorInfo[1], doorInfo[2]
                        , extractSemiInfo(doorInfo[3])
                        , extractSemiInfo(doorInfo[4])
                        , extractSemiInfo(doorInfo[5])
                ));
            }
        }
    }
    private void extractUsers(String fileName)
    {
        LinkedList<String> buffer = this.readDatFile(fileName);
        for (String line : buffer)
        {
            String [] userInfo = line.split("\t");
            if (userInfo.length > 3)
            {
                this.userList.add(new User(userInfo[0], userInfo[1], userInfo[2], extractSemiInfo(userInfo[3])));
            }else if (userInfo.length > 2)
            {
                this.userList.add(new User(userInfo[0], userInfo[1], userInfo[2]));
            }
        }
    }

    private LinkedList<String> readDatFile(String fileName){
        LinkedList<String> buffer = new LinkedList<>();
        File datFile = new File(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(datFile))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null && sCurrentLine.length() > 1) {
                //System.out.println(sCurrentLine);

                buffer.add(sCurrentLine);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No access log yet.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private User getUser(String inputId)
    {
        for (User user : this.userList)
            if (user.getId().equals(inputId)) return user;
        return null;
    }

    private Door getDoor(String inputId)
    {
        for (Door door : doorList)
            if (door.getId().equals(inputId)) return door;
        return null;
    }

    private static void printList(LinkedList<String> list)
    {
        for(String str : list)
            System.out.print(str + " ");
    }

    private void viewAUser(Scanner scanner)
    {
        System.out.println("Input a user id");
        String inputId = scanner.nextLine();
        if (inputId.equals("-1")) System.exit(0);
        User u = this.getUser(inputId);
        if (u != null)
        {
            for (Door door : doorList)
                if (door.authenticate(u))
                    System.out.println(u.getId() + " can access " + door.getId());
        }else
            viewAUser(scanner);
    }

    private void viewADoor(Scanner scanner)
    {
        System.out.println("Input a door id");
        String inputId = scanner.nextLine();
        if (inputId.equals("-1")) System.exit(0);
        Door door = getDoor(inputId);
        if (door == null)
        {
            System.err.println("In valid door id");
            viewADoor(scanner);
        }else
        {
            System.out.println(door.getId() + " access records: ");
            for (String record : readDatFile("doorLog.dat"))
                if (record.split("\t")[1].toLowerCase().equals(door.getId().toLowerCase()))
                    System.out.println(record);
            System.out.println("\n");
        }
    }

    public void adminView(Scanner scanner, User curUser)
    {
        System.out.println("\t1\tView a door");
        System.out.println("\t2\tView a user");
        System.out.println("\t-1\tQuit");
        String opt = scanner.nextLine();
        if (opt.equals("-1")) System.exit(0);
        if (opt.equals("1"))
            viewADoor(scanner);
        else if (opt.equals("2"))
            viewAUser(scanner);
        adminView(scanner, curUser);
    }

    public void userView(Scanner scanner, User curUser)
    {
        System.out.println("\t1\tAttempt to open a door");
        System.out.println("\t2\tQuery door opening time");
        System.out.println("\t3\tSwitch user");
        System.out.println("\t-1\tQuit");
        String opt = scanner.nextLine();
        if (opt.equals("-1")) System.exit(0);
        if (opt.equals("3")) return;
        handleUserOpt(scanner, curUser, opt);
    }

    private void openDoor(User user, Door door)
    {
        if(door.authenticate(user))
        {
            System.out.println("Door " + door.getId() + " opened");
            door.recordLog(user);
        }
        else
            System.err.println("You don't have access to this room!");
    }

    private void handleUserOpt(Scanner scanner, User user, String opt)
    {
        System.out.println("Please input the door ID");
        String doorId = scanner.nextLine();
        if (doorId.equals("-1")) System.exit(0);
        Door door = getDoor(doorId);
        if (door != null)
        {
            switch (opt)
            {
                case "1":
                    openDoor(user, door);
                    userView(scanner, user);
                    break;
                case "2":
                    System.out.println("Opening time: " + door.getStartTime() + " to " + door.getEndTime());
                    System.out.println("Open it?");
                    System.out.println("\t1\tyes");
                    System.out.println("\t2\tno");
                    String od = scanner.nextLine();
                    if (od.equals("-1")) System.exit(0);
                    if (od.equals("1")) openDoor(user, door);
                    userView(scanner, user);
                    break;
                default:
                    userView(scanner, user);
                    break;
            }
        }else
            handleUserOpt(scanner, user, opt);
    }

    public static void main(String[] args)
    {
        DoorAccessSystem das = new DoorAccessSystem();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to door access system");
        while (true)
        {
            System.out.println("Please input a user id or type -1 to exit");
            String id = scanner.nextLine();
            if (id.equals("-1")) break;
            User currentUser = das.getUser(id);
            if (currentUser == null)
            {
                System.err.println("Invalid input");
                continue;
            }
            System.out.print("Hello ");
            if (currentUser.getDepartment() != null)
                printList(currentUser.getDepartment());
            System.out.println(currentUser.getRoll() + ": " + currentUser.getName());
            System.out.println("Menu:");
            if (!currentUser.getRoll().equals("Administrator"))
                das.userView(scanner, currentUser);
            else
                das.adminView(scanner, currentUser);
        }
    }

}
