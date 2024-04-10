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
import com.dicoding.githubuser.databinding.FragmentFollowersBinding
import com.dicoding.githubuser.ui.viewmodel.FollowersViewModel

class FollowersFragment : Fragment() {

    private var _binding: FragmentFollowersBinding? = null
    private val binding: FragmentFollowersBinding
        get() = requireNotNull(_binding) { "FragmentFollowersBinding is null" }
    private lateinit var viewModel: FollowersViewModel
    private lateinit var adapter: UserAdapter
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFollowersBinding.bind(view)

        username = arguments?.getString(DetailUserActivity.EXTRA_USERNAME) ?: ""
        Log.d("FollowersFragment", "Username: $username")

        adapter = UserAdapter()

        binding.rvUserFollowers.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = this@FollowersFragment.adapter
        }

        viewModel = ViewModelProvider(this)[FollowersViewModel::class.java]
        viewModel.viewListFollowers(username)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.listFollowers.observe(viewLifecycleOwner) { followers ->
            adapter.submitList(followers)
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

        fun newInstance(username: String): FollowersFragment {
            val fragment = FollowersFragment()
            val bundle = Bundle().apply {
                putString(EXTRA_USERNAME, username)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}