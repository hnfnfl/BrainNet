package com.jaylangkung.eoffice_korem.surat

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.SuratImg
import com.jaylangkung.eoffice_korem.dataClass.SuratPdf
import com.jaylangkung.eoffice_korem.databinding.BottomSheetGambarSuratBinding
import com.jsibbold.zoomage.ZoomageView
import java.io.File
import java.net.URL

fun showFilesSurat(ctx: Context, imgs: ArrayList<SuratImg>?, pdfs: ArrayList<SuratPdf>?) {
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
        if (imgs?.isNotEmpty() == true || pdfs?.isNotEmpty() == true) {
            if (imgs != null) {
                for (img in imgs) {
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

                    llSuratFiles.addView(imageView)
                }
            }

            if (pdfs != null) {
                for (pdf in pdfs) {
                    val url = pdf.pdf
                    val outputNameFile = url.substring(url.lastIndexOf("/") + 1)
                    val file = downloadFile(url, outputNameFile)
                    val pdfView = PDFView(ctx, null).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
                        ).apply {
                            setMargins(0, 20, 0, 0)
                        }
                        id = pdf.id
                        fromFile(file)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .defaultPage(0)
                            .enableAnnotationRendering(false)
                            .password(null)
                            .scrollHandle(null)
                            .enableAntialiasing(true)
                            .spacing(0)
                            .load()
                    }

                    llSuratFiles.addView(pdfView)

                    Runtime.getRuntime().addShutdownHook(Thread {
                        file.delete()
                    })
                }
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

            llSuratFiles.addView(emptyImageView)
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    dialog.show()
}

fun downloadFile(url: String, fileName: String): File {
//    val file = File(fileName)
//    if (file.exists()) {
//        // The file already exists, so we don't need to create it
//        return file
//    }
    val path = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS
    )
    val file = File(path, "/$fileName")
    Thread {
        val inputStream = URL(url).openStream()
        val outputStream = file.outputStream()
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }.start()

    return file
}