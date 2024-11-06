package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Repository.head;
import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    File gitletRepo = join(GITLET_DIR, "this");
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
                validateNumArgs("init", args, 1);
                gitlet.init();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                gitlet.add(args[1]);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                gitlet.rm(args[1]);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                gitlet.commit(args[1]);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                gitlet.log();
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                gitlet.global_log();
                break;
            case "find":
                validateNumArgs("find", args, 2);
                gitlet.find(args[1]);
                break;
            case "status":
                validateNumArgs("status", args, 1);
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
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                gitlet.rm_branch(args[1]);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                gitlet.reset(args[1]);
            default:
                System.out.println("No command with that name exists.");
        }
    }


/**
 * Checks the number of arguments versus the expected number,
 * throws a RuntimeException if they do not match.
 *
 * @param cmd Name of command you are validating
 * @param args Argument array from command line
 * @param n Number of expected arguments
 */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (GITLET_DIR.exists()) {
            if (args.length != n) {
                throw new RuntimeException(
                        String.format("Incorrect operands."));
            }
        }
    }
}
