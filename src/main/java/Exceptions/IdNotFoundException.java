package Exceptions;

public class IdNotFoundException extends ReportException {
    private final long id;
    private final String entityType;

    public IdNotFoundException(String entityType, long id) {
        super(String.format("%s с id=%d не найден", entityType, id));
        this.entityType = entityType;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }
}