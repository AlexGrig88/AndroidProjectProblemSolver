package com.alexgrig.education.problemsolver.fragments

import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.alexgrig.education.problemsolver.databinding.FragmentDialogPhotoBinding

class PhotoDialogFragment : DialogFragment() {

    lateinit var binding: FragmentDialogPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogPhotoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.photoDialogImageView
            .setImageBitmap(requireArguments().getParcelable(KEY_PHOTO_BITMAP))

        binding.okDialogButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(ConfirmationDialogFragment.TAG, "Dialog dismissed")
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Toast.makeText(requireContext(), "Dialog canceled", Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val TAG = "PhotoDialog"
        private const val KEY_PHOTO_BITMAP = "KEY_PHOTO_BITMAP"

        fun newInstance(img: Bitmap): PhotoDialogFragment {
            val args = Bundle().apply {
                putParcelable(KEY_PHOTO_BITMAP, img)
            }
            return PhotoDialogFragment().apply {
                arguments = args
            }
        }
    }
}