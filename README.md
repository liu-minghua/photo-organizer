# 📄 **LICENSE (MIT License)**

```
MIT License

Copyright (c) 2026 Minghua

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

You can save this as:

```
LICENSE
```

in your project root.

---

# 📘 **Updated README.md**

Below is the updated version with the new **License** section and a few refinements to make it shine on GitHub.

```
# Photo Organizer

A clean, modern JavaFX + Spring Boot desktop application designed to help users organize, migrate, and manage large photo collections with speed and clarity.  
This project includes a fully automated build pipeline using Maven, jlink, and jpackage to produce a native Windows MSI installer backed by a custom Java runtime image.

Photo Organizer is engineered for reliability, reproducibility, and ease of distribution — the result of a careful, methodical refinement process.

---

## 🌟 Features

- **JavaFX desktop UI**  
  Smooth, responsive interface for browsing and organizing photos.

- **Spring Boot backend**  
  Provides configuration, dependency management, and application lifecycle support.

- **Custom Java runtime image**  
  Built with `jlink` to include only the modules the app actually needs.

- **Native Windows installer (MSI)**  
  Created with `jpackage`, complete with Start Menu integration.

- **Reproducible build pipeline**  
  Scripts ensure consistent results with no manual steps or guesswork.

- **Thin JAR + curated dependencies**  
  Clean separation of application code and runtime libraries.

---

## 📦 Project Structure

```
photo-organizer/
│
├── src/                         # Application source code
│
├── scripts/                     # Automated build scripts
│   ├── populate-app-folder.ps1
│   ├── build-runtime.ps1
│   └── build-installer.ps1
│
├── jpackage-input/              # Inputs for jpackage
│   ├── app/                     # Populated automatically with runtime dependencies
│   └── photo-organizer-1.0.0.jar
│
├── .mvn/                        # Maven Wrapper support
├── mvnw
├── mvnw.cmd
│
├── pom.xml
├── PACKAGING_GUIDE.md
├── README.md
└── .gitignore
```

---

## 🚀 Build & Packaging Instructions

These steps reproduce the exact build pipeline used to generate the native Windows installer.

### 1. Populate runtime dependencies

Regenerates the dependency list and copies only the required runtime JARs into `jpackage-input/app`.

```
.\scripts\populate-app-folder.ps1
```

### 2. Build the custom Java runtime image

Uses `jlink` with JavaFX JMODs to create a minimal runtime.

```
.\scripts\build-runtime.ps1
```

### 3. Build the Windows MSI installer

Creates a native installer with Start Menu integration.

```
.\scripts\build-installer.ps1
```

The resulting MSI will appear in:

```
installer/
```

---

## 🛠 Requirements

- Windows 10 or later  
- JDK 21 (with JavaFX JMODs installed)  
- PowerShell  
- Git (optional)  

Maven is **not required** thanks to the Maven Wrapper (`mvnw`, `mvnw.cmd`).

---

## 📚 How It Works

Photo Organizer uses:

- **Spring Boot’s PropertiesLauncher** to load a thin JAR and external dependencies  
- **JavaFX** for the UI layer  
- **jlink** to build a custom runtime image  
- **jpackage** to produce a native installer  
- **PowerShell scripts** to automate the entire process  

The result is a clean, reproducible build pipeline that anyone can run.

---

## 🤝 Contributing

Contributions, suggestions, and improvements are welcome.  
Feel free to open issues or submit pull requests.

---

## 📄 License

This project is licensed under the **MIT License**.  
See the `LICENSE` file for details.

---

## 🌱 Acknowledgments

This project was refined through a careful, iterative process focused on clarity, correctness, and maintainability.  
Special thanks to the open‑source Java, JavaFX, and Spring communities whose tools make projects like this possible.