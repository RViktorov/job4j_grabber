package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {
    private final Connection connection;

    private Post createPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("link"),
                rs.getString("description"),
                rs.getLong("time")
        );
    }

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        String sql = """
                INSERT INTO post (title, link, description, time)
                VALUES ( ?, ?, ?, ?)
                ON CONFLICT (link) DO NOTHING
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
          //  ps.setLong(1, post.getId());
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getLink());
            ps.setString(3, post.getDescription());

            // если null
            ps.setLong(4, post.getTime() == null
                    ? System.currentTimeMillis()
                    : post.getTime());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving post", e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        String sql = "SELECT id, title, link, description, time FROM post";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(createPost(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading posts", e);
        }
        return result;
    }

    @Override
    public Optional<Post> findById(Long id) {

        String sql = """
                SELECT id, title, link, description, time
                FROM post
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(
                            createPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding post by id", e);
        }
        return Optional.empty();
    }
}
