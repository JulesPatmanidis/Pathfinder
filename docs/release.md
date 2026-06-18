# Release Workflow

Pushes to `main` build `Pathfinder.jar` with GitHub Actions.

To publish a release:

```bash
git tag v1.0.1
git push origin v1.0.1
```

The tag push creates a GitHub Release and attaches the built JAR.
