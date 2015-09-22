package bench;

import bench.CommandHandler.Command;

public class CommandException extends Exception{
    private static final long serialVersionUID = -5919575297318030181L;
    
    private Command cmd_;
    private String msg_;
    public CommandException(Command cmd, String msg) {
        cmd_ = cmd;
        msg_ = msg;
    }
    public CommandException(Command cmd, String fmt, Object... args) {
        cmd_ = cmd;
        msg_ = String.format(fmt, args);
    }
    public String getMessage() {
        return String.format("Failure %s: %s\n", cmd_, msg_.trim());
    }
}