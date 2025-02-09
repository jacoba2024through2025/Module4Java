import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static String correctUsername = "user123";
    private static String correctPassword = "password123";
    private static int loggedInUserId = -1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean loggedIn = false;
        boolean accountCreated = false;


        System.out.println("Welcome to the Daily Blog!");


        while (!accountCreated) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Log in");
            System.out.println("2. Create an Account");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:

                    loggedIn = login(scanner);
                    if (loggedIn) {
                        System.out.println("Login successful! Welcome to the Daily Blog.");
                        accountCreated = true;
                    }
                    break;

                case 2:

                    createAccount(scanner);

                    System.out.println("Account created successfully! You can now log in with your new credentials.");
                    break;

                case 3:

                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }


        while (loggedIn) {
            String currentUser = getLoggedInUsername();

            System.out.println("Active user: " + currentUser);

            System.out.println("\nPlease select an option:");
            System.out.println("1. My Posts");
            System.out.println("2. Search Posts");
            System.out.println("3. View My Profile");
            System.out.println("4. Create a new post");
            System.out.println("5. Log Out");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:

                    System.out.println("Viewing my posts...");
                    viewPosts();
                    break;
                case 2:

                    searchPosts(scanner);
                    break;
                case 3:
                    viewProfile(scanner);
                    break;

                case 4:
                    createPost(scanner);
                    break;

                case 5:

                    loggedIn = false;
                    accountCreated = false;
                    System.out.println("You have logged out.");
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }


        main(args);
        scanner.close();
    }

    // Static method for logging in
    private static boolean login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();


        try {

            Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022");

            // Prepare SQL to select user from database based on username
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, username);


            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                String storedPassword = rs.getString("password");  // Retrieve the stored password from the database

                if (password.equals(storedPassword)) {

                    loggedInUserId = rs.getInt("user_id"); // Store the user_id
                    System.out.println("Login successful! Welcome to the Daily Blog.");
                    rs.close();
                    pst.close();
                    connection.close();
                    return true;
                } else {

                    System.out.println("Invalid password. Please try again.");
                    rs.close();
                    pst.close();
                    connection.close();
                    return false;
                }
            } else {

                System.out.println("Invalid username. Please try again.");
                rs.close();
                pst.close();
                connection.close();
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while trying to log in.");
            return false;
        }
    }


    // Static method for creating a new account
    private static void createAccount(Scanner scanner) {
        try {

            Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022");

            // Get user input
            System.out.print("Enter a new username: ");
            String newUsername = scanner.nextLine();
            System.out.print("Enter your email: ");
            String newEmail = scanner.nextLine();
            System.out.print("Enter a new password: ");
            String newPassword = scanner.nextLine();

            // Check if the username already exists in the database
            String checkUsernameSql = "SELECT 1 FROM users WHERE username = ?";  // Updated column name
            PreparedStatement checkUsernamePst = connection.prepareStatement(checkUsernameSql);
            checkUsernamePst.setString(1, newUsername);
            ResultSet rs = checkUsernamePst.executeQuery();

            if (rs.next()) {

                System.out.println("Username already exists. Please choose a different username.");
                rs.close();
                checkUsernamePst.close();
                connection.close();
                return;
            }
            rs.close();
            checkUsernamePst.close();

            // statement to insert user into the users table
            String sql = "INSERT INTO users (username, email, password, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";  // Updated column name
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, newUsername);
            pst.setString(2, newEmail);
            pst.setString(3, newPassword);

            //execute the insert statement
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account created successfully!");


                String verifySql = "SELECT * FROM users WHERE email = ?";
                PreparedStatement verifyPst = connection.prepareStatement(verifySql);
                verifyPst.setString(1, newEmail);
                ResultSet verifyRs = verifyPst.executeQuery();

                if (verifyRs.next()) {
                    System.out.println("Verified: Account saved successfully in the database.");
                    System.out.println("User ID: " + verifyRs.getInt("user_id"));
                    System.out.println("Username: " + verifyRs.getString("username"));  // Updated column name
                    System.out.println("Email: " + verifyRs.getString("email"));
                    System.out.println("Created at: " + verifyRs.getTimestamp("created_at"));
                }

                verifyRs.close();
                verifyPst.close();
            } else {
                System.out.println("Account creation failed. Please try again.");
            }


            pst.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while trying to create the account.");
        }
    }

    public static void viewProfile(Scanner scanner) {
        if (loggedInUserId == -1) {
            System.out.println("You must log in first.");
            return;
        }

        try {

            Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022");

            // check if the user already has a profile
            String checkProfileSql = "SELECT * FROM profiles WHERE user_id = ?";
            PreparedStatement checkProfilePst = connection.prepareStatement(checkProfileSql);
            checkProfilePst.setInt(1, loggedInUserId); // Use the stored user_id
            ResultSet rs = checkProfilePst.executeQuery();

            if (rs.next()) {
                // user already has a profile, display it
                System.out.println("Profile Information:");
                System.out.println("Bio: " + rs.getString("bio"));
                System.out.println("Avatar URL: " + rs.getString("avatar_url"));
            } else {
                // create a profile if the user doesnt have one
                System.out.println("You do not have a profile. Would you like to create one? (Y/N)");
                String choice = scanner.nextLine().toUpperCase();

                if (choice.equals("Y")) {

                    System.out.print("Enter your bio: ");
                    String bio = scanner.nextLine();
                    System.out.print("Enter your avatar URL: ");
                    String avatarUrl = scanner.nextLine();

                    // insert the new profile into the database
                    String insertProfileSql = "INSERT INTO profiles (user_id, bio, avatar_url) VALUES (?, ?, ?)";
                    PreparedStatement insertPst = connection.prepareStatement(insertProfileSql);
                    insertPst.setInt(1, loggedInUserId); // Use the stored user_id
                    insertPst.setString(2, bio);
                    insertPst.setString(3, avatarUrl);

                    int rowsAffected = insertPst.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Your profile has been created successfully!");
                    } else {
                        System.out.println("There was an error creating your profile. Please try again.");
                    }

                    insertPst.close();
                } else {
                    System.out.println("You chose not to create a profile at this time.");
                }
            }

            // check if the user has any posts
            String checkPostsSql = "SELECT * FROM posts WHERE user_id = ?";
            PreparedStatement checkPostsPst = connection.prepareStatement(checkPostsSql);
            checkPostsPst.setInt(1, loggedInUserId); // Use the stored user_id
            ResultSet postRs = checkPostsPst.executeQuery();

            // check if the user has created any posts
            boolean hasPosts = false;
            while (postRs.next()) {
                if (!hasPosts) {
                    System.out.println("\nYour Posts:");
                    hasPosts = true;
                }
                // display each post
                System.out.println("Title: " + postRs.getString("title"));
                System.out.println("Content: " + postRs.getString("content"));
                System.out.println("---------");
            }

            // if no posts are found, display a message
            if (!hasPosts) {
                System.out.println("You haven't created any posts.");
            }


            postRs.close();
            checkPostsPst.close();
            rs.close();
            checkProfilePst.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while trying to view or create your profile.");
        }
    }

    public static void viewPosts() {
        if (loggedInUserId == -1) {
            System.out.println("You must log in first.");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022")) {

            // queries all posts made by the logged in user
            String fetchPostsSql = "SELECT post_id, title, content, favorites_counter FROM posts WHERE user_id = ?";
            PreparedStatement fetchPostsPst = connection.prepareStatement(fetchPostsSql);
            fetchPostsPst.setInt(1, loggedInUserId);
            ResultSet postsRs = fetchPostsPst.executeQuery();

            // initially set postsExist to false.
            boolean postsExist = false;

            // if the query is successful, we can set the boolean to true
            while (postsRs.next()) {
                postsExist = true;

                int postId = postsRs.getInt("post_id");
                String title = postsRs.getString("title");
                String content = postsRs.getString("content");
                int favoritesCounter = postsRs.getInt("favorites_counter");

                // get comments for the post using fetchCommentsForPost
                List<String> comments = fetchCommentsForPost(postId, connection);

                // Display post info
                System.out.println("Title: " + title);
                System.out.println("Content: " + content);
                System.out.println("Favorites: " + favoritesCounter); // Display favorite count
                System.out.println("Comments: ");
                for (String comment : comments) {
                    System.out.println("- " + comment);
                }

                System.out.println("-----------------------------------");
            }

            if (!postsExist) {

                System.out.println("You have not created any posts.");
            }

            postsRs.close();
            fetchPostsPst.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while trying to view posts.");
        }
    }




    // Method to fetch comments for a specific post

    public static List<String> fetchCommentsForPost(int postId, Connection connection) {
        List<String> comments = new ArrayList<>();

        try {
            // query all comments made by users and also join the users table to be able to get the user
            String fetchCommentsSql = "SELECT u.username, c.content FROM comments c "
                    + "JOIN users u ON c.user_id = u.user_id "
                    + "WHERE c.post_id = ? ORDER BY c.created_at ASC";
            PreparedStatement fetchCommentsPst = connection.prepareStatement(fetchCommentsSql);
            fetchCommentsPst.setInt(1, postId);


            ResultSet commentsRs = fetchCommentsPst.executeQuery();

            while (commentsRs.next()) {
                String username = commentsRs.getString("username");  // get the username
                String commentContent = commentsRs.getString("content");  // get the comment content


                String formattedComment = username + ": " + commentContent;
                comments.add(formattedComment);  // Adds the comment to the list
            }

            commentsRs.close();
            fetchCommentsPst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while fetching comments for the post.");
        }

        return comments;
    }




    private static String getLoggedInUsername() {
        String username = null;

        try {
            // connect to the PostgreSQL database
            Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022");

            // get the username of the logged-in user
            String sql = "SELECT username FROM users WHERE user_id = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, loggedInUserId);  // Use the stored user_id
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                username = rs.getString("username");
            }


            rs.close();
            pst.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while retrieving the username.");
        }

        return username;
    }

    public static void createPost(Scanner scanner) {
        if (loggedInUserId == -1) {
            System.out.println("You must log in first.");
            return;
        }

        try {
            // connect to the PostgreSQL database
            Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022");

            // get user input for post title and content
            System.out.print("What would you like to name the post? ");
            String title = scanner.nextLine();

            System.out.print("What would you like to write in your post? ");
            String content = scanner.nextLine();

            // insert the new post into the 'posts' table
            String sql = "INSERT INTO posts (user_id, title, content) VALUES (?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, loggedInUserId);  // use the logged-in user's user_id
            pst.setString(2, title);
            pst.setString(3, content);

            // completes the insert statement
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Your post has been created successfully!");

                // Get the postId of the new post
                String selectSql = "SELECT post_id, title, content FROM posts WHERE user_id = ? AND title = ? AND content = ?";
                PreparedStatement selectPst = connection.prepareStatement(selectSql);
                selectPst.setInt(1, loggedInUserId);
                selectPst.setString(2, title);
                selectPst.setString(3, content);
                ResultSet rs = selectPst.executeQuery();

                if (rs.next()) {
                    int postId = rs.getInt("post_id");
                    String postTitle = rs.getString("title");
                    String postContent = rs.getString("content");

                    // Create a Post object and use its methods
                    Posts newPost = new Posts(postId, postTitle, postContent, 0); // Initial favorites count is 0
                    newPost.displayPost(); // Display post details

                    // Optionally, you can return this Post object to the caller if you need it elsewhere
                }

                rs.close();
                selectPst.close();
            } else {
                System.out.println("There was an error creating your post. Please try again.");
            }

            pst.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while trying to create your post.");
        }
    }


    public static void searchPosts(Scanner scanner) {
        List<Posts> posts = new ArrayList<>();
        Posts selectedPost = null;  // currently selected post

        if (loggedInUserId == -1) {
            System.out.println("You must log in first.");
            return;
        }

        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022");

            // Get user input for the username to search
            System.out.print("Provide a username to search their posts: ");
            String searchUsername = scanner.nextLine();

            // Run a query to see if the specified user exists in the database
            String checkUserSql = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement checkUserPst = connection.prepareStatement(checkUserSql);
            checkUserPst.setString(1, searchUsername);
            ResultSet userRs = checkUserPst.executeQuery();

            if (userRs.next()) {
                // If user exists, get their user_id
                int targetUserId = userRs.getInt("user_id");

                // get posts created by the user
                String fetchPostsSql = "SELECT post_id, title, content, favorites_counter FROM posts WHERE user_id = ?";
                PreparedStatement fetchPostsPst = connection.prepareStatement(fetchPostsSql);
                fetchPostsPst.setInt(1, targetUserId);
                ResultSet postsRs = fetchPostsPst.executeQuery();

                // get posts and create Post objects
                while (postsRs.next()) {
                    int postId = postsRs.getInt("post_id");
                    String title = postsRs.getString("title");
                    String content = postsRs.getString("content");
                    int favoritesCounter = postsRs.getInt("favorites_counter");

                    // create new objects and add to the list
                    Posts post = new Posts(postId, title, content, favoritesCounter);
                    posts.add(post);
                }

                // if posts are found, list them
                if (!posts.isEmpty()) {
                    System.out.println("Found the following posts from " + searchUsername + ":");
                    for (int i = 0; i < posts.size(); i++) {
                        Posts post = posts.get(i);
                        System.out.println((i + 1) + ". " + post.getTitle() + " (Favorites: " + post.getFavoritesCounter() + ")");
                    }

                    // user input
                    System.out.print("Enter the title of the post you want to select: ");
                    String postTitle = scanner.nextLine();

                    // if the selected post exists or not
                    for (Posts post : posts) {
                        if (post.getTitle().equalsIgnoreCase(postTitle)) {
                            selectedPost = post;
                            break;
                        }
                    }

                    if (selectedPost != null) {
                        System.out.println("You have selected the post: " + selectedPost.getTitle());
                        // print the content of the selected post
                        System.out.println("Post Content:");
                        System.out.println(selectedPost.getContent());

                        // display the favorite count
                        System.out.println("This post has " + selectedPost.getFavoritesCounter() + " favorites.");


                        System.out.print("Would you like to comment on this post? (yes/no): ");
                        String commentChoice = scanner.nextLine();

                        if (commentChoice.equalsIgnoreCase("yes")) {
                            // calls the method to allow the user to comment
                            commentOnPost(scanner, selectedPost.getPostId());
                        } else {
                            System.out.println("You chose not to comment on this post.");
                        }

                        // asks the user if they want to favorite this post
                        System.out.println("Would you like to favorite this post? (yes/no): ");
                        String favoriteChoice = scanner.nextLine();
                        if (favoriteChoice.equalsIgnoreCase("yes")) {

                            favoriteOnPost(scanner, selectedPost.getPostId());
                            System.out.print("You have favorited this post");
                        }
                    } else {
                        System.out.println("No post found with that title.");
                    }
                }

            } else {
                System.out.println("User not found.");
            }

            userRs.close();
            checkUserPst.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while trying to search posts.");
        }
    }








    public static void commentOnPost(Scanner scanner, int postId) {
        if (loggedInUserId == -1) {
            System.out.println("You must log in first.");
            return;
        }

        try {
            // connect to the database
            Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022");

            // get user input for the comment
            System.out.print("Enter your comment: ");
            String content = scanner.nextLine();

            // insert the comment into the 'comments' table
            String sql = "INSERT INTO comments (user_id, post_id, content, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, loggedInUserId);  // Use the logged-in user's user_id
            pst.setInt(2, postId);           // Use the post_id passed as a parameter
            pst.setString(3, content);

            // runs the insert statement
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Your comment has been posted successfully!");
            } else {
                System.out.println("There was an error posting your comment. Please try again.");
            }


            pst.close();
            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while trying to comment on the post.");
        }
    }

    public static void favoriteOnPost(Scanner scanner, int postId) {
        if (loggedInUserId == -1) {
            System.out.println("You must log in first.");
            return;
        }

        try {
            // connect to the database
            Connection connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "Qu!stors2022");

            // query to check if there are any users that have favorited a post
            String checkFavoriteSql = "SELECT is_favorited FROM favorites WHERE user_id = ? AND post_id = ?";
            PreparedStatement checkFavoritePst = connection.prepareStatement(checkFavoriteSql);
            checkFavoritePst.setInt(1, loggedInUserId);
            checkFavoritePst.setInt(2, postId);
            ResultSet favoriteRs = checkFavoritePst.executeQuery();

            if (favoriteRs.next()) {

                boolean isFavorited = favoriteRs.getBoolean("is_favorited");
                boolean newFavoriteStatus = !isFavorited; // Toggle the status

                // update favorite status in the favorites table
                String updateFavoriteSql = "UPDATE favorites SET is_favorited = ? WHERE user_id = ? AND post_id = ?";
                PreparedStatement updateFavoritePst = connection.prepareStatement(updateFavoriteSql);
                updateFavoritePst.setBoolean(1, newFavoriteStatus);
                updateFavoritePst.setInt(2, loggedInUserId);
                updateFavoritePst.setInt(3, postId);
                int rowsUpdated = updateFavoritePst.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Post favorite status updated successfully.");

                    //If the user favorites a post, we want to update that counter
                    if (newFavoriteStatus) {
                        // increment favorite counter if post was favorited
                        String incrementFavoriteCounterSql = "UPDATE posts SET favorites_counter = COALESCE(favorites_counter, 0) + 1 WHERE post_id = ?";
                        PreparedStatement incrementFavoriteCounterPst = connection.prepareStatement(incrementFavoriteCounterSql);
                        incrementFavoriteCounterPst.setInt(1, postId);
                        incrementFavoriteCounterPst.executeUpdate();
                        System.out.println("Post favorite counter incremented.");
                    } else {
                        // decrement favorite counter if post was unfavorited
                        String decrementFavoriteCounterSql = "UPDATE posts SET favorites_counter = COALESCE(favorites_counter, 0) - 1 WHERE post_id = ?";
                        PreparedStatement decrementFavoriteCounterPst = connection.prepareStatement(decrementFavoriteCounterSql);
                        decrementFavoriteCounterPst.setInt(1, postId);
                        decrementFavoriteCounterPst.executeUpdate();
                        System.out.println("Post favorite counter decremented.");
                    }
                } else {
                    System.out.println("Failed to update the favorite status.");
                }

                updateFavoritePst.close();
            } else {

                String insertFavoriteSql = "INSERT INTO favorites (user_id, post_id, is_favorited) VALUES (?, ?, ?)";
                PreparedStatement insertFavoritePst = connection.prepareStatement(insertFavoriteSql);
                insertFavoritePst.setInt(1, loggedInUserId);
                insertFavoritePst.setInt(2, postId);
                insertFavoritePst.setBoolean(3, true); // Mark as favorited
                int rowsInserted = insertFavoritePst.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Post favorited successfully.");

                    // update the favorite counter with the incremented/decremented number
                    String incrementFavoriteCounterSql = "UPDATE posts SET favorites_counter = COALESCE(favorites_counter, 0) + 1 WHERE post_id = ?";
                    PreparedStatement incrementFavoriteCounterPst = connection.prepareStatement(incrementFavoriteCounterSql);
                    incrementFavoriteCounterPst.setInt(1, postId);
                    incrementFavoriteCounterPst.executeUpdate();
                    System.out.println("Post favorite counter incremented.");
                } else {
                    System.out.println("Failed to favorite the post.");
                }

                insertFavoritePst.close();
            }


            favoriteRs.close();
            checkFavoritePst.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while trying to favorite the post.");
        }
    }



}




