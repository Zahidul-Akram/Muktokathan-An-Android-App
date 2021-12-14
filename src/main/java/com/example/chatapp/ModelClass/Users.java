package com.example.chatapp.ModelClass;

public class Users {
    public String name, email, uid, phone_no, status,imageUri;

    public Users() {
    }

    public Users(String name, String email, String uid, String phone_no, String status,String imageUri) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.phone_no = phone_no;
        this.status = status;
        this.imageUri=imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

