package com.jaylangkung.eoffice_korem.surat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.SuratImg
import com.jaylangkung.eoffice_korem.databinding.BottomSheetGambarSuratBinding
import com.jsibbold.zoomage.ZoomageView

fun showImageSurat(ctx: Context, data: ArrayList<SuratImg>?) {
    val binding = BottomSheetGambarSuratBinding.inflate(LayoutInflater.from(ctx))
    val dialog = BottomSheetDialog(ctx).apply {
        setContentView(binding.root)
        setCancelable(false)
    }
    BottomSheetBehavior.from(binding.root.parent as View).apply {
        state = BottomSheetBehavior.STATE_EXPANDED
        isHideable = false
        isDraggable = false
    }

    binding.apply {
        if (data?.isNotEmpty() == true) {
            for (img in data) {
                val imageView = ZoomageView(ctx).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(0, 20, 0, 0)
                    }
                    scaleType = ImageView.ScaleType.MATRIX
                    id = img.id
                    autoCenter = true
                    isZoomable = true
                    setScaleRange(1f, 4f)
                }

                Glide.with(ctx)
                    .load(img.img)
                    .placeholder(R.drawable.ic_empty)
                    .error(R.drawable.ic_empty)
                    .into(imageView)

                llSuratImage.addView(imageView)
            }
        } else {
            // add empty images
            val emptyImageView = ZoomageView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(0, 20, 0, 0)
                }
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setImageResource(R.drawable.ic_empty)
                autoCenter = true
                isZoomable = true
                setScaleRange(1f, 4f)
            }

            llSuratImage.addView(emptyImageView)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    dialog.show()
}