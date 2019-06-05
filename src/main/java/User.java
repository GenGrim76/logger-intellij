public class User {
    private String serverAddress;
    private String email;
    private String password;
    private String token;

    User (String s, String e, String p){
        this.serverAddress = s;
        this.email = e;
        this.password = p;
    }

    public String getServerAddress(){
        return this.serverAddress;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public void setToken(String t){this.token = t;}

    public String getToken(){return this.token;}
}
