import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shurish.musicplayerapi.Data
import com.shurish.musicplayerapi.RunningService
import com.shurish.musicplayerapi.databinding.ItemViewBinding

class MyAdapter(val context :Context) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val mediaPlayer = MediaPlayer()
    private lateinit var currentPlayingUri: String // Store the URI of the currently playing music
    private lateinit var playIntent: Intent // Store the intent for playing music
    private lateinit var pauseIntent: Intent // Store the intent for pausing music

    class MyViewHolder(val binding : ItemViewBinding ) : RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return  oldItem== newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val music = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(music.album.cover).into(itemImage)
            itemMusicName.text = music.title

            playButton.setOnClickListener {
                currentPlayingUri = music.preview // Update the currently playing URI
                val imageUrl = music.album.cover
                playIntent = Intent(context, RunningService::class.java).apply {
                    action = RunningService.Actions.PLAY.toString()
                    putExtra("musicUri", currentPlayingUri) // Send the URI to the service
                    putExtra("imageUrl", imageUrl)// Send the URI to the service
                    putExtra("imageName", music.title)// Send the URI to the service
                }
                context.startService(playIntent) // Start the service for playing music
            }

            pauseButton.setOnClickListener {
                pauseIntent = Intent(context, RunningService::class.java).apply {
                    action = RunningService.Actions.PAUSE.toString()
                }
                context.startService(pauseIntent) // Start the service for pausing music
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mediaPlayer.release()
    }
}
