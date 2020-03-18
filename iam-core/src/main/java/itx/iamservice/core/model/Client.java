package itx.iamservice.core.model;

import java.util.Objects;

public final class Client {

    private final ClientId id;
    private final String secret;

    public Client(ClientId id, String secret) {
        this.id = id;
        this.secret = secret;
    }

    public ClientId getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id) &&
                Objects.equals(secret, client.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, secret);
    }

    @Override
    public String toString() {
        return id.getId();
    }

}
