package ru.netology.repository;
import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class PostRepository {
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public PostRepository() {
        Post defaultPost = new Post(1L, "Hello");
        addPost(defaultPost.getId(), defaultPost);
        counter.getAndIncrement();
        Post defaultPost2 = new Post(2L, "World");
        addPost(defaultPost2.getId(), defaultPost2);
        counter.getAndIncrement();
    }

    private void addPost(long id, Post post) {
        posts.put(id, post);
    }

    public List<Post> all() {
        return new ArrayList<>(posts.values()).stream()
                .filter(x -> !x.isDeleted())
                .collect(Collectors.toList());
    }

    public Optional<Post> getById(long id) {
        Post post = posts.get(id);
        if (post != null) {
            if (!post.isDeleted()) {
                return Optional.of(post);
            }
        }
        return Optional.empty();
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(counter.incrementAndGet());
            addPost(post.getId(), post);
            return post;
        } else {
            if (posts.containsKey(post.getId())) {
                Post changedPost = posts.get(post.getId());
                if (!changedPost.isDeleted()) {
                    changedPost.setContent(post.getContent());
                    return changedPost;
                }
            }
        }
        return null;
    }

    public boolean removeById(long id) {
        if (posts.get(id) != null) {
            posts.get(id).setDeleteStatus(true);
            counter.getAndDecrement();
            return true;
        } else {
            return false;
        }
    }
}
