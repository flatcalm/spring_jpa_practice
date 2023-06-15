package com.spring.jpa.chap01_basic.entity;

public class User {

    private String name;

    private int age;

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private static class UserBuilder {
        private String name;
        private int age;

        public User build() {
            User user = new User();
            user.setName(this.name);
            user.setAge(this.age);
            return user;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

    }
}
