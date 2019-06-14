package com.training.spring.bigcorp.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Représente une autorité (droits) donnée à un utilisateur
 */
@Entity
@Table(name="authorities")
public class Authority {

    /**
     * Caractérisation de l'autorité
     */
    @EmbeddedId
    private AuthorityId authorityId;

    // GETTER AND SETTER
    public AuthorityId getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(AuthorityId authorityId) {
        this.authorityId = authorityId;
    }

    // EQUALS AND HASHCODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority)) return false;
        Authority authority = (Authority) o;
        return Objects.equals(authorityId, authority.authorityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId);
    }
}
