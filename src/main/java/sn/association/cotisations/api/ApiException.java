package sn.association.cotisations.api;

/** Erreur renvoyée telle quelle au client : {"erreur": message} avec le code HTTP donné. */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
