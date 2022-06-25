package com.jaylangkung.brainnet_staff.menu_pelanggan.restart

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
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ItemUserBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserItemHolder>(), Filterable {

    private var listUser = ArrayList<UserEntity>()
    private var listUserFilter = ArrayList<UserEntity>()

    fun setUserItem(userItem: List<UserEntity>?) {
        if (userItem == null) return
        this.listUser.clear()
        this.listUser.addAll(userItem)
        this.listUserFilter = userItem as ArrayList<UserEntity>
        notifyItemRangeChanged(0, listUser.size)
    }

    class UserItemHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var myPreferences: MySharedPreferences

        fun bind(userItem: UserEntity) {
            with(binding) {
                myPreferences = MySharedPreferences(itemView.context)

                val nama = userItem.nama.capitalizeWords()
                val paket = userItem.paket
                val alamat = userItem.alamat_pasang.capitalizeWords()
                val user = userItem.user
                val pass = userItem.password
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
                            service.restartUser(user, pass, nama, paket, tokenAuth).enqueue(object : Callback<DefaultResponse> {
                                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                                    if (response.isSuccessful) {
                                        if (response.body()!!.status == "success") {
                                            Toasty.success(itemView.context, response.body()!!.message, Toasty.LENGTH_LONG).show()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemHolder {
        val itemUserBinding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserItemHolder(itemUserBinding)
    }

    override fun onBindViewHolder(holder: UserItemHolder, position: Int) {
        val vendorItem = listUser[position]
        holder.bind(vendorItem)
    }

    override fun getItemCount(): Int = listUser.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val filterResults = FilterResults()

                if (constraint == null || constraint.length < 0) {
                    filterResults.count = listUserFilter.size
                    filterResults.values = listUserFilter
                } else {
                    val charSearch = constraint.toString()

                    val resultList = ArrayList<UserEntity>()

                    for (row in listUserFilter) {
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
                listUser = results?.values as ArrayList<UserEntity>
                notifyDataSetChanged()
            }

        }
    }
}



