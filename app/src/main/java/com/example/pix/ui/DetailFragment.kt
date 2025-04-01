package com.example.pix.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.pix.data.flickr.dto.PhotoDto
import com.example.pix.databinding.FragmentDetailBinding
import com.example.pix.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()

    companion object {
        private const val PHOTO_ID_KEY = "photo_id"
        private const val SECRET_KEY = "secret"
        private const val SERVER_KEY = "server"
        private const val TITLE_KEY = "title"

        fun newInstance(photo: PhotoDto): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(PHOTO_ID_KEY, photo.id)
                    putString(SECRET_KEY, photo.secret)
                    putString(SERVER_KEY, photo.server)
                    putString(TITLE_KEY, photo.title)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photo = arguments?.let { bundle ->
            PhotoDto(
                id = bundle.getString(PHOTO_ID_KEY) ?: "",
                secret = bundle.getString(SECRET_KEY) ?: "",
                server = bundle.getString(SERVER_KEY) ?: "",
                title = bundle.getString(TITLE_KEY) ?: ""
            )
        }

        photo?.let {
            viewModel.setPhoto(it)
            viewModel.loadPhoto(it.id)
            loadImage(it)
        } ?: run {
            Toast.makeText(requireContext(), "Photo data not available", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun loadImage(photo: PhotoDto) {
        binding.fullScreenTitle.text = photo.title

        Glide.with(this)
            .load(photo.getImageUrl("b"))
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    viewModel.setImageLoadingState(Resource.Error("Failed to load image"))
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    viewModel.setImageLoadingState(Resource.Success(Unit))
                    return false
                }
            })
            .into(binding.zoomageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}