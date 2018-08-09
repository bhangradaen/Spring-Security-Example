package com.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.user.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "roles")
public class Role implements Serializable {

    private static final long serialVersionUID = -768547695478694L;

    @Id
    @Column(name = "role_id")
    private int roleId;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ROLE_FUNCTION",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "role_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "function_id", referencedColumnName = "function_id"))
    private Collection<Function> functions;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(Collection<Function> functions) {
        this.functions = functions;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

}

