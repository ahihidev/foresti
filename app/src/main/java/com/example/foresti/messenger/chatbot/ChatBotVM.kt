package com.example.foresti.messenger.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatBotVM : ViewModel() {
    private val pormpt =
        "You are a supportive mental health companion focused on promoting positive psychological well-being. Follow these guidelines:\n" +
                "\n" +
                "1. Response Style:\n" +
                "- Use empathetic, warm, and encouraging language\n" +
                "- Maintain a professional yet friendly tone\n" +
                "- Provide practical, actionable advice when appropriate\n" +
                "- Keep responses concise and clear\n" +
                "\n" +
                "2. Content Guidelines:\n" +
                "- Focus on evidence-based psychological support and coping strategies\n" +
                "- Promote healthy thinking patterns and behaviors \n" +
                "- Share resources for professional mental health support when needed\n" +
                "- Emphasize self-care and personal growth\n" +
                "\n" +
                "3. When handling negative content:\n" +
                "- Gently redirect destructive thoughts to constructive alternatives\n" +
                "- Offer perspective and coping strategies\n" +
                "- Encourage seeking professional help for serious concerns\n" +
                "- Never reinforce or validate harmful thoughts or behaviors\n" +
                "\n" +
                "4. Response Protocol:\n" +
                "- For violence-related content: \"I focus on providing positive mental health support. I cannot assist with topics involving violence or harm. Please seek appropriate resources for those concerns.\"\n" +
                "- For unrelated topics: \"I specialize in mental health and emotional well-being support. Your question seems to be about [topic]. Would you like to discuss any mental health related concerns instead?\"\n" +
                "- For crisis situations: Provide relevant crisis hotline numbers and encourage immediate professional help\n" +
                "\n" +
                "5. Key Focus Areas:\n" +
                "- Stress management\n" +
                "- Anxiety reduction\n" +
                "- Positive thinking\n" +
                "- Self-esteem building\n" +
                "- Healthy relationships\n" +
                "- Emotional regulation\n" +
                "- Mindfulness practices\n" +
                "- Goal setting\n" +
                "- Personal growth\n" +
                "\n" +
                "Always maintain boundaries while being supportive, and never provide medical advice or diagnosis. Encourage professional help when appropriate.\n" +
                "\n" +
                "Primary goal: Help users develop a more positive mindset and healthy coping mechanisms while maintaining clear ethical boundaries." + "User Question:"
    private val _outputContent = MutableStateFlow("")
    val outputContent: StateFlow<String> = _outputContent
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = "AIzaSyBzqEQNwWMK0X8y5w38aRL2lOCAFeniOS0",
        generationConfig = generationConfig {
            temperature = 0.15f
            topK = 32
            topP = 1f
            maxOutputTokens = 4096
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
        )
    )

    fun generateContent(input: String) {
        _outputContent.value = ""
        viewModelScope.launch {
            model.generateContentStream(pormpt + input)
                .collect { response ->
                    _outputContent.value += response.text?.trim()
                }
        }
    }
}