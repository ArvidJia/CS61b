package gitlet;

import java.io.File;

import static gitlet.Repository.*;
import static gitlet.Utils.join;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Arvid Jia
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command");
            System.exit(0);
        }

        Repository repo = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                repo.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if (args.length != 2) {
                    System.out.println("Please enter two arguments");
                    System.exit(0);
                }
                String fileName = args[1];
                File file = join(CWD, fileName);
                if (file.exists() && file.isFile()) {
                    repo.add(fileName);
                } else {
                    System.out.println("File not found");
                    System.exit(0);
                }
                break;
            case "commit":
                break;
            case "log":
                break;
            // TODO: FILL THE REST IN
        }
    }
}


