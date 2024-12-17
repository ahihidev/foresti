package com.example.foresti.messenger

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foresti.databinding.FragmentMessengerBinding
import com.example.foresti.messenger.chatbot.ChatBotActivity

class MessengerFragment : Fragment() {
    private var _binding: FragmentMessengerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessengerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.llChatBot.setOnClickListener {
            startActivity(Intent(requireContext(), ChatBotActivity::class.java))
        }
    }

}