package com.jaylangkung.eoffice_korem.surat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.SuratImg
import com.jaylangkung.eoffice_korem.dataClass.SuratPdf
import com.jaylangkung.eoffice_korem.databinding.BottomSheetGambarSuratBinding
import com.jsibbold.zoomage.ZoomageView
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
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
            if (pdfs != null) {
                for (pdf in pdfs) {
                    val btnPdf = Button(ctx).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 20, 0, 0)
                        }
                        id = pdf.id
                        setTextColor(AppCompatResources.getColorStateList(context, R.color.white))
                        background = AppCompatResources.getDrawable(context, R.drawable.rounded_box)
                        backgroundTintList = AppCompatResources.getColorStateList(context, R.color.primaryColor)
                        text = pdf.pdf.substring(pdf.pdf.lastIndexOf("/") + 1)
                        setOnClickListener {
                            Toasty.info(ctx, "Membuka PDF", Toasty.LENGTH_SHORT).show()
                            downloadAndOpenPdf(ctx, pdf.pdf)
                        }
                    }
                    llSuratFiles.addView(btnPdf)
                }
            }

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

                    Glide.with(ctx).load(img.img).placeholder(R.drawable.ic_empty).error(R.drawable.ic_empty).into(imageView)

                    llSuratFiles.addView(imageView)
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

@OptIn(DelicateCoroutinesApi::class)
fun downloadAndOpenPdf(context: Context, pdfUrl: String) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            // Download the PDF file
            val url = URL(pdfUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val inputStream = connection.inputStream
            val fileName = pdfUrl.substring(pdfUrl.lastIndexOf("/") + 1)
            val file = File(context.filesDir, "pdfs/$fileName")
            file.parentFile?.mkdirs()

            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.close()
            inputStream.close()

            // Open the prompt to choose an application to open the downloaded file
            withContext(Dispatchers.Main) {
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
                val contentUri = FileProvider.getUriForFile(context, "com.jaylangkung.eoffice_korem.fileprovider", file)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(contentUri, mimeType)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}