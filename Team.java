import java.io.Serializable;
import java.util.*;

public class Team implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private Map<User, Role> members;
    private User creator;
    private long createdDate;
    
    public Team(String name, String description, User creator) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.createdDate = System.currentTimeMillis() / (1000 * 60 * 60 * 24);
        this.members = new HashMap<>();
        
        // Dodaj twórcę jako administratora
        this.members.put(creator, Role.createAdministrator());
    }
    
    public void addMember(User user, Role role) {
        members.put(user, role);
    }
    
    public void removeMember(User user) {
        if (!user.equals(creator)) { // Nie można usunąć twórcy
            members.remove(user);
        }
    }
    
    public void updateMemberRole(User user, Role newRole) {
        if (members.containsKey(user) && !user.equals(creator)) {
            members.put(user, newRole);
        }
    }
    
    public boolean isMember(User user) {
        return members.containsKey(user);
    }
    
    public Role getUserRole(User user) {
        return members.get(user);
    }
    
    public Set<User> getMembers() {
        return members.keySet();
    }
    
    public Map<User, Role> getMembersWithRoles() {
        return new HashMap<>(members);
    }
    
    public boolean isCreator(User user) {
        return creator.equals(user);
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
    
    public User getCreator() {
        return creator;
    }
    
    public long getCreatedDate() {
        return createdDate;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Team team = (Team) obj;
        return name.equals(team.name) && creator.equals(team.creator);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, creator);
    }
}