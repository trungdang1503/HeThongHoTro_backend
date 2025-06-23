from dotenv import load_dotenv
import os
import sqlite3
from telebot import TeleBot, util
from flask import Flask, jsonify, request, send_from_directory
from threading import Thread, Lock
import datetime

load_dotenv()
USER_ID = os.getenv('USER_ID')
BOT_TOKEN = os.getenv('BOT_TOKEN')
DATABASE_FILE = 'telegram_archive.db'
STORAGE_PATH = 'storage'

bot = TeleBot(BOT_TOKEN)
app = Flask(__name__)
db_lock = Lock()

def init_db():
    os.makedirs(STORAGE_PATH, exist_ok=True)
    with db_lock:
        conn = sqlite3.connect(DATABASE_FILE, check_same_thread=False)
        cursor = conn.cursor()
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS messages (
                notification_id INTEGER PRIMARY KEY,
                original_message_id INTEGER NOT NULL,
                group_id INTEGER NOT NULL,
                group_name TEXT NOT NULL,
                sender TEXT NOT NULL,
                content_type TEXT NOT NULL,
                content TEXT,
                file_id TEXT,
                local_file_path TEXT,
                timestamp TEXT NOT NULL
            )
        ''')
        conn.commit()
        conn.close()

@bot.message_handler(func=lambda msg: True, content_types=util.content_type_media)
def handle_messages(message):
    if message.chat.type in ['group', 'supergroup']:
        group_title, group_id, sender, content_type, caption = message.chat.title, message.chat.id, message.from_user.full_name, message.content_type, message.caption or ""
        sent = None
        message_content, file_id, local_file_path = "", None, None
        
        sent = bot.forward_message(USER_ID, group_id, message.message_id)

        if content_type != 'text':
            try:
                if content_type == 'photo': file_id = message.photo[-1].file_id
                elif content_type == 'document': file_id = message.document.file_id
                elif content_type == 'video': file_id = message.video.file_id
                elif content_type == 'audio': file_id = message.audio.file_id
                elif content_type == 'voice': file_id = message.voice.file_id
                elif content_type == 'animation': file_id = message.animation.file_id
                
                if file_id:
                    file_info = bot.get_file(file_id)
                    downloaded_file = bot.download_file(file_info.file_path)
                    
                    original_filename = os.path.basename(file_info.file_path)
                    local_file_path = os.path.join(STORAGE_PATH, f"{sent.message_id}_{original_filename}")
                    
                    with open(local_file_path, 'wb') as new_file:
                        new_file.write(downloaded_file)
                    
                    print(f"Đã tải và lưu file tại: {local_file_path}")
            except Exception as e:
                print(f"Lỗi khi tải file: {e}")
                local_file_path = None
        
        if content_type == 'text': message_content = message.text
        else: message_content = caption
        
        if sent:
            with db_lock:
                conn = sqlite3.connect(DATABASE_FILE, check_same_thread=False)
                cursor = conn.cursor()
                cursor.execute(
                    "INSERT INTO messages (notification_id, original_message_id, group_id, group_name, sender, content_type, content, file_id, local_file_path, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    (sent.message_id, message.message_id, group_id, group_title, sender, content_type, message_content, file_id, local_file_path, datetime.datetime.now().isoformat())
                )
                conn.commit()
                conn.close()

@app.route('/messages', methods=['GET'])
def get_messages():
    with db_lock:
        conn = sqlite3.connect(DATABASE_FILE, check_same_thread=False)
        conn.row_factory = sqlite3.Row
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM messages ORDER BY notification_id DESC LIMIT 100")
        messages = [dict(row) for row in cursor.fetchall()]
        conn.close()
    return jsonify(messages)

@app.route('/files/<int:notification_id>', methods=['GET'])
def get_file(notification_id):
    with db_lock:
        conn = sqlite3.connect(DATABASE_FILE, check_same_thread=False)
        cursor = conn.cursor()
        cursor.execute("SELECT local_file_path FROM messages WHERE notification_id = ?", (notification_id,))
        result = cursor.fetchone()
        conn.close()
    
    if result and result[0]:
        file_path = result[0]
        directory = os.path.dirname(file_path)
        filename = os.path.basename(file_path)
        return send_from_directory(directory, filename, as_attachment=False)
    else:
        return jsonify({"status": "error", "message": "File not found or not accessible"}), 404

@app.route('/reply', methods=['POST'])
def send_reply():
    data = request.get_json()
    notification_id = data.get('notification_id')
    with db_lock:
        conn = sqlite3.connect(DATABASE_FILE, check_same_thread=False)
        cursor = conn.cursor()
        cursor.execute("SELECT group_id, original_message_id FROM messages WHERE notification_id = ?", (notification_id,))
        result = cursor.fetchone()
        conn.close()

    if result:
        group_id, original_message_id = result
        bot.send_message(group_id, data.get('reply_text'), reply_to_message_id=original_message_id)
        return jsonify({"status": "success"})
    return jsonify({"status": "error", "message": "Message not found"}), 404

def run_bot():
    print("Bot đang khởi động...")
    bot.infinity_polling()

if __name__ == '__main__':
    init_db()
    bot_thread = Thread(target=run_bot)
    bot_thread.daemon = True
    bot_thread.start()
    print("API server đang khởi động tại http://localhost:5000")
    app.run(host='0.0.0.0', port=5000)