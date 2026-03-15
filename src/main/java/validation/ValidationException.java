package validation;

public class ValidationException extends RuntimeException { //непроверяемое искл
    public ValidationException(String message) {
        super(message); //это вызов конструктора родительского класса
    }
}
