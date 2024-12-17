package com.example.foresti.call

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foresti.R
import com.example.foresti.databinding.FragmentFakeCallBinding

class FakeCallFragment : Fragment(), SurfaceHolder.Callback {
    private var _binding: FragmentFakeCallBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private var isSurfaceCreated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFakeCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.videoSurface.holder.addCallback(this)
        binding.btnEndCall.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, CallFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initVideo() {
        try {
            val assetFileDescriptor = requireContext().assets.openFd("fake_video.mp4")
            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                setDisplay(binding.videoSurface.holder)
                isLooping = true
                prepareAsync()
                setOnPreparedListener { start() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isSurfaceCreated = true
        initVideo()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceCreated = false
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int, height: Int
    ) {
    }
}
