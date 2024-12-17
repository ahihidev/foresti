package com.example.foresti.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foresti.R
import com.example.foresti.databinding.FragmentCallBinding

class CallFragment : Fragment() {
    private var _binding: FragmentCallBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun initListener() {
        val adapter = FakeCallAdapter { contact ->
            val fragment = FakeCallFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerView.adapter = adapter
        adapter.submitList(
            listOf(
                Contact("Mother"),
                Contact("Father"),
                Contact("Brother"),
                Contact("Sister")
            )
        )
    }

}