package gitlet;
import java.io.IOException;
import static gitlet.Repository.GITLET_DIR;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author chen
 */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        Repository gitlet = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                gitlet.init();
                break;
            case "add":
                validateNumArgs(args, 2);
                gitlet.add(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                gitlet.rm(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                gitlet.commit(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                gitlet.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                gitlet.global_log();
                break;
            case "find":
                validateNumArgs(args, 2);
                gitlet.find(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                gitlet.status();
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {
                    gitlet.checkoutHead(args[2], "head");
                } else if (args.length == 4 && args[2].equals("--")) {
                    gitlet.checkoutHead(args[3], args[1]);
                } else if (args.length == 2) {
                    gitlet.checkoutBranch(args[1]);
                }
                break;
            case "branch":
                validateNumArgs(args,2);
                gitlet.branch(args[2]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                gitlet.rm_branch(args[1]);
                break;
            case "reset":
                validateNumArgs(args, 2);
                gitlet.reset(args[1]);
            case "merge":
                validateNumArgs(args, 2);
                gitlet.merge(args[1]);
            default:
                System.out.println("No command with that name exists.");
        }
    }


/**
 * Checks the number of arguments versus the expected number,
 * throws a RuntimeException if they do not match.
 *
 * @param args Argument array from command line
 * @param n Number of expected arguments
 */
    public static void validateNumArgs(String[] args, int n) {
        if (GITLET_DIR.exists()) {
            if (args.length != n) {
                throw new RuntimeException("Incorrect operands.");
            }
        }
    }
}
