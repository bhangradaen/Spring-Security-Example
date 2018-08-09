package com.security.model;

import java.util.Date;
import java.util.List;

public class Token {

    private String sub;
    private int userId;
    private Date expirationDate;
    private List<String> scopes;

    public Token() {}

    public Token(String sub, int userId, Date expirationDate, List<String> scopes) {
        this.sub = sub;
        this.userId = userId;
        this.expirationDate = expirationDate;
        this.scopes = scopes;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

}

