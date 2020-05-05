package itx.iamservice.core.model.keys;

import java.util.Arrays;
import java.util.Objects;

public class ModelKey<T> {

    private final Class<T> type;
    private final Id[] ids;

    public ModelKey(Class<T> type, Id... ids) {
        this.type = type;
        this.ids = ids;
    }

    public ModelKey(Class<T> type, Id id) {
        this.type = type;
        this.ids = new Id[] { id };
    }

    public ModelKey(Class<T> type, String id) {
        this.type = type;
        this.ids = new Id[] { new Id(id) {}};
    }

    public Class<T> getType() {
        return type;
    }

    public Id[] getIds() {
        return ids;
    }

    public boolean startsWith(ModelKey modelKey) {
        if (ids.length < modelKey.getIds().length) {
            return false;
        }
        for(int i=0; i<modelKey.getIds().length; i++) {
            if (!ids[i].equals(modelKey.getIds()[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelKey modelKey = (ModelKey) o;
        return Objects.equals(type, modelKey.type) &&
                Arrays.equals(ids, modelKey.ids);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(ids);
        return result;
    }

    public static <T> ModelKey<T> from(Class<T> type, Id ... ids) {
        return new ModelKey(type, ids);
    }

    @Override
    public String toString() {
        return type.getName() + ":" + Arrays.toString(ids);
    }

}
