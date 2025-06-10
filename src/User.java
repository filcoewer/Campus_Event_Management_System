public abstract class User {
    protected String id;
    protected String name;
    protected String password;

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public boolean checkPassword(String pw) {
        return password != null && password.equals(pw);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract void displayMenu();
}
