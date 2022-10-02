package com.jaylangkung.indirisma.menu_pelanggan.restart

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ItemUserBinding
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class UserAdapter : RecyclerView.Adapter<UserAdapter.ItemHolder>(), Filterable {

    private var list = ArrayList<UserEntity>()
    private var listFilter = ArrayList<UserEntity>()

    fun setItem(item: List<UserEntity>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        this.listFilter = item as ArrayList<UserEntity>
        notifyItemRangeChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var myPreferences: MySharedPreferences

        fun bind(item: UserEntity) {
            with(binding) {
                myPreferences = MySharedPreferences(itemView.context)

                val nama = item.nama.capitalizeWords()
                val paket = item.paket
                val alamat = item.alamat_pasang.capitalizeWords()
                val user = item.user
                val pass = item.password
                val tokenAuth = itemView.context.getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
                tvName.text = itemView.context.getString(R.string.user_name, nama, paket)
                tvAddress.text = itemView.context.getString(R.string.user_address, alamat)
                tvUsername.text = itemView.context.getString(R.string.user_username, user)
                tvPass.text = itemView.context.getString(R.string.user_pass, pass)

                tvUsername.setOnClickListener {
                    copyTextToClipboard(user)
                }

                tvPass.setOnClickListener {
                    copyTextToClipboard(pass)
                }

                itemView.setOnClickListener {
                    Log.e("debug", "username = $user, pass = $pass")
                    val mDialog = MaterialDialog.Builder(itemView.context as Activity)
                        .setTitle("Konfirmasi Restart")
                        .setMessage("Apakah Anda yakin ingin me-restart router menu_pelanggan $nama?")
                        .setCancelable(true)
                        .setPositiveButton(itemView.context.getString(R.string.yes), R.drawable.ic_restart)
                        { dialogInterface, _ ->
                            val service = RetrofitClient().apiRequest().create(DataService::class.java)
                            service.restartUser(user, pass, nama, paket, tokenAuth, "true").enqueue(object : Callback<DefaultResponse> {
                                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                                    if (response.isSuccessful) {
                                        if (response.body()!!.status == "success") {
                                            Toasty.warning(itemView.context, "Router User sedang dalam proses restart", Toasty.LENGTH_LONG).show()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                    Toasty.error(itemView.context, t.message.toString(), Toasty.LENGTH_LONG).show()
                                }
                            })
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton(itemView.context.getString(R.string.no), R.drawable.ic_close)
                        { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                        .build()
                    // Show Dialog
                    mDialog.show()
                }
            }
        }

        private fun String.capitalizeWords(): String =
            split(" ").joinToString(" ") { it ->
                it.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }

        private fun copyTextToClipboard(text: String) {
            val myClipboard = itemView.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("copied", text)
            myClipboard.setPrimaryClip(myClip)
            Toasty.success(itemView.context, "Teks berhasil disalin", Toasty.LENGTH_LONG).show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val filterResults = FilterResults()

                if (constraint == null || constraint.length < 0) {
                    filterResults.count = listFilter.size
                    filterResults.values = listFilter
                } else {
                    val charSearch = constraint.toString()

                    val resultList = ArrayList<UserEntity>()

                    for (row in listFilter) {
                        if (row.nama.lowercase().contains(charSearch.lowercase())
                            ||
                            row.alamat_pasang.lowercase().contains(charSearch.lowercase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    filterResults.count = resultList.size
                    filterResults.values = resultList
                }
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                list = results?.values as ArrayList<UserEntity>
                notifyDataSetChanged()
            }

        }
    }
}



