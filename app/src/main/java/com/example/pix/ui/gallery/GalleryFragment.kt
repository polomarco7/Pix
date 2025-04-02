package com.example.pix.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pix.R
import com.example.pix.databinding.FragmentGalleryBinding
import com.example.pix.ui.gallery.GalleryViewModel
import com.example.pix.ui.adapter.FlickrPhotoAdapter
import com.example.pix.ui.adapter.PhotosLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var adapter: FlickrPhotoAdapter
    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()
        setupMenu()
        setupToolbar()
    }

    private fun setupAdapter() {
        adapter = FlickrPhotoAdapter { photo ->
            val bundle = Bundle().apply {
                putString("photo_id", photo.id)
                putString("secret", photo.secret)
                putString("server", photo.server)
                putString("title", photo.title)
            }

            findNavController().navigate(
                R.id.action_galleryFragment_to_detailFragment,
                bundle
            )
        }
    }

    private fun setupRecyclerView() {
        val loadStateAdapter = PhotosLoadStateAdapter {
            adapter.retry()
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter?.getItemViewType(position)) {
                            FlickrPhotoAdapter.Companion.LOADING_ITEM_TYPE -> spanCount
                            else -> 1
                        }
                    }
                }
            }
            adapter = this@GalleryFragment.adapter.withLoadStateHeaderAndFooter(
                header = loadStateAdapter,
                footer = loadStateAdapter
            )
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.photos.collectLatest { pagingDataFlow ->
                pagingDataFlow?.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading

                val errorState = loadState.refresh as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${it.error.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_gallery, menu)

                val searchItem = menu.findItem(R.id.action_search)
                searchView = searchItem.actionView as SearchView

                setupSearchView()
                observeQueryChanges()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        }, viewLifecycleOwner)
    }

    private fun setupSearchView() {
        searchView?.apply {
            queryHint = getString(R.string.search_hint)

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchQuery.collect { query ->
                    if (!isIconified) {
                        setQuery(query, false)
                    }
                }
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.searchPhotos(it)
                        searchView?.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        if (it.length >= 3) {
                            viewModel.searchPhotos(it)
                        }
                    }
                    return true
                }
            })
        }
    }

    private fun observeQueryChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchQuery.collect { query ->
                if (searchView?.isIconified == false && searchView?.query?.toString() != query) {
                    searchView?.setQuery(query, false)
                }
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(requireActivity().findViewById(R.id.toolbar))
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(false)
                title = getString(R.string.app_name)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}