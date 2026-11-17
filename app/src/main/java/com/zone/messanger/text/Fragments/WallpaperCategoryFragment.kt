package com.zone.messanger.text.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.zone.messanger.text.PANDA_Activity.SetWallpaperActivity
import com.zone.messanger.text.PANDA_Adapter.WallpaperAdapter
import com.zone.messanger.text.PANDA_ads.WallpaperCategory
import com.zone.messanger.text.databinding.FragmentWallpaperCategoryBinding

class WallpaperCategoryFragment : Fragment() {

    private var _binding: FragmentWallpaperCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var category: WallpaperCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        category = requireArguments()
            .getParcelable(ARG_CATEGORY)
            ?: error("WallpaperCategory missing in arguments")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWallpaperCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shuffledWallpapers = category.wallpapers.shuffled()

        val adapter = WallpaperAdapter { wallpaper ->
            val intent = Intent(requireContext(), SetWallpaperActivity::class.java).apply {
                putExtra(SetWallpaperActivity.WALLPAPER_URL, wallpaper.imageUrl)
            }

            startActivity(intent)
        }

        binding.rvWallpapers.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvWallpapers.adapter = adapter
        binding.rvWallpapers.setHasFixedSize(true)

        adapter.submitList(shuffledWallpapers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: WallpaperCategory): WallpaperCategoryFragment {
            return WallpaperCategoryFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CATEGORY, category)
                }
            }
        }
    }
}