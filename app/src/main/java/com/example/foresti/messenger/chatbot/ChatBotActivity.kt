package com.example.foresti.messenger.chatbot

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foresti.R
import com.example.foresti.databinding.ActivityChatBotBinding
import kotlinx.coroutines.launch

class ChatBotActivity : AppCompatActivity() {
    private val viewModel: ChatBotVM by viewModels()
    private val chatBotAdapter by lazy {
        ChatBotAdapter()
    }
    private lateinit var binding: ActivityChatBotBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initModel()
        initListener()
    }

    private fun initView() {
        binding.run {
            recyclerViewMessage.apply {
                layoutManager =
                    LinearLayoutManager(this@ChatBotActivity, LinearLayoutManager.VERTICAL, false)
                adapter = chatBotAdapter
            }
        }
    }

    private fun initModel() {
        lifecycleScope.launch {
            viewModel.outputContent.collect { response ->
                if (response.isNotEmpty()) {
                    chatBotAdapter.run {
                        updateBotMessageIfAvailable(response)
                    }
                }
            }
        }
    }

    private fun initListener() {
        binding.run {

            btnSend.setOnClickListener {
                val prompt = edtPrompt.text.toString()

                val userMessage = AiMessage(
                    id = MessageSenderType.USER.name + System.currentTimeMillis().toString(),
                    text = prompt,
                    senderType = MessageSenderType.USER
                )
                val botMessage = AiMessage(
                    id = MessageSenderType.BOT.name + System.currentTimeMillis().toString(),
                    text = "Thinking...",
                    senderAvatar = BitmapFactory.decodeResource(resources, R.drawable.ic_avatar),
                    senderType = MessageSenderType.BOT
                )

                chatBotAdapter.appendList(listOf(userMessage, botMessage))
                viewModel.generateContent(prompt)
                edtPrompt.text.clear()
            }
        }
    }
}