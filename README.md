# IJ2GDocs
![Build](https://github.com/danthe1st/IJ2GDocs/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/io.github.danthe1st.ij2gdocs.svg)](https://plugins.jetbrains.com/plugin/17433-ij2gdocs)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/io.github.danthe1st.ij2gdocs.svg)](https://plugins.jetbrains.com/plugin/17433-ij2gdocs)

<!-- Plugin description -->
IJ2GDocs allows you to mirror a file opened in IntelliJ IDEA™ to Google Docs™.

![2021-08-15 11-45-01](https://user-images.githubusercontent.com/34687786/129474396-1dfba440-d652-4980-a314-33666ff25b8b.gif)

## Usage

* Open the file you want to mirror to Google Docs in IntelliJ (with this plugin installed).
* Click the button `Mirror This File to a Google Document` under the option menu `Tools`.<br/>
  ![image](https://user-images.githubusercontent.com/34687786/129474136-72902ac5-e728-451a-88ab-fbf089c986fd.png)
* The default browser should open automatically. Authorize the application to access Google Docs.
* Open the Google Document to mirror to in a web browser and copy the document ID.<br/>
  ![image](https://user-images.githubusercontent.com/34687786/123838090-e2ed4400-d90b-11eb-8459-4fd418a71ff4.png)
* Enter the document ID in the respective prompt in eclipse.<br/>
  ![image](https://user-images.githubusercontent.com/34687786/129474190-cc7490ec-53be-4abc-9547-98e26f2145d4.png)
* The content of the opened file should be copied to the google document and automatically updated.

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "IJ2GDocs"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/danthe1st/IJ2GDocs/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Dev Setup
### Required programs
In order to develop this plugin, [IntelliJ IDEA](https://www.jetbrains.com/idea/) is required with a JDK set up.

The [Gradle](https://plugins.jetbrains.com/plugin/13112-gradle) and [Plugin DevKit](https://plugins.jetbrains.com/docs/intellij/setting-up-environment.html#configuring-intellij-platform-sdk) plugins need to be installed and [enabled](https://www.jetbrains.com/help/idea/managing-plugins.html) (both plugins should be installed by default).

### Download
This repository uses [git submodules](https://git-scm.com/book/en/v2/Git-Tools-Submodules) for shared code.
Because of this, the parameter `--recurse-submodules` is required when cloning the repository:
```bash
git clone --recurse-submodules https://github.com/danthe1st/IJ2GDocs
```

Alternatively, IDE2gDocs can be cloned directly from IntelliJ IDEA using the option `File`>`New`>`Project from Version Control`.

### Setup
The plugin can be imported in IntelliJ IDEA as a Gradle project.

This should automatically load run configurations.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
