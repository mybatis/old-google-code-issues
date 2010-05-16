package domain.blog;

public class Author extends ImmutableAuthor {

  public Author() {
    super(-1, null, null, null, null, null);
  }

  public Author(Integer id, String username, String password, String email, String bio, Section section) {
    super(id, username, password, email, bio, section);
  }

  public Author(Integer id) {
    super(id, null, null, null, null, null);
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public void setFavouriteSection(Section favouriteSection) {
    this.favouriteSection = favouriteSection;
  }

  public String toString() {
    return "Author : " + id + " : " + username + " : " + email;
  }
}