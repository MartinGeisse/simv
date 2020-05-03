package name.martingeisse.simv.cpu.instruction;

public class InstructionDecodingException extends Exception {

    public InstructionDecodingException() {
    }

    public InstructionDecodingException(String message) {
        super(message);
    }

    public InstructionDecodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstructionDecodingException(Throwable cause) {
        super(cause);
    }

}
