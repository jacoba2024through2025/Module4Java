public class Posts {
    private int postId;
    private String title;
    private String content;
    private int favoritesCounter;

    // Constructor
    public Posts(int postId, String title, String content, int favoritesCounter) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.favoritesCounter = favoritesCounter;
    }

    // Getters and Setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFavoritesCounter() {
        return favoritesCounter;
    }

    public void setFavoritesCounter(int favoritesCounter) {
        this.favoritesCounter = favoritesCounter;
    }

    // Method to display post details
    public void displayPost() {
        System.out.println("Post ID: " + postId);
        System.out.println("Title: " + title);
        System.out.println("Content: " + content);
        System.out.println("Favorites: " + favoritesCounter);
    }
}
