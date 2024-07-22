package com.pasha.about_app

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.pasha.about_app.databinding.FragmentAboutAppBinding
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.picasso.PicassoDivImageLoader
import com.yandex.div.video.ExoDivPlayerFactory


class AboutAppFragment : Fragment() {
    private var _binding: FragmentAboutAppBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageLoader = PicassoDivImageLoader(requireContext())
        val configuration = DivConfiguration.Builder(imageLoader)
            .actionHandler(CustomDivActionHandler(
                navigationBackAction = {
                    findNavController().navigateUp()
                }
            ))
            .divPlayerFactory(ExoDivPlayerFactory(requireContext()))
            .visualErrorsEnabled(true)
            .build()

        val divData = readJSONFromAssets(Constants.FileName)?.asDiv2DataWithTemplates()
        val div2View = Div2View(
            Div2Context(
                requireActivity(),
                configuration,
                lifecycleOwner = viewLifecycleOwner
            )
        )
        binding.root.addView(div2View)
        div2View.setData(divData, DivDataTag(divData!!.logId))

        div2View.setVariable("is_dark_theme", isDarkTheme().toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun isDarkTheme(): Boolean {
        return requireActivity().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}