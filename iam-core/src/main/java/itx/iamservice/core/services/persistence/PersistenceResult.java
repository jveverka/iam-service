package itx.iamservice.core.services.persistence;

public interface PersistenceResult {

    default boolean isSuccessful() {
        return true;
    }

    static PersistenceResult ok() {
        return new PersistenceResult() {
        };
    }

    static PersistenceResult failed() {
        return new PersistenceResult() {
            public boolean isSuccessful() {
                return false;
            }
        };
    }

}
