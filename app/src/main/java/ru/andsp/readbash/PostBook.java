package ru.andsp.readbash;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class PostBook implements Serializable {

    private int position;

    private List<Post> posts;

    String positionLaBel() {
        return String.format(Locale.getDefault(), "%d/%d", position + 1, posts.size());
    }

    void setPosts(List<Post> posts) {
        this.posts.clear();
        position = -1;
        if (posts != null)
            for (Post post : posts) {
                this.posts.add(post);
            }
    }

    PostBook() {
        posts = new ArrayList<>();
    }

    Post next() {
        if (hasNext())
            position += 1;
        return posts.get(position);
    }

    boolean hasNext() {
        return !posts.isEmpty() && (position + 1) < posts.size();
    }
}
