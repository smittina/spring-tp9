package com.training.spring.bigcorp.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

/**
 * Représente un utilisateur du site
 */
@Entity
@Table(name="users")
public class User {

    /**
     * Identifiant
     */
    @Id
    @Size(max=200)
    private String username;

    /**
     * Mot de passe
     */
    @NotNull
    @Size(max=200)
    private String password;

    /**
     * Autorisé à pénétrer sur le site ?
     */
    @NotNull
    private boolean enabled;

    /**
     * Liste des différents droits qu'il possède sur le site
     */
    @OneToMany
    private Set<Authority> authorities;

    // GETTERS AND SETTERS

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    // EQUALS AND HASHCODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return enabled == user.enabled &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(authorities, user.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, enabled, authorities);
    }
}
