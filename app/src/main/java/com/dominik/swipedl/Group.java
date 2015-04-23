package com.dominik.swipedl;

import java.util.ArrayList;

public class Group {

    // List of users in this instance of group
    private ArrayList<User> users = new ArrayList<>();
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void addUserToGroup(User newUser) {
        for (User user : users){
            if (user.getId() == newUser.getId()) {
                return;
            }
        }
        users.add(newUser);
        newUser.addGroup(this);
    }

    public void removeUserFromGroup(User user) {
        if (users.contains(user)) {
            users.remove(user);
            user.removeGroup(this);
        }
    }

    public String[][] getUsers() {
        String[][] rUsers = new String[users.size()][2];
        int i = 0;
        for (User user : users) {
            rUsers[i][0] = user.getName();
            rUsers[i][1] = Integer.toString(user.getId());
        }
        return rUsers;
    }
}