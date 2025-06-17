import java.io.Serializable;

public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private byte perm;
    
    public Role(String name, String description, byte perm) {
        this.name = name;
        this.description = description;
        this.perm = perm;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public byte getPerm() {
        return perm;
    }
    
    public void setPerm(byte perm) {
        this.perm = perm;
    }
    
    public static Role createAdministrator() {
        return new Role("Administrator", "Administrator Zespołu", (byte) 127);
    }
    
    public static Role createMember() {
        return new Role("Członek", "Członek Zespołu", (byte) 7);
    }
    
    public static Role createViewer() {
        return new Role("Obserwator", "Obserwator Zespołu", (byte) 1);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return name.equals(role.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}