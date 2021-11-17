<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# IJ2GDocs Changelog

## [Unreleased]
### Changed
- Update dependencies

## [1.2.1]
### Changed
- Update dependencies
- Send multiple requests to the Google Document in batch

## [1.2.0]
### Added
- A notification is shown when mirroring is started/stopped

### Changed
- The mirror button is enabled whenever a project is visible and not after indexes are updated/loaded
- A different text is shown in order to disable mirroring

## [1.1.0]
### Added
- Plugin signing
- Allow to reset OAuth2 authorization in Settings (`Settings`>`Tools`>`IJ2gDocs`)

### Changed
- Using submodules for common Google API code
- Only rewrite modified parts instead of the whole document (if possible)
- Update dependencies

### Fixed
- OAuth2 credentials are saved but not loaded

## [1.0.0]
### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Mirror files to Google Docs