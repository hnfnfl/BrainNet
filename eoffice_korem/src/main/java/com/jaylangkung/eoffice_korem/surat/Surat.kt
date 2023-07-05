package com.jaylangkung.eoffice_korem.surat

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.SuratImg
import com.jaylangkung.eoffice_korem.databinding.BottomSheetGambarSuratBinding

fun showImageSurat(ctx: Context, data: ArrayList<SuratImg>?) {
    val binding: BottomSheetGambarSuratBinding = BottomSheetGambarSuratBinding.inflate(LayoutInflater.from(ctx))
    val dialog = BottomSheetDialog(ctx).apply {
        setCancelable(false)
        setContentView(binding.root)
        behavior.apply {
            isHideable = false
        }
    }

    binding.apply {
        data?.let {
            for (img in it) {
                val imageView = ZoomClass(ctx)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(0, 12, 0, 0)
                }
                imageView.layoutParams = layoutParams
                imageView.scaleType = ImageView.ScaleType.MATRIX
                imageView.id = img.id
                Glide.with(ctx)
                    .load(img.img)
                    .apply(RequestOptions().override(1600))
                    .placeholder(R.drawable.ic_empty)
                    .error(R.drawable.ic_empty)
                    .into(imageView)
                llSuratImage.addView(imageView)
            }
        }

        separator.setOnClickListener {
            dialog.dismiss()
        }
    }

    dialog.show()

}