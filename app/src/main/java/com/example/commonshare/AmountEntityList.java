package com.example.commonshare;

public class AmountEntityList {
    private String balance;
    private String remarks;
    private String userid;
    private String transactiontype;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public String getLasttransaction() {
        return lasttransaction;
    }

    public void setLasttransaction(String lasttransaction) {
        this.lasttransaction = lasttransaction;
    }

    private String group;
    private String lasttransaction;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTransactiontype() {
        return transactiontype;
    }

    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public AmountEntityList() {

    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
