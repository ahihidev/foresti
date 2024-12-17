package com.example.foresti.messenger.chatbot

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foresti.databinding.ItemAiMessageReceivedBinding
import com.example.foresti.databinding.ItemAiMessageSentBinding

class ChatBotAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val differ = AsyncListDiffer(this, ChatBotDiffUtil)

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int =
        when (differ.currentList[position].senderType) {
            MessageSenderType.USER -> VIEW_TYPE_SENT
            MessageSenderType.BOT -> VIEW_TYPE_RECEIVED
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_SENT) {
            SentAiMessageViewHolder(
                ItemAiMessageSentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceivedAiMessageViewHolder(
                ItemAiMessageReceivedBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = differ.currentList[position]
        when (holder) {
            is SentAiMessageViewHolder -> {
                holder.bind(message)
            }

            is ReceivedAiMessageViewHolder -> {
                holder.bind(message)
            }
        }
    }

    fun appendList(list: List<AiMessage>) {
        differ.submitList(differ.currentList + list)
    }

    fun updateBotMessageIfAvailable(text: String) {
        val lastPosition = differ.currentList.size - 1

        if (differ.currentList.isEmpty() || differ.currentList[lastPosition].senderType != MessageSenderType.BOT) {
            return
        }

        val currentList = differ.currentList.toMutableList()
        currentList[lastPosition] = currentList[lastPosition].copy(text = text)

        differ.submitList(currentList) {
            notifyItemChanged(lastPosition)
        }
    }

    private fun findMarkdownIndices(text: String): Pair<Map<MarkdownType, List<IntRange>>, String> {
        val patterns = mapOf(
            MarkdownType.HEADING_1 to Regex("^#(?!#) ?(.+)$", RegexOption.MULTILINE),
            MarkdownType.HEADING_2 to Regex("^## ?(.+)$", RegexOption.MULTILINE),
            MarkdownType.BOLD to Regex("\\*\\*(?!\\*)(.*?)\\*\\*")
        )

        val matches = mutableMapOf<MarkdownType, MutableList<IntRange>>()
        var processedText = text

        for ((type, regex) in patterns) {
            regex.findAll(processedText).forEach { match ->
                val fullMatch = match.value
                val content = match.groupValues[1]
                val startIndex = processedText.indexOf(fullMatch)

                processedText = processedText.replaceFirst(fullMatch, content)

                matches.getOrPut(type) { mutableListOf() }
                    .add(
                        IntRange(
                            if (startIndex < 0) 0 else startIndex,
                            startIndex + content.length - 1
                        )
                    )
            }
        }

        return matches to processedText
    }

    private fun getFormatAiResponseText(text: String): SpannableStringBuilder {
        val (indices, processedText) = findMarkdownIndices(text)
        val spannableStringBuilder = SpannableStringBuilder(processedText)

        indices.forEach { (type, rages) ->
            rages.forEach { range ->
                when (type) {
                    MarkdownType.HEADING_1 -> {
                        spannableStringBuilder.setSpan(
                            RelativeSizeSpan(1.5f),
                            range.first,
                            range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannableStringBuilder.setSpan(
                            StyleSpan(Typeface.BOLD),
                            range.first,
                            range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

                    MarkdownType.HEADING_2 -> {
                        spannableStringBuilder.setSpan(
                            RelativeSizeSpan(1.3f),
                            range.first,
                            range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannableStringBuilder.setSpan(
                            StyleSpan(Typeface.BOLD),
                            range.first,
                            range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

                    MarkdownType.BOLD -> {
                        spannableStringBuilder.setSpan(
                            StyleSpan(Typeface.BOLD),
                            range.first,
                            range.last + 1,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }

        return spannableStringBuilder
    }


    inner class SentAiMessageViewHolder(private val binding: ItemAiMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: AiMessage) {
            binding.run {
                tvMessage.text = message.text
            }
        }
    }

    inner class ReceivedAiMessageViewHolder(private val binding: ItemAiMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: AiMessage) {
            binding.run {
                tvMessage.text = message.text?.let { getFormatAiResponseText(it) }
                ivAvatar.setImageBitmap(message.senderAvatar)
            }
        }
    }

    object ChatBotDiffUtil : DiffUtil.ItemCallback<AiMessage>() {
        override fun areItemsTheSame(oldItem: AiMessage, newItem: AiMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AiMessage, newItem: AiMessage): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
}
