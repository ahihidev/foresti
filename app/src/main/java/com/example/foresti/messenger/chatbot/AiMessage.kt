package com.example.foresti.messenger.chatbot

import android.graphics.Bitmap

data class AiMessage(
    val id: String,
    val text: String?,
    val image: Bitmap? = null,
    val senderAvatar: Bitmap? = null,
    val senderType: MessageSenderType
)