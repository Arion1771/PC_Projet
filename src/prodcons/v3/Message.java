package prodcons.v3;

public class Message {
    private String content;
    private int ID;
    public Message(String content, int ID) {
        this.content = content;
        this.ID = ID;
        }
    
        
    public int getID() {
        return ID;
    }

    public String getContent() {
        return content;
    }
    public String toString() {
        return "ID :" + ID + " Content :" + content;
    }
}