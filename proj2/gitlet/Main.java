package gitlet;

import static gitlet.Repository.BRANCH;
import static gitlet.Utils.readContentsAsString;

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
            case "rm":
                Repository.rmCommand(args[1]);
                break;
            case "log":
                Repository.logCommand();
                break;
            case "global-log":
                Repository.globallogCommand();
                break;
            case "find":
                Repository.findCommand(args[1]);
                break;
            case "status":
                Repository.statusCommand();
                break;
            case "checkout":
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                    }
                    Repository.checkoutOne(args[2]);
                }
                if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                    }
                    Repository.checkoutTwo(args[1], args[3]);
                }
                if (args.length == 2) {
                    String currentBranch = readContentsAsString(BRANCH);
                    if (currentBranch.equals(args[1])) {
                        System.out.println("No need to checkout the current branch.");
                        System.exit(0);
                    }
                    Repository.checkoutThree(args[1]);
                }
                break;
            case "branch":
                if (args.length == 2) {
                    Repository.branchCommand(args[1]);
                }
                break;
            case "rm-branch":
                if (args.length == 2) {
                    Repository.rmbranchCommand(args[1]);
                }
                break;
            case "reset":
                if (args.length == 2) {
                    Repository.resetCommand(args[1]);
                }
                break;
            case "merge":
                if (args.length == 2) {
                    Repository.mergeCommand(args[1]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
