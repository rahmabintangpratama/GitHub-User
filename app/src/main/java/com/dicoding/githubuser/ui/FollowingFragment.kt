package com.dicoding.githubuser.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.databinding.FragmentFollowingBinding
import com.dicoding.githubuser.ui.viewmodel.FollowingViewModel

class FollowingFragment : Fragment() {

    private var _binding: FragmentFollowingBinding? = null
    private val binding: FragmentFollowingBinding
        get() = requireNotNull(_binding) { "FragmentFollowingBinding is null" }
    private lateinit var viewModel: FollowingViewModel
    private lateinit var adapter: UserAdapter
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFollowingBinding.bind(view)

        username = arguments?.getString(DetailUserActivity.EXTRA_USERNAME) ?: ""
        Log.d("FollowingFragment", "Username: $username")

        adapter = UserAdapter()

        binding.rvUserFollowing.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = this@FollowingFragment.adapter
        }

        viewModel = ViewModelProvider(this)[FollowingViewModel::class.java]
        viewModel.viewListFollowing(username)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.listFollowing.observe(viewLifecycleOwner) { following ->
            adapter.submitList(following)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val EXTRA_USERNAME = "extra_username"

        fun newInstance(username: String): FollowingFragment {
            val fragment = FollowingFragment()
            val bundle = Bundle().apply {
                putString(EXTRA_USERNAME, username)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}