package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author vv
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.initCommand();
                break;
            case "add":
                Repository.addCommand(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    System.out.println("Please enter a commit message.");
                }
                Repository.commitCommand(args[1]);
                break;
            case "checkout":
                break;
            case "rm":
                Repository.rmCommand(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
