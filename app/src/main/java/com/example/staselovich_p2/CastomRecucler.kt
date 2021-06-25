package com.example.staselovich_p2

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.staselovich_p2.Arrays.ArrayViewModel
import com.example.staselovich_p2.DataBase.User
import com.example.staselovich_p2.databinding.RecuclerViewBinding
import com.google.api.services.gmail.Gmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CastomRecucler(
    private val list: List<User>,
    private val context: Context,
    private val service: Gmail,
     val arrayViewModel: ArrayViewModel
) : androidx.recyclerview.widget.ListAdapter<UserMessageModell, CastomRecucler.MyHolder>(DiffCallBack()) {
    class MyHolder(val binding:RecuclerViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bindInternet(item: UserMessageModell) {
binding.textData.text = item.data
binding.text2Subject.text = item.subject
binding.text3From.text = item.from
    if(item.atachmenId.isNotEmpty()){
        binding.imageInvestments.visibility = View.VISIBLE
    }
        }
        fun bindNoInternet(item: User) {
            binding.textData.text = item.date
            binding.text2Subject.text = item.subject
            binding.text3From.text = item.from
            if(item.atachmenId.isNotEmpty()){
                binding.imageInvestments.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(RecuclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        when (isOnline(context)) {
            true -> {
                holder.bindInternet(UserMessagesModelClass.dataObject[position])
                holder.binding.imageInvestments.setOnClickListener {
                    Toast.makeText(context, "attachments downloaded", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        val currentMessage = UserMessagesModelClass.dataObject[position]
                            arrayViewModel.getData(service,currentMessage.messageId,currentMessage.atachmenId,currentMessage.filename)

                       }
                }
            }

            false -> {
                holder.bindNoInternet(list.get(position))
                holder.binding.imageInvestments.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        val currentMessage = UserMessagesModelClass.dataObject[position]
                        arrayViewModel.getData(service,currentMessage.messageId,currentMessage.atachmenId,currentMessage.filename)
                        Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getItemCount(): Int {
        return when (isOnline(context)) {
            true -> UserMessagesModelClass.dataObject.size
            false -> list.size
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ServiceCast")
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }

    class DiffCallBack: DiffUtil.ItemCallback<UserMessageModell>() {
        override fun areItemsTheSame(
            oldItem: UserMessageModell,
            newItem: UserMessageModell
        ): Boolean = oldItem.messageId == newItem.messageId

        override fun areContentsTheSame(
            oldItem: UserMessageModell,
            newItem: UserMessageModell
        ): Boolean = oldItem == newItem
    }
}