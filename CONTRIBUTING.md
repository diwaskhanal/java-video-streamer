# Contributing to JavaFX Video Streaming System

Thank you for your interest in contributing! This document provides guidelines for contributing to this project.

## How to Contribute

### Reporting Bugs

If you find a bug, please open an issue with:
- A clear, descriptive title
- Steps to reproduce the issue
- Expected vs actual behavior
- Your environment (Java version, OS, etc.)
- Any relevant logs or screenshots

### Suggesting Enhancements

Enhancement suggestions are welcome! Please:
- Check existing issues to avoid duplicates
- Clearly describe the feature and its benefits
- Provide examples or mockups if applicable

### Pull Requests

1. **Fork the Repository**
   ```bash
   git clone git@github.com:YOUR_USERNAME/javafx-video-streaming.git
   ```

2. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make Your Changes**
   - Write clean, documented code
   - Follow existing code style and conventions
   - Add comments where necessary

4. **Test Your Changes**
   - Ensure the server compiles and runs
   - Test the client thoroughly
   - Verify database operations work correctly

5. **Commit Your Changes**
   ```bash
   git commit -m "Add: brief description of changes"
   ```
   
   Use conventional commit messages:
   - `Add:` for new features
   - `Fix:` for bug fixes
   - `Update:` for updates to existing features
   - `Refactor:` for code refactoring
   - `Docs:` for documentation changes

6. **Push and Create PR**
   ```bash
   git push origin feature/your-feature-name
   ```
   Then open a Pull Request on GitHub

## Code Style Guidelines

### Java Code
- Use 4 spaces for indentation
- Follow standard Java naming conventions
- Add JavaDoc comments for public methods
- Keep methods focused and under 50 lines when possible
- Use meaningful variable names

### Example:
```java
/**
 * Streams video data to the connected client.
 * 
 * @param videoId The ID of the video to stream
 * @param dataOutput The output stream to write to
 * @param clientIp The IP address of the client
 * @throws IOException if streaming fails
 */
private void streamVideoById(int videoId, DataOutputStream dataOutput, String clientIp) 
        throws IOException {
    // Implementation
}
```

## Development Setup

1. Install prerequisites (JDK 17+, JavaFX, MySQL)
2. Set up database using `docs/database_schema.sql`
3. Compile and test both server and client
4. Make your changes
5. Test thoroughly before submitting PR

## Areas for Contribution

Here are some ideas for contributions:

- **Security**: Add authentication/authorization
- **Features**: Playlist support, video upload, user accounts
- **UI/UX**: Improved design, dark mode toggle, responsive layout
- **Performance**: Optimize streaming, caching mechanisms
- **Documentation**: Tutorials, better examples, diagrams
- **Testing**: Unit tests, integration tests
- **Cross-platform**: Windows/Linux scripts and documentation

## Questions?

Feel free to open an issue for any questions about contributing!

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
