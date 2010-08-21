package org.apache.ibatis.submitted.cglib_lazy_error;

public class Person {
    
    private Long id;
    private String firstName;
    private String lastName;
    private Person parent;
    public Person getAncestor() {
        if (getParent() == null) {
            return this;
        } else {
            return getParent().getAncestor();
        }
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Person) {
            return this.getId() == ((Person) obj).getId();
        }
        return false;
    }
    @Override
    public int hashCode() {
        return id != null ? id.intValue() : null;
    }
    
    
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Person getParent() {
        return parent;
    }
    public void setParent(Person parent) {
        this.parent = parent;
    }
}
