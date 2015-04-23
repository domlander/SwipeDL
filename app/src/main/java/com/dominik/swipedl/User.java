package com.dominik.swipedl;

import java.util.ArrayList;

public class User {

    private static int idCounter = 100;
    private int id;
    private String name;

    // groups that the user belongs to
    private ArrayList<Group> groups = new ArrayList<>();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }
    public void addGroup(Group group) {
        groups.add(group);
    }
    public void removeGroup(Group group) {
        groups.remove(group);
    }

    public User(String name) {
        this.name = name;
        this.id = idCounter++;
    }

}