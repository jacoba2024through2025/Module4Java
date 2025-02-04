import java.util.List;

public class ViewingPost {
    private String title;
    private String content;
    private List<String> comments;

    public ViewingPost(String title, String content, List<String> comments) {
        this.title = title;
        this.content = content;
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;

    }

    public List<String> getComments() {
        return comments;
    }

    public void displayPost() {
        System.out.println("Title: " + title);
        System.out.println("Content: " + content);
        System.out.println("Comments: ");

        if (comments.isEmpty()) {
            System.out.println("No comments found.");
        } else {
            for (String comment : comments) {
                System.out.println("-" + comment);
            }
        }
    }
}
