import java.util.Scanner;

public abstract class Skill {
    protected String name;
    protected int mpCost;
    
    public Skill(String name, int mpCost) {
         this.name = name; this.mpCost = mpCost; 
        }

    public String getName() { return name; }
    public int getMpCost() { return mpCost; }
    public abstract void use(Character user, GameEngine engine, Scanner scanner);
}