```markdown
# 🤝 Contributing to Photo Organizer

Thank you for your interest in contributing!  
Photo Organizer is built to help people bring clarity and order to their photo libraries, and your ideas can make it even better.

This guide explains how to get involved, report issues, propose features, and submit code changes.

---

## 🧭 Ways to Contribute

### 🐞 Report Bugs
If something isn’t working as expected:
- Open an issue
- Describe the problem clearly
- Include steps to reproduce
- Add logs or screenshots if helpful

### 💡 Suggest Features
Have an idea that would help others?
- Check the [Roadmap](ROADMAP.md)
- Open a feature request issue
- Explain the use case and why it matters

### 🛠️ Submit Code Improvements
Pull requests are welcome!  
Before you start:
1. Open an issue to discuss the change  
2. Fork the repository  
3. Create a feature branch  
4. Write clear, maintainable code  
5. Add or update documentation if needed  
6. Submit a pull request with a concise description

---

## 🧰 Development Setup

### Requirements
- Java 21+
- Maven
- Git
- Windows (for MSI packaging)

### Build the project
```bash
mvn clean install
```

### Run the application
```bash
mvn spring-boot:run
```

### Build the installer
```powershell
.\scripts\build-installer.ps1
```

---

## 📐 Code Style
- Follow standard Java conventions
- Keep methods small and focused
- Prefer clarity over cleverness
- Document non-obvious logic

---

## 🛡️ Code of Conduct
Be respectful, constructive, and inclusive.  
We welcome contributors of all backgrounds and experience levels.

---

## 🎉 Thank You
Your time and effort make this project better for everyone.  
Whether you’re fixing a bug, proposing a feature, or improving documentation — your contribution matters.
```