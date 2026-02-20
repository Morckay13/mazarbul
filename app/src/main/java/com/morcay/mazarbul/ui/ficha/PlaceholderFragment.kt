package com.morcay.mazarbul.ui.ficha

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.morcay.mazarbul.R

class PlaceholderFragment : Fragment(R.layout.fragment_placeholder) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val txt = arguments?.getString(ARG_TEXT) ?: ""
        view.findViewById<TextView>(R.id.tvPlaceholder).text = txt
    }

    companion object {
        private const val ARG_TEXT = "text"

        fun newInstance(text: String): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply { putString(ARG_TEXT, text) }
            }
        }
    }
}