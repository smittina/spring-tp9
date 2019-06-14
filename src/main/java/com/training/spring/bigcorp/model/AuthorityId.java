package com.training.spring.bigcorp.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * Caractérise une autorité (droit) donnée à un utilisateur
 */
@Embeddable
public class AuthorityId implements Serializable {

    /**
     * Identifiant
     */
    @Size(max=200)
    private String username;

    /**
     * L'autorité
     */
    @Size(max=50)
    private String authority;

    // GETTERS AND SETTERS

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    // EQUALS AND HASHCODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorityId)) return false;
        AuthorityId that = (AuthorityId) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authority);
    }
}
