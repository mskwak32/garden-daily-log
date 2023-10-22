package com.mskwak.presentation.ui.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.mskwak.presentation.R
import com.mskwak.presentation.databinding.DialogSelectPhotoBinding
import com.mskwak.presentation.util.showPermissionDeniedSnackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SelectPhotoDialog : DialogFragment() {
    private lateinit var binding: DialogSelectPhotoBinding
    var imageBitmapListener: ((bitmap: Bitmap) -> Unit)? = null

    private val imageSelectResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                result.data?.data?.let { uri ->
                    val bitmap = contentUriToBitmap(uri)
                    imageBitmapListener?.invoke(bitmap)
                }
                dismiss()
            } else {
                Timber.d("getImageFail: resultCode= ${result.resultCode}")
            }
        }
    private val cameraResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    imageBitmapListener?.invoke(it)
                }
                dismiss()
            } else {
                Timber.d("getCameraFail: resultCode= ${result.resultCode}")
            }
        }
    private val imagePermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                openImageSelector()
            } else {
                requireContext().showPermissionDeniedSnackbar(
                    requireView(),
                    R.string.message_photo_image_permission
                )
            }
        }
    private val cameraPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                requireContext().showPermissionDeniedSnackbar(
                    requireView(),
                    R.string.message_photo_camera_permission
                )
            }
        }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSelectPhotoBinding.inflate(requireActivity().layoutInflater)
        val builder = AlertDialog.Builder(requireContext(), R.style.WrapContentDialog).apply {
            setView(binding.root)
        }
        return builder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.dialog = this
        return binding.root
    }

    fun onCameraClick() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                requireContext().showPermissionDeniedSnackbar(
                    requireView(),
                    R.string.message_photo_camera_permission
                )
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun onImageClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            openImageSelector()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openImageSelector()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    requireContext().showPermissionDeniedSnackbar(
                        requireView(),
                        R.string.message_photo_image_permission
                    )
                }
                else -> {
                    imagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun openImageSelector() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        imageSelectResultLauncher.launch(intent)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            cameraResultLauncher.launch(intent)
        }
    }

    private fun contentUriToBitmap(contentUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(requireContext().contentResolver, contentUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, contentUri)
        }
    }
}