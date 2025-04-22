package com.meet.shared_inventory.Models;

public class Registration_Model {
    String Name,Email,Company_Name,Company_id,Role;

    public Registration_Model(){

    }

    public Registration_Model(String name, String email, String company_Name, String company_id, String role) {
        Name = name;
        Email = email;
        Company_Name = company_Name;
        Company_id = company_id;
        Role = role;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCompany_Name() {
        return Company_Name;
    }

    public void setCompany_Name(String company_Name) {
        Company_Name = company_Name;
    }

    public String getCompany_id() {
        return Company_id;
    }

    public void setCompany_id(String company_id) {
        Company_id = company_id;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }
}
