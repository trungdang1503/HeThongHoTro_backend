from dotenv import load_dotenv
import os
from telebot import TeleBot

load_dotenv()

USER_ID = os.getenv('USER_ID')
BOT_TOKEN = os.getenv('BOT_TOKEN')

bot = TeleBot(BOT_TOKEN)

message_links = {}

@bot.message_handler(commands=['id'])
def send_id(message):
    user_id = message.from_user.id
    print(f"ID người dùng: {user_id}")
    bot.reply_to(message, f"Chào bạn! ID của bạn là {user_id}")

@bot.message_handler(commands=['start', 'hello'])
def send_welcome(message):
    bot.reply_to(message, "Bot đã sẵn sàng tiếp nhận và thông báo tin nhắn nhóm.")

@bot.message_handler(func=lambda msg: True, content_types=[
    'text', 'photo', 'document', 'video', 'audio', 'voice', 'animation'
])
def handle_group_messages(message):
    if message.chat.type in ['group', 'supergroup']:
        group_title = message.chat.title
        group_id = message.chat.id
        sender = message.from_user.full_name

        caption = message.caption or ""
        notify_text = (
            f"Tin nhắn từ nhóm:\n"
            f"Nhóm: {group_title}\n"
            f"Người gửi: {sender}\n"
        )

        sent = None
        if message.content_type == 'text':
            notify_text += f"Nội dung: {message.text}"
            sent = bot.send_message(USER_ID, notify_text)

        elif message.content_type == 'photo':
            file_id = message.photo[-1].file_id
            notify_text += f"Gửi một ảnh\nCaption: {caption}"
            sent = bot.send_photo(USER_ID, file_id, caption=notify_text)

        elif message.content_type == 'document':
            file_id = message.document.file_id
            notify_text += f"Gửi một tài liệu\nCaption: {caption}"
            sent = bot.send_document(USER_ID, file_id, caption=notify_text)

        elif message.content_type == 'video':
            file_id = message.video.file_id
            notify_text += f"Gửi một video\nCaption: {caption}"
            sent = bot.send_video(USER_ID, file_id, caption=notify_text)

        elif message.content_type == 'animation':
            file_id = message.animation.file_id
            notify_text += f"Gửi một ảnh GIF động\nCaption: {caption}"
            sent = bot.send_animation(USER_ID, file_id, caption=notify_text)

        elif message.content_type in ['audio', 'voice']:
            file_id = message.audio.file_id if message.content_type == 'audio' else message.voice.file_id
            notify_text += f"Gửi một file âm thanh\nCaption: {caption}"
            sent = bot.send_audio(USER_ID, file_id, caption=notify_text)

        if sent:
            message_links[sent.message_id] = (group_id, message.message_id)

    elif (
        str(message.from_user.id) == USER_ID
        and message.reply_to_message
        and message.reply_to_message.message_id in message_links
    ):
        group_id, reply_to_msg_id = message_links[message.reply_to_message.message_id]

        if message.content_type == 'text':
            bot.send_message(group_id, message.text, reply_to_message_id=reply_to_msg_id)

        elif message.content_type == 'photo':
            file_id = message.photo[-1].file_id
            bot.send_photo(group_id, file_id, caption=message.caption, reply_to_message_id=reply_to_msg_id)

        elif message.content_type == 'document':
            file_id = message.document.file_id
            bot.send_document(group_id, file_id, caption=message.caption, reply_to_message_id=reply_to_msg_id)

        elif message.content_type == 'video':
            file_id = message.video.file_id
            bot.send_video(group_id, file_id, caption=message.caption, reply_to_message_id=reply_to_msg_id)

        elif message.content_type == 'animation':
            file_id = message.animation.file_id
            bot.send_animation(group_id, file_id, caption=message.caption, reply_to_message_id=reply_to_msg_id)

        elif message.content_type in ['audio', 'voice']:
            file_id = message.audio.file_id if message.content_type == 'audio' else message.voice.file_id
            bot.send_audio(group_id, file_id, caption=message.caption, reply_to_message_id=reply_to_msg_id)

bot.infinity_polling()
